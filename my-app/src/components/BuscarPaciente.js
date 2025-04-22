import React, { useState } from "react";
import { getPersonalDataByDocument } from "../services/api"; 

const BuscarPaciente = ({ onPacienteEncontrado }) => {
  const [documentId, setDocumentId] = useState("");
  const [error, setError] = useState("");

  const handleBuscar = async () => {
    try {
      const paciente = await getPersonalDataByDocument(documentId);
      onPacienteEncontrado(paciente);  // ✅ Paciente encontrado
      setError("");
    } catch (err) {
      setError("Paciente no encontrado");
      onPacienteEncontrado(null);     // ✅ Notificar que no se encontró
    }
  };

  return (
    <div>
      <h3>Buscar Paciente</h3>
      <input
        type="text"
        value={documentId}
        onChange={(e) => setDocumentId(e.target.value)}
        placeholder="Documento del paciente"
      />
      <button onClick={handleBuscar}>Buscar</button>
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
};

export default BuscarPaciente;
