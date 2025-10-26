import React, { useState, useEffect } from 'react';
import axios from 'axios';

function App() {
  const [message, setMessage] = useState('Loading...');

  useEffect(() => {
    // Test backend connection
    axios.get('http://localhost:8080/api/test')
      .then(response => {
        setMessage(response.data);
      })
      .catch(error => {
        setMessage('Error connecting to backend: ' + error.message);
      });
  }, []);

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center">
      <div className="bg-white rounded-lg shadow-2xl p-8 max-w-md w-full">
        <h1 className="text-4xl font-bold text-gray-800 mb-4 text-center">
          🛒 WholesaleConnect My Project
        </h1>
        <p className="text-gray-600 text-center mb-6">
          B2B Marketplace for Wholesalers & Retailers
        </p>
        <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
          <p className="font-bold">Backend Status:</p>
          <p>{message}</p>
        </div>
        <div className="text-center text-gray-500 text-sm">
          <p>Day 1 Setup Complete ✅</p>
          <p className="mt-2">Ready to build something amazing!</p>
        </div>
      </div>
    </div>
  );
}

export default App;