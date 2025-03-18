import React, { useState } from 'react';
import './styles/PersonalDataForm.css';

const PersonalDataForm = () => {
  const [formData, setFormData] = useState({
    documentType: '',
    documentId: '',
    firstName: '',
    lastName: '',
    birthDate: '',
    gender: '',
    address: '',
    contact: '',
    gmail: '',
    });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Datos personales:', formData);
    alert('Datos guardados correctamente');
  };

  return (
    <div className="form-container">
      <h2>Datos Personales</h2>
      <form onSubmit={handleSubmit} className="personal-form">
        {[
          { label: 'Tipo de Documento', name: 'documentType', type: 'select', options: ["Registro civil (RC)","Tarjeta de identidad (TI)",
            "Cédula de ciudadanía (CC)", "Tarjeta de extranjería (TE)", "Cédula de extranjería (CE)",
            "Número de identificación tributaria (NIT)","Pasaporte (PP)","Permiso especial de permanencia (PEP)",
            "Documento de identificación extranjero (DIE)", 'Otro'] },
          { label: 'Número de Documento', name: 'documentId', type: 'text' },
          { label: 'Nombres', name: 'firstName', type: 'text' },
          { label: 'Apellidos', name: 'lastName', type: 'text' },
          { label: 'Fecha de Nacimiento', name: 'birthDate', type: 'date' },
          { label: 'Genero', name: 'gender', type: 'select', options: ['Masculino', 'Femenino',"Otro"] },
          { label: 'Dirección', name: 'address', type: 'text' },
          { label: 'Contacto (Teléfono)', name: 'contact', type: 'text' },
          { label: 'Contacto (Correo)', name: 'gmail', type: 'text' },

        ].map((field) => (
          <div className="input-group" key={field.name}>
            <label>{field.label}:</label>
            {field.type === 'select' ? (
              <select name={field.name} value={formData[field.name]} onChange={handleChange}>
                <option value="">Seleccione</option>
                {field.options.map((option) => (
                  <option key={option} value={option.toLowerCase()}>{option}</option>
                ))}
              </select>
            ) : (
              <input type={field.type} name={field.name} value={formData[field.name]} onChange={handleChange} />
            )}
          </div>
        ))}

        <button type="submit" className="save-button">Guardar</button>
      </form>
    </div>
  );
};

export default PersonalDataForm;
