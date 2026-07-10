import { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/client';
import { useTheme } from './ThemeContext';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const { applyHue } = useTheme();

  useEffect(() => {
    const token = localStorage.getItem('token');
    const name = localStorage.getItem('userName');
    const email = localStorage.getItem('userEmail');
    const plan = localStorage.getItem('userPlan');
    if (token) {
      setUser({ token, name, email, plan: plan || 'FREE' });
    }
    setLoading(false);
  }, []);

  // Persiste una respuesta de auth (login/register/update) en estado + localStorage
  const persistAuth = (data) => {
    localStorage.setItem('token', data.token);
    localStorage.setItem('userName', data.name);
    localStorage.setItem('userEmail', data.email);
    localStorage.setItem('userPlan', data.plan || 'FREE');
    setUser({ token: data.token, name: data.name, email: data.email, plan: data.plan || 'FREE' });
    applyHue(data.themeHue != null ? data.themeHue : 265);
  };

  const login = async (email, password) => {
    const { data } = await api.post('/auth/login', { email, password });
    persistAuth(data);
  };

  const register = async (name, email, password) => {
    const { data } = await api.post('/auth/register', { name, email, password });
    persistAuth(data);
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userName');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userPlan');
    setUser(null);
  };

  const updateUserPlan = (plan) => {
    localStorage.setItem('userPlan', plan);
    setUser((prev) => prev ? { ...prev, plan } : prev);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, loading, persistAuth, updateUserPlan }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
