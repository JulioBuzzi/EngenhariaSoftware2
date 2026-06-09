import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { compromissoService, contatoService } from '../services/api';

function CompromissoForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const [compromisso, setCompromisso] = useState({
    titulo: '', data: '', hora: '', descricao: '', contato: null
  });
  const [contatos, setContatos] = useState([]);

  useEffect(() => {
    contatoService.listar().then(res => setContatos(res.data));
    if (id) {
      compromissoService.buscar(id).then(res => setCompromisso(res.data));
    }
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (id) {
        await compromissoService.atualizar(id, compromisso);
      } else {
        await compromissoService.criar(compromisso);
      }
      navigate('/compromissos');
    } catch (error) {
      console.error('Erro ao salvar compromisso:', error);
    }
  };

  return (
    <div>
      <h2>{id ? 'Editar Compromisso' : 'Novo Compromisso'}</h2>
      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label>Título *</label>
          <input type="text" value={compromisso.titulo} required
            onChange={e => setCompromisso({...compromisso, titulo: e.target.value})} />
        </div>
        <div className="form-group">
          <label>Data *</label>
          <input type="date" value={compromisso.data} required
            onChange={e => setCompromisso({...compromisso, data: e.target.value})} />
        </div>
        <div className="form-group">
          <label>Hora</label>
          <input type="time" value={compromisso.hora}
            onChange={e => setCompromisso({...compromisso, hora: e.target.value})} />
        </div>
        <div className="form-group">
          <label>Descrição</label>
          <textarea value={compromisso.descricao}
            onChange={e => setCompromisso({...compromisso, descricao: e.target.value})} />
        </div>
        <div className="form-group">
          <label>Contato vinculado</label>
          <select value={compromisso.contato?.id || ''}
            onChange={e => setCompromisso({...compromisso,
              contato: e.target.value ? {id: parseInt(e.target.value)} : null})}>
            <option value="">Selecione um contato</option>
            {contatos.map(c => (
              <option key={c.id} value={c.id}>{c.nome}</option>
            ))}
          </select>
        </div>
        <button type="submit" className="btn btn-primary">Salvar</button>
        <button type="button" className="btn" onClick={() => navigate('/compromissos')}>Cancelar</button>
      </form>
    </div>
  );
}

export default CompromissoForm;
