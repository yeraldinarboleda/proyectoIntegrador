import React from 'react';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import AIComponent from './components/AIComponent';
import DashboardComponent from './containers/DashboardComponent'; // Importa el nuevo componente


import 'bootstrap/dist/css/bootstrap.min.css';


import './App.css';
import LoginComponent from './components/LoginComponent';

function App() {
  return (
    <Router basename="/">
      <div className="App">
        <header className="App-header">
          <Link to="/" style={{ display: 'inline-block' }}>
            <img
              src="/logo.jpeg"
              alt="CardioHealth AI Logo"
              style={{ width: 200, height: 200, objectFit: 'contain', marginBottom: 10, cursor: 'pointer' }}
            />
          </Link>
          <nav style={{ display: 'flex', justifyContent: 'space-around', width: '50%' }}>
            <Link to="/">Login</Link>
            <Link to="/ia">IA</Link>
            <Link to="/dashboard">Dashboard</Link>
          </nav>
          <Routes>
            <Route path="/" element={<LoginComponent />} />
            {/* Ruta para el componente AIComponent en la ruta raíz de /ia */}
            <Route path="/ia" element={<AIComponent />} />
            
            {/* Nueva ruta para el componente Dashboard */}
            <Route path="/dashboard" element={<DashboardComponent />} />
          </Routes>
          {/* <img src={logo} className="App-logo" alt="logo" /> */}
      
        </header>
      </div>
    </Router>
  );
}

export default App;
