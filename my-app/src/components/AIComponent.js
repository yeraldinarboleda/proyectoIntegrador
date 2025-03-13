import React, { useState } from 'react';
import { generateContent } from '../services/AIService';

const AIComponent = () => {
    const [text, setText] = useState('');
    const [result, setResult] = useState('');
    const [files, setFiles] = useState([]);

    const handleGenerate = async () => {
        try {
            const data = await generateContent(text, files);
            if (data.candidates && data.candidates.length > 0) {
                // Extraer el texto del primer candidato
                const content = data.candidates[0].content.parts[0].text;
                setResult(content);
            } else {
                setResult('No content generated.');
            }
        } catch (error) {
            console.error('Error generating content:', error);
            setResult('Error generating content.');
        }
    };

    const handleKeyPress = (event) => {
        if (event.key === 'Enter') {
            handleGenerate();
        }
    };

    const handleFileChange = (event) => {
        setFiles(event.target.files);
    };

    return (
        <div>
            <input
                type="text"
                value={text}
                onChange={(e) => setText(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="Enter text"
            />
            <input
                type="file"
                multiple
                onChange={handleFileChange}
            />
            <button onClick={handleGenerate}>Generate</button>
            <div>
                <h3>Result:</h3>
                <p>{result}</p>
            </div>
        </div>
    );
};

export default AIComponent;