import React, { useState } from 'react';
import { generateContent } from '../services/AIService';

function AIComponent() {
    const [inputText, setInputText] = useState('');
    const [selectedFiles, setSelectedFiles] = useState([]);
    const [aiResponse, setAiResponse] = useState('');

    const handleFileChange = (event) => {
        setSelectedFiles(event.target.files);
    };

    const handleGenerate = async () => {
        const response = await generateContent(inputText, selectedFiles);
        if (response) {
            // Mostrar la respuesta cruda como JSON formateado
            setAiResponse(JSON.stringify(response, null, 2));
        } else {
            setAiResponse("Error al obtener respuesta de la API.");
        }
    };

    return (
        <div className="ai-component">
            <h1>Generar Diagnóstico con Gemini y Vision AI</h1>

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

            <div>
                <label htmlFor="fileInput">Sube imágenes para analizar con Vision AI:</label>
                <input
                    id="fileInput"
                    type="file"
                    onChange={handleFileChange}
                    multiple
                />
            </div>

            <div>
                <button onClick={handleGenerate}>Generar Respuesta</button>
            </div>

            {aiResponse && (
                <div className="response-section">
                    <h2>Respuesta Generada</h2>
                    <pre style={{ whiteSpace: 'pre-wrap', wordWrap: 'break-word' }}>{aiResponse}</pre>
                </div>
        )}
        </div>
    );
}

export default AIComponent;