# iniciar backend
cd demo
./mvnw clean install
docker compose up -d
./mvnw spring-boot:run


# pruebas backend 
./mvnw clean test

http://localhost:8081/api/ai/generate?text=  
# iniciar frontend
cd my-app
npm install
npm start
http://localhost:3000

# Iniciar microservicio
python python-predictor\serve_predict.py  

# ARQUITECTURA BACKEND  
src/main/java/com/projectname/  
├── controller/            # Controladores REST (API)  
├── service/               # Servicios  
├── repository/            # Repositorios y lógica de persistencia  
├── model/                 # Entidades y DTOs  
├── ai/                    # Módulo de inteligencia artificial  
└── dto/                   # Clases de transferencia de datos (DTO)  

# ARQUITECTURA FRONTEND  
src/  
├── components/            # Componentes de presentación (formularios, gráficos, tablas)  
├── containers/            # Contenedores de lógica de negocio y conexión con API  
├── services/              # Servicios para conectar con el backend  
├── hooks/                 # Hooks personalizados para la lógica compartida  
├── context/               # API de contexto para gestión de estado  
└── utils/                 # Utilidades generales  
