import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import ExameList from '../components/ExameList';
import { exameService } from '../services/api';

jest.mock('../services/api');

const examesMock = [
  { id: 1, descricao: 'Hemograma completo', posologia: 'Jejum de 8 horas',  atendimento: { titulo: 'Consulta de rotina' } },
  { id: 2, descricao: 'Glicemia em jejum',  posologia: 'Jejum de 12 horas', atendimento: { titulo: 'Sessão de terapia' } },
];

const renderComponent = () =>
  render(<MemoryRouter><ExameList /></MemoryRouter>);

describe('ExameList', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('exibe mensagem de carregando inicialmente', () => {
    exameService.listar.mockResolvedValue({ data: [] });
    renderComponent();
    expect(screen.getByText('Carregando...')).toBeInTheDocument();
  });

  test('renderiza lista de exames corretamente', async () => {
    exameService.listar.mockResolvedValue({ data: examesMock });
    renderComponent();
    await waitFor(() => {
      expect(screen.getByText('Hemograma completo')).toBeInTheDocument();
      expect(screen.getByText('Glicemia em jejum')).toBeInTheDocument();
      expect(screen.getByText('Jejum de 8 horas')).toBeInTheDocument();
      expect(screen.getByText('Consulta de rotina')).toBeInTheDocument();
    });
  });

  test('exibe mensagem quando lista está vazia', async () => {
    exameService.listar.mockResolvedValue({ data: [] });
    renderComponent();
    await waitFor(() => {
      expect(screen.getByText('Nenhum exame cadastrado.')).toBeInTheDocument();
    });
  });

  test('exibe botão de novo exame', async () => {
    exameService.listar.mockResolvedValue({ data: [] });
    renderComponent();
    await waitFor(() => {
      expect(screen.getByText('+ Novo Exame')).toBeInTheDocument();
    });
  });

  test('chama deletar ao confirmar exclusão', async () => {
    exameService.listar.mockResolvedValue({ data: examesMock });
    exameService.deletar.mockResolvedValue({});
    window.confirm = jest.fn(() => true);

    renderComponent();
    await waitFor(() => screen.getByText('Hemograma completo'));

    const botoesExcluir = screen.getAllByText('Excluir');
    fireEvent.click(botoesExcluir[0]);

    await waitFor(() => {
      expect(exameService.deletar).toHaveBeenCalledWith(1);
    });
  });

  test('não chama deletar ao cancelar exclusão', async () => {
    exameService.listar.mockResolvedValue({ data: examesMock });
    window.confirm = jest.fn(() => false);

    renderComponent();
    await waitFor(() => screen.getByText('Hemograma completo'));

    const botoesExcluir = screen.getAllByText('Excluir');
    fireEvent.click(botoesExcluir[0]);

    expect(exameService.deletar).not.toHaveBeenCalled();
  });
});