import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import ProfissionalSaudeList from './components/ProfissionalSaudeList';
import ProfissionalSaudeForm from './components/ProfissionalSaudeForm';
import AtendimentoList from './components/AtendimentoList';
import AtendimentoForm from './components/AtendimentoForm';
import ExameList from './components/ExameList';
import ExameForm from './components/ExameForm';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        <nav className="navbar">
          <h1>🩺 Sistema de Saúde</h1>
          <div className="nav-links">
            <Link to="/profissionais">Profissionais de Saúde</Link>
            <Link to="/atendimentos">Atendimentos</Link>
            <Link to="/exames">Exames</Link>
          </div>
        </nav>

        <main className="container">
          <Routes>
            <Route path="/" element={<ProfissionalSaudeList />} />
            <Route path="/profissionais" element={<ProfissionalSaudeList />} />
            <Route path="/profissionais/novo" element={<ProfissionalSaudeForm />} />
            <Route path="/profissionais/editar/:id" element={<ProfissionalSaudeForm />} />
            <Route path="/atendimentos" element={<AtendimentoList />} />
            <Route path="/atendimentos/novo" element={<AtendimentoForm />} />
            <Route path="/atendimentos/editar/:id" element={<AtendimentoForm />} />
            <Route path="/exames" element={<ExameList />} />
            <Route path="/exames/novo" element={<ExameForm />} />
            <Route path="/exames/editar/:id" element={<ExameForm />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;