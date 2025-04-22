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

  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    const numericData = {
      ...formData,
      hemoglobinA1c: parseFloat(formData.hemoglobinA1c),
      fastingGlucose: parseFloat(formData.fastingGlucose),
      totalCholesterol: parseFloat(formData.totalCholesterol),
      hdl: parseFloat(formData.hdl),
      ldl: parseFloat(formData.ldl),
      triglycerides: parseFloat(formData.triglycerides),
      creatinine: parseFloat(formData.creatinine),
    };

    try {
      const response = await fetch('http://localhost:8081/api/lab-results', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(numericData)
      });

      if (!response.ok) {
        throw new Error('Error al guardar resultados de laboratorio');
      }

      alert('Resultados de laboratorio guardados correctamente');
      setFormData({
        hemoglobinA1c: '',
        fastingGlucose: '',
        totalCholesterol: '',
        hdl: '',
        ldl: '',
        triglycerides: '',
        creatinine: '',
      });
    } catch (error) {
      alert('Error al enviar los datos');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-container">
      <h2>Resultados de Laboratorio</h2>
      <form onSubmit={handleSubmit} className="lab-form">
        {[
          { label: 'Hemoglobina glicosilada (%)', name: 'hemoglobinA1c', step: '0.1' },
          { label: 'Glucosa en ayunas (mg/dL)', name: 'fastingGlucose' },
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
              required
            />
          </div>
        ))}

        <button type="submit" className="save-button" disabled={loading}>
          {loading ? 'Guardando...' : 'Guardar'}
        </button>
      </form>
    </div>
  );
};

export default LabResultsForm;
