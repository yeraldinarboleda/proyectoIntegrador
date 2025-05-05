import React, { useState } from 'react';
import PersonalDataForm from '../components/PersonalDataForm';
import MedicalDataForm from '../components/MedicalDataForm';
import RiskFactorsForm from '../components/RiskFactorsForm';
import LabResultsForm from '../components/LabResultsForm';
import CardioResultsForm from '../components/CardioResultsForm';
import BuscarPaciente from '../components/BuscarPaciente';
import './DashboardComponent.css';

const DashboardComponent = () => {
  const [activeSection, setActiveSection] = useState('personal');
  const [paciente, setPaciente] = useState(null);
  const [buscado, setBuscado] = useState(false);

  const handleBusqueda = (data) => {
    setPaciente(data);
    setBuscado(true);
  };

  return (
    <div className="main-container">
      <header className="dashboard-header">
        <h2>🩺 Panel de Salud</h2>
      </header>

      {!buscado ? (
        <div className="buscar-container">
          <BuscarPaciente onPacienteEncontrado={handleBusqueda} />
        </div>
      ) : (
        <div className="content-container">
          <div className="paciente-info">
            {paciente ? (
              <p><strong>Paciente encontrado:</strong> {paciente.firstName} {paciente.lastName}</p>
            ) : (
              <p className="paciente-no-encontrado">Paciente no encontrado. Puedes registrarlo a continuación.</p>
            )}
            <button className="buscar-otro-btn" onClick={() => { setPaciente(null); setBuscado(false); }}>
              🔁 Buscar otro paciente
            </button>
          </div>

          <nav className="tab-buttons">
            <button className={activeSection === 'personal' ? 'active' : ''} onClick={() => setActiveSection('personal')}>Datos Personales</button>
            <button className={activeSection === 'medical' ? 'active' : ''} onClick={() => setActiveSection('medical')}>Datos Médicos</button>
            <button className={activeSection === 'risk' ? 'active' : ''} onClick={() => setActiveSection('risk')}>Factores de Riesgo</button>
            <button className={activeSection === 'lab' ? 'active' : ''} onClick={() => setActiveSection('lab')}>Resultados de Laboratorio</button>
            <button className={activeSection === 'cardio' ? 'active' : ''} onClick={() => setActiveSection('cardio')}>Resultados Cardiológicos</button>
          </nav>

          <div className="form-section">
            {activeSection === 'personal' && <PersonalDataForm initialData={paciente} documentId={paciente?.documentId} />}
            {activeSection === 'medical' && <MedicalDataForm documentId={paciente?.documentId} />}
            {activeSection === 'risk' && <RiskFactorsForm documentId={paciente?.documentId} />}
            {activeSection === 'lab' && <LabResultsForm documentId={paciente?.documentId} />}
            {activeSection === 'cardio' && <CardioResultsForm documentId={paciente?.documentId} />}
          </div>
        </div>
      )}
    </div>
  );
};

export default DashboardComponent;
