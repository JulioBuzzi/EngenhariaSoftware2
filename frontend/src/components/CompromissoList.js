import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { compromissoService } from '../services/api';

function CompromissoList() {
  const [compromissos, setCompromissos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarCompromissos();
  }, []);

  const carregarCompromissos = async () => {
    try {
      const response = await compromissoService.listar();
      setCompromissos(response.data);
    } catch (error) {
      console.error('Erro ao carregar compromissos:', error);
    } finally {
      setLoading(false);
    }
  };

  const deletarCompromisso = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este compromisso?')) {
      try {
        await compromissoService.deletar(id);
        carregarCompromissos();
      } catch (error) {
        console.error('Erro ao deletar compromisso:', error);
      }
    }
  };

  if (loading) return <p>Carregando...</p>;

  return (
    <div>
      <div className="header">
        <h2>📅 Compromissos</h2>
        <Link to="/compromissos/novo" className="btn btn-primary">+ Novo Compromisso</Link>
      </div>

      <table className="table">
        <thead>
          <tr>
            <th>Título</th>
            <th>Data</th>
            <th>Hora</th>
            <th>Contato</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {compromissos.map(comp => (
            <tr key={comp.id}>
              <td>{comp.titulo}</td>
              <td>{comp.data}</td>
              <td>{comp.hora}</td>
              <td>{comp.contato?.nome || '-'}</td>
              <td>
                <Link to={`/compromissos/editar/${comp.id}`} className="btn btn-sm">Editar</Link>
                <button onClick={() => deletarCompromisso(comp.id)} className="btn btn-danger btn-sm">
                  Excluir
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {compromissos.length === 0 && <p className="empty">Nenhum compromisso cadastrado.</p>}
    </div>
  );
}

export default CompromissoList;
