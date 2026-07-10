import api from './client';

export const PLAN_INFO = {
  FREE:            { label: 'Gratis',         price: '$0',          period: '',          badge: '' },
  PREMIUM_MONTHLY: { label: 'Premium Mensual', price: '$4.000',     period: '/mes',      badge: '' },
  PREMIUM_ANNUAL:  { label: 'Premium Anual',  price: '$35.000',     period: '/año',      badge: '27% OFF' },
  FAMILY:          { label: 'Plan Familiar',   price: 'Desde $6.000', period: '/mes',    badge: 'Próximamente' },
};

export const PLAN_FEATURES = {
  free: [
    'Hasta 2 mascotas',
    'Ficha médica básica',
    'Recordatorios',
    'Consulta de razas',
  ],
  premium: [
    'Mascotas ilimitadas',
    'Historial médico completo',
    'Recordatorios avanzados',
    'Respaldo automático en la nube',
    'Estadísticas de salud',
    'Exportación de fichas en PDF',
    'Notificaciones inteligentes',
    'Próxima integración con veterinarias',
  ],
  family: [
    'Todo lo de Premium',
    'Conecta con otros familiares',
    'Seguimiento compartido de rachas',
    'Alertas si alguien olvidó algo',
  ],
};

export const isPremium = (plan) => ['PREMIUM_MONTHLY', 'PREMIUM_ANNUAL', 'FAMILY'].includes(plan);

export async function updatePlan(plan) {
  const { data } = await api.put('/subscription', { plan });
  return data;
}

export async function getPlan() {
  const { data } = await api.get('/subscription');
  return data.plan;
}
