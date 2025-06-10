import React, { useState } from 'react';
import './styles/CardioResultsForm.css';
import { uploadCardioResults } from "../services/api";

const CardioResultsForm = ({ documentId }) => {
  const [electroFiles, setElectroFiles] = useState([]);
  const [ecoFiles, setEcoFiles] = useState([]);
  const [formFields, setFormFields] = useState({
    chestPainType: '',
    restingECG: '',
    exerciseAngina: '',
  });
  const [loading, setLoading] = useState(false);

  const handleElectroChange = (e) => {
    setElectroFiles([...e.target.files]);
  };

  const handleEcoChange = (e) => {
    setEcoFiles([...e.target.files]);
  };

  const handleInputChange = (e) => {
    setFormFields({ ...formFields, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const formData = new FormData();
      
      formData.append('documentId', documentId);

      electroFiles.forEach(file => formData.append('electrocardiogramFiles', file));
      ecoFiles.forEach(file => formData.append('echocardiogramFiles', file));

      formData.append('chestPainType', formFields.chestPainType);
      formData.append('restingECG', formFields.restingECG);
      formData.append('exerciseAngina', formFields.exerciseAngina);

      await uploadCardioResults(formData);

      alert('Resultados cardiológicos guardados correctamente');
      setElectroFiles([]);
      setEcoFiles([]);
      setFormFields({
        chestPainType: '',
        restingECG: '',
        exerciseAngina: '',
      });
    } catch (error) {
      alert('Error al enviar archivos');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-container">
      <h2>Resultados Cardiológicos</h2>
      <form onSubmit={handleSubmit} className="cardio-form">
        <div className="input-group">
          <label>Electrocardiograma (PDF o imagen):</label>
          <input type="file" multiple accept="image/*,application/pdf" onChange={handleElectroChange} />
        </div>
        <div className="input-group">
          <label>Ecocardiograma (PDF o imagen):</label>
          <input type="file" multiple accept="image/*,application/pdf" onChange={handleEcoChange} />
        </div>
        <div className="input-group">
          <label>Tipo de dolor torácico:</label>
          <select name="chestPainType" value={formFields.chestPainType} onChange={handleInputChange} required>
            <option value="">Seleccionar</option>
            <option value="0">Angina típica</option>
            <option value="1">Angina atípica</option>
            <option value="2">Dolor no anginal</option>
            <option value="3">Asintomático</option>
          </select>
        </div>
        <div className="input-group">
          <label>ECG en reposo:</label>
          <select name="restingECG" value={formFields.restingECG} onChange={handleInputChange} required>
            <option value="">Seleccionar</option>
            <option value="0">Normal</option>
            <option value="1">Anormalidad en la onda ST-T</option>
            <option value="2">Hipertrofia ventricular izquierda probable o definitiva</option>
          </select>
        </div>
        <div className="input-group">
          <label>Angina inducida por ejercicio:</label>
          <select name="exerciseAngina" value={formFields.exerciseAngina} onChange={handleInputChange} required>
            <option value="">Seleccionar</option>
            <option value="0">No</option>
            <option value="1">Sí</option>
          </select>
        </div>
        <button type="submit" className="save-button" disabled={loading}>
          {loading ? 'Guardando...' : 'Guardar'}
        </button>
      </form>
    </div>
  );
};

export default CardioResultsForm;