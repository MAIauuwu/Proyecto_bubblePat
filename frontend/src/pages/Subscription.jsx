import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { PLAN_INFO, PLAN_FEATURES, isPremium, updatePlan } from '../api/plans';
import logo from '../assets/BubblePat.png';

const Check = () => <span className="text-emerald-400">✓</span>;

export default function Subscription() {
  const navigate = useNavigate();
  const { user, updateUserPlan } = useAuth();
  const currentPlan = user?.plan || 'FREE';

  const handleSelect = async (plan) => {
    if (plan === 'FAMILY') return;
    try {
      await updatePlan(plan);
      updateUserPlan(plan);
    } catch {
      alert('Error al actualizar el plan');
    }
  };

  const plans = [
    { key: 'FREE', info: PLAN_INFO.FREE, features: PLAN_FEATURES.free, highlight: false, locked: false },
    { key: 'PREMIUM_MONTHLY', info: PLAN_INFO.PREMIUM_MONTHLY, features: PLAN_FEATURES.premium, highlight: true, locked: false },
    { key: 'PREMIUM_ANNUAL', info: PLAN_INFO.PREMIUM_ANNUAL, features: PLAN_FEATURES.premium, highlight: true, locked: false },
    { key: 'FAMILY', info: PLAN_INFO.FAMILY, features: PLAN_FEATURES.family, highlight: false, locked: true },
  ];

  const cardClass = (p) => {
    const base = 'bg-white/80 rounded-2xl p-6 flex flex-col shadow-sm border-2 ';
    if (currentPlan === p.key) return base + 'border-rose-400 ring-2 ring-rose-200';
    if (p.highlight) return base + 'border-rose-200';
    return base + 'border-pink-100';
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-pink-50 via-rose-50 to-purple-50">
      <nav className="bg-white/60 backdrop-blur-sm border-b border-pink-100">
        <div className="max-w-6xl mx-auto px-4 py-3 flex items-center gap-3">
          <button onClick={() => navigate('/app')} className="text-rose-400 hover:text-rose-500 font-medium text-sm">← Atrás</button>
          <img src={logo} alt="BubblePat" className="h-10" />
        </div>
      </nav>

      <div className="max-w-5xl mx-auto px-4 py-8">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-rose-500">Tu suscripción</h1>
          <p className="text-rose-300 mt-2">Plan actual: <span className="font-bold text-rose-500">{PLAN_INFO[currentPlan]?.label || 'Gratis'}</span></p>
        </div>

        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-5">
          {plans.map((p) => (
            <div key={p.key} className={cardClass(p)}>
              {p.info.badge && <span className="self-start text-[11px] bg-rose-100 text-rose-500 px-2 py-0.5 rounded-full font-medium mb-2">{p.info.badge}</span>}
              <h3 className="text-lg font-bold text-rose-500">{p.info.label}</h3>
              <p className="text-2xl font-extrabold text-rose-500 mt-1">{p.info.price}<span className="text-sm font-normal text-rose-300">{p.info.period}</span></p>

              <ul className="mt-4 space-y-1.5 flex-1">
                {p.features.map((f) => (
                  <li key={f} className="flex items-start gap-2 text-sm text-rose-400"><Check /> {f}</li>
                ))}
              </ul>

              {currentPlan === p.key ? (
                <div className="mt-5 text-center bg-emerald-100 text-emerald-600 py-2.5 rounded-lg font-medium text-sm">✓ Plan activo</div>
              ) : p.locked ? (
                <div className="mt-5 text-center bg-purple-100 text-purple-400 py-2.5 rounded-lg font-medium text-sm cursor-not-allowed">Próximamente</div>
              ) : (
                <button onClick={() => handleSelect(p.key)}
                  className={`mt-5 py-2.5 rounded-lg font-medium text-sm transition ${p.highlight ? 'bg-rose-400 text-white hover:bg-rose-500' : 'bg-gray-100 text-rose-400 hover:bg-gray-200'}`}>
                  {isPremium(currentPlan) && !isPremium(p.key) ? 'Bajar a gratis' : 'Cambiar a este plan'}
                </button>
              )}
            </div>
          ))}
        </div>

        <p className="text-center text-xs text-rose-200 mt-8">
          🔒 Demo: los cambios de plan son simulados. La integración de pagos estará disponible próximamente.
        </p>
      </div>
    </div>
  );
}
