import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' }
});

// ========== CONTATOS ==========
export const contatoService = {
  listar: () => api.get('/contatos'),
  buscar: (id) => api.get(`/contatos/${id}`),
  criar: (contato) => api.post('/contatos', contato),
  atualizar: (id, contato) => api.put(`/contatos/${id}`, contato),
  deletar: (id) => api.delete(`/contatos/${id}`)
};

// ========== COMPROMISSOS ==========
export const compromissoService = {
  listar: () => api.get('/compromissos'),
  buscar: (id) => api.get(`/compromissos/${id}`),
  criar: (compromisso) => api.post('/compromissos', compromisso),
  atualizar: (id, compromisso) => api.put(`/compromissos/${id}`, compromisso),
  deletar: (id) => api.delete(`/compromissos/${id}`)
};

// ========== PROFISSIONAIS DE SAÚDE ==========
export const profissionalSaudeService = {
  listar: () => api.get('/profissionais-saude'),
  buscar: (id) => api.get(`/profissionais-saude/${id}`),
  criar: (profissional) => api.post('/profissionais-saude', profissional),
  atualizar: (id, profissional) => api.put(`/profissionais-saude/${id}`, profissional),
  deletar: (id) => api.delete(`/profissionais-saude/${id}`)
};

export default api;
