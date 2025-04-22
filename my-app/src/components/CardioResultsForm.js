import React, { useState } from 'react';
import './styles/CardioResultsForm.css';
import { uploadCardioResults } from "../services/api";

const CardioResultsForm = () => {
  const [electroFiles, setElectroFiles] = useState([]);
  const [ecoFiles, setEcoFiles] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleElectroChange = (e) => {
    setElectroFiles([...e.target.files]);
  };

  const handleEcoChange = (e) => {
    setEcoFiles([...e.target.files]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await uploadCardioResults(electroFiles, ecoFiles);
      alert('Resultados cardiológicos guardados correctamente');
      setElectroFiles([]);
      setEcoFiles([]);
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
        <button type="submit" className="save-button" disabled={loading}>
          {loading ? 'Guardando...' : 'Guardar'}
        </button>
      </form>
    </div>
  );
};

export default CardioResultsForm;
