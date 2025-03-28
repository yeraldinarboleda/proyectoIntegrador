import React, { useState, useEffect } from 'react';
import './styles/MedicalDataForm.css';

const MedicalDataForm = () => {
  const [formData, setFormData] = useState({
    waistCircumference: '',
    systolicPressure: '',
    diastolicPressure: '',
    heartRate: '',
    weight: '',
    height: '',
    bmi: '',
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (formData.weight && formData.height) {
      const heightInMeters = formData.height / 100;
      const bmiValue = (formData.weight / (heightInMeters * heightInMeters)).toFixed(2);
      setFormData((prevData) => ({ ...prevData, bmi: bmiValue }));
    }
  }, [formData.weight, formData.height]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const validateForm = () => {
    const newErrors = {};
    Object.keys(formData).forEach((key) => {
      if (!formData[key] && key !== 'bmi') {
        newErrors[key] = 'Este campo es obligatorio';
      }
    });
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validateForm()) {
      console.log('Datos médicos:', formData);
      alert('Datos médicos guardados correctamente');
    } else {
      alert('Por favor, complete todos los campos obligatorios');
    }
  };

  return (
    <div className="form-container">
      <h2>Datos Médicos</h2>
      <form onSubmit={handleSubmit} className="medical-form">
        {[
          { label: 'Circunferencia de la cintura (cm)', name: 'waistCircumference' },
          { label: 'Presión Arterial Sistólica (mmHg)', name: 'systolicPressure' },
          { label: 'Presión Arterial Diastólica (mmHg)', name: 'diastolicPressure' },
          { label: 'Frecuencia Cardíaca (bpm)', name: 'heartRate' },
          { label: 'Peso (kg)', name: 'weight', step: '0.1' },
          { label: 'Altura (cm)', name: 'height', step: '0.1' },
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
            {errors[field.name] && <p className="error-message">{errors[field.name]}</p>}
          </div>
        ))}

        <div className="input-group">
          <label>Índice de Masa Corporal (IMC):</label>
          <input type="text" name="bmi" value={formData.bmi} readOnly />
        </div>

        <button type="submit" className="save-button">Guardar</button>
      </form>
    </div>
  );
};

export default MedicalDataForm;
