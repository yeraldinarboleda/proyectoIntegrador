import axios from 'axios';

const API_URL = 'http://localhost:8081/api/ai/generate';

export const generateContent = async (text, files) => {
    const formData = new FormData();

    // Agregar texto al formulario
    if (text) {
        formData.append('text', text);
    }

    // Verificar y agregar archivos al formulario
    if (files) {
        // Usar Array.from() para convertir FileList a un array
        const filesArray = Array.from(files);

        filesArray.forEach((file) => {
            console.log("Archivo enviado:", file.name, "Tipo:", file.type); // Verifica el tipo MIME
            formData.append('file', file); // Usar 'file' en lugar de 'files'
        });
    }

    try {
        for (let pair of formData.entries()) {
            console.log(pair[0] + ', ' + pair[1]);
        }
        console.log("FormData:", formData); // Agregar esto para inspeccionar el objeto completo
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