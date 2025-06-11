import React from 'react';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import AIComponent from './components/AIComponent';
import DashboardComponent from './containers/DashboardComponent';
import LoginComponent from './components/LoginComponent';

import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

import { AuthProvider, useAuth } from './components/AuthContext';

const Navigation = () => {
  const { authenticated } = useAuth();

  return (
    <nav style={{ display: 'flex', justifyContent: 'space-around', width: '50%' }}>
      {!authenticated && <Link to="/">Login</Link>}
      {authenticated && (
        <>
          <Link to="/ia">IA</Link>
          <Link to="/dashboard">Panel de Salud</Link>
        </>
      )}
    </nav>
  );
};

function App() {
  return (
    <AuthProvider>
      <Router basename="/">
        <div className="App">
          <header className="App-header">
            <Link to="/" style={{ display: 'inline-block' }}>
              <img
                src="/logo.jpeg"
                alt="CardioHealth AI Logo"
                style={{
                  width: 200,
                  height: 200,
                  objectFit: 'contain',
                  marginBottom: 10,
                  cursor: 'pointer',
                }}
              />
            </Link>

            <Navigation />

            <Routes>
              <Route path="/" element={<LoginComponent />} />
              <Route path="/ia" element={<AIComponent />} />
              <Route path="/dashboard" element={<DashboardComponent />} />
            </Routes>
          </header>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
