# iniciar backend
./mvnw clean install
./mvnw spring-boot:run

# iniciar frontend
cd my-app
npm install
npm start

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

