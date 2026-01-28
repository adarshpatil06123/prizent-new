import React, { useState } from 'react';

const ConnectionTest: React.FC = () => {
  const [testResult, setTestResult] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false);

  const testConnection = async () => {
    setIsLoading(true);
    setTestResult('Testing connection...');
    
    try {
      // Test API Gateway connection
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ 
          username: 'admin',
          password: 'admin123',
          clientId: 'Test Client'
        })
      });

      if (response.ok) {
        const data = await response.json();
        setTestResult(`✅ Connection successful! Token received: ${data.token ? 'YES' : 'NO'}`);
      } else {
        const errorText = await response.text();
        setTestResult(`❌ HTTP ${response.status}: ${errorText}`);
      }
    } catch (error) {
      setTestResult(`❌ Network Error: ${error}`);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={{ 
      position: 'fixed', 
      top: 10, 
      right: 10, 
      background: 'white', 
      padding: '20px', 
      border: '1px solid #ccc', 
      zIndex: 9999,
      maxWidth: '400px'
    }}>
      <h3>Backend Connection Test</h3>
      <button 
        onClick={testConnection} 
        disabled={isLoading}
        style={{ marginBottom: '10px', padding: '10px' }}
      >
        {isLoading ? 'Testing...' : 'Test Connection'}
      </button>
      <div style={{ fontSize: '12px', wordBreak: 'break-all' }}>
        {testResult}
      </div>
    </div>
  );
};

export default ConnectionTest;