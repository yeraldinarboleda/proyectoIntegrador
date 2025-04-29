# serve_predict.py

from flask import Flask, request, jsonify
import joblib
import json
import pandas as pd
import os

app = Flask(__name__)

# Carga el modelo y las importancias al iniciar la app
MODEL_PATH = os.path.join(os.path.dirname(__file__), "models", "cardio_pipeline_calibrated.joblib")
FI_PATH    = os.path.join(os.path.dirname(__file__), "models", "feature_importances.json")

model = joblib.load(MODEL_PATH)
with open(FI_PATH, "r", encoding="utf-8") as f:
    feature_importances = json.load(f)

@app.route("/", methods=["GET"])
def home():
    return jsonify({"message": "Servidor de predicción cardiológica funcionando. Usa POST /predict"}), 200

@app.route("/favicon.ico")
def favicon():
    return "", 404

# Orden de las columnas que espera el pipeline
FEATURE_ORDER = [
    "age", "restingBP", "serumcholestrol", "maxheartrate", 
    "oldpeak", "noofmajorvessels",
    "gender", "fastingbloodsugar", "exerciseangia",
    "chestpain", "restingrelectro", "slope"
]

@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json(force=True)
    # Validación de presencia de todas las columnas
    missing = [feat for feat in FEATURE_ORDER if feat not in data]
    if missing:
        return jsonify({
            "error": "Faltan características obligatorias",
            "missing_features": missing
        }), 400

    try:
        # Construye DataFrame en el orden correcto
        X = pd.DataFrame([data], columns=FEATURE_ORDER, dtype=float)
        # Obtiene probabilidad de clase 1 (enfermedad)
        proba = model.predict_proba(X)[0, 1]
        # Prepara top 5 features importantes
        top5 = feature_importances[:5]
        print((float(proba), 4))
        return jsonify({
            "probability": round(float(proba), 4),
            "top_features": top5
        }), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    # Ejecuta en localhost:5000
    app.run(host="0.0.0.0", port=5000, debug=True)


    """datos paciente con problemas cardiacos:
{
  "age": 68,                   // Edad avanzada
  "restingBP": 165,            // Hipertensión moderada-alta
  "serumcholestrol": 300,      // Colesterol elevado
  "maxheartrate": 110,         // Ritmo máximo bajo para su edad
  "oldpeak": 4.2,              // Depresión ST pronunciada
  "noofmajorvessels": 2,       // 2 vasos principales afectados
  "gender": 1,                 // 1 = masculino
  "fastingbloodsugar": 1,      // Azúcar en ayunas alta
  "exerciseangia": 1,          // Angina inducida por ejercicio
  "chestpain": 0,              // 0 = angina típica (dolor fuerte)
  "restingrelectro": 1,        // 1 = ST-T anormal
  "slope": 2                   // 2 = segmento ST plano (fact. de riesgo)
}



datos paciente sano:
{
  "age":               30,
  "restingBP":        120,
  "serumcholestrol":  180,
  "maxheartrate":     170,
  "oldpeak":           0.0,
  "noofmajorvessels":  0,
  "gender":            1,
  "fastingbloodsugar": 0,
  "exerciseangia":     0,
  "chestpain":      3,          // 3 = asymptomatic (sin dolor)
  "restingrelectro":0,          // 0 = normal ECG
  "slope":          1        // 1 = upsloping (ST ascendente)
}

paciente intermedio:
{
  "age": 55,
  "restingBP": 140,
  "serumcholestrol": 240,
  "maxheartrate": 130,
  "oldpeak": 1.5,
  "noofmajorvessels": 1,
  "gender": 0,
  "fastingbloodsugar": 1,
  "exerciseangia": 0,
  "chestpain": 2,
  "restingrelectro": 0,
  "slope": 1
}

"""
    
