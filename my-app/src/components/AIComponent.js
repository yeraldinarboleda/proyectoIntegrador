import React, { useState } from 'react';
import { generateContent } from '../services/AIService';
import './styles/AIComponent.css';

function AIComponent() {
    const [inputText, setInputText] = useState('');
    const [selectedFiles, setSelectedFiles] = useState([]);
    const [aiResponse, setAiResponse] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const handleFileChange = (event) => {
        const files = Array.from(event.target.files);
        setSelectedFiles(files); // Reemplaza los archivos seleccionados
    };

    const handleRemoveFiles = () => {
        setSelectedFiles([]); // Limpia los archivos seleccionados
    };

    const handleGenerate = async () => {
        setIsLoading(true);
        setAiResponse('');
        try {
            const response = await generateContent(inputText, selectedFiles);
            if (response) {
                let cleanedResponse = response.replace(/^"|"$/g, '');
                cleanedResponse = cleanedResponse.replace(/\\n/g, '<br />');
                cleanedResponse = cleanedResponse.replace(/\**\**/g, '');
                setAiResponse(cleanedResponse);
            } else {
                setAiResponse("Error al obtener respuesta de la API.");
            }
        } catch (error) {
            setAiResponse("Error al procesar la solicitud.");
        } finally {
            setIsLoading(false);
        }
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

            <div>
                <button onClick={handleGenerate} disabled={isLoading}>
                    {isLoading ? 'Generando...' : 'Generar Respuesta'}
                </button>
            </div>

            {isLoading && (
                <div className="loading-spinner" style={{ margin: '20px' }}>
                    <div className="spinner" />
                </div>
            )}

            {aiResponse && (
                <div className="response-section" style={{ margin: '30px' }}>
                    <h2>Respuesta Generada</h2>
                    <div dangerouslySetInnerHTML={{ __html: aiResponse }} />
                </div>
            )}
        </div>
    );
}

export default AIComponent;