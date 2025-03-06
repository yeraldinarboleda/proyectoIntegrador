export const generateContent = async (text) => {
    const response = await fetch(`http://localhost:8081/api/ai/generate?text=${encodeURIComponent(text)}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    });
    const data = await response.json();
    console.log(data); // Imprime la respuesta en la consola
    return data;
};