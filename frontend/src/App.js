import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import ContatoList from './components/ContatoList';
import ContatoForm from './components/ContatoForm';
import CompromissoList from './components/CompromissoList';
import CompromissoForm from './components/CompromissoForm';
import ProfissionalSaudeList from './components/ProfissionalSaudeList';
import ProfissionalSaudeForm from './components/ProfissionalSaudeForm';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        <nav className="navbar">
          <h1>📅 Agenda Web</h1>
          <div className="nav-links">
            <Link to="/contatos">Contatos</Link>
            <Link to="/compromissos">Compromissos</Link>
            <Link to="/profissionais">Profissionais de Saúde</Link>
          </div>
        </nav>

        <main className="container">
          <Routes>
            <Route path="/" element={<ContatoList />} />
            <Route path="/contatos" element={<ContatoList />} />
            <Route path="/contatos/novo" element={<ContatoForm />} />
            <Route path="/contatos/editar/:id" element={<ContatoForm />} />
            <Route path="/compromissos" element={<CompromissoList />} />
            <Route path="/compromissos/novo" element={<CompromissoForm />} />
            <Route path="/compromissos/editar/:id" element={<CompromissoForm />} />
            <Route path="/profissionais" element={<ProfissionalSaudeList />} />
            <Route path="/profissionais/novo" element={<ProfissionalSaudeForm />} />
            <Route path="/profissionais/editar/:id" element={<ProfissionalSaudeForm />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
