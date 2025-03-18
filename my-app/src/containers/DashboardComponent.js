import React, { useState } from 'react';
import PersonalDataForm from '../components/PersonalDataForm';
import MedicalDataForm from '../components/MedicalDataForm';
import RiskFactorsForm from '../components/RiskFactorsForm';
import LabResultsForm from '../components/LabResultsForm';
import './DashboardComponent.css';


const DashboardComponent = () => {
  const [activeSection, setActiveSection] = useState('personal');

  return (
    <div>
      <h2>Panel de Salud</h2>
      <nav>
        <button onClick={() => setActiveSection('personal')}>Datos Personales</button>
        <button onClick={() => setActiveSection('medical')}>Datos Médicos</button>
        <button onClick={() => setActiveSection('risk')}>Factores de Riesgo</button>
        <button onClick={() => setActiveSection('lab')}>Resultados de Laboratorio</button>
      </nav>

      <div>
        {activeSection === 'personal' && <PersonalDataForm />}
        {activeSection === 'medical' && <MedicalDataForm />}
        {activeSection === 'risk' && <RiskFactorsForm />}
        {activeSection === 'lab' && <LabResultsForm />}
      </div>
    </div>
  );
};

export default DashboardComponent;
