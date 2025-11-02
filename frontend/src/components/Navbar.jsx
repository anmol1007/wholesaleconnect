import React from 'react';
import { ShoppingCart, User, Home, Package } from 'lucide-react';

/**
 * Navbar Component - Professional navigation bar
 * Shows at top of every page
 */
const Navbar = () => {
  return (
    // Gradient background with shadow
    <nav className="bg-gradient-to-r from-blue-600 to-purple-600 shadow-lg">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo Section */}
          <div className="flex items-center space-x-2">
            <ShoppingCart className="h-8 w-8 text-white" />
            <span className="text-white text-2xl font-bold">
              WholesaleConnect
            </span>
          </div>
          
          {/* Navigation Links */}
          <div className="flex space-x-4">
            <NavLink icon={<Home size={20} />} text="Home" />
            <NavLink icon={<Package size={20} />} text="Products" />
            <NavLink icon={<User size={20} />} text="Profile" />
          </div>
        </div>
      </div>
    </nav>
  );
};

/**
 * NavLink Component - Individual navigation link
 * Reusable for each menu item
 */
const NavLink = ({ icon, text }) => {
  return (
    <button className="flex items-center space-x-2 text-white hover:bg-white/20 px-3 py-2 rounded-md transition-colors duration-200">
      {icon}
      <span>{text}</span>
    </button>
  );
};

export default Navbar;