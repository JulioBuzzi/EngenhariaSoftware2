import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' }
});

// ========== PROFISSIONAIS DE SAÚDE ==========
export const profissionalSaudeService = {
  listar: () => api.get('/profissionais-saude'),
  buscar: (id) => api.get(`/profissionais-saude/${id}`),
  criar: (profissional) => api.post('/profissionais-saude', profissional),
  atualizar: (id, profissional) => api.put(`/profissionais-saude/${id}`, profissional),
  deletar: (id) => api.delete(`/profissionais-saude/${id}`)
};

// ========== ATENDIMENTOS ==========
export const atendimentoService = {
  listar: () => api.get('/atendimentos'),
  buscar: (id) => api.get(`/atendimentos/${id}`),
  listarPorProfissional: (profissionalId) => api.get(`/atendimentos/profissional/${profissionalId}`),
  criar: (atendimento) => api.post('/atendimentos', atendimento),
  atualizar: (id, atendimento) => api.put(`/atendimentos/${id}`, atendimento),
  deletar: (id) => api.delete(`/atendimentos/${id}`)
};

// ========== EXAMES DE LABORATÓRIO ==========
export const exameService = {
  listar: () => api.get('/exames-laboratorio'),
  buscar: (id) => api.get(`/exames-laboratorio/${id}`),
  criar: (exame) => api.post('/exames-laboratorio', exame),
  atualizar: (id, exame) => api.put(`/exames-laboratorio/${id}`, exame),
  deletar: (id) => api.delete(`/exames-laboratorio/${id}`)
};

export default api;