import React, { useState } from 'react';
import './styles/RiskFactorsForm.css';

const RiskFactorsForm = ({ documentId }) => {
  const [formData, setFormData] = useState({
    smoking: false,
    drugUse: false,
    alcoholConsumption: false,
    physicalActivity: '',
    diet: '',
    diabetes: false,
    diabetesType: '',
    highCholesterol: false,
    hypertension: false,
    medicationUse: false,
    cardiovascularDiseases: false,
    cardiovascularDiseaseType: '',
    otherCardiovascularDiseases: '',
  });

  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, type, checked, value } = e.target;
    setFormData({ ...formData, [name]: type === 'checkbox' ? checked : value });
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.physicalActivity) newErrors.physicalActivity = 'Este campo es obligatorio';
    if (!formData.diet) newErrors.diet = 'Este campo es obligatorio';
    if (formData.diabetes && !formData.diabetesType) newErrors.diabetesType = 'Seleccione un tipo de diabetes';
    if (formData.cardiovascularDiseases && !formData.cardiovascularDiseaseType)
      newErrors.cardiovascularDiseaseType = 'Seleccione un tipo de enfermedad cardiovascular';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) {
      alert('Por favor, complete todos los campos obligatorios');
      return;
    }

    setLoading(true);

    const cleanedData = {
      ...formData,
      documentId,
      diabetesType: formData.diabetes ? formData.diabetesType : null,
      cardiovascularDiseaseType: formData.cardiovascularDiseases ? formData.cardiovascularDiseaseType : null,
      otherCardiovascularDiseases: formData.cardiovascularDiseases ? formData.otherCardiovascularDiseases : null,
    };

    try {
      const response = await fetch('http://localhost:8081/api/risk-factors', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(cleanedData)
      });

      if (!response.ok) throw new Error('Error al guardar factores de riesgo');

      alert('Factores de riesgo guardados correctamente');
      setFormData({
        smoking: false,
        drugUse: false,
        alcoholConsumption: false,
        physicalActivity: '',
        diet: '',
        diabetes: false,
        diabetesType: '',
        highCholesterol: false,
        hypertension: false,
        medicationUse: false,
        cardiovascularDiseases: false,
        cardiovascularDiseaseType: '',
        otherCardiovascularDiseases: '',
      });
    } catch (error) {
      alert('Error al guardar factores de riesgo');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-container">
      <h2>Factores de Riesgo</h2>
      <form onSubmit={handleSubmit} className="risk-form">
        {[
          { label: '¿Fuma?', name: 'smoking' },
          { label: '¿Consume sustancias psicoactivas?', name: 'drugUse' },
          { label: '¿Consume alcohol?', name: 'alcoholConsumption' },
          { label: 'Colesterol elevado', name: 'highCholesterol' },
          { label: 'Hipertensión arterial', name: 'hypertension' },
          { label: '¿Usa medicamentos?', name: 'medicationUse' },
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

        <div className="input-group checkbox-group">
          <input
            type="checkbox"
            name="diabetes"
            checked={formData.diabetes}
            onChange={handleChange}
          />
          <label>Diabetes</label>
        </div>

        {formData.diabetes && (
          <div className="input-group">
            <label>Tipo de diabetes:</label>
            <select name="diabetesType" value={formData.diabetesType} onChange={handleChange}>
              <option value="">Seleccione</option>
              <option value="type1">Tipo 1</option>
              <option value="type2">Tipo 2</option>
              <option value="gestational">Gestacional</option>
              <option value="other">Otro</option>
            </select>
            {errors.diabetesType && <p className="error-message">{errors.diabetesType}</p>}
          </div>
        )}

        <div className="input-group checkbox-group">
          <input
            type="checkbox"
            name="cardiovascularDiseases"
            checked={formData.cardiovascularDiseases}
            onChange={handleChange}
          />
          <label>Enfermedades cardiovasculares previas</label>
        </div>

        {formData.cardiovascularDiseases && (
          <>
            <div className="input-group">
              <label>Tipo de enfermedad cardiovascular:</label>
              <select name="cardiovascularDiseaseType" value={formData.cardiovascularDiseaseType} onChange={handleChange}>
                <option value="">Seleccione</option>
                <option value="heartAttack">Infarto</option>
                <option value="arrhythmia">Arritmia</option>
                <option value="heartFailure">Insuficiencia cardíaca</option>
                <option value="other">Otro</option>
              </select>
              {errors.cardiovascularDiseaseType && <p className="error-message">{errors.cardiovascularDiseaseType}</p>}
            </div>
            <div className="input-group">
              <label>Otras enfermedades cardiovasculares (separadas por comas):</label>
              <input
                type="text"
                name="otherCardiovascularDiseases"
                value={formData.otherCardiovascularDiseases}
                onChange={handleChange}
                placeholder="Ejemplo: Angina, Miocardiopatía"
              />
            </div>
          </>
        )}

        <div className="input-group">
          <label>Nivel de actividad física:</label>
          <select name="physicalActivity" value={formData.physicalActivity} onChange={handleChange}>
            <option value="">Seleccione</option>
            <option value="low">Baja</option>
            <option value="moderate">Moderada</option>
            <option value="high">Alta</option>
          </select>
          {errors.physicalActivity && <p className="error-message">{errors.physicalActivity}</p>}
        </div>

        <div className="input-group">
          <label>Dieta:</label>
          <select name="diet" value={formData.diet} onChange={handleChange}>
            <option value="">Seleccione</option>
            <option value="healthy">Saludable</option>
            <option value="average">Promedio</option>
            <option value="unhealthy">No saludable</option>
          </select>
          {errors.diet && <p className="error-message">{errors.diet}</p>}
        </div>

        <button type="submit" className="save-button" disabled={loading}>
          {loading ? 'Guardando...' : 'Guardar'}
        </button>
      </form>
    </div>
  );
};

export default RiskFactorsForm;
