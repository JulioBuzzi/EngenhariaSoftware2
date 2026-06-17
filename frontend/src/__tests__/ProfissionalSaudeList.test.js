import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import ProfissionalSaudeList from '../components/ProfissionalSaudeList';
import { profissionalSaudeService } from '../services/api';

jest.mock('../services/api');

const profissionaisMock = [
  { id: 1, nome: 'Dr. Carlos', telefone: '(31) 99999-0001', endereco: 'Rua A, 1', categoria: 'MEDICO' },
  { id: 2, nome: 'Psi. Ana',   telefone: '(31) 99999-0002', endereco: 'Rua B, 2', categoria: 'PSICOLOGO' },
];

const renderComponent = () =>
  render(<MemoryRouter><ProfissionalSaudeList /></MemoryRouter>);

describe('ProfissionalSaudeList', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('exibe mensagem de carregando inicialmente', () => {
    profissionalSaudeService.listar.mockResolvedValue({ data: [] });
    renderComponent();
    expect(screen.getByText('Carregando...')).toBeInTheDocument();
  });

  test('renderiza lista de profissionais corretamente', async () => {
    profissionalSaudeService.listar.mockResolvedValue({ data: profissionaisMock });
    renderComponent();
    await waitFor(() => {
      expect(screen.getByText('Dr. Carlos')).toBeInTheDocument();
      expect(screen.getByText('Psi. Ana')).toBeInTheDocument();
      expect(screen.getByText('MEDICO')).toBeInTheDocument();
      expect(screen.getByText('PSICOLOGO')).toBeInTheDocument();
    });
  });

  test('exibe mensagem quando lista está vazia', async () => {
    profissionalSaudeService.listar.mockResolvedValue({ data: [] });
    renderComponent();
    await waitFor(() => {
      expect(screen.getByText('Nenhum profissional cadastrado.')).toBeInTheDocument();
    });
  });

  test('exibe botão de novo profissional', async () => {
    profissionalSaudeService.listar.mockResolvedValue({ data: [] });
    renderComponent();
    await waitFor(() => {
      expect(screen.getByText('+ Novo Profissional')).toBeInTheDocument();
    });
  });

  test('chama deletar ao confirmar exclusão', async () => {
    profissionalSaudeService.listar.mockResolvedValue({ data: profissionaisMock });
    profissionalSaudeService.deletar.mockResolvedValue({});
    window.confirm = jest.fn(() => true);

    renderComponent();
    await waitFor(() => screen.getByText('Dr. Carlos'));

    const botoesExcluir = screen.getAllByText('Excluir');
    fireEvent.click(botoesExcluir[0]);

    await waitFor(() => {
      expect(profissionalSaudeService.deletar).toHaveBeenCalledWith(1);
    });
  });

  test('não chama deletar ao cancelar exclusão', async () => {
    profissionalSaudeService.listar.mockResolvedValue({ data: profissionaisMock });
    window.confirm = jest.fn(() => false);

    renderComponent();
    await waitFor(() => screen.getByText('Dr. Carlos'));

    const botoesExcluir = screen.getAllByText('Excluir');
    fireEvent.click(botoesExcluir[0]);

    expect(profissionalSaudeService.deletar).not.toHaveBeenCalled();
  });
});