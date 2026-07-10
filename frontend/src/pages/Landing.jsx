import { useState } from 'react';
import { Link } from 'react-router-dom';
import logo from '../assets/BubblePat.png';
import icon from '../assets/BubblePatIcon.png';
import heroImg from '../assets/hero-web.png';
import { useAuth } from '../context/AuthContext';
import { PLAN_INFO, PLAN_FEATURES } from '../api/plans';

/* Iconos SVG inline (sin dependencias externas) */
const Icon = {
  Paw: (p) => (
    <svg viewBox="0 0 24 24" fill="currentColor" {...p}><path d="M12 14c-2.4 0-5 1.4-5 4v1c0 .6.4 1 1 1h8c.6 0 1-.4 1-1v-1c0-2.6-2.6-4-5-4Zm-4.5-2.2a1.8 1.8 0 1 0 0-3.6 1.8 1.8 0 0 0 0 3.6Zm9 0a1.8 1.8 0 1 0 0-3.6 1.8 1.8 0 0 0 0 3.6ZM12 5a1.9 1.9 0 1 0 0 3.8A1.9 1.9 0 0 0 12 5Zm5 4.8a1.6 1.6 0 1 0 0-3.2 1.6 1.6 0 0 0 0 3.2ZM7 9.8a1.6 1.6 0 1 0 0-3.2 1.6 1.6 0 0 0 0 3.2Z"/></svg>
  ),
  Streak: (p) => (
    <svg viewBox="0 0 24 24" fill="currentColor" {...p}><path d="M13.5 1c.5 3.8-1.7 5.6-3.2 7.4C8.6 10.4 8 12 8 13.8 8 17.8 11.2 21 15 21c.5 0 1 0 1.5-.1-1-1.2-1.5-2.6-1.5-4.1 0-2.8 1.9-4.6 3.4-6.2.4 1 .6 2 .6 3.2 0 .4 0 .8-.1 1.2 1-.5 2.1-1.4 2.6-3 .8 1 1 2.4 1 3.6C22.5 19.5 18 23 12 23 6 23 2 19 2 14c0-4 2.2-6.8 4.6-9.2C9.2 2.4 11.7 1.8 13.5 1Z"/></svg>
  ),
  Bell: (p) => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M18 8a6 6 0 1 0-12 0c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.7 21a2 2 0 0 1-3.4 0"/></svg>
  ),
  Syringe: (p) => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="m18 2 4 4"/><path d="m17 7 3-3"/><path d="M19 9 8.7 19.3a2.4 2.4 0 0 1-3.4 0l-.6-.6a2.4 2.4 0 0 1 0-3.4L15 5"/><path d="m9 11 4 4"/><path d="m5 19-3 3"/><path d="M14 4 20 10"/></svg>
  ),
  Heart: (p) => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M19 14c1.5-1.5 3-3.3 3-5.5C22 5.4 19.6 3 16.5 3 14.4 3 13 4 12 5c-1-1-2.4-2-4.5-2C4.4 3 2 5.4 2 8.5c0 2.2 1.5 4 3 5.5l7 7Z"/></svg>
  ),
  Lock: (p) => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" {...p}><rect width="18" height="11" x="3" y="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
  ),
  Check: (p) => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M20 6 9 17l-5-5"/></svg>
  ),
  Menu: (p) => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" {...p}><path d="M4 6h16M4 12h16M4 18h16"/></svg>
  ),
  Close: (p) => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" {...p}><path d="M18 6 6 18M6 6l12 12"/></svg>
  ),
};

const benefits = [
  { icon: Icon.Streak, title: 'Rachas y constancia', text: 'Marca el cuidado diario y construye una racha de días consecutivos. Premiemos la constancia de tu mascota.' },
  { icon: Icon.Bell, title: 'Recordatorios inteligentes', text: 'Nunca olvides una cita veterinaria, baño o vacuna. Alertas con estado de urgencia: vencido, hoy o próximo.' },
  { icon: Icon.Heart, title: 'Ficha médica completa', text: 'Lleva el historial clínico, vacunas y bienestar de cada mascota en un solo lugar, siempre a mano.' },
];

const steps = [
  { n: '1', title: 'Crea tu cuenta', text: 'Regístrate gratis en segundos. Solo necesitas un email y una contraseña.' },
  { n: '2', title: 'Agrega a tu mascota', text: 'Crea la ficha con su nombre, especie y raza. La imagen se genera sola.' },
  { n: '3', title: 'Cuida a diario', text: 'Registra rutinas, mantén tu racha viva y recibe recordatorios a tiempo.' },
];

const NAV_ITEMS = [
  { href: '#funciones', label: 'Funciones' },
  { href: '#como-funciona', label: 'Cómo funciona' },
  { href: '#suscripciones', label: 'Suscripciones' },
  { href: '#contacto', label: 'Contacto' },
];

export default function Landing() {
  const [open, setOpen] = useState(false);
  const { user } = useAuth();
  const authed = !!user;

  return (
    <div className="min-h-screen bg-gradient-to-br from-pink-50 via-rose-50 to-purple-50 text-rose-500">
      {/* ===== HEADER ===== */}
      <header className="sticky top-0 z-40 bg-white/70 backdrop-blur-md border-b border-pink-100">
        <div className="max-w-6xl mx-auto px-4 py-3 flex justify-between items-center">
          <a href="#inicio" className="flex items-center gap-2">
            <img src={logo} alt="BubblePat" className="h-9 sm:h-12" />
          </a>

          <nav className="hidden md:flex items-center gap-7">
            {NAV_ITEMS.map((it) => (
              <a key={it.href} href={it.href} className="text-rose-400 hover:text-rose-500 font-medium transition">{it.label}</a>
            ))}
          </nav>

          <div className="hidden md:flex items-center gap-3">
            {authed ? (
              <Link to="/app" className="bg-rose-300 text-white px-5 py-2 rounded-lg hover:bg-rose-400 transition font-semibold shadow-sm">Ir a mi panel</Link>
            ) : (
              <>
                <Link to="/login" className="text-rose-400 hover:text-rose-500 font-medium transition">Entrar</Link>
                <Link to="/register" className="bg-rose-300 text-white px-5 py-2 rounded-lg hover:bg-rose-400 transition font-semibold shadow-sm">Regístrate</Link>
              </>
            )}
          </div>

          {/* Botón menú móvil */}
          <button onClick={() => setOpen(v => !v)} className="md:hidden text-rose-400 p-2" aria-label="Menú">
            {open ? <Icon.Close className="w-6 h-6" /> : <Icon.Menu className="w-6 h-6" />}
          </button>
        </div>

        {/* Menú desplegable móvil */}
        {open && (
          <div className="md:hidden border-t border-pink-100 bg-white/95 backdrop-blur-md px-4 py-4 flex flex-col gap-4">
            {NAV_ITEMS.map((it) => (
              <a key={it.href} href={it.href} onClick={() => setOpen(false)} className="text-rose-400 hover:text-rose-500 font-medium transition">{it.label}</a>
            ))}
            <div className="flex flex-col gap-3 pt-2 border-t border-pink-100">
              {authed ? (
                <Link to="/app" onClick={() => setOpen(false)} className="bg-rose-300 text-white text-center px-5 py-2 rounded-lg hover:bg-rose-400 transition font-semibold">Ir a mi panel</Link>
              ) : (
                <>
                  <Link to="/login" onClick={() => setOpen(false)} className="text-rose-400 font-medium text-center">Entrar</Link>
                  <Link to="/register" onClick={() => setOpen(false)} className="bg-rose-300 text-white text-center px-5 py-2 rounded-lg hover:bg-rose-400 transition font-semibold">Regístrate</Link>
                </>
              )}
            </div>
          </div>
        )}
      </header>

      {/* ===== HERO ===== */}
      <section id="inicio" className="max-w-6xl mx-auto px-4 pt-12 pb-16 sm:pt-20 sm:pb-24">
        <div className="grid md:grid-cols-2 gap-10 items-center">
          <div className="text-center md:text-left">
            <span className="inline-flex items-center gap-1.5 bg-rose-100 text-rose-500 text-sm font-medium px-3 py-1 rounded-full mb-5">
              <Icon.Paw className="w-4 h-4" /> El cuidado de tu mascota, simplificado
            </span>
            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-extrabold text-rose-500 leading-tight">
              Cuida a tu mascota <span className="text-rose-400">cada día</span>, sin olvidar nada.
            </h1>
            <p className="mt-5 text-lg text-rose-300 max-w-lg mx-auto md:mx-0">
              BubblePat organiza las rutinas, fichas médicas, vacunas y recordatorios de tu mascota en un solo lugar, premiendo tu constancia con rachas.
            </p>
            <div className="mt-8 flex flex-col sm:flex-row gap-3 justify-center md:justify-start">
              {authed ? (
                <Link to="/app" className="bg-rose-300 text-white px-7 py-3 rounded-lg hover:bg-rose-400 transition font-semibold shadow-md text-lg">Ir a mi panel</Link>
              ) : (
                <>
                  <Link to="/register" className="bg-rose-300 text-white px-7 py-3 rounded-lg hover:bg-rose-400 transition font-semibold shadow-md text-lg">Empieza gratis</Link>
                  <Link to="/login" className="bg-white text-rose-400 border border-rose-200 px-7 py-3 rounded-lg hover:bg-rose-50 transition font-semibold text-lg">Entrar</Link>
                </>
              )}
            </div>
            <div className="mt-6 flex items-center gap-2 justify-center md:justify-start text-rose-300 text-sm">
              <Icon.Lock className="w-4 h-4" />
              <span>Datos seguros y privados. Registro gratuito, sin tarjeta.</span>
            </div>
          </div>

          <div className="relative">
            <div className="absolute inset-0 bg-rose-200/40 rounded-[2.5rem] blur-2xl"></div>
            <img src={heroImg} alt="Mascota feliz cuidada con BubblePat" className="relative w-full rounded-[2rem] shadow-xl object-cover aspect-square" />
          </div>
        </div>
      </section>

      {/* ===== BENEFICIOS ===== */}
      <section id="funciones" className="bg-white/60 backdrop-blur-sm border-y border-pink-100">
        <div className="max-w-6xl mx-auto px-4 py-16 sm:py-20">
          <div className="text-center mb-12">
            <h2 className="text-3xl sm:text-4xl font-bold text-rose-500">Todo lo que tu mascota necesita</h2>
            <p className="mt-3 text-rose-300 max-w-xl mx-auto">Herramientas pensadas para que el cuidado diario sea fácil y motivador.</p>
          </div>
          <div className="grid md:grid-cols-3 gap-6">
            {benefits.map((b) => (
              <div key={b.title} className="bg-white/80 rounded-2xl border border-pink-100 p-7 text-center shadow-sm hover:shadow-md hover:border-rose-200 transition">
                <div className="w-14 h-14 mx-auto rounded-2xl bg-rose-100 flex items-center justify-center text-rose-400 mb-5">
                  <b.icon className="w-7 h-7" />
                </div>
                <h3 className="text-xl font-bold text-rose-500 mb-2">{b.title}</h3>
                <p className="text-rose-300 leading-relaxed">{b.text}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ===== CÓMO FUNCIONA ===== */}
      <section id="como-funciona" className="max-w-6xl mx-auto px-4 py-16 sm:py-20">
        <div className="text-center mb-12">
          <h2 className="text-3xl sm:text-4xl font-bold text-rose-500">Empieza en 3 pasos</h2>
          <p className="mt-3 text-rose-300">Crear tu cuenta y comenzar a cuidar es muy rápido.</p>
        </div>
        <div className="grid md:grid-cols-3 gap-8">
          {steps.map((s) => (
            <div key={s.n} className="relative text-center">
              <div className="w-16 h-16 mx-auto rounded-full bg-rose-300 text-white text-2xl font-bold flex items-center justify-center shadow-md mb-5">{s.n}</div>
              <h3 className="text-xl font-bold text-rose-500 mb-2">{s.title}</h3>
              <p className="text-rose-300 leading-relaxed max-w-xs mx-auto">{s.text}</p>
            </div>
          ))}
        </div>

        {/* Seguridad visual */}
        <div className="mt-14 bg-white/80 rounded-2xl border border-pink-100 p-6 sm:p-8 flex flex-col sm:flex-row items-center gap-5 shadow-sm">
          <div className="w-14 h-14 shrink-0 rounded-2xl bg-emerald-100 flex items-center justify-center text-emerald-500">
            <Icon.Lock className="w-7 h-7" />
          </div>
          <div className="flex-1 text-center sm:text-left">
            <h3 className="text-lg font-bold text-rose-500">Tus datos están seguros y son privados</h3>
            <p className="text-rose-300 text-sm mt-1">Tu información y la de tu mascota están protegidas y nunca se comparten con terceros.</p>
          </div>
          {!authed && (
            <Link to="/register" className="bg-rose-300 text-white px-6 py-2.5 rounded-lg hover:bg-rose-400 transition font-semibold shadow-sm whitespace-nowrap">Crear cuenta</Link>
          )}
        </div>
      </section>

      {/* ===== SUSCRIPCIONES ===== */}
      <section id="suscripciones" className="bg-white/60 backdrop-blur-sm border-y border-pink-100">
        <div className="max-w-6xl mx-auto px-4 py-16 sm:py-20">
          {/* Card principal */}
          <div className="bg-gradient-to-r from-rose-200/80 via-pink-200/80 to-purple-200/80 rounded-3xl p-8 sm:p-10 mb-10 text-center shadow-sm border border-rose-200">
            <Icon.Heart className="w-10 h-10 mx-auto text-rose-400 mb-3" />
            <h2 className="text-3xl sm:text-4xl font-bold text-rose-500">Suscripciones</h2>
            <p className="mt-3 text-rose-400 max-w-xl mx-auto">Empieza gratis y mejora cuando lo necesites. Precios en pesos chilenos (CLP).</p>
          </div>

          {/* Cards de planes */}
          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-5">
            {/* Plan Gratuito */}
            <div className="bg-white/80 rounded-2xl border border-pink-100 p-6 shadow-sm flex flex-col">
              <h3 className="text-lg font-bold text-rose-500">{PLAN_INFO.FREE.label}</h3>
              <p className="text-3xl font-extrabold text-rose-500 mt-1">{PLAN_INFO.FREE.price}</p>
              <ul className="mt-4 space-y-2 flex-1">
                {PLAN_FEATURES.free.map((f) => (
                  <li key={f} className="flex items-start gap-2 text-sm text-rose-400">
                    <Icon.Check className="w-4 h-4 mt-0.5 shrink-0 text-emerald-400" /> {f}
                  </li>
                ))}
              </ul>
              {authed ? (
                <Link to="/app" className="mt-5 block text-center bg-gray-100 text-rose-400 py-2.5 rounded-lg font-medium">Tu plan actual</Link>
              ) : (
                <Link to="/register" className="mt-5 block text-center bg-gray-100 text-rose-400 py-2.5 rounded-lg font-medium hover:bg-gray-200 transition">Empezar gratis</Link>
              )}
            </div>

            {/* Premium Mensual */}
            <div className="bg-gradient-to-br from-rose-50 to-pink-50 rounded-2xl border-2 border-rose-300 p-6 shadow-md flex flex-col relative">
              <span className="absolute -top-3 left-1/2 -translate-x-1/2 bg-rose-400 text-white text-xs font-bold px-3 py-1 rounded-full">Más popular</span>
              <h3 className="text-lg font-bold text-rose-500">{PLAN_INFO.PREMIUM_MONTHLY.label}</h3>
              <p className="text-3xl font-extrabold text-rose-500 mt-1">{PLAN_INFO.PREMIUM_MONTHLY.price}<span className="text-sm font-normal text-rose-300">{PLAN_INFO.PREMIUM_MONTHLY.period}</span></p>
              <ul className="mt-4 space-y-2 flex-1">
                {PLAN_FEATURES.premium.map((f) => (
                  <li key={f} className="flex items-start gap-2 text-sm text-rose-400">
                    <Icon.Check className="w-4 h-4 mt-0.5 shrink-0 text-emerald-400" /> {f}
                  </li>
                ))}
              </ul>
              <Link to={authed ? "/subscription" : "/register"} className="mt-5 block text-center bg-rose-400 text-white py-2.5 rounded-lg font-medium hover:bg-rose-500 transition">Suscribirme</Link>
            </div>

            {/* Premium Anual */}
            <div className="bg-gradient-to-br from-amber-50 to-rose-50 rounded-2xl border-2 border-amber-300 p-6 shadow-md flex flex-col relative">
              <span className="absolute -top-3 left-1/2 -translate-x-1/2 bg-amber-400 text-white text-xs font-bold px-3 py-1 rounded-full">{PLAN_INFO.PREMIUM_ANNUAL.badge}</span>
              <h3 className="text-lg font-bold text-rose-500">{PLAN_INFO.PREMIUM_ANNUAL.label}</h3>
              <p className="text-3xl font-extrabold text-rose-500 mt-1">{PLAN_INFO.PREMIUM_ANUAL.price}<span className="text-sm font-normal text-rose-300">{PLAN_INFO.PREMIUM_ANUAL.period}</span></p>
              <p className="text-xs text-emerald-500 font-medium mt-1">Ahorras ~$13.000 al año</p>
              <ul className="mt-3 space-y-2 flex-1">
                {PLAN_FEATURES.premium.map((f) => (
                  <li key={f} className="flex items-start gap-2 text-sm text-rose-400">
                    <Icon.Check className="w-4 h-4 mt-0.5 shrink-0 text-emerald-400" /> {f}
                  </li>
                ))}
              </ul>
              <Link to={authed ? "/subscription" : "/register"} className="mt-5 block text-center bg-amber-400 text-white py-2.5 rounded-lg font-medium hover:bg-amber-500 transition">Suscribirme</Link>
            </div>

            {/* Plan Familiar */}
            <div className="bg-gradient-to-br from-purple-50 to-rose-50 rounded-2xl border border-purple-200 p-6 shadow-sm flex flex-col relative">
              <span className="absolute -top-3 left-1/2 -translate-x-1/2 bg-purple-400 text-white text-xs font-bold px-3 py-1 rounded-full">{PLAN_INFO.FAMILY.badge}</span>
              <h3 className="text-lg font-bold text-rose-500">{PLAN_INFO.FAMILY.label}</h3>
              <p className="text-3xl font-extrabold text-rose-500 mt-1">{PLAN_INFO.FAMILY.price}<span className="text-sm font-normal text-rose-300">{PLAN_INFO.FAMILY.period}</span></p>
              <ul className="mt-4 space-y-2 flex-1">
                {PLAN_FEATURES.family.map((f) => (
                  <li key={f} className="flex items-start gap-2 text-sm text-rose-400">
                    <Icon.Check className="w-4 h-4 mt-0.5 shrink-0 text-emerald-400" /> {f}
                  </li>
                ))}
              </ul>
              <div className="mt-5 text-center bg-purple-100 text-purple-400 py-2.5 rounded-lg font-medium">Más info pronto</div>
            </div>
          </div>
        </div>
      </section>

      {/* ===== CTA FINAL ===== */}
      {!authed && (
        <section className="max-w-6xl mx-auto px-4 pb-16 sm:pb-20">
          <div className="bg-gradient-to-r from-rose-300 to-rose-400 rounded-3xl p-10 sm:p-14 text-center shadow-lg">
            <h2 className="text-3xl sm:text-4xl font-bold text-white">¿Listo para mimar a tu mascota?</h2>
            <p className="mt-3 text-white/90 text-lg max-w-xl mx-auto">Únete a BubblePat y mantén el cuidado de tu mascota siempre al día.</p>
            <Link to="/register" className="inline-block mt-7 bg-white text-rose-400 px-8 py-3 rounded-lg hover:bg-rose-50 transition font-bold text-lg shadow-md">Registrarme gratis</Link>
          </div>
        </section>
      )}

      {/* ===== FOOTER ===== */}
      <footer id="contacto" className="bg-white/70 border-t border-pink-100">
        <div className="max-w-6xl mx-auto px-4 py-12 grid gap-8 sm:grid-cols-3">
          <div>
            <div className="flex items-center gap-2 mb-3">
              <img src={icon} alt="" className="w-9 h-9" />
              <span className="font-bold text-rose-500 text-lg">BubblePat</span>
            </div>
            <p className="text-rose-300 text-sm max-w-xs">La app que hace del cuidado diario de tu mascota un hábito feliz.</p>
          </div>
          <div>
            <h4 className="font-semibold text-rose-500 mb-3">Enlaces</h4>
            <ul className="space-y-2 text-sm">
              <li><a href="#funciones" className="text-rose-300 hover:text-rose-400 transition">Funciones</a></li>
              <li><a href="#como-funciona" className="text-rose-300 hover:text-rose-400 transition">Cómo funciona</a></li>
              <li><Link to="/login" className="text-rose-300 hover:text-rose-400 transition">Entrar</Link></li>
              <li><Link to="/register" className="text-rose-300 hover:text-rose-400 transition">Crear cuenta</Link></li>
            </ul>
          </div>
          <div>
            <h4 className="font-semibold text-rose-500 mb-3">Contacto</h4>
            <ul className="space-y-2 text-sm text-rose-300">
              <li>mau.ramirezn@duocuc.cl</li>
              <li>all.sepulveda@duocuc.cl</li>
              <li className="flex items-center gap-1.5"><Icon.Lock className="w-4 h-4" /> Datos seguros y privados</li>
            </ul>
          </div>
        </div>
        <div className="border-t border-pink-100">
          <div className="max-w-6xl mx-auto px-4 py-5 flex flex-col sm:flex-row justify-between items-center gap-3 text-xs text-rose-300">
            <p>© {new Date().getFullYear()} BubblePat. Proyecto académico, uso educativo.</p>
            <div className="flex gap-5">
              <a href="#" className="hover:text-rose-400 transition">Información legal</a>
              <a href="#" className="hover:text-rose-400 transition">Términos de servicio</a>
              <a href="#" className="hover:text-rose-400 transition">Privacidad</a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}
