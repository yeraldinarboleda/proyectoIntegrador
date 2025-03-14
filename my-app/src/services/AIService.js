import axios from 'axios';

const API_URL = 'http://localhost:8081/api/ai/generate';

export const generateContent = async (text, files) => {
    const formData = new FormData();
    
    // Agregar texto al formulario
    if (text) {
        formData.append('text', text);
    }

    // Agregar archivos al formulario
    if (files && files.length > 0) {
        files.forEach((file) => {
            formData.append('files', file);
        });
    }

    try {
        // Realizar la solicitud POST
        const response = await axios.post(API_URL, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });

        // Devolver la respuesta
        return response.data;

    } catch (error) {
        console.error("Error en la solicitud:", error);
        return null;
    }
};
