import React, { useState } from "react";
import "./styles/Login.css";

const Login = () => {
  const [formData, setFormData] = useState({
    documentId: "",
    password: "",
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!formData.documentId || !formData.password) {
      alert("Por favor, completa todos los campos");
      return;
    }
    console.log("Login exitoso con", formData);
    alert("Inicio de sesión exitoso");
  };

  return (
    <div className="form-container">
      <h2>Iniciar Sesión</h2>
      <form onSubmit={handleSubmit} className="login-form">
        <div className="input-group">
          <label htmlFor="documentId">Documento de Identidad:</label>
          <input
            type="text"
            id="documentId"
            name="documentId"
            value={formData.documentId}
            onChange={handleChange}
          />
        </div>
        <div className="input-group">
          <label htmlFor="password">Contraseña:</label>
          <input
            type="password"
            id="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
          />
        </div>
        <button type="submit" className="save-button">Ingresar</button>
      </form>
    </div>
  );
};

export default Login;

