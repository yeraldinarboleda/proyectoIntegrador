import React, { useState } from 'react';
import './styles/RiskFactorsForm.css';

const RiskFactorsForm = () => {
  const [formData, setFormData] = useState({
    smoking: false,
    drugUse: false,
    alcoholConsumption: false,
    physicalActivity: '',
    diet: '',
    diabetes: false,
    highCholesterol: false,
    hypertension: false,
    medicationUse: false,
    cardiovascularDiseases: false,
  });

  const handleChange = (e) => {
    const { name, type, checked, value } = e.target;
    setFormData({ ...formData, [name]: type === 'checkbox' ? checked : value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Factores de riesgo:', formData);
    alert('Factores de riesgo guardados correctamente');
  };

  return (
    <div className="form-container">
      <h2>Factores de Riesgo</h2>
      <form onSubmit={handleSubmit} className="risk-form">
        {[
          { label: '¿Fuma?', name: 'smoking' },
          { label: '¿Consume sustancias psicoactivas?', name: 'drugUse' },
          { label: '¿Consume alcohol?', name: 'alcoholConsumption' },
          { label: 'Diabetes', name: 'diabetes' },
          { label: 'Colesterol elevado', name: 'highCholesterol' },
          { label: 'Hipertensión arterial', name: 'hypertension' },
          { label: '¿Usa medicamentos?', name: 'medicationUse' },
          { label: 'Enfermedades cardiovasculares previas', name: 'cardiovascularDiseases' },
        ].map((field) => (
          <div className="input-group checkbox-group" key={field.name}>
            <input
              type="checkbox"
              name={field.name}
              checked={formData[field.name]}
              onChange={handleChange}
            />
            <label>{field.label}</label>
          </div>
        ))}
        
        <div className="input-group">
          <label>Nivel de actividad física:</label>
          <select name="physicalActivity" value={formData.physicalActivity} onChange={handleChange}>
            <option value="">Seleccione</option>
            <option value="low">Baja</option>
            <option value="moderate">Moderada</option>
            <option value="high">Alta</option>
          </select>
        </div>

        <div className="input-group">
          <label>Dieta:</label>
          <select name="diet" value={formData.diet} onChange={handleChange}>
            <option value="">Seleccione</option>
            <option value="healthy">Saludable</option>
            <option value="average">Promedio</option>
            <option value="unhealthy">No saludable</option>
          </select>
        </div>

        <button type="submit" className="save-button">Guardar</button>
      </form>
    </div>
  );
};

export default RiskFactorsForm;
