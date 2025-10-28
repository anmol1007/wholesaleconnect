-- WholesaleConnect Database Setup Script
-- PostgreSQL 14+
-- Run this script to create the complete database schema

-- Create Database (run as superuser)
-- CREATE DATABASE wholesaleconnect;
-- \c wholesaleconnect;

-- Enable UUID extension (optional, for UUID primary keys)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- DROP TABLES (if exists - for fresh setup)
-- =====================================================
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS credit_ledger CASCADE;
DROP TABLE IF EXISTS credit_limits CASCADE;
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- =====================================================
-- 1. USERS TABLE
-- =====================================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('DISTRIBUTOR', 'WHOLESALER', 'RETAILER', 'ADMIN')),
    business_name VARCHAR(255),
    gst_number VARCHAR(50),
    address TEXT,
    is_active BOOLEAN DEFAULT false,
    kyc_verified BOOLEAN DEFAULT false,
    kyc_document_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for users table
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_is_active ON users(is_active);

-- =====================================================
-- 2. PRODUCTS TABLE
-- =====================================================
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    brand VARCHAR(100),
    mrp DECIMAL(10,2) NOT NULL CHECK (mrp > 0),
    selling_price DECIMAL(10,2) NOT NULL CHECK (selling_price > 0),
    stock_quantity INTEGER DEFAULT 0 CHECK (stock_quantity >= 0),
    moq INTEGER DEFAULT 1 CHECK (moq > 0),
    image_urls TEXT[],
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for products table
CREATE INDEX idx_products_seller ON products(seller_id);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_is_active ON products(is_active);
CREATE INDEX idx_products_name ON products(name);

-- =====================================================
-- 3. ORDERS TABLE
-- =====================================================
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    buyer_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    seller_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    gst_amount DECIMAL(10,2) NOT NULL CHECK (gst_amount >= 0),
    grand_total DECIMAL(10,2) NOT NULL CHECK (grand_total >= 0),
    payment_method VARCHAR(50) NOT NULL CHECK (payment_method IN ('CASH', 'ONLINE', 'UDAR')),
    payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (payment_status IN ('PENDING', 'PAID', 'OVERDUE', 'CANCELLED')),
    udar_duration INTEGER CHECK (udar_duration IN (7, 15, 30)),
    due_date DATE,
    order_status VARCHAR(50) NOT NULL DEFAULT 'PENDING_APPROVAL' CHECK (order_status IN ('PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    rejected_at TIMESTAMP,
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    cancelled_at TIMESTAMP
);

-- Indexes for orders table
CREATE INDEX idx_orders_buyer ON orders(buyer_id);
CREATE INDEX idx_orders_seller ON orders(seller_id);
CREATE INDEX idx_orders_payment_status ON orders(payment_status);
CREATE INDEX idx_orders_order_status ON orders(order_status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);
CREATE INDEX idx_orders_due_date ON orders(due_date);

-- =====================================================
-- 4. ORDER ITEMS TABLE
-- =====================================================
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for order_items table
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);

