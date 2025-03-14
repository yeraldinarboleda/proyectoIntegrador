import React, { useState } from 'react';
import { generateContent } from '../services/AIService'; // Ajusta la ruta según la ubicación real

function AIComponent() {
    const [inputText, setInputText] = useState('');
    const [selectedFiles, setSelectedFiles] = useState(null);
    const [aiResponse, setAiResponse] = useState('');

    const handleGenerate = async () => {
        // Llamar al servicio para obtener la respuesta de la IA
        const response = await generateContent(inputText, selectedFiles);
        setAiResponse(response); // Actualizar la respuesta en el frontend
    };

    const handleFileChange = (event) => {
        setSelectedFiles(event.target.files);
    };

    return (
        <div>
            <textarea 
                value={inputText}
                onChange={(e) => setInputText(e.target.value)}
                placeholder="Escribe tu texto aquí..."
            />
            <input type="file" multiple onChange={handleFileChange} />
            <button onClick={handleGenerate}>Generar Respuesta</button>

            {aiResponse && <div><h3>Respuesta de la IA:</h3><p>{aiResponse}</p></div>}
        </div>
    );
}

export default AIComponent;
