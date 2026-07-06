import { useNavigate } from 'react-router-dom';
import { useTheme } from '../context/ThemeContext';
import logo from '../assets/BubblePat.png';

// Swatch de color para un hue dado (pastel medio)
const swatch = (h) => `hsl(${h}, 64%, 70%)`;

export default function Settings() {
  const navigate = useNavigate();
  const { hue, setHue, presets } = useTheme();

  const inputClass = 'px-3 py-2 border border-pink-100 rounded-lg text-sm bg-pink-50/50 focus:ring-2 focus:ring-rose-300 outline-none';

  return (
    <div className="min-h-screen bg-gradient-to-br from-pink-50 via-rose-50 to-purple-50">
      <nav className="bg-white/60 backdrop-blur-sm border-b border-pink-100">
        <div className="max-w-6xl mx-auto px-4 py-3 flex justify-between items-center">
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

      <div className="max-w-xl mx-auto px-4 py-8 space-y-6">
        <div>
          <h2 className="text-2xl font-bold text-rose-500">Apariencia</h2>
          <p className="text-rose-300 text-sm">Personaliza el color de tu BubblePat. Todos son tonos pastel y se guardan en este navegador.</p>
        </div>

        {/* Temas predefinidos */}
        <div className="bg-white/70 backdrop-blur-sm p-5 rounded-xl border border-pink-100">
          <h3 className="font-bold text-rose-500 mb-3">🎨 Temas</h3>
          <div className="grid grid-cols-4 gap-3">
            {presets.map((p) => (
              <button key={p.hue} onClick={() => setHue(p.hue)}
                className={`flex flex-col items-center gap-1 p-3 rounded-xl border-2 transition ${
                  hue === p.hue ? 'border-rose-400 bg-rose-50' : 'border-transparent bg-pink-50/50 hover:bg-pink-100'
                }`}>
                <span className="w-8 h-8 rounded-full shadow-inner border border-white" style={{ background: swatch(p.hue) }} />
                <span className="text-[11px] text-rose-400 font-medium">{p.emoji} {p.name}</span>
              </button>
            ))}
          </div>
        </div>

        {/* Ruedita personalizada */}
        <div className="bg-white/70 backdrop-blur-sm p-5 rounded-xl border border-pink-100">
          <h3 className="font-bold text-rose-500 mb-1">🌀 Tu propio color</h3>
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

        {/* Vista previa en vivo */}
        <div className="bg-white/70 backdrop-blur-sm p-5 rounded-xl border border-pink-100">
          <h3 className="font-bold text-rose-500 mb-3">👁️ Vista previa</h3>
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

        <div className="flex gap-3">
          <button onClick={() => navigate('/')}
            className="flex-1 bg-rose-300 text-white py-2 rounded-lg hover:bg-rose-400 transition font-medium shadow-sm">
            Guardar y volver al panel
          </button>
        </div>
      </div>
    </div>
  );
}
