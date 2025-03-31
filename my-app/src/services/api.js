const API_URL = "http://localhost:8081/api"; 

export const sendPersonalData = async (personalData) => {
  try {
    const response = await fetch(`${API_URL}/personal-data`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(personalData),
    });

    if (!response.ok) {
      throw new Error("Error al enviar datos");
    }

    return await response.json(); // Devuelve la respuesta del backend
  } catch (error) {
    console.error("Error:", error);
    throw error;
  }
};
