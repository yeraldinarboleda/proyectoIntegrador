import axios from 'axios';

const API_URL = "http://localhost:8081/api"; 

export const sendPersonalData = async (personalData) => {
  try {
    const response = await fetch(`${API_URL}/personal-data`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(personalData),
    });

    if (!response.ok) {
      throw new Error("Error al enviar datos");
    }

    return await response.json(); // Devuelve la respuesta del backend
  } catch (error) {
    console.error("Error:", error);
    throw error;
  }
};

export const sendMedicalData = async (data) => {
  const response = await fetch(`${API_URL}/medical-data`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    throw new Error("Error al guardar datos médicos");
  }

  return response.json();
};

export const sendRiskFactors = async (data) => {
  const response = await fetch(`${API_URL}/risk-factors`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    throw new Error("Error al guardar factores de riesgo");
  }

  return response.json();
};

export const sendLabResults = async (data) => {
  const response = await fetch(`${API_URL}/lab-results`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    throw new Error("Error al guardar resultados de laboratorio");
  }

  return response.json();
};


export const uploadCardioResults = async (formData) => {
  const response = await fetch(`${API_URL}/cardio-results/upload`, {
    method: 'POST',
    body: formData,
  });

  if (!response.ok) {
    throw new Error('Error al subir archivos de resultados cardiológicos');
  }

  return await response.json();
};



export const getPersonalDataByDocument = async (documentId) => {
  const response = await fetch(`${API_URL}/personal-data/document/${documentId}`);
  if (!response.ok) {
    throw new Error("Paciente no encontrado");
  }
  return await response.json(); // Retorna el objeto del paciente
};

