import React, { useState } from 'react';
import { generateContent } from '../services/AIService';
import './AIComponent.css';

function AIComponent() {
    const [inputText, setInputText] = useState('');
    const [selectedFiles, setSelectedFiles] = useState([]);
    const [aiResponse, setAiResponse] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const handleFileChange = (event) => {
        const files = Array.from(event.target.files);
        const validFiles = files.filter((file) =>
            ['image/jpeg', 'image/png', 'application/pdf'].includes(file.type)
        );
        validFiles.forEach((file) => {
            console.log("Archivo seleccionado:", file.name, "Tipo:", file.type);
        });
        if (validFiles.length !== files.length) {
            alert("Algunos archivos no son válidos. Solo se permiten imágenes y PDFs.");
        }
        setSelectedFiles(validFiles); // Solo guarda archivos válidos
    };

    const handleRemoveFiles = () => {
        setSelectedFiles([]); // Limpia los archivos seleccionados
    };

    const formatResponse = (text) => {
        return text
            .replace(/\n\n/g, '<br><br>')        // Doble salto de línea para párrafos
            .replace(/\n/g, '<br>')              // Salto de línea simple
            .replace(/\*\*(.*?)\*\*/g, '<b>$1</b>');  // Negrita para contenido entre **texto**
    };
    

    const handleGenerate = async () => {
        setIsLoading(true);
        setAiResponse('');
        try {
            const response = await generateContent(inputText, selectedFiles);
            console.log('Respuesta completa del backend:', response);
            const formattedText = formatResponse(response);
            setAiResponse(formattedText);
        } catch (error) {
            console.error('Error en la solicitud:', error);
            setAiResponse('Error al generar el contenido. Inténtalo de nuevo.');
        }
        setIsLoading(false);
    };

    const handleKeyDown = (event) => {
        if (event.key === 'Enter') {
            handleGenerate();
        }
    };

    return (
        <div className="ai-component">
        <h1>Generar Diagnóstico con Gemini y Vision AI</h1>

        <div className="text-input-container">
            <label htmlFor="textInput">Texto para análisis de Gemini:</label>
            <textarea
                id="textInput"
                value={inputText}
                onChange={(e) => setInputText(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder="Escribe el texto para análisis de Gemini"
                className="text-input"
            />
        </div>

        <div className="file-input-container" style={{ margin: '20px' }}>
            <label htmlFor="fileInput">Sube imágenes para analizar con Vision AI:</label>
            {selectedFiles.length === 0 ? (
                <input
                    id="fileInput"
                    type="file"
                    onChange={handleFileChange}
                    multiple
                    className="file-input"
                />
            ) : (
                <button
                    className="remove-file-button"
                    onClick={handleRemoveFiles}
                >
                    Quitar Archivos Seleccionados
                </button>
            )}
            {selectedFiles.length > 0 && (
                <div className="selected-files">
                    {selectedFiles.map((file, index) => (
                        <span key={index} className="file-name">
                            {file.name}
                        </span>
                    ))}
                </div>
            )}
        </div>
            <button onClick={handleGenerate} disabled={isLoading}>
                {isLoading ? 'Generando...' : 'Generar Contenido'}
            </button>
            {isLoading && (
                <div className="loading-spinner" style={{ margin: '20px' }}>
                    <div className="spinner" />
                </div>
            )}
            {aiResponse && (
                <div 
                    className="ai-response" 
                    dangerouslySetInnerHTML={{ __html: aiResponse }} 
                />
            )}
        </div>
    );
}

export default AIComponent;