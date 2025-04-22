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
  const [paciente, setPaciente] = useState(null); // Paciente encontrado o null
  const [buscado, setBuscado] = useState(false); // Solo mostrar formularios después de buscar

  const handleBusqueda = (data) => {
    setPaciente(data); // Puede ser null si no se encuentra
    setBuscado(true);  // Marca que ya se hizo una búsqueda
  };

  return (
    <div>
      <h2>Panel de Salud</h2>

      {!buscado ? (
        <BuscarPaciente onPacienteEncontrado={handleBusqueda} />
      ) : (
        <>
          {/* Mostrar info si existe */}
          {paciente ? (
            <p><strong>Paciente encontrado:</strong> {paciente.firstName} {paciente.lastName}</p>
          ) : (
            <p style={{ color: 'red' }}>Paciente no encontrado. Puedes registrarlo a continuación.</p>
          )}

          <nav>
            <button onClick={() => setActiveSection('personal')}>Datos Personales</button>
            <button onClick={() => setActiveSection('medical')}>Datos Médicos</button>
            <button onClick={() => setActiveSection('risk')}>Factores de Riesgo</button>
            <button onClick={() => setActiveSection('lab')}>Resultados de Laboratorio</button>
            <button onClick={() => setActiveSection('cardio')}>Resultados Cardiológicos</button>
            <button onClick={() => { setPaciente(null); setBuscado(false); }}>🔁 Buscar otro paciente</button>
          </nav>

          <div>
            {activeSection === 'personal' && (
              <PersonalDataForm
                initialData={paciente}
                documentId={paciente?.documentId}
              />
            )}
            {activeSection === 'medical' && (
              <MedicalDataForm documentId={paciente?.documentId} />
            )}
            {activeSection === 'risk' && (
              <RiskFactorsForm documentId={paciente?.documentId} />
            )}
            {activeSection === 'lab' && (
              <LabResultsForm documentId={paciente?.documentId} />
            )}
            {activeSection === 'cardio' && (
              <CardioResultsForm documentId={paciente?.documentId} />
            )}
          </div>
        </>
      )}
    </div>
  );
};

export default DashboardComponent;
