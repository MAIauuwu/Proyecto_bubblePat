import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTheme } from '../context/ThemeContext';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';
import logo from '../assets/BubblePat.png';

// Swatch de color para un hue dado (pastel medio)
const swatch = (h) => `hsl(${h}, 64%, 70%)`;

export default function Settings() {
  const navigate = useNavigate();
  const { hue, setHue, presets } = useTheme();
  const { user, persistAuth } = useAuth();

  const [profile, setProfile] = useState({ name: user?.name || '', email: user?.email || '' });
  const [profileMsg, setProfileMsg] = useState(null);
  const [pwd, setPwd] = useState({ currentPassword: '', newPassword: '', confirm: '' });
  const [pwdMsg, setPwdMsg] = useState(null);

  const saveProfile = async (e) => {
    e.preventDefault();
    setProfileMsg(null);
    try {
      const { data } = await api.put('/users/me', { name: profile.name, email: profile.email });
      persistAuth(data); // refresca token (por si cambió el email) + nombre
      setProfileMsg({ ok: true, text: 'Perfil actualizado correctamente.' });
    } catch (err) {
      setProfileMsg({ ok: false, text: err.response?.data?.error || 'No se pudo actualizar el perfil.' });
    }
  };

  const savePassword = async (e) => {
    e.preventDefault();
    setPwdMsg(null);
    if (pwd.newPassword !== pwd.confirm) {
      setPwdMsg({ ok: false, text: 'La confirmación no coincide con la nueva contraseña.' });
      return;
    }
    try {
      await api.put('/users/me/password', { currentPassword: pwd.currentPassword, newPassword: pwd.newPassword });
      setPwd({ currentPassword: '', newPassword: '', confirm: '' });
      setPwdMsg({ ok: true, text: 'Contraseña cambiada correctamente.' });
    } catch (err) {
      setPwdMsg({ ok: false, text: err.response?.data?.error || 'No se pudo cambiar la contraseña.' });
    }
  };

  const inputClass = 'px-3 py-2 border border-pink-100 rounded-lg text-sm bg-pink-50/50 focus:ring-2 focus:ring-rose-300 outline-none';

  const SECTIONS = [
    { id: 'appearance', label: 'Apariencia', desc: 'Color y tema' },
    { id: 'profile', label: 'Datos de la cuenta', desc: 'Nombre y correo' },
    { id: 'password', label: 'Contraseña', desc: 'Cambiar contraseña' },
  ];
  const [section, setSection] = useState('appearance');

  const navBtn = (id) =>
    `w-full text-left px-4 py-3 rounded-lg text-sm font-medium transition ${
      section === id ? 'bg-rose-300 text-white shadow-sm' : 'text-rose-400 hover:bg-rose-50'
    }`;

  return (
    <div className="min-h-screen bg-gradient-to-br from-pink-50 via-rose-50 to-purple-50">
      <nav className="bg-white/60 backdrop-blur-sm border-b border-pink-100">
        <div className="max-w-5xl mx-auto px-4 py-3 flex justify-between items-center">
          <div className="flex items-center gap-3">
            <button onClick={() => navigate('/')}
              className="flex items-center gap-1 text-rose-400 hover:text-rose-500 font-medium text-sm transition"
              title="Volver al panel">
              <span className="text-lg">←</span>
              <span className="hidden sm:inline">Atrás</span>
            </button>
            <img src={logo} alt="BubblePat" className="h-14" />
          </div>
          <span className="text-rose-300 text-sm font-medium">Configuración</span>
        </div>
      </nav>

      <div className="max-w-5xl mx-auto px-4 py-8 flex flex-col sm:flex-row gap-6">
        {/* ===== Sidebar ===== */}
        <aside className="sm:w-56 shrink-0">
          <div className="bg-white/70 backdrop-blur-sm p-3 rounded-xl border border-pink-100 space-y-1 sm:sticky sm:top-4">
            {SECTIONS.map((s) => (
              <button key={s.id} onClick={() => setSection(s.id)} className={navBtn(s.id)}>
                <span className="block">{s.label}</span>
                <span className={`block text-[11px] font-normal ${section === s.id ? 'text-white/80' : 'text-rose-300'}`}>{s.desc}</span>
              </button>
            ))}
          </div>
        </aside>

        {/* ===== Contenido ===== */}
        <div className="flex-1 max-w-xl space-y-6">
          {section === 'appearance' && (
            <>
              <div>
                <h2 className="text-2xl font-bold text-rose-500">Apariencia</h2>
                <p className="text-rose-300 text-sm">Personaliza el color de tu BubblePat. Todos son tonos pastel y se guardan en este navegador.</p>
              </div>

              <div className="bg-white/70 backdrop-blur-sm p-5 rounded-xl border border-pink-100">
                <h3 className="font-bold text-rose-500 mb-3">Temas</h3>
                <div className="grid grid-cols-4 gap-3">
                  {presets.map((p) => (
                    <button key={p.hue} onClick={() => setHue(p.hue)}
                      className={`flex flex-col items-center gap-1 p-3 rounded-xl border-2 transition ${
                        hue === p.hue ? 'border-rose-400 bg-rose-50' : 'border-transparent bg-pink-50/50 hover:bg-pink-100'
                      }`}>
                      <span className="w-8 h-8 rounded-full shadow-inner border border-white" style={{ background: swatch(p.hue) }} />
                      <span className="text-[11px] text-rose-400 font-medium">{p.name}</span>
                    </button>
                  ))}
                </div>
              </div>

              <div className="bg-white/70 backdrop-blur-sm p-5 rounded-xl border border-pink-100">
                <h3 className="font-bold text-rose-500 mb-1">Tu propio color</h3>
                <p className="text-xs text-rose-300 mb-3">Mueve la ruedita para elegir cualquier tono. Ideal si buscas algo distinto o necesitas más contraste.</p>
                <input type="range" min="0" max="360" value={hue}
                  onChange={(e) => setHue(Number(e.target.value))}
                  className="w-full h-3 rounded-full appearance-none cursor-pointer mb-2"
                  style={{ background: 'linear-gradient(to right, hsl(0,70%,70%), hsl(60,70%,70%), hsl(120,70%,70%), hsl(180,70%,70%), hsl(240,70%,70%), hsl(300,70%,70%), hsl(360,70%,70%))' }} />
                <div className="flex items-center justify-between text-xs text-rose-300">
                  <span>Tono: {hue}°</span>
                  <button onClick={() => setHue(350)} className="text-rose-400 hover:underline">Restablecer</button>
                </div>
              </div>

              <div className="bg-white/70 backdrop-blur-sm p-5 rounded-xl border border-pink-100">
                <h3 className="font-bold text-rose-500 mb-3">Vista previa</h3>
                <div className="rounded-xl border border-pink-100 p-4 bg-pink-50/50 space-y-3">
                  <div className="flex items-center gap-2">
                    <span className="bg-rose-300 text-white px-4 py-2 rounded-lg text-sm font-medium">Botón primario</span>
                    <span className="bg-emerald-200 text-emerald-700 px-3 py-1 rounded-lg text-xs">Completar</span>
                  </div>
                  <p className="text-rose-500 font-bold text-lg">¡Hola! Soy el Asistente BubblePat</p>
                  <p className="text-rose-300 text-sm">Texto secundario y descripciones.</p>
                  <div className="h-2 rounded-full bg-pink-100 overflow-hidden">
                    <div className="h-full bg-rose-400" style={{ width: '70%' }} />
                  </div>
                </div>
                <p className="text-[11px] text-rose-200 mt-2">La vista previa, los botones y toda la app cambian al instante con el tono elegido.</p>
              </div>
            </>
          )}

          {section === 'profile' && (
            <>
              <div>
                <h2 className="text-2xl font-bold text-rose-500">Datos de la cuenta</h2>
                <p className="text-rose-300 text-sm">Tu información personal.</p>
              </div>
              <form onSubmit={saveProfile} className="bg-white/70 backdrop-blur-sm p-5 rounded-xl border border-pink-100 space-y-3">
                <div>
                  <label className="block text-xs text-rose-300 mb-1">Nombre</label>
                  <input value={profile.name} onChange={(e) => setProfile({ ...profile, name: e.target.value })}
                    className={inputClass + ' w-full'} required />
                </div>
                <div>
                  <label className="block text-xs text-rose-300 mb-1">Correo</label>
                  <input type="email" value={profile.email} onChange={(e) => setProfile({ ...profile, email: e.target.value })}
                    className={inputClass + ' w-full'} required />
                </div>
                {profileMsg && (
                  <p className={`text-xs font-medium ${profileMsg.ok ? 'text-emerald-600' : 'text-rose-500'}`}>{profileMsg.text}</p>
                )}
                <button type="submit" className="bg-rose-300 text-white px-4 py-2 rounded-lg text-sm hover:bg-rose-400 transition font-medium shadow-sm">
                  Guardar datos
                </button>
              </form>
            </>
          )}

          {section === 'password' && (
            <>
              <div>
                <h2 className="text-2xl font-bold text-rose-500">Cambiar contraseña</h2>
                <p className="text-rose-300 text-sm">Mantén tu cuenta segura con una contraseña actualizada.</p>
              </div>
              <form onSubmit={savePassword} className="bg-white/70 backdrop-blur-sm p-5 rounded-xl border border-pink-100 space-y-3">
                <div>
                  <label className="block text-xs text-rose-300 mb-1">Contraseña actual</label>
                  <input type="password" value={pwd.currentPassword}
                    onChange={(e) => setPwd({ ...pwd, currentPassword: e.target.value })}
                    className={inputClass + ' w-full'} required />
                </div>
                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs text-rose-300 mb-1">Nueva (mín. 6)</label>
                    <input type="password" value={pwd.newPassword}
                      onChange={(e) => setPwd({ ...pwd, newPassword: e.target.value })}
                      className={inputClass + ' w-full'} required />
                  </div>
                  <div>
                    <label className="block text-xs text-rose-300 mb-1">Confirmar</label>
                    <input type="password" value={pwd.confirm}
                      onChange={(e) => setPwd({ ...pwd, confirm: e.target.value })}
                      className={inputClass + ' w-full'} required />
                  </div>
                </div>
                {pwdMsg && (
                  <p className={`text-xs font-medium ${pwdMsg.ok ? 'text-emerald-600' : 'text-rose-500'}`}>{pwdMsg.text}</p>
                )}
                <button type="submit" className="bg-rose-300 text-white px-4 py-2 rounded-lg text-sm hover:bg-rose-400 transition font-medium shadow-sm">
                  Cambiar contraseña
                </button>
              </form>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
