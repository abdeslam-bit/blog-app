import React, { createContext, useContext, useState, useCallback, useEffect } from 'react';
import { apiService } from '../services/apiService';

interface AuthUser {
  id: string;
  name: string;
  email: string;
}

interface AuthContextType {
  isAuthenticated: boolean;
  user: AuthUser | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  token: string | null;
}

interface AuthProviderProps {
  children: React.ReactNode;
}

const parseUserFromToken = (token: string): AuthUser | null => {
  try {
    const parts = token.split('.');
    if (parts.length < 2) return null;
    const base64Url = parts[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    const payload = JSON.parse(jsonPayload);
    return {
      id: payload.id || payload.sub,
      name: payload.name || payload.sub?.split('@')[0] || 'User',
      email: payload.sub || '',
    };
  } catch {
    return null;
  }
};

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const initialToken = localStorage.getItem('token');
  const validInitialToken = initialToken && initialToken.trim() !== '' && initialToken !== 'undefined' && initialToken !== 'null' ? initialToken : null;
  
  const [token, setToken] = useState<string | null>(validInitialToken);
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(!!validInitialToken);
  const [user, setUser] = useState<AuthUser | null>(validInitialToken ? parseUserFromToken(validInitialToken) : null);

  // Initialize auth state from token
  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    if (storedToken && storedToken.trim() !== '' && storedToken !== 'undefined' && storedToken !== 'null') {
      const parsed = parseUserFromToken(storedToken);
      if (parsed) {
        setIsAuthenticated(true);
        setUser(parsed);
        setToken(storedToken);
      } else {
        localStorage.removeItem('token');
        setIsAuthenticated(false);
        setUser(null);
        setToken(null);
      }
    } else {
      localStorage.removeItem('token');
      setIsAuthenticated(false);
      setUser(null);
      setToken(null);
    }
  }, []);

  const login = useCallback(async (email: string, password: string) => {
    const response = await apiService.login({ email, password });
    
    localStorage.setItem('token', response.token);
    setToken(response.token);
    setUser(parseUserFromToken(response.token));
    setIsAuthenticated(true);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('token');
    setIsAuthenticated(false);
    setUser(null);
    setToken(null);
    apiService.logout();
    const axiosInstance = apiService['api'];
    delete axiosInstance.defaults.headers.common['Authorization'];
  }, []);

  // Update apiService token when it changes
  useEffect(() => {
    const axiosInstance = apiService['api'];
    if (token && token.trim() !== '' && token !== 'undefined' && token !== 'null') {
      axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } else {
      delete axiosInstance.defaults.headers.common['Authorization'];
    }
  }, [token]);

  const value = {
    isAuthenticated,
    user,
    login,
    logout,
    token
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export default AuthContext;
