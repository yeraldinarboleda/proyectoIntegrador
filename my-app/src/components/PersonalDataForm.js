import React, { useEffect, useState } from "react";
import { sendPersonalData } from "../services/api";
import "./styles/PersonalDataForm.css";

const PersonalDataForm = ({ initialData }) => {
  const [formData, setFormData] = useState({
    documentType: "",
    documentId: "",
    firstName: "",
    lastName: "",
    birthDate: "",
    gender: "",
    address: "",
    contact: "",
    gmail: "",
  });

  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  // Si initialData cambia, actualiza el formulario
  useEffect(() => {
    if (initialData) {
      setFormData(initialData);
    }
  }, [initialData]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const validateForm = () => {
    const newErrors = {};
    Object.keys(formData).forEach((key) => {
      if (!formData[key] && key !== "documentType" && key !== "gender") {
        newErrors[key] = "Este campo es obligatorio";
      }
    });
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) {
      alert("Por favor, complete todos los campos obligatorios");
      return;
    }

    setLoading(true);
    try {
      const response = await sendPersonalData(formData);
      alert("Datos guardados correctamente: " + JSON.stringify(response));
    } catch (error) {
      alert("Error al guardar los datos");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-container">
      <h2>{initialData ? "Editar Datos del Paciente" : "Registrar Nuevo Paciente"}</h2>
      <form onSubmit={handleSubmit} className="personal-form">
        {[
          { label: "Tipo de Documento", name: "documentType", type: "select", options: ["Registro civil (RC)", "Tarjeta de identidad (TI)", "Cédula de ciudadanía (CC)", "Tarjeta de extranjería (TE)", "Cédula de extranjería (CE)", "Número de identificación tributaria (NIT)", "Pasaporte (PP)", "Permiso especial de permanencia (PEP)", "Documento de identificación extranjero (DIE)", "Otro"] },
          { label: "Número de Documento", name: "documentId", type: "text" },
          { label: "Nombres", name: "firstName", type: "text" },
          { label: "Apellidos", name: "lastName", type: "text" },
          { label: "Fecha de Nacimiento", name: "birthDate", type: "date" },
          { label: "Género", name: "gender", type: "select", options: ["Masculino", "Femenino", "Otro"] },
          { label: "Dirección", name: "address", type: "text" },
          { label: "Contacto (Teléfono)", name: "contact", type: "text" },
          { label: "Contacto (Correo)", name: "gmail", type: "text" },
        ].map((field) => (
          <div className="input-group" key={field.name}>
            <label>{field.label}:</label>
            {field.type === "select" ? (
              <select name={field.name} value={formData[field.name]} onChange={handleChange} required>
                <option value="">Seleccione</option>
                {field.options.map((option) => (
                  <option key={option} value={option.toLowerCase()}>{option}</option>
                ))}
              </select>
            ) : (
              <input
                type={field.type}
                name={field.name}
                value={formData[field.name]}
                onChange={handleChange}
                required
              />
            )}
            {errors[field.name] && <p className="error-message">{errors[field.name]}</p>}
          </div>
        ))}

        <button type="submit" className="save-button" disabled={loading}>
          {loading ? "Guardando..." : "Guardar"}
        </button>
      </form>
    </div>
  );
};

export default PersonalDataForm;
