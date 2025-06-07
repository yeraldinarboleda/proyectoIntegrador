// src/components/AIComponent.js

import React, { useState } from 'react';
import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  Legend
} from 'recharts';
import { generateContent } from '../services/AIService';
import './styles/AIComponent.css';

function AIComponent() {
  const [inputText, setInputText] = useState('');
  const [selectedFiles, setSelectedFiles] = useState([]);
  const [aiResponse, setAiResponse] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [dashboardData, setDashboardData] = useState(null);
  const [simData, setSimData] = useState(null);
  const [showDownloadOptions, setShowDownloadOptions] = useState(false);

  // 1) Manejo de selección de archivos
  const handleFileChange = (event) => {
    const files = Array.from(event.target.files);
    const validFiles = files.filter((file) =>
      ['image/jpeg', 'image/png', 'application/pdf'].includes(file.type)
    );
    if (validFiles.length !== files.length) {
      alert('Algunos archivos no son válidos. Solo se permiten imágenes y PDFs.');
    }
    setSelectedFiles(validFiles);
  };

  const handleRemoveFiles = () => {
    setSelectedFiles([]);
  };

  // 2) Formateador de texto (Markdown básico → HTML)
  const formatResponse = (text) => {
    return text
      .replace(/\n\n/g, '<br><br>')
      .replace(/\n/g, '<br>')
      .replace(/\*\*(.*?)\*\*/g, '<b>$1</b>');
  };

  // 3) Función principal: llamar a Gemini, extraer viñetas del Dashboard y simular
  const handleGenerate = async () => {
    setIsLoading(true);
    setAiResponse('');
    setDashboardData(null);
    setSimData(null);

    try {
      // 3.1) Llamar al backend que integra Vision AI + predicción + Gemini
      const resp = await generateContent(inputText, selectedFiles);

      // 3.2) Extraer sólo la parte diagnóstica/recomendaciones (antes del Dashboard)
      let clean = resp;
      // Buscamos “Dashboard del Paciente” o “Dashboard y Simulaciones” (letras iniciales “E.” o “e.”)
      const dashIdx = clean.search(/^[Ee]\.\s*(?:Dashboard del Paciente|Dashboard y Simulaciones)/m);
      if (dashIdx >= 0) {
        clean = clean.slice(0, dashIdx);
      }
      setAiResponse(formatResponse(clean));
      // 3.3) Capturar la probabilidad (%) que aparece en el texto (robusto a variantes)
    // 1) Extraer probabilidad antes de cortar nada:
    let p = 0
    const probMatch = resp.match(/Probabilidad[^:\d]*[:\s,]*([\d]+(?:\.\d+)?)/i)
    if (probMatch) {
      let raw = parseFloat(probMatch[1])
      p = raw > 1 ? raw / 100 : raw
    }
    console.log('Probabilidad extraída:', p)

    // 2) Cortar sólo a partir del título de Dashboard (sin “Probabilidad”):
    clean = resp
    const dashStart = clean.search(/Dashboard del Paciente/i)
    if (dashStart > 0) {
      clean = clean.slice(0, dashStart)
    }
    setAiResponse(formatResponse(clean))

    // 3) Extraer el bloque de texto que viene **después** de “Dashboard del Paciente”
    //    y que contenga líneas con “* Edad:”, “* Género:”, etc.
    const dashBlockMatch = resp.match(
      /Dashboard del Paciente[\s\S]*?\n([\s\S]*?)(?=\nVisualizaciones|Escenarios|$)/i
    )

    // 4) Inicializamos parsed con defaults
    const parsed = {
      Edad: '--',
      Género: '--',
      'Presión arterial sistólica': '--',
      Colesterol: '--',
      'Frecuencia cardíaca máxima': '--',
      Oldpeak: '--',
      'Número de vasos principales afectados': '--',
      'Angina inducida por ejercicio': '--'
    }

    if (dashBlockMatch) {
      const lines = dashBlockMatch[1].split('\n')
      lines.forEach(line => {
        // Sólo líneas que empiezan con “* ”
        const m = line.match(/^\*\s*(.+?)\s*:\s*(.+)$/)
        if (!m) return
        const key = m[1].trim()
        const val = m[2].trim()

        if (/^Edad/i.test(key)) {
          parsed.Edad = val
        } else if (/^G[eé]nero/i.test(key)) {
          parsed.Género = val
        } else if (/Presi[óo]n.*sist[oó]lica/i.test(key)) {
          parsed['Presión arterial sistólica'] = val
        } else if (/Colesterol/i.test(key)) {
          parsed.Colesterol = val
        } else if (/Frecuencia.*card[ií]aca.*m[aá]xima/i.test(key)) {
          parsed['Frecuencia cardíaca máxima'] = val
        } else if (/Oldpeak|depresión.*ST/i.test(key)) {
          parsed.Oldpeak = val
        } else if (/vasos.*principales/i.test(key)) {
          parsed['Número de vasos principales afectados'] = val
        } else if (/Angina.*ejercicio/i.test(key)) {
          parsed['Angina inducida por ejercicio'] = val
        }
      })
    }

    // 5) Pasar strings a valores “limpios”
    const num = s => {
      const m = (s||'').match(/([\d.]+)/)
      return m ? m[1] : '--'
    }

    // 6) Montar dashboardData
    const dashObj = {
      age:              num(parsed.Edad),
      gender:           parsed.Género,
      restingBP:        num(parsed['Presión arterial sistólica']),
      serumcholestrol:  num(parsed.Colesterol),
      maxheartrate:     num(parsed['Frecuencia cardíaca máxima']),
      oldpeak:          num(parsed.Oldpeak),
      noofmajorvessels: num(parsed['Número de vasos principales afectados']),
      exerciseangia:    parsed['Angina inducida por ejercicio'],
      probability:      p,
      popMale:          0.50,
      popFemale:        0.43
    }

      setDashboardData(dashObj);

      // 3.8) Simulaciones (porcentajes en 0–100)
      const months = [0, 6, 12, 24];
      const noSim = [
        p,
        Math.min(1, p + 0.005 * 6),
        Math.min(1, p + 0.005 * 12),
        Math.min(1, p + 0.005 * 24)
      ];
      const dietSim = [
        p,
        Math.max(0, p * 0.85),
        Math.max(0, p * 0.70),
        Math.max(0, p * 0.50)
      ];
      const exSim = [
        p,
        Math.max(0, p * 0.90),
        Math.max(0, p * 0.80),
        Math.max(0, p * 0.70)
      ];
      // Combinado: hasta 70% de reducción a 24 meses
      const allSim = [
        p,
        Math.max(0, p * 0.75),
        Math.max(0, p * 0.50),
        Math.max(0, p * 0.30)
      ];

      const simArr = months.map((m, i) => ({
        month:            m,
        'Sin cambios':    parseFloat((noSim[i] * 100).toFixed(1)),
        'Mejora dieta':   parseFloat((dietSim[i] * 100).toFixed(1)),
        'Más ejercicio':  parseFloat((exSim[i] * 100).toFixed(1)),
        'Probabilidad general reducida': parseFloat((allSim[i] * 100).toFixed(1)),
        'Pobl. Hombre':   parseFloat((0.50 * 100).toFixed(1)),
        'Pobl. Mujer':    parseFloat((0.43 * 100).toFixed(1))
      }));

      setSimData(simArr);
    } catch (err) {
      console.error(err);
      setAiResponse('Error al generar el contenido. Inténtalo de nuevo.');
    } finally {
      setIsLoading(false);
    }
  };

  // 4) Descarga de PDF/CSV/Excel (sin cambios)
  const handleDownload = async (format) => {
    if (!aiResponse) return;
    let url, filename;
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
        body: JSON.stringify({ content: aiResponse })
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

  // 5) Render completo
  return (
    <div className="ai-component">
      <h1>Generar Diagnóstico con Gemini y Vision AI</h1>

      {/* Texto libre para Gemini */}
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

      {/* Upload de archivos para Vision AI */}
      <div className="file-input-container" style={{ margin: '20px' }}>
        <label htmlFor="fileInput">Sube imágenes/PDF para Vision AI:</label>
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
            Quitar Archivos
          </button>
        )}
        {selectedFiles.length > 0 && (
          <div className="selected-files">
            {selectedFiles.map((file, idx) => (
              <span key={idx} className="file-name">{file.name}</span>
            ))}
          </div>
        )}
      </div>

      {/* Botón Generar */}
      <button onClick={handleGenerate} disabled={isLoading}>
        {isLoading ? 'Generando...' : 'Generar Contenido'}
      </button>

      {/* Opciones de descarga (PDF/CSV/Excel) */}
      {aiResponse && (
        <div className={`download-container ${showDownloadOptions ? 'expanded' : ''}`}>
          <button
            onClick={() => setShowDownloadOptions(!showDownloadOptions)}
            className="download-button main-download"
          >
            Descargar Resultado
          </button>
          <div className={`download-options ${showDownloadOptions ? 'visible' : ''}`}>
            <button className="download-button" onClick={() => handleDownload('pdf')}>PDF</button>
            <button className="download-button" onClick={() => handleDownload('csv')}>CSV</button>
            <button className="download-button" onClick={() => handleDownload('xlsx')}>Excel</button>
          </div>
        </div>
      )}

      {/* Spinner mientras carga */}
      {isLoading && (
        <div className="loading-spinner" style={{ margin: '20px' }}>
          <div className="spinner" />
        </div>
      )}

      {/* Texto de diagnóstico + recomendaciones (HTML formateado) */}
      {aiResponse && (
        <div
          className="ai-response"
          dangerouslySetInnerHTML={{ __html: aiResponse }}
        />
      )}

      {/* ——————————————————————————————————————————————————————————————
           DASHBOARD: Tarjetas (cards) con datos del paciente y riesgos poblacionales
         —————————————————————————————————————————————————————————————— */}
      {dashboardData && (
      <>
      <h2 style={{ marginTop: '32px', textAlign: 'center' }}>Dashboard</h2>
        <div className="dashboard-cards">
          <div className="card">
            <h3>Edad</h3>
            <p>{dashboardData.age} años</p>
          </div>
          <div className="card">
            <h3>Género</h3>
            <p>{dashboardData.gender}</p>
          </div>
          <div className="card">
            <h3>Presión Sistólica</h3>
            <p>{dashboardData.restingBP} mmHg</p>
          </div>
          <div className="card">
            <h3>Colesterol</h3>
            <p>{dashboardData.serumcholestrol} mg/dL</p>
          </div>
          <div className="card">
            <h3>FC Máxima</h3>
            <p>{dashboardData.maxheartrate} lpm</p>
          </div>
          <div className="card">
            <h3>Oldpeak</h3>
            <p>{dashboardData.oldpeak} mm</p>
          </div>
          <div className="card">
            <h3>Vasos Principales</h3>
            <p>{dashboardData.noofmajorvessels}</p>
          </div>
          <div className="card">
            <h3>Angina Ejercicio</h3>
            <p>{dashboardData.exerciseangia}</p>
          </div>
          <div className="card">
            <h3>Riesgo Actual</h3>
            <p>{(dashboardData.probability * 100).toFixed(1)} %</p>
          </div>
          <div className="card">
            <h3>Riesgo Poblacional (Hombre)</h3>
            <p>{(dashboardData.popMale * 100).toFixed(1)} %</p>
          </div>
          <div className="card">
            <h3>Riesgo Poblacional (Mujer)</h3>
            <p>{(dashboardData.popFemale * 100).toFixed(1)} %</p>
          </div>
        </div>
        </>
      )}

      {/* ——————————————————————————————————————————————————————————————
           GRÁFICA DE LÍNEAS (Simulaciones) con Recharts
         —————————————————————————————————————————————————————————————— */}
      {simData && (
        <>
        <h2 style={{ marginTop: '32px', textAlign: 'center' }}>
          Probabilidad en el tiempo si sigue o no las recomendaciones
        </h2>
        <ResponsiveContainer width="100%" height={350}>
          <LineChart
            data={simData}
            margin={{ top: 20, right: 40, left: 0, bottom: 20 }}
          >
            <XAxis
              dataKey="month"
              label={{ value: 'Meses', position: 'insideBottomRight', offset: -10 }}
            />
            <YAxis
              domain={[0, 100]}
              tickFormatter={(v) => `${v.toFixed(0)}%`}
              label={{ value: 'Riesgo (%)', angle: -90, position: 'insideLeft' }}
            />
            <Tooltip formatter={(v) => `${v.toFixed(1)}%`} />
            <Legend verticalAlign="top" height={36} />
            <Line
              type="monotone"
              dataKey="Sin cambios"
              name="Sin cambios"
              stroke="#8884d8"
              dot={{ r: 4 }}
            />
            <Line
              type="monotone"
              dataKey="Mejora dieta"
              name="Mejora dieta"
              stroke="#82ca9d"
              dot={{ r: 4 }}
            />
            <Line
              type="monotone"
              dataKey="Más ejercicio"
              name="Más ejercicio"
              stroke="#ffc658"
              dot={{ r: 4 }}
            />
            <Line
              type="monotone"
              dataKey="Probabilidad general reducida" 
              name="Probabilidad general reducida"
              stroke="#9607f5"
              dot={{ r: 4 }}
            />
            <Line
              type="monotone"
              dataKey="Pobl. Hombre"
              name="Pobl. Hombre"
              stroke="#ff7300"
              strokeDasharray="5 5"
              dot={{ r: 4 }}
            />
            <Line
              type="monotone"
              dataKey="Pobl. Mujer"
              name="Pobl. Mujer"
              stroke="#ff0000"
              strokeDasharray="5 5"
              dot={{ r: 4 }}
            />
          </LineChart>
        </ResponsiveContainer>
        </>
      )}
    </div>
  );
}

export default AIComponent;
