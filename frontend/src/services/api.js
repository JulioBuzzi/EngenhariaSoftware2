import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' }
});

// ========== CONTATOS (DEV 1 - Ana) ==========
export const contatoService = {
  listar: () => api.get('/contatos'),
  buscar: (id) => api.get(`/contatos/${id}`),
  criar: (contato) => api.post('/contatos', contato),
  atualizar: (id, contato) => api.put(`/contatos/${id}`, contato),
  deletar: (id) => api.delete(`/contatos/${id}`)
};

// ========== COMPROMISSOS (DEV 2 - Bruno) ==========
export const compromissoService = {
  listar: () => api.get('/compromissos'),
  buscar: (id) => api.get(`/compromissos/${id}`),
  criar: (compromisso) => api.post('/compromissos', compromisso),
  atualizar: (id, compromisso) => api.put(`/compromissos/${id}`, compromisso),
  deletar: (id) => api.delete(`/compromissos/${id}`)
};

export default api;
