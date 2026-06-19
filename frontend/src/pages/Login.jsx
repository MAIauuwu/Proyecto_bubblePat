import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Link, useNavigate } from 'react-router-dom';
import logo from '../assets/BubblePat.png';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await login(email, password);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.error || 'Error al iniciar sesión');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-pink-50 via-rose-50 to-purple-50">
      <div className="bg-white/80 backdrop-blur-sm p-8 rounded-2xl shadow-lg w-full max-w-md border border-pink-100">
        <img src={logo} alt="BubblePat" className="w-48 mx-auto mb-4" />
        <h2 className="text-xl text-center text-rose-300 mb-8">Inicia Sesión</h2>

        {error && <div className="bg-rose-50 text-rose-500 p-3 rounded-lg mb-4 border border-rose-100">{error}</div>}

        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full px-4 py-3 border border-pink-100 rounded-lg focus:ring-2 focus:ring-rose-300 focus:border-transparent outline-none bg-pink-50/50"
            required
          />
          <input
            type="password"
            placeholder="Contraseña"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full px-4 py-3 border border-pink-100 rounded-lg focus:ring-2 focus:ring-rose-300 focus:border-transparent outline-none bg-pink-50/50"
            required
          />
          <button
            type="submit"
            className="w-full bg-rose-300 text-white py-3 rounded-lg hover:bg-rose-400 transition font-semibold shadow-sm"
          >
            Entrar
          </button>
        </form>

        <p className="text-center text-rose-300 mt-6">
          ¿No tienes cuenta? <Link to="/register" className="text-rose-400 hover:underline font-medium">Regístrate</Link>
        </p>
      </div>
    </div>
  );
}
