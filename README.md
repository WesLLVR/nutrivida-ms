# NutriVida - Backend con Microservicios

Backend de la Clínica Nutricional NutriVida (Temuco), construido con Spring Boot, descubrimiento de servicios con Eureka, enrutamiento con API Gateway, comunicación entre microservicios con OpenFeign y autenticación con JWT y roles.

## Contexto del proyecto

El sistema permite que los pacientes revisen el catálogo de servicios, compren servicios y obtengan su boleta, pidan citas con un nutricionista y revisen su progreso. Los nutricionistas confirman sus citas y registran las fichas de control de sus pacientes. El administrador gestiona usuarios, servicios, citas, ventas y los mensajes que llegan desde el formulario de contacto.

## Integrantes

- Lucas Valderrama
- Bastián González
- Pedro Cid

## Tecnologías

- Java 17 y Spring Boot 3.2.5
- Spring Cloud 2023.0.1 (Eureka, Gateway y OpenFeign)
- Spring Data JPA con MySQL (XAMPP)
- Lombok
- springdoc-openapi (Swagger)
- spring-security-crypto (BCrypt para las contraseñas)
- JUnit 5 y Mockito para las pruebas, con H2 en memoria

### Microservicios

| Microservicio | Puerto | Base de datos | Descripción |
|---|---|---|---|
| usuarioMS | 8081 | usuarios_db | Usuarios, roles, regiones y comunas. Login, registro y renovación del token JWT |
| catalogoMS | 8082 | catalogo_db | Servicios, tipos de servicio y nutricionistas |
| citaMS | 8083 | citas_db | Citas entre paciente y nutricionista (pedir, confirmar, cancelar, reprogramar) |
| fichaMS | 8084 | fichas_db | Fichas de control con peso, talla, cintura, IMC y plan alimenticio |
| ventaMS | 8085 | ventas_db | Compras de servicios y boletas con neto, IVA y total |
| contactoMS | 8086 | contactos_db | Mensajes del formulario de contacto |

### Infraestructura

| Componente | Puerto | Descripción |
|---|---|---|
| eureka-server | 8761 | Servidor de descubrimiento de servicios |
| gateway | 8080 | API Gateway - punto único de entrada para el frontend |

### Comunicación entre microservicios (OpenFeign)

| Microservicio | Consulta a | Para qué |
|---|---|---|
| citaMS | catalogoMS | Validar que el nutricionista exista y obtener su nombre |
| citaMS | usuarioMS | Obtener el nombre del paciente |
| fichaMS | catalogoMS | Saber qué nutricionista está creando la ficha (por su correo) |
| fichaMS | usuarioMS | Obtener el nombre del paciente |
| ventaMS | catalogoMS | Tomar el precio real de cada servicio comprado |

## Rutas principales del Gateway

```
http://localhost:8080/api/v1/auth
http://localhost:8080/api/v1/usuarios
http://localhost:8080/api/v1/regiones
http://localhost:8080/api/v1/roles
http://localhost:8080/api/v1/servicios
http://localhost:8080/api/v1/tipos-servicio
http://localhost:8080/api/v1/nutricionistas
http://localhost:8080/api/v1/citas
http://localhost:8080/api/v1/fichas
http://localhost:8080/api/v1/ventas
http://localhost:8080/api/v1/contactos
```

## Autenticación con JWT y roles

1. El usuario inicia sesión en `POST /api/v1/auth/login` con su correo y contraseña.
2. usuarioMS revisa la contraseña con BCrypt y genera un token JWT firmado con HS256. El token guarda el id, el correo y el rol del usuario y dura 60 minutos.
3. El frontend envía el token en cada petición con el encabezado `Authorization: Bearer <token>`.
4. Cada microservicio tiene un filtro (`JwtFiltro`) que verifica la firma y la fecha de expiración del token. Si falta o no es válido responde **401**.
5. Cada controlador revisa el rol con la clase `Permisos`. Si el rol no tiene permiso responde **403**.
6. Antes de que el token venza, el frontend pide uno nuevo en `POST /api/v1/auth/renovar`.

| Rol | Puede |
|---|---|
| ADMINISTRADOR | CRUD de usuarios y servicios, ver y gestionar todas las citas, ventas y mensajes |
| NUTRICIONISTA | Ver y confirmar sus citas, crear y editar las fichas de sus pacientes |
| PACIENTE | Comprar servicios, pedir, cancelar y reprogramar sus citas, ver sus fichas y compras |

Rutas públicas (sin token): login, registro, regiones, listado de servicios, tipos de servicio, nutricionistas y envío de mensajes de contacto.

## Documentación Swagger

```
http://localhost:8081/swagger-ui.html   (usuarioMS)
http://localhost:8082/swagger-ui.html   (catalogoMS)
http://localhost:8083/swagger-ui.html   (citaMS)
http://localhost:8084/swagger-ui.html   (fichaMS)
http://localhost:8085/swagger-ui.html   (ventaMS)
http://localhost:8086/swagger-ui.html   (contactoMS)
```

Para probar las rutas protegidas en Swagger: hacer login en usuarioMS, copiar el `token` de la respuesta, presionar **Authorize** y pegarlo.

## Instrucciones de ejecución

Encender MySQL desde XAMPP. Las bases de datos se crean solas por `createDatabaseIfNotExist=true`, pero también se pueden crear a mano:

```sql
CREATE DATABASE usuarios_db;
CREATE DATABASE catalogo_db;
CREATE DATABASE citas_db;
CREATE DATABASE fichas_db;
CREATE DATABASE ventas_db;
CREATE DATABASE contactos_db;
```

### Orden de arranque

1. **eureka-server** (primero siempre) - puerto 8761
2. Microservicios base: usuarioMS y catalogoMS
3. Microservicios que consultan a otros: citaMS, fichaMS y ventaMS
4. contactoMS
5. **gateway** (último) - puerto 8080

Cada uno se levanta desde su carpeta con:

```
./mvnw spring-boot:run
```

Las tablas se crean automáticamente con `spring.jpa.hibernate.ddl-auto=update`. Los datos de ejemplo se cargan con el `DataLoader` de cada microservicio.

### Usuarios de prueba

Todos tienen la contraseña `1234`.

| Correo | Rol |
|---|---|
| admin@nutrivida.cl | ADMINISTRADOR |
| usuario@correo.cl | PACIENTE |
| carolina.fuentes@nutrivida.cl | NUTRICIONISTA |
| rodrigo.sepulveda@nutrivida.cl | NUTRICIONISTA |
| daniela.morales@nutrivida.cl | NUTRICIONISTA |
| felipe.araya@nutrivida.cl | NUTRICIONISTA |

### Pruebas

Desde la carpeta de cada microservicio:

```
./mvnw test
```

### Verificación

- Panel de Eureka: `http://localhost:8761`
- Documentación Swagger de cada microservicio
- Frontend: `http://localhost:5173` (repositorio nutrivida-frontend)
