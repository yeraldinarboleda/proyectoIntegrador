import React, { useState } from 'react';
import { generateContent } from '../services/AIService'; // Ajusta la ruta según la ubicación real

function AIComponent() {
    const [inputText, setInputText] = useState('');  // Texto para Gemini
    const [selectedFiles, setSelectedFiles] = useState([]);  // Archivos para Vision
    const [aiResponse, setAiResponse] = useState('');  // Respuesta generada por AI

    const handleFileChange = (event) => {
        setSelectedFiles(event.target.files);  // Guardar los archivos seleccionados
    };

    const handleGenerate = async () => {
        const response = await generateContent(inputText, selectedFiles);  // Llamada al backend
        setAiResponse(response);  // Guardar la respuesta en el estado para mostrarla en pantalla
    };

    return (
        <div className="ai-component">
            <h1>Generar Diagnóstico con Gemini y Vision AI</h1>
            
            {/* Campo de texto para análisis de Gemini */}
            <div>
                <label htmlFor="textInput">Texto para análisis de Gemini:</label>
                <input 
                    id="textInput"
                    type="text" 
                    value={inputText} 
                    onChange={(e) => setInputText(e.target.value)} 
                    placeholder="Escribe el texto para análisis de Gemini"
                />
            </div>

            {/* Campo para subir archivos (Vision AI) */}
            <div>
                <label htmlFor="fileInput">Sube imágenes para analizar con Vision AI:</label>
                <input 
                    id="fileInput"
                    type="file" 
                    onChange={handleFileChange} 
                    multiple // Permite seleccionar múltiples archivos
                />
            </div>

            {/* Botón para generar respuesta */}
            <div>
                <button onClick={handleGenerate}>Generar Respuesta</button>
            </div>

            {/* Mostrar respuesta generada */}
            {aiResponse && (
                <div className="response-section">
                    <h2>Respuesta Generada</h2>
                    <pre>{aiResponse}</pre>
                </div>
            )}
        </div>
    );
}

export default AIComponent;