-- =====================================================
-- 5. CREDIT LEDGER TABLE
-- =====================================================
CREATE TABLE credit_ledger (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    retailer_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    seller_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    due_date DATE NOT NULL,
    payment_date DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PAID', 'OVERDUE', 'CANCELLED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for credit_ledger table
CREATE INDEX idx_credit_ledger_retailer ON credit_ledger(retailer_id);
CREATE INDEX idx_credit_ledger_seller ON credit_ledger(seller_id);
CREATE INDEX idx_credit_ledger_status ON credit_ledger(status);
CREATE INDEX idx_credit_ledger_due_date ON credit_ledger(due_date);

-- =====================================================
-- 6. CREDIT LIMITS TABLE
-- =====================================================
CREATE TABLE credit_limits (
    id BIGSERIAL PRIMARY KEY,
    retailer_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    seller_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    credit_limit DECIMAL(10,2) DEFAULT 0 CHECK (credit_limit >= 0),
    outstanding_dues DECIMAL(10,2) DEFAULT 0 CHECK (outstanding_dues >= 0),
    available_credit DECIMAL(10,2) DEFAULT 0 CHECK (available_credit >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(retailer_id, seller_id)
);

-- Indexes for credit_limits table
CREATE INDEX idx_credit_limits_retailer ON credit_limits(retailer_id);
CREATE INDEX idx_credit_limits_seller ON credit_limits(seller_id);

-- =====================================================
-- 7. PAYMENTS TABLE
-- =====================================================
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    payment_method VARCHAR(50) NOT NULL CHECK (payment_method IN ('ONLINE', 'CASH', 'BANK_TRANSFER')),
    transaction_id VARCHAR(255),
    razorpay_order_id VARCHAR(255),
    razorpay_payment_id VARCHAR(255),
    razorpay_signature VARCHAR(500),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED')),
    payment_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for payments table
CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_transaction_id ON payments(transaction_id);

-- =====================================================
-- 8. NOTIFICATIONS TABLE
-- =====================================================
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL CHECK (type IN ('ORDER', 'PAYMENT', 'CREDIT', 'SYSTEM')),
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for notifications table
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);

-- =====================================================
-- TRIGGERS FOR UPDATED_AT TIMESTAMPS
-- =====================================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply trigger to tables with updated_at column
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_credit_ledger_updated_at BEFORE UPDATE ON credit_ledger
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_credit_limits_updated_at BEFORE UPDATE ON credit_limits
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- SAMPLE DATA FOR TESTING
-- =====================================================

-- Insert Admin User (Password: admin123 - hashed with bcrypt)
INSERT INTO users (email, password, name, phone, role, is_active, kyc_verified)
VALUES ('admin@wholesaleconnect.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin User', '9999999999', 'ADMIN', true, true);

-- Insert Sample Distributor (Password: dist123)
INSERT INTO users (email, password, name, phone, role, business_name, gst_number, is_active, kyc_verified)
VALUES ('distributor@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ABC Distributors', '9876543210', 'DISTRIBUTOR', 'ABC Distributors Pvt Ltd', '27AABCU9603R1ZM', true, true);

-- Insert Sample Wholesaler (Password: whole123)
INSERT INTO users (email, password, name, phone, role, business_name, gst_number, is_active, kyc_verified)
VALUES ('wholesaler@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'XYZ Wholesalers', '9876543211', 'WHOLESALER', 'XYZ Wholesale Trading', '27AABCU9603R1ZN', true, true);

-- Insert Sample Retailer (Password: retail123)
INSERT INTO users (email, password, name, phone, role, business_name, gst_number, is_active, kyc_verified)
VALUES ('retailer@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Retail Shop', '9876543212', 'RETAILER', 'My Retail Shop', '27AABCU9603R1ZO', true, true);

-- Insert Sample Products (Distributor's products - seller_id = 2)
INSERT INTO products (seller_id, name, description, category, brand, mrp, selling_price, stock_quantity, moq, is_active)
VALUES 
(2, 'Dairy Milk Chocolate 50g', 'Cadbury Dairy Milk Chocolate 50g pack', 'Chocolates', 'Cadbury', 50.00, 45.00, 1000, 10, true),
(2, 'Dairy Milk Chocolate 100g', 'Cadbury Dairy Milk Chocolate 100g pack', 'Chocolates', 'Cadbury', 100.00, 90.00, 800, 10, true),
(2, '5 Star Chocolate', 'Cadbury 5 Star Chocolate', 'Chocolates', 'Cadbury', 20.00, 18.00, 1500, 20, true),
(2, 'Perk Chocolate', 'Cadbury Perk Chocolate', 'Chocolates', 'Cadbury', 10.00, 9.00, 2000, 50, true);

-- Insert Sample Products (Wholesaler's products - seller_id = 3)
INSERT INTO products (seller_id, name, description, category, brand, mrp, selling_price, stock_quantity, moq, is_active)
VALUES 
(3, 'Parle-G Biscuits 1kg', 'Parle-G Glucose Biscuits 1kg pack', 'Biscuits', 'Parle', 80.00, 75.00, 500, 5, true),
(3, 'Good Day Cookies', 'Britannia Good Day Butter Cookies', 'Cookies', 'Britannia', 60.00, 55.00, 400, 10, true);

-- Set Credit Limits for Retailer (retailer_id = 4)
INSERT INTO credit_limits (retailer_id, seller_id, credit_limit, outstanding_dues, available_credit)
VALUES 
(4, 2, 50000.00, 0.00, 50000.00),  -- Retailer with Distributor
(4, 3, 25000.00, 0.00, 25000.00);  -- Retailer with Wholesaler

-- =====================================================
-- COMPLETION MESSAGE
-- =====================================================

DO $$
BEGIN
    RAISE NOTICE '================================================';
    RAISE NOTICE 'WholesaleConnect Database Setup Complete!';
    RAISE NOTICE '================================================';
    RAISE NOTICE 'Tables Created: 8';
    RAISE NOTICE 'Indexes Created: Multiple for optimization';
    RAISE NOTICE 'Sample Data: 4 users, 6 products, 2 credit limits';
    RAISE NOTICE '================================================';
    RAISE NOTICE 'Next Steps:';
    RAISE NOTICE '1. Update application.properties with DB credentials';
    RAISE NOTICE '2. Run Spring Boot application';
    RAISE NOTICE '3. Test API endpoints with browser/Postman';
    RAISE NOTICE '================================================';
END $$;