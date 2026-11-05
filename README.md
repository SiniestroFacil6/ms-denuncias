# ms-denuncias

Microservicio del sistema **SiniestroFácil** encargado del registro y seguimiento de denuncias de siniestros automotrices. Genera un folio único por denuncia, gestiona su estado y almacena las fotografías asociadas (relación 1:N).

## Stack

- Java 21
- Spring Boot 4 (Web MVC, Data JPA, Validation, Security + OAuth2 Resource Server)
- MySQL 8
- Maven (wrapper incluido: `mvnw` / `mvnw.cmd`)
- Lombok
- springdoc-openapi (Swagger UI)
- Docker

## Arquitectura interna

```
src/main/java/cl/duoc/siniestrofacil/ms_denuncias
├── config/        SecurityConfig (validación JWT), SwaggerConfig
├── controller/    DenunciaController (endpoints REST)
├── dto/           DenunciaRequestDTO, DenunciaResponseDTO
├── model/         Denuncia (1) ──< Fotografia (N)
├── repository/    DenunciaRepository (Spring Data JPA)
└── service/       DenunciaService (lógica de negocio)
```

### Modelo de datos

| Tabla         | Campos principales                                                                 |
|---------------|------------------------------------------------------------------------------------|
| `denuncias`   | `id`, `folio` (único), `rut_asegurado`, `patente_vehiculo`, `descripcion`, `fecha_registro`, `estado` |
| `fotografia`  | `id`, `url_imagen`, `denuncia_id` (FK → `denuncias.id`)                            |

`Denuncia` usa `@OneToMany(mappedBy = "denuncia", cascade = ALL, orphanRemoval = true)` y `Fotografia` usa `@ManyToOne` + `@JoinColumn(name = "denuncia_id")`.

## Requisitos

- JDK 21
- MySQL 8 corriendo en `localhost:3307` (o Docker)
- Git

## Configuración

Las credenciales se leen desde variables de entorno, con valores por defecto para desarrollo local (`src/main/resources/application.properties`):

| Variable      | Por defecto | Descripción               |
|---------------|-------------|---------------------------|
| `DB_HOST`     | `localhost` | Host de MySQL             |
| `DB_PORT`     | `3307`      | Puerto de MySQL           |
| `DB_USERNAME` | `root`      | Usuario de la BD          |
| `DB_PASSWORD` | `root`      | Contraseña de la BD       |

La base de datos `db_denuncias` se crea automáticamente (`createDatabaseIfNotExist=true`) y las tablas se generan con `spring.jpa.hibernate.ddl-auto=update`.

## Ejecución local

```bash
# 1. Clonar
git clone https://github.com/SiniestroFacil6/ms-denuncias.git
cd ms-denuncias

# 2. Levantar MySQL (si no lo tienes instalado)
docker run -d --name mysql-denuncias -p 3307:3306 \
  -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=db_denuncias mysql:8.0

# 3. Compilar y empaquetar
./mvnw clean package          # Windows: mvnw.cmd clean package

# 4. Ejecutar
java -jar target/ms-denuncias-0.0.1-SNAPSHOT.jar
```

El servicio queda disponible en `http://localhost:8081`.

### Con Docker

```bash
docker build -t ms-denuncias .
docker run -p 8081:8081 -e DB_HOST=host.docker.internal -e DB_PORT=3307 ms-denuncias
```

Para levantar el sistema completo (4 microservicios + 4 bases de datos) ver el repositorio `siniestrofacil-infra`.

## Seguridad

Todos los endpoints requieren un token JWT en el header:

```
Authorization: Bearer <token>
```

El token lo emite **ms-usuarios** (`POST http://localhost:8082/usuarios/login`) y se valida con la misma clave `jwt.secret` (HMAC-SHA256). Los roles se leen desde el claim `roles`.

## Endpoints

Base URL: `http://localhost:8081/api/v1/denuncias`

| Método   | Ruta                              | Rol requerido | Descripción                    | Respuesta OK     |
|----------|-----------------------------------|---------------|--------------------------------|------------------|
| `POST`   | `/`                               | autenticado   | Registra una denuncia          | `201 Created`    |
| `GET`    | `/`                               | autenticado   | Lista todas las denuncias      | `200 OK`         |
| `GET`    | `/{id}`                           | autenticado   | Obtiene una denuncia por ID    | `200 OK` / `404` |
| `PUT`    | `/{id}/estado?nuevoEstado=VALOR`  | `ADMIN`       | Actualiza el estado            | `200 OK`         |
| `DELETE` | `/{id}`                           | `ADMIN`       | Elimina una denuncia           | `204 No Content` |

### Ejemplo: registrar denuncia

```http
POST /api/v1/denuncias
Authorization: Bearer <token>
Content-Type: application/json

{
  "rutAsegurado": "19123456-7",
  "patenteVehiculo": "AB12CD",
  "descripcion": "Choque por alcance en Av. Providencia",
  "fotografiasUrls": [
    "https://ejemplo.cl/fotos/1.jpg",
    "https://ejemplo.cl/fotos/2.jpg"
  ]
}
```

Respuesta `201 Created`:

```json
{
  "folio": "A1B2C3D4",
  "rutAsegurado": "19123456-7",
  "patenteVehiculo": "AB12CD",
  "descripcion": "Choque por alcance en Av. Providencia",
  "estado": "PENDIENTE_ASIGNACION",
  "fechaRegistro": "2026-11-05T17:30:00",
  "fotografiasUrls": ["https://ejemplo.cl/fotos/1.jpg", "https://ejemplo.cl/fotos/2.jpg"]
}
```

### Errores

| Código | Caso                                             |
|--------|--------------------------------------------------|
| `400`  | Campos obligatorios vacíos (`@NotBlank`)         |
| `401`  | Sin token o token inválido                       |
| `403`  | Token válido pero sin rol `ADMIN`                |
| `404`  | Denuncia no encontrada (`GET /{id}`)             |

## Pruebas

```bash
./mvnw test
```

Incluye pruebas unitarias de `DenunciaService` con JUnit 5 y Mockito. La prueba `contextLoads` levanta el contexto completo, por lo que requiere MySQL disponible (paso 2 de la ejecución local).

## Flujo de ramas

- `main`: versión estable, solo recibe merges desde `develop` vía Pull Request.
- `develop`: integración de funcionalidades.
- `feature/*`: desarrollo de cada funcionalidad, se integra a `develop` vía Pull Request.

Los commits siguen el formato *Conventional Commits* (`feat:`, `fix:`, `docs:`, `test:`, `build:`, `chore:`).
