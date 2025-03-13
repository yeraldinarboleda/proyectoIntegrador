export const generateContent = async (text, files) => {
    const formData = new FormData();
    const prompt = `eres un cardiologo experto y tienes que realizar analizar un informe para diagnosticos, pdf, radiografias que te pasen ${text}`;
    
    formData.append("text", prompt);

    // Solo añade archivos si se han seleccionado
    if (files && files.length > 0) {
        Array.from(files).forEach((file) => {
            formData.append("files", file);
        });
    }

    // Cambia la ruta base para apuntar a localhost:3000/ia
    const response = await fetch('http://localhost:8081/api/ia/generate', {
        method: 'POST',
        body: formData,
    });

    if (!response.ok) {
        console.error('Error en la solicitud:', response.statusText);
        return { error: 'Failed to generate content' };
    }

    const data = await response.json();
    console.log(data);
    return data;
};
