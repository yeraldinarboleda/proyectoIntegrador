import React, { useState } from 'react';
import './styles/LabResultsForm.css'; 

const LabResultsForm = () => {
  const [formData, setFormData] = useState({
    hemoglobinA1c: '',
    fastingGlucose: '',
    totalCholesterol: '',
    hdl: '',
    ldl: '',
    triglycerides: '',
    creatinine: '',
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Resultados de laboratorio:', formData);
    alert('Resultados de laboratorio guardados correctamente');
  };

  return (
    <div className="form-container">
      <h2>Resultados de Laboratorio</h2>
      <form onSubmit={handleSubmit} className="lab-form">
        {[
          { label: 'Hemoglobina glicosilada (%)', name: 'hemoglobinA1c', step: '0.1' },
          { label: 'Niveles de glucosa en sangre en ayunas (mg/dL)', name: 'fastingGlucose' },
          { label: 'Colesterol total (mg/dL)', name: 'totalCholesterol' },
          { label: 'HDL - Colesterol bueno (mg/dL)', name: 'hdl' },
          { label: 'LDL - Colesterol malo (mg/dL)', name: 'ldl' },
          { label: 'Triglicéridos (mg/dL)', name: 'triglycerides' },
          { label: 'Creatinina (mg/dL)', name: 'creatinine', step: '0.01' },
        ].map((field) => (
          <div className="input-group" key={field.name}>
            <label>{field.label}:</label>
            <input
              type="number"
              name={field.name}
              value={formData[field.name]}
              onChange={handleChange}
              step={field.step || '1'}
            />
          </div>
        ))}

        <button type="submit" className="save-button">Guardar</button>
      </form>
    </div>
  );
};

export default LabResultsForm;
