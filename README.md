# Auth Service API 🔐

Servicio de autenticación y gestión de usuarios con **Spring Boot 4** y **JWT**. API moderna, segura y escalable lista para producción.

## ¿Qué es?

Un backend completo de autenticación que maneja:
- **Registro e inicio de sesión** con JWT
- **Gestión de usuarios** (CRUD completo)
- **Control de acceso** por roles (ADMIN, USER)
- **Renovación de tokens** sin credenciales
- **Cambio de contraseña y email** seguros

Perfecto como base para cualquier aplicación web o móvil que necesite autenticación.

## Stack Tecnológico

- **Java 21** - Lenguaje de programación
- **Spring Boot 4.0.2** - Framework web
- **Spring Security 7.0.2** - Autenticación y autorización
- **JWT (JJWT 0.12.5)** - Tokens seguros
- **JPA/Hibernate** - ORM para base de datos
- **H2 Database** - Base de datos embebida (desarrollo)
- **Swagger/OpenAPI 3.0** - Documentación automática
- **Lombok** - Menos boilerplate de código

## Características Principales

✅ **Autenticación con JWT** - Tokens seguros y sin sesión  
✅ **Roles y permisos** - Control granular de acceso  
✅ **API REST** - Endpoints limpios y bien documentados  
✅ **Validaciones** - DTOs con validación automática  
✅ **CORS configurado** - Listo para frontend en localhost:5173  
✅ **Manejo de errores** - Excepciones personalizadas  
✅ **Documentación interactiva** - Swagger UI integrado  

## Endpoints Principales

### Autenticación (sin token)
```
POST   /auth/register      → Registrar nuevo usuario
POST   /auth/login         → Login y obtener token JWT
POST   /auth/refresh       → Renovar token expirado
GET    /auth/              → Health check
```

### Perfil del Usuario (requiere token)
```
GET    /user/me            → Email del usuario logueado
GET    /user/profile       → Datos completos del perfil
PATCH  /users/{id}/password → Cambiar contraseña
PATCH  /users/{id}/email   → Cambiar email
```

### Administración (solo ADMIN)
```
GET    /users              → Listar todos los usuarios
GET    /users/{id}         → Obtener usuario específico
DELETE /users/{id}         → Eliminar usuario
PATCH  /users/{id}/toggle-status → Activar/desactivar usuario
GET    /admin/panel        → Panel administrativo
```

## Instalación y Uso

### Requisitos
- Java 21+
- Maven 3.8+

### Iniciar el servidor
```bash
# Clonar y entrar al proyecto
git clone <repo>
cd auth-service-springboot

# Compilar y ejecutar
./mvnw spring-boot:run

# El servidor estará en http://localhost:8080
```

### Acceder a la documentación
```
http://localhost:8080/swagger-ui.html
```

Aquí puedes probar todos los endpoints sin escribir código.

## Uso Rápido

### 1. Registrarse
```bash
curl -X POST http://localhost:8080/auth/register \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "usuario@email.com",
    "password": "password123"
  }'
```

### 2. Iniciar sesión
```bash
curl -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "usuario@email.com",
    "password": "password123"
  }'

# Respuesta: {"token": "eyJhbGciOiJIUzI1NiJ9..."}
```

### 3. Obtener perfil (con token)
```bash
curl -X GET http://localhost:8080/user/profile \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...'
```

## Seguridad

- **Contraseñas hasheadas** con BCrypt
- **JWT stateless** - Sin sesiones en servidor
- **Validación de entrada** - DTOs con anotaciones @Valid
- **Control de acceso** - @PreAuthorize por rol
- **CORS seguro** - Solo localhost:5173 en desarrollo
- **Token refresh** - Mantén sesiones sin credenciales

## Configuración

Las variables de entorno se configuran en `application.yaml`:

```yaml
jwt:
  secret: your-super-secret-key-change-this
  expiration: 3600000  # 1 hora en ms

spring:
  datasource:
    url: jdbc:h2:mem:testdb  # O tu BD
```

## Estructura del Proyecto

```
src/main/java/com/johan/authservice/
├── controller/       → Endpoints REST
├── service/          → Lógica de negocio
├── dto/              → Objetos de transferencia
├── entity/           → Modelos de BD
├── security/         → JWT y autenticación
├── config/           → Configuración Spring
├── exception/        → Manejo de errores
└── repository/       → Acceso a datos
```

## Próximas Mejoras (Optional)

- [ ] Recuperación de contraseña por email
- [ ] Autenticación de dos factores
- [ ] Refresh token rotation
- [ ] Auditoría de acciones
- [ ] Rate limiting

## Contacto

johan.estrada.proyectos@gmail.com

---

**Versión:** 1.0  
**Estado:** Producción  
**Licencia:** MIT

