import React, { useState } from 'react';
import { generateContent } from '../services/AIService';
import './styles/AIComponent.css';

function AIComponent() {
    const [inputText, setInputText] = useState('');
    const [selectedFiles, setSelectedFiles] = useState([]);
    const [aiResponse, setAiResponse] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [showDownloadOptions, setShowDownloadOptions] = useState(false);

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
        setSelectedFiles(validFiles);
    };

    const handleRemoveFiles = () => {
        setSelectedFiles([]);
    };

    const formatResponse = (text) => {
        return text
            .replace(/\n\n/g, '<br><br>')
            .replace(/\n/g, '<br>')
            .replace(/\*\*(.*?)\*\*/g, '<b>$1</b>');
    };

    const handleGenerate = async () => {
        setIsLoading(true);
        setAiResponse('');
        try {
            const response = await generateContent(inputText, selectedFiles);
            const formattedText = formatResponse(response);
            setAiResponse(formattedText);
        } catch (error) {
            console.error('Error en la solicitud:', error);
            setAiResponse('Error al generar el contenido. Inténtalo de nuevo.');
        }
        setIsLoading(false);
    };

    const handleDownload = async (format) => {
        if (!aiResponse) return;

        let url;
        let filename;
        switch (format) {
            case 'pdf':
                url = 'http://localhost:8081/api/ai/download/pdf';
                filename = 'resultado.pdf';
                break;
            case 'csv':
                url = 'http://localhost:8081/api/ai/download/csv';
                filename = 'resultado.csv';
                break;
            case 'xlsx':
                url = 'http://localhost:8081/api/ai/download/excel';
                filename = 'resultado.xlsx';
                break;
            default:
                return;
        }

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ content: aiResponse }),
            });

            if (!response.ok) throw new Error(`Error ${response.status}`);

            const blob = await response.blob();
            const blobUrl = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = blobUrl;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(blobUrl);
        } catch (err) {
            console.error('Error descargando:', err);
            alert('No se pudo descargar el archivo.');
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
                    <button className="remove-file-button" onClick={handleRemoveFiles}>
                        Quitar Archivos Seleccionados
                    </button>
                )}
                {selectedFiles.length > 0 && (
                    <div className="selected-files">
                        {selectedFiles.map((file, index) => (
                            <span key={index} className="file-name">{file.name}</span>
                        ))}
                    </div>
                )}
            </div>
            
            <button onClick={handleGenerate} disabled={isLoading}>
                {isLoading ? 'Generando...' : 'Generar Contenido'}
            </button>
            
            {aiResponse && (
                <div className={`download-container ${showDownloadOptions ? "expanded" : ""}`}>
                    <button 
                        onClick={() => setShowDownloadOptions(!showDownloadOptions)} 
                        className="download-button main-download"
                    >
                        Descargar Resultado
                    </button>
                    <div className={`download-options ${showDownloadOptions ? "visible" : ""}`}>
                        <button className="download-button" onClick={() => handleDownload('pdf')}>PDF</button>
                        <button className="download-button" onClick={() => handleDownload('csv')}>CSV</button>
                        <button className="download-button" onClick={() => handleDownload('xlsx')}>Excel</button>
                    </div>
                </div>
            )}
            
            {isLoading && (
                <div className="loading-spinner" style={{ margin: '20px' }}>
                    <div className="spinner" />
                </div>
            )}
            
            {aiResponse && (
                <div className="ai-response" dangerouslySetInnerHTML={{ __html: aiResponse }} />
            )}
        </div>
    );
}

export default AIComponent;
