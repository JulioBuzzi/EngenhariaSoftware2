import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { atendimentoService, profissionalSaudeService } from '../services/api';

const RECEITAS = [
  { value: 'REMEDIO', label: 'Remédio' },
  { value: 'ATIVIDADE_FISICA', label: 'Atividade Física' },
  { value: 'ATIVIDADE_MENTAL', label: 'Atividade Mental' },
];

function AtendimentoForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const [profissionais, setProfissionais] = useState([]);
  const [atendimento, setAtendimento] = useState({
    titulo: '',
    data: '',
    horario: '',
    linkVideoconferencia: '',
    receitas: [],
    profissionalSaude: { id: '' }
  });

  useEffect(() => {
    profissionalSaudeService.listar().then(res => setProfissionais(res.data));
    if (id) {
      atendimentoService.buscar(id).then(res => setAtendimento(res.data));
    }
  }, [id]);

  const toggleReceita = (value) => {
    const atual = atendimento.receitas || [];
    const novas = atual.includes(value)
      ? atual.filter(r => r !== value)
      : [...atual, value];
    setAtendimento({ ...atendimento, receitas: novas });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (id) {
        await atendimentoService.atualizar(id, atendimento);
      } else {
        await atendimentoService.criar(atendimento);
      }
      navigate('/atendimentos');
    } catch (error) {
      console.error('Erro ao salvar atendimento:', error);
    }
  };

  return (
    <div>
      <h2>{id ? 'Editar Atendimento' : 'Novo Atendimento'}</h2>
      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label>Título *</label>
          <input type="text" value={atendimento.titulo} required
            onChange={e => setAtendimento({ ...atendimento, titulo: e.target.value })} />
        </div>
        <div className="form-group">
          <label>Data *</label>
          <input type="date" value={atendimento.data} required
            onChange={e => setAtendimento({ ...atendimento, data: e.target.value })} />
        </div>
        <div className="form-group">
          <label>Horário *</label>
          <input type="time" value={atendimento.horario} required
            onChange={e => setAtendimento({ ...atendimento, horario: e.target.value })} />
        </div>
        <div className="form-group">
          <label>Link Videoconferência</label>
          <input type="text" value={atendimento.linkVideoconferencia || ''}
            onChange={e => setAtendimento({ ...atendimento, linkVideoconferencia: e.target.value })} />
        </div>
        <div className="form-group">
          <label>Profissional de Saúde *</label>
          <select required value={atendimento.profissionalSaude?.id || ''}
            onChange={e => setAtendimento({ ...atendimento, profissionalSaude: { id: Number(e.target.value) } })}>
            <option value="">Selecione...</option>
            {profissionais.map(p => (
              <option key={p.id} value={p.id}>{p.nome} - {p.categoria}</option>
            ))}
          </select>
        </div>
        <div className="form-group">
          <label>Receitas</label>
          <div>
            {RECEITAS.map(r => (
              <label key={r.value} style={{ marginRight: '1rem' }}>
                <input type="checkbox"
                  checked={(atendimento.receitas || []).includes(r.value)}
                  onChange={() => toggleReceita(r.value)}
                  style={{ marginRight: '0.3rem' }} />
                {r.label}
              </label>
            ))}
          </div>
        </div>
        <button type="submit" className="btn btn-primary">Salvar</button>
        <button type="button" className="btn" onClick={() => navigate('/atendimentos')}>Cancelar</button>
      </form>
    </div>
  );
}

export default AtendimentoForm;