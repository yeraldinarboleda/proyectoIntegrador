import React, { useState } from "react";
import { Heart, Activity, Lock, User, Eye, EyeOff } from "lucide-react";
import "./styles/Login.css";

import { useNavigate } from "react-router-dom";

const LoginComponent = () => {
  const [formData, setFormData] = useState({ documentId: "", password: "" });
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };
const navigate = useNavigate();

 const handleSubmit = async () => {
  setLoading(true);
  setError("");

  try {
    const response = await fetch("http://localhost:8081/api/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(formData),
    });

    if (!response.ok) {
      throw new Error("Credenciales inválidas");
    }

    // Redirige al dashboard
    navigate("/dashboard");  // Cambia "/dashboard" por tu ruta real
  } catch (err) {
    setError("Credenciales inválidas");
  } finally {
    setLoading(false);
  }
};



  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-header">
          <div className="login-header-logo">
            <Heart className="heart-icon h-8 w-8" fill="#f87171" />
            <div className="login-header-text">
              <h2 className="login-header-title">CardioHealth AI</h2>
              <p className="login-header-subtitle">Sistema de diagnóstico cardiovascular</p>
            </div>
          </div>
          <Activity className="ecg-icon" />
        </div>

        <div className="login-body">
          <h3 className="login-title">Iniciar Sesión</h3>

          {error && <div className="login-alert">{error}</div>}

          <div className="form-group">
            <label htmlFor="documentId">Documento de Identidad</label>
            <div className="input-wrapper">
              <User className="input-icon" />
              <input
                type="text"
                name="documentId"
                id="documentId"
                value={formData.documentId}
                onChange={handleChange}
                className="input-field"
                placeholder="Ingrese su documento"
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="password">Contraseña</label>
            <div className="input-wrapper">
              <Lock className="input-icon" />
              <input
                type={showPassword ? "text" : "password"}
                name="password"
                id="password"
                value={formData.password}
                onChange={handleChange}
                className="input-field"
                placeholder="Ingrese su contraseña"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="password-toggle"
              >
                {showPassword ? <EyeOff /> : <Eye />}
              </button>
            </div>
          </div>

          <div className="login-options">
            <div className="remember-option">
              <input
                id="remember"
                type="checkbox"
                checked={rememberMe}
                onChange={() => setRememberMe(!rememberMe)}
              />
              <label htmlFor="remember">Recordarme</label>
            </div>
            <a href="#" className="forgot-password">¿Olvidó su contraseña?</a>
          </div>

          <button
            onClick={handleSubmit}
            disabled={loading}
            className="login-button"
          >
            {loading ? (
              <>
                <div className="spinner"></div>
                Procesando...
              </>
            ) : (
              "Ingresar"
            )}
          </button>

          <div className="navigation-links">
            <a href="/">Login</a>
            <a href="/ia">IA</a>
            <a href="/dashboard">Dashboard</a>
          </div>
        </div>

        <div className="bg-gray-50 px-4 py-3 text-center text-xs text-gray-500">
          CardioHealth AI © {new Date().getFullYear()} | Versión 1.0
        </div>
      </div>
    </div>
  );
};

export default LoginComponent;
