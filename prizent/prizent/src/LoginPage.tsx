import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from './hooks/useAuth';
import './LoginPage.css';

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, isLoading } = useAuth();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    
    if (!username || !password) {
      setError('Please enter both username and password');
      return;
    }

    const result = await login(username, password);
    
    if (result.success) {
      // Get the intended destination from location state (set by ProtectedRoute)
      const from = location.state?.from?.pathname || '/superadmin';
      navigate(from);
    } else {
      setError(result.error || 'Login failed');
    }
  };

  return (
    <div className="login-bg">
      <div className="login-form-bg">
        <div className="login-content">
          <h1 className="login-title">Prizent</h1>
          <p className="login-subtitle">Where fashion meets intelligent pricing</p>
          
          <form className="login-form" onSubmit={handleSubmit}>
            <div className="login-form-group">
              <label htmlFor="email" className="login-label">Username or Email</label>
              <input 
                type="text" 
                id="email" 
                className="login-input" 
                placeholder="Enter the username" 
                autoComplete="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                disabled={isLoading}
              />
            </div>
            <div className="login-form-group">
              <label htmlFor="password" className="login-label">Your Password</label>
              <input 
                type="password" 
                id="password" 
                className="login-input" 
                placeholder="Enter the password" 
                autoComplete="current-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={isLoading}
              />
            </div>
            {error && (
              <div style={{ color: 'red', fontSize: '14px', marginBottom: '10px' }}>
                {error}
              </div>
            )}
            <div className="login-actions">
              <button type="button" className="forgot-password-btn">Forgot password?</button>
            </div>
            <button type="submit" className="login-btn" disabled={isLoading}>
              {isLoading ? 'Signing in...' : 'Access workspace'}
            </button>
          </form>
        </div>
      </div>
      <div className="login-image-bg"></div>
    </div>
  );
};

export default LoginPage;