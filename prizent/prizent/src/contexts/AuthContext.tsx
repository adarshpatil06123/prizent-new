import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';

interface User {
  id: string;
  username: string;
  name: string;
  emailId: string;
  role: string;
  clientId: string;
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (username: string, password: string) => Promise<{ success: boolean; error?: string }>;
  logout: () => void;
  isAuthenticated: boolean;
  isAdmin: boolean;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export { AuthContext };

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const [isLoading, setIsLoading] = useState(false);

  const login = async (username: string, password: string): Promise<{ success: boolean; error?: string }> => {
    setIsLoading(true);
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ 
          username, 
          password,
          clientId: "Test Client"
        })
      });

      if (response.status === 403) {
        const errorData = await response.json();
        return { success: false, error: errorData.message || 'Access denied: Admin privileges required' };
      }

      if (!response.ok) {
        try {
          const errorData = await response.json();
          return { success: false, error: errorData.message || 'Invalid credentials' };
        } catch {
          return { success: false, error: `HTTP ${response.status}: ${response.statusText}` };
        }
      }

      const data = await response.json();
      console.log('AuthContext: Login response data:', data);
      
      if (data.success && data.token) {
        console.log('AuthContext: Setting token:', data.token);
        setToken(data.token);
        localStorage.setItem('token', data.token);
        console.log('AuthContext: Token saved to localStorage');
        
        // Decode JWT to get user info (simple base64 decode for demo)
        try {
          const payload = JSON.parse(atob(data.token.split('.')[1]));
          console.log('AuthContext: Decoded JWT payload:', payload);
          const userData: User = {
            id: payload.user_id,
            username: payload.sub,
            name: username, // Backend doesn't return name in token, using username
            emailId: username, // Assuming username is email
            role: payload.role,
            clientId: payload.client_id
          };
          setUser(userData);
          console.log('AuthContext: User data set:', userData);
        } catch (decodeError) {
          console.error('Token decode error:', decodeError);
        }
        
        return { success: true };
      } else {
        return { success: false, error: data.message || 'Login failed' };
      }
    } catch (error) {
      console.error('Login error:', error);
      return { success: false, error: 'Network error. Please check your connection.' };
    } finally {
      setIsLoading(false);
    }
  };

  const logout = useCallback(async () => {
    try {
      if (token) {
        // Call backend logout endpoint
        await fetch('/api/auth/logout', {
          method: 'POST',
          headers: { 
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
      }
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      // Clear local state regardless of backend call success
      setUser(null);
      setToken(null);
      localStorage.removeItem('token');
    }
  }, [token]);

  // Initialize user from existing token on app start
  useEffect(() => {
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const currentTime = Date.now() / 1000;
        
        // Check if token is expired
        if (payload.exp && payload.exp < currentTime) {
          logout();
        } else {
          const userData: User = {
            id: payload.user_id,
            username: payload.sub,
            name: payload.sub,
            emailId: payload.sub,
            role: payload.role,
            clientId: payload.client_id
          };
          setUser(userData);
        }
      } catch (error) {
        console.error('Token validation error:', error);
        logout();
      }
    }
  }, [token, logout]);

  return (
    <AuthContext.Provider value={{
      user,
      token,
      login,
      logout,
      isAuthenticated: !!token && !!user,
      isAdmin: user?.role === 'ADMIN',
      isLoading
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};