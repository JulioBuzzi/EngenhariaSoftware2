import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import AtendimentoList from '../components/AtendimentoList';
import { atendimentoService } from '../services/api';

jest.mock('../services/api');

const atendimentosMock = [
  {
    id: 1, titulo: 'Consulta de rotina', data: '2026-06-25', horario: '09:00',
    profissionalSaude: { nome: 'Dr. Carlos' }, receitas: ['REMEDIO']
  },
  {
    id: 2, titulo: 'Sessão de terapia', data: '2026-06-26', horario: '15:00',
    profissionalSaude: { nome: 'Psi. Ana' }, receitas: ['ATIVIDADE_MENTAL']
  },
];

const renderComponent = () =>
  render(<MemoryRouter><AtendimentoList /></MemoryRouter>);

describe('AtendimentoList', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('exibe mensagem de carregando inicialmente', () => {
    atendimentoService.listar.mockResolvedValue({ data: [] });
    renderComponent();
    expect(screen.getByText('Carregando...')).toBeInTheDocument();
  });

  test('renderiza lista de atendimentos corretamente', async () => {
    atendimentoService.listar.mockResolvedValue({ data: atendimentosMock });
    renderComponent();
    await waitFor(() => {
      expect(screen.getByText('Consulta de rotina')).toBeInTheDocument();
      expect(screen.getByText('Sessão de terapia')).toBeInTheDocument();
      expect(screen.getByText('Dr. Carlos')).toBeInTheDocument();
      expect(screen.getByText('Psi. Ana')).toBeInTheDocument();
    });
  });

  test('exibe mensagem quando lista está vazia', async () => {
    atendimentoService.listar.mockResolvedValue({ data: [] });
    renderComponent();
    await waitFor(() => {
      expect(screen.getByText('Nenhum atendimento cadastrado.')).toBeInTheDocument();
    });
  });

  test('exibe botão de novo atendimento', async () => {
    atendimentoService.listar.mockResolvedValue({ data: [] });
    renderComponent();
    await waitFor(() => {
      expect(screen.getByText('+ Novo Atendimento')).toBeInTheDocument();
    });
  });

  test('formata receitas corretamente', async () => {
    atendimentoService.listar.mockResolvedValue({ data: atendimentosMock });
    renderComponent();
    await waitFor(() => {
      expect(screen.getByText('Remédio')).toBeInTheDocument();
      expect(screen.getByText('Ativ. Mental')).toBeInTheDocument();
    });
  });

  test('chama deletar ao confirmar exclusão', async () => {
    atendimentoService.listar.mockResolvedValue({ data: atendimentosMock });
    atendimentoService.deletar.mockResolvedValue({});
    window.confirm = jest.fn(() => true);

    renderComponent();
    await waitFor(() => screen.getByText('Consulta de rotina'));

    const botoesExcluir = screen.getAllByText('Excluir');
    fireEvent.click(botoesExcluir[0]);

    await waitFor(() => {
      expect(atendimentoService.deletar).toHaveBeenCalledWith(1);
    });
  });
});