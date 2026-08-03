# AGENTS.md — GestionPropiedades

Guía de contexto para agentes de IA y desarrolladores que trabajen en este proyecto.

## Visión general

API RESTful en Spring Boot para gestionar propiedades en alquiler: propiedades, inquilinos, contratos y pagos. Orientado a pequeños propietarios y agencias inmobiliarias.

## Tech Stack

- Java 21
- Spring Boot 4.0.3
- Spring Data JPA
- Spring Boot Validation
- Spring Boot WebMVC
- Springdoc OpenAPI 3.0.1 (Swagger UI)
- Flyway (migraciones de BD, solo en `prod`)
- Lombok
- Base de datos: H2 (perfil `dev`), PostgreSQL (perfil `prod`)
- Maven (wrapper `./mvnw`)
- JUnit 5 + Mockito + AssertJ (tests)

## Estructura del proyecto

```
src/main/java/com/example/gestionpropiedades/
  config/        # Beans de configuración (@Configuration)
  controller/    # Capa web: @RestController (solo delega en servicios)
  service/       # Capa de negocio: @Service + @Transactional
  repository/    # Capa de datos: interfaces que extienden JpaRepository
  entity/        # Entidades JPA (@Entity)
  entity/enums/  # Enumerados del dominio
  dto/           # Records de entrada/salida (request/response)
  exception/     # Excepciones de dominio y manejador global
src/main/resources/
  application.yml            # Configuración base
  application-dev.yml        # Perfil desarrollo (H2)
  application-prod.yml       # Perfil producción (PostgreSQL)
src/test/java/...            # Espejo de src/main
```

## Convenciones de código

Basadas en las skills instaladas en `.agents/skills/` (`java-coding-standards`, `java-springboot`, `java-docs`).

### Arquitectura y capas

- **Controller**: `@RestController`, usa DTOs de entrada/salida, `@Valid` en los request bodies, NO expone entidades JPA al cliente. Nombrado singular: `PropiedadController`, `InquilinoController`, `ContratoController`, `PagoController`.
- **Service**: `@Service`, contiene toda la lógica de negocio, es stateless, usa inyección por constructor, métodos con `@Transactional` al nivel más granular posible. Nombrado: `PropiedadService`, `InquilinoService`, `ContratoService`, `PagoService`, `UsuarioService`.
- **Repository**: extiende `JpaRepository<T, ID>` SIEMPRE con tipo genérico (nunca raw types). Consultas derivadas o `@Query`. NO lleva `@Repository` (redundante con Spring Data).
- **Entity**: `@Entity`, anotaciones JPA, Lombok `@Data` + `@Builder`, validaciones JPA (`@Column(nullable=false, unique=true)`, `@Enumerated`, etc.).
- **DTO**: usar **records** de Java para inmutablebilidad. Separar `XRequest` (entrada) de `XResponse` (salida). NO exponer entidades directamente.

### Inyección de dependencias

- Siempre inyección por **constructor** con campos `private final`.
- Nunca `@Autowired` por campo.

### Naming

- Clases/records: `PascalCase`
- Métodos/campos: `camelCase`
- Constantes: `UPPER_SNAKE_CASE`
- Endpoints RESTful en plural: `/api/propiedades`, `/api/inquilinos`, `/api/contratos`, `/api/pagos`, `/api/usuarios`

### Manejo de excepciones

- Excepciones de dominio unchecked (ej. `PropiedadNotFoundException`, `ContratoActivoException`, `PagoDuplicadoException`).
- Manejo global con `@RestControllerAdvice` que devuelve respuestas JSON consistentes.
- No capturar `catch (Exception ex)` salvo para re-lanzar con contexto o loggear.

### Validación

- Bean Validation (JSR 380) en DTOs: `@NotBlank`, `@NotNull`, `@Email`, `@DecimalMin`, `@Size`, etc.
- Activarla en controllers con `@Valid`.

### Inmutabilidad y tipos

- Records para DTOs, `final` para campos de dependencias.
- `Optional` en métodos de búsqueda de repositorios (`findBy...`).
- Evitar raw types, magic numbers y cadenas largas de parámetros (usar DTOs/builders).

### Logging

- SLF4J: `private static final Logger log = LoggerFactory.getLogger(X.class);`
- Logging parametrizado: `log.info("fetch_propiedad id={}", id);`

### Documentación

- Javadoc en miembros públicos/protegidos (primeras frases de summary, `@param`, `@return`, `@throws`). Ver skill `java-docs`.

### Naming del paquete y archivos

- Paquete base: `com.example.gestionpropiedades`
- Un tipo público por archivo. Mantener nombres correctos (ej. `PagoController`, no `PagoControler`).

## Reglas de negocio (CRÍTICAS — no perder de vista)

Estas reglas deben implementarse y cumplirse en toda modificación:

