import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Navbar from './components/Navbar';
import ProductCard from './components/ProductCard';
import { Package, Search, Filter } from 'lucide-react';

function App() {

  // === STATE MANAGEMENT ===
  // useState creates reactive variables that trigger re-render when changed

  const [products, setProducts] = useState([]);  // All products from API
  const [loading, setLoading] = useState(true);  // Loading state
  const [searchTerm, setSearchTerm] = useState('');  // Search input
  const [selectedCategory, setSelectedCategory] = useState('All');  // Category filter
  const [categories, setCategories] = useState(['All']);  // Available categories

  // === FETCH DATA ON COMPONENT MOUNT ===
  // useEffect runs when component loads
  useEffect(() => {
    fetchProducts();
  }, []);  // Empty array = run only once on mount

  /**
   * Fetch products from backend API
   */
  const fetchProducts = async () => {
    try {
      setLoading(true);
      
      // Call API
      const response = await axios.get('http://localhost:8080/api/products/active');
      
      // Update state with data
      setProducts(response.data);
      
      // Extract unique categories
      const uniqueCategories = ['All', ...new Set(response.data.map(p => p.category))];
      setCategories(uniqueCategories);
      
      setLoading(false);
    } catch (error) {
      console.error('Error fetching products:', error);
      setLoading(false);
    }
  };
  
  // === FILTER PRODUCTS ===
  const filteredProducts = products.filter(product => {
    // Filter by search term
    const matchesSearch = product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         product.description?.toLowerCase().includes(searchTerm.toLowerCase());
    
    // Filter by category
    const matchesCategory = selectedCategory === 'All' || product.category === selectedCategory;
    
    return matchesSearch && matchesCategory;
  });
  
  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navigation Bar */}
      <Navbar />
      
      {/* Hero Section */}
      <div className="bg-gradient-to-r from-blue-600 to-purple-600 text-white py-12">
        <div className="max-w-7xl mx-auto px-4 text-center">
          <h1 className="text-5xl font-bold mb-4">
            Welcome to WholesaleConnect
          </h1>
          <p className="text-xl opacity-90">
            Your B2B Marketplace for Wholesale Trading
          </p>
          <div className="mt-6 flex items-center justify-center space-x-4">
            <div className="bg-white/20 backdrop-blur-sm px-6 py-3 rounded-lg">
              <span className="text-3xl font-bold">{products.length}</span>
              <p className="text-sm">Products Available</p>
            </div>
            <div className="bg-white/20 backdrop-blur-sm px-6 py-3 rounded-lg">
              <span className="text-3xl font-bold">{categories.length - 1}</span>
              <p className="text-sm">Categories</p>
            </div>
          </div>
        </div>
      </div>
      
      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* Search & Filter Bar */}
        <div className="bg-white rounded-xl shadow-md p-6 mb-8">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Search Box */}
            <div className="relative">
              <Search className="absolute left-3 top-3 text-gray-400" size={20} />
              <input
                type="text"
                placeholder="Search products..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border-2 border-gray-200 rounded-lg focus:border-blue-500 focus:outline-none transition-colors"
              />
            </div>
            
            {/* Category Filter */}
            <div className="relative">
              <Filter className="absolute left-3 top-3 text-gray-400" size={20} />
              <select
                value={selectedCategory}
                onChange={(e) => setSelectedCategory(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border-2 border-gray-200 rounded-lg focus:border-blue-500 focus:outline-none appearance-none cursor-pointer transition-colors"
              >
                {categories.map(category => (
                  <option key={category} value={category}>
                    {category}
                  </option>
                ))}
              </select>
            </div>
          </div>
          
          {/* Results Count */}
          <div className="mt-4 text-sm text-gray-600">
            Showing <span className="font-bold text-blue-600">{filteredProducts.length}</span> products
          </div>
        </div>
        
        {/* Loading State */}
        {loading && (
          <div className="flex items-center justify-center py-20">
            <div className="animate-spin rounded-full h-16 w-16 border-b-4 border-blue-600"></div>
          </div>
        )}
        
        {/* No Products Found */}
        {!loading && filteredProducts.length === 0 && (
          <div className="text-center py-20">
            <Package className="h-24 w-24 text-gray-300 mx-auto mb-4" />
            <h3 className="text-2xl font-bold text-gray-600 mb-2">
              No products found
            </h3>
            <p className="text-gray-500">
              Try adjusting your search or filters
            </p>
          </div>
        )}
        
        {/* Product Grid */}
        {!loading && filteredProducts.length > 0 && (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {filteredProducts.map(product => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        )}
      </div>
      
      {/* Footer */}
      <footer className="bg-gray-800 text-white py-8 mt-16">
        <div className="max-w-7xl mx-auto px-4 text-center">
          <p className="text-lg font-semibold mb-2">
            Day 3 Complete ✅
          </p>
          <p className="text-gray-400">
            Products, Orders, and Modern UI - All Working!
          </p>
        </div>
      </footer>
    </div>
  );
}

export default App;