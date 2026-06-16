import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { profissionalSaudeService } from '../services/api';

function ProfissionalSaudeList() {
  const [profissionais, setProfissionais] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarProfissionais();
  }, []);

  const carregarProfissionais = async () => {
    try {
      const response = await profissionalSaudeService.listar();
      setProfissionais(response.data);
    } catch (error) {
      console.error('Erro ao carregar profissionais:', error);
    } finally {
      setLoading(false);
    }
  };

  const deletarProfissional = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este profissional?')) {
      try {
        await profissionalSaudeService.deletar(id);
        carregarProfissionais();
      } catch (error) {
        console.error('Erro ao deletar profissional:', error);
      }
    }
  };

  if (loading) return <p>Carregando...</p>;

  return (
    <div>
      <div className="header">
        <h2>🩺 Profissionais de Saúde</h2>
        <Link to="/profissionais/novo" className="btn btn-primary">+ Novo Profissional</Link>
      </div>

      <table className="table">
        <thead>
          <tr>
            <th>Nome</th>
            <th>Telefone</th>
            <th>Endereço</th>
            <th>Categoria</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {profissionais.map(profissional => (
            <tr key={profissional.id}>
              <td>{profissional.nome}</td>
              <td>{profissional.telefone}</td>
              <td>{profissional.endereco}</td>
              <td>{profissional.categoria}</td>
              <td>
                <Link to={`/profissionais/editar/${profissional.id}`} className="btn btn-sm">Editar</Link>
                <button onClick={() => deletarProfissional(profissional.id)} className="btn btn-danger btn-sm">
                  Excluir
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {profissionais.length === 0 && <p className="empty">Nenhum profissional cadastrado.</p>}
    </div>
  );
}

export default ProfissionalSaudeList;