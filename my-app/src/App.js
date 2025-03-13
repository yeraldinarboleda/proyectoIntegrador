import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import AIComponent from './components/AIComponent';
import DashboardComponent from './components/DashboardComponent'; // Importa el nuevo componente
import './App.css';

function App() {
  return (
    <Router basename="/ia">
      <div className="App">
        <header className="App-header">
          <h1>AI Content Generator</h1>
          <Routes>
            {/* Ruta para el componente AIComponent en la ruta raíz de /ia */}
            <Route path="/" element={<AIComponent />} />
            
            {/* Nueva ruta para el componente Dashboard */}
            <Route path="/dashboard" element={<DashboardComponent />} />
          </Routes>
          {/* <img src={logo} className="App-logo" alt="logo" /> */}
          <p>
            Edit <code>src/App.js</code> and save to reload.
          </p>
          <a
            className="App-link"
            href="https://reactjs.org"
            target="_blank"
            rel="noopener noreferrer"
          >
            Learn React
          </a>
        </header>
      </div>
    </Router>
  );
}

export default App;