1. **Una propiedad no puede tener varios contratos activos simultáneamente.**
   - Al crear/activar un contrato para una propiedad, verificar que no exista otro `Contrato` con estado `ACTIVO` para esa misma propiedad. Si existe → lanzar `ContratoActivoException`.
   - Aplicar a nivel de servicio y reforzar con consulta de repositorio (posible control de concurrencia).

2. **Los pagos no se pueden duplicar para el mismo período.**
   - Un `Pago` tiene un período (mes/año) asociado a un `Contrato`.
   - Restricción de unicidad en BD: `@UniqueConstraint(columnNames = {"contrato_id", "periodo"})`.
   - Además validar en el servicio antes de persistir → si existe → lanzar `PagoDuplicadoException`.

3. **El estado de la propiedad se actualiza automáticamente según el contrato.**
   - Al activar un contrato → `Propiedad.estado = OCUPADA`.
   - Al finalizar/cancelar el contrato → `Propiedad.estado = DISPONIBLE`.
   - Este cambio ocurre dentro de la misma transacción que modifica el contrato.

## Reglas de seguridad (FASE 4)

- Roles: `ADMINISTRADOR`, `PROPIETARIO`, `INQUILINO`.
- Contraseñas SIEMPRE con BCrypt, nunca en texto plano.
- JWT para autenticación/autorización.
- Endpoints protegidos por rol.

## Roadmap por fases

### FASE 1 — Base funcional (COMPLETADA)
- [x] Modelar entidades: `Usuario`, `Propiedad`, `Inquilino`, `Contrato`, `Pago` con relaciones y enums.
- [x] Enums en `entity/enums/`: `Rol`, `EstadoPropiedad`, `EstadoContrato`, `EstadoPago`, `MetodoPago`.
- [x] Configurar `application.yml` + perfiles `dev` (H2) y `prod` (PostgreSQL).
- [x] Repositorios con tipo genérico + `UsuarioRepository` (falta).
- [x] DTOs como records (request/response) por dominio.
- [x] CRUD completo: `Propiedad`, `Inquilino`, `Contrato`, `Pago`, `Usuario`.
- [x] Corrección de typo: `PagoControler` → `PagoController`.
- [x] Manejo global de excepciones (`@RestControllerAdvice` + excepciones de dominio).
- [x] AGENTS.md (este archivo).

### FASE 2 — Reglas de negocio y dashboard (COMPLETADA)
- [x] Contratos activos únicos por propiedad (regla 1).
- [x] Pagos únicos por período (regla 2).
- [x] Actualización automática de estado de propiedad (regla 3).
- [x] Dashboard: ingresos totales, pagos pendientes, contratos activos.
- [x] Endpoints de ciclo de vida del contrato: `/api/contratos/{id}/activar`, `/finalizar`, `/cancelar`.
- [x] Test de integración `ReglasNegocioIntegrationTest` cubriendo las 3 reglas y el dashboard.

### FASE 3 — Calidad y robustez (COMPLETADA)
- [x] Migraciones de BD con Flyway (solo perfil `prod`; `dev`/`test` usan H2 con `ddl-auto=update` y Flyway deshabilitado).
- [x] Migración inicial `V1__create_tables.sql` que coincide con el esquema de las entidades (tablas, constraints, índices únicos).
- [x] Tests unitarios con Mockito para todos los servicios (`*ServiceTest`).
- [x] Test de integración `ReglasNegocioIntegrationTest` (reglas de negocio + dashboard).
- [x] Locking optimista `@Version` en `Contrato`, `Pago` y `Propiedad`.
- [x] Perfil `test` (`src/test/resources/application-test.yml`) + `@ActiveProfiles("test")` en tests de contexto.

### FASE 4 — Seguridad
- [ ] Spring Security + JWT con roles.
- [ ] Hashing BCrypt, endpoints protegidos, filtros JWT.

### FASE 5 — Futuras
- [ ] Generación de contratos en PDF.
- [ ] Recordatorios de pago por email.
- [ ] Integración de pagos en línea.
- [ ] Docker (`docker-compose.yml` con PostgreSQL).
- [ ] Despliegue en la nube (AWS / Render).
- [ ] Entidad `Mantenimiento` (diseño BD del README).

## Comandos

```bash
./mvnw compile          # compilar
./mvnw test             # ejecutar tests
./mvnw spring-boot:run  # arrancar (usa perfil dev por defecto)
./mvnw clean install    # build completo
```

Perfiles: `-Dspring-boot.run.profiles=dev` o `prod`. Swagger UI disponible en `/swagger-ui.html` (o ruta configurada por springdoc).

## Notas importantes

- No commitear secretos ni credenciales. Usar variables de entorno en `prod`.
- Antes de terminar cualquier tarea: ejecutar `./mvnw compile` y `./mvnw test`.
- Mantener el código en español en logs/mensajes solo si el dominio lo requiere; los identificadores y nombres de clases en inglés.
