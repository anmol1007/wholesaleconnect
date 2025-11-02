import React from 'react';
import { ShoppingCart, Star, Package } from 'lucide-react';

/**
 * ProductCard Component - Displays individual product
 * Modern card design with hover effects
 * 
 * @param {Object} product - Product data from API
 */
const ProductCard = ({ product }) => {
  return (
    <div className="bg-white rounded-xl shadow-md hover:shadow-2xl transition-shadow duration-300 overflow-hidden group">
      {/* Product Image */}
      <div className="relative h-48 bg-gradient-to-br from-gray-100 to-gray-200 overflow-hidden">
        {product.imageUrls && product.imageUrls.length > 0 ? (
          <img
            src={product.imageUrls[0]}
            alt={product.name}
            className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
          />
        ) : (
          // Placeholder if no image
          <div className="flex items-center justify-center h-full">
            <Package className="h-24 w-24 text-gray-400" />
          </div>
        )}
        
        {/* Stock Badge */}
        {product.stockQuantity === 0 && (
          <div className="absolute top-2 right-2 bg-red-500 text-white px-3 py-1 rounded-full text-xs font-bold">
            Out of Stock
          </div>
        )}
      </div>
      
      {/* Product Details */}
      <div className="p-4">
        {/* Product Name */}
        <h3 className="text-lg font-bold text-gray-800 mb-1 line-clamp-2">
          {product.name}
        </h3>
        
        {/* Category & Brand */}
        <div className="flex items-center space-x-2 mb-2">
          <span className="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded-full">
            {product.category}
          </span>
          <span className="text-xs text-gray-500">{product.brand}</span>
        </div>
        
        {/* Price */}
        <div className="flex items-baseline space-x-2 mb-3">
          <span className="text-2xl font-bold text-green-600">
            ₹{product.sellingPrice}
          </span>
          {product.mrp > product.sellingPrice && (
            <span className="text-sm text-gray-400 line-through">
              ₹{product.mrp}
            </span>
          )}
        </div>
        
        {/* Stock Info */}
        <div className="flex items-center justify-between mb-3">
          <span className="text-sm text-gray-600">
            Stock: <span className="font-semibold">{product.stockQuantity}</span>
          </span>
          <span className="text-xs text-gray-500">
            MOQ: {product.moq}
          </span>
        </div>
        
        {/* Add to Cart Button */}
        <button
          disabled={product.stockQuantity === 0}
          className={`w-full flex items-center justify-center space-x-2 py-2 rounded-lg font-semibold transition-all duration-200 ${
            product.stockQuantity === 0
              ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
              : 'bg-gradient-to-r from-blue-600 to-purple-600 text-white hover:from-blue-700 hover:to-purple-700 hover:shadow-lg'
          }`}
        >
          <ShoppingCart size={20} />
          <span>Add to Cart</span>
        </button>
      </div>
    </div>
  );
};

export default ProductCard;