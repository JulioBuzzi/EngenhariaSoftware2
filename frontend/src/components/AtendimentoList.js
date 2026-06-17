import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { atendimentoService } from '../services/api';

function AtendimentoList() {
  const [atendimentos, setAtendimentos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregar();
  }, []);

  const carregar = async () => {
    try {
      const response = await atendimentoService.listar();
      setAtendimentos(response.data);
    } catch (error) {
      console.error('Erro ao carregar atendimentos:', error);
    } finally {
      setLoading(false);
    }
  };

  const deletar = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este atendimento?')) {
      try {
        await atendimentoService.deletar(id);
        carregar();
      } catch (error) {
        console.error('Erro ao deletar atendimento:', error);
      }
    }
  };

  const formatarReceitas = (receitas) => {
    if (!receitas || receitas.length === 0) return '-';
    const labels = { REMEDIO: 'Remédio', ATIVIDADE_FISICA: 'Ativ. Física', ATIVIDADE_MENTAL: 'Ativ. Mental' };
    return receitas.map(r => labels[r] || r).join(', ');
  };

  if (loading) return <p>Carregando...</p>;

  return (
    <div>
      <div className="header">
        <h2>📋 Atendimentos</h2>
        <Link to="/atendimentos/novo" className="btn btn-primary">+ Novo Atendimento</Link>
      </div>

      <table className="table">
        <thead>
          <tr>
            <th>Título</th>
            <th>Data</th>
            <th>Horário</th>
            <th>Profissional</th>
            <th>Receitas</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {atendimentos.map(a => (
            <tr key={a.id}>
              <td>{a.titulo}</td>
              <td>{a.data}</td>
              <td>{a.horario}</td>
              <td>{a.profissionalSaude?.nome || '-'}</td>
              <td>{formatarReceitas(a.receitas)}</td>
              <td>
                <Link to={`/atendimentos/editar/${a.id}`} className="btn btn-sm">Editar</Link>
                <button onClick={() => deletar(a.id)} className="btn btn-danger btn-sm">Excluir</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {atendimentos.length === 0 && <p className="empty">Nenhum atendimento cadastrado.</p>}
    </div>
  );
}

export default AtendimentoList;