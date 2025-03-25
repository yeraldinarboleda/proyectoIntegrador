import React, { useState } from 'react';
import './styles/CardioResultsForm.css';

const CardioResultsForm = () => {
  const [electroFiles, setElectroFiles] = useState([]);
  const [ecoFiles, setEcoFiles] = useState([]);

  const handleElectroChange = (e) => {
    setElectroFiles([...electroFiles, ...e.target.files]);
  };

  const handleEcoChange = (e) => {
    setEcoFiles([...ecoFiles, ...e.target.files]);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Electrocardiograma:', electroFiles);
    console.log('Ecocardiograma:', ecoFiles);
    alert('Resultados cardiológicos guardados correctamente');
  };

  return (
    <div className="form-container">
      <h2>Resultados Cardiológicos</h2>
      <form onSubmit={handleSubmit} className="cardio-form">
        <div className="input-group">
          <label>Cargar Electrocardiograma (imágenes o PDFs):</label>
          <input type="file" accept="image/*,application/pdf" multiple onChange={handleElectroChange} />
        </div>
        <div className="input-group">
          <label>Cargar Ecocardiograma (imágenes o PDFs):</label>
          <input type="file" accept="image/*,application/pdf" multiple onChange={handleEcoChange} />
        </div>
        <button type="submit" className="save-button">Guardar</button>
      </form>
      {(electroFiles.length > 0 || ecoFiles.length > 0) && (
        <div className="file-preview">
          <h3>Archivos seleccionados:</h3>
          {electroFiles.length > 0 && (
            <div>
              <h4>Electrocardiograma:</h4>
              <ul>
                {electroFiles.map((file, index) => (
                  <li key={index}>{file.name}</li>
                ))}
              </ul>
            </div>
          )}
          {ecoFiles.length > 0 && (
            <div>
              <h4>Ecocardiograma:</h4>
              <ul>
                {ecoFiles.map((file, index) => (
                  <li key={index}>{file.name}</li>
                ))}
              </ul>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default CardioResultsForm;
