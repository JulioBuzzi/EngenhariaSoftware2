import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { contatoService } from '../services/api';

function ContatoForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const [contato, setContato] = useState({
    nome: '', telefone: '', email: '', endereco: ''
  });

  useEffect(() => {
    if (id) {
      contatoService.buscar(id).then(res => setContato(res.data));
    }
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (id) {
        await contatoService.atualizar(id, contato);
      } else {
        await contatoService.criar(contato);
      }
      navigate('/contatos');
    } catch (error) {
      console.error('Erro ao salvar contato:', error);
    }
  };

  return (
    <div>
      <h2>{id ? 'Editar Contato' : 'Novo Contato'}</h2>
      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label>Nome *</label>
          <input type="text" value={contato.nome} required
            onChange={e => setContato({...contato, nome: e.target.value})} />
        </div>
        <div className="form-group">
          <label>Telefone</label>
          <input type="text" value={contato.telefone}
            onChange={e => setContato({...contato, telefone: e.target.value})} />
        </div>
        <div className="form-group">
          <label>Email</label>
          <input type="email" value={contato.email}
            onChange={e => setContato({...contato, email: e.target.value})} />
        </div>
        <div className="form-group">
          <label>Endereço</label>
          <input type="text" value={contato.endereco}
            onChange={e => setContato({...contato, endereco: e.target.value})} />
        </div>
        <button type="submit" className="btn btn-primary">Salvar</button>
        <button type="button" className="btn" onClick={() => navigate('/contatos')}>Cancelar</button>
      </form>
    </div>
  );
}

export default ContatoForm;
