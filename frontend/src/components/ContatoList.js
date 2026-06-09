import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { contatoService } from '../services/api';

function ContatoList() {
  const [contatos, setContatos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarContatos();
  }, []);

  const carregarContatos = async () => {
    try {
      const response = await contatoService.listar();
      setContatos(response.data);
    } catch (error) {
      console.error('Erro ao carregar contatos:', error);
    } finally {
      setLoading(false);
    }
  };

  const deletarContato = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este contato?')) {
      try {
        await contatoService.deletar(id);
        carregarContatos();
      } catch (error) {
        console.error('Erro ao deletar contato:', error);
      }
    }
  };

  if (loading) return <p>Carregando...</p>;

  return (
    <div>
      <div className="header">
        <h2>📋 Contatos</h2>
        <Link to="/contatos/novo" className="btn btn-primary">+ Novo Contato</Link>
      </div>

      <table className="table">
        <thead>
          <tr>
            <th>Nome</th>
            <th>Telefone</th>
            <th>Email</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {contatos.map(contato => (
            <tr key={contato.id}>
              <td>{contato.nome}</td>
              <td>{contato.telefone}</td>
              <td>{contato.email}</td>
              <td>
                <Link to={`/contatos/editar/${contato.id}`} className="btn btn-sm">Editar</Link>
                <button onClick={() => deletarContato(contato.id)} className="btn btn-danger btn-sm">
                  Excluir
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {contatos.length === 0 && <p className="empty">Nenhum contato cadastrado.</p>}
    </div>
  );
}

export default ContatoList;
