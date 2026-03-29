# Reservas Hotel

API REST para gestión de clientes, habitaciones y reservas de un hotel. Proyecto académico con **Spring Boot 4**, **JPA/Hibernate**, base **H2 en memoria** y patrones de diseño **Strategy** (pagos) y **Singleton** (métricas del sistema vía Spring).

## Requisitos

- **JDK 25** (según `pom.xml`; ajusta `java.version` si usas otra versión compatible con Spring Boot 4)
- **Maven 3.9+**

## Cómo ejecutar

```bash
cd reservas-hotel
mvn spring-boot:run
```

La aplicación queda en **http://localhost:8081** (`server.port` en `application.properties`).

Empaquetado:

```bash
mvn clean package -DskipTests
java -jar target/reservas-hotel-0.0.1-SNAPSHOT.jar
```

## Configuración

| Propiedad | Valor |
|-----------|--------|
| Base de datos | H2 en memoria (`hoteldb`) |
| DDL | `create-drop` (esquema y datos de prueba se recrean en cada arranque) |
| Consola H2 | Habilitada en **http://localhost:8081/h2-console** |
| JDBC URL (consola) | `jdbc:h2:mem:hoteldb` |
| Usuario / contraseña | `sa` / *(vacío)* |

Los datos iniciales se cargan con **`import.sql`** (ejecutado por Hibernate **después** de crear el tablas). `spring.sql.init.mode=never` evita que Spring ejecute `data.sql` antes del esquema JPA.

## Modelo de dominio (resumen)

- **`Usuario`** (abstracto, herencia **SINGLE_TABLE**): subtipos **`Cliente`** (`dtype = CLIENTE`, campo `membresia`) y **`Empleado`** (`dtype = Empleado`, `cargo`, `salario`).
- **`Habitacion`**: tipo (`TipoHabitacion`), precio por noche, disponibilidad, capacidad.
- **`Reserva`**: cliente, habitación, fechas, estado (`EstadoReserva`), total.
- **`Pago`**: asociado 1:1 a reserva; método (`MetodoPago`), estado (`EstadoPago`).

Enums principales: `TipoHabitacion`, `EstadoReserva`, `EstadoPago`, `MetodoPago`.

## Patrones y capas

- **Strategy**: interfaz `PagoStrategy` con implementaciones para efectivo, tarjeta y transferencia; el servicio de reservas elige la estrategia según el método de pago.
- **Singleton (conceptual)**: `GestorSistema` como `@Component` de Spring (un solo bean en el contexto) con contadores de reservas, pagos y cancelaciones; expuesto en la API.
- **DTOs**: `ReservaRequestDTO` (entrada con validación) y `ReservaResponseDTO` (salida).
- **Excepciones**: `RecursosNoEncontradoException` (404), `ReservaInvalidadException` (400); centralizadas en `GlobalExceptionHandler` junto con errores de validación (`@Valid`).

Reglas destacadas en reservas: validación de fechas, disponibilidad de habitación, descuentos por membresía (`GOLD` / `SILVER` en lógica de negocio; los datos de prueba en `import.sql` usan otras etiquetas de membresía), integración con pago y actualización de disponibilidad de habitación.

## API REST

### Clientes — `/api/clientes`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/clientes` | Listar todos |
| GET | `/api/clientes/{id}` | Obtener por id |
| POST | `/api/clientes` | Crear (cuerpo: entidad `Cliente`) |
| PUT | `/api/clientes/{id}` | Actualizar |
| DELETE | `/api/clientes/{id}` | Eliminar |
| GET | `/api/clientes/por-membresia` | Agrupar clientes por membresía |
| GET | `/api/clientes/gold-nombres` | Nombres en mayúsculas de clientes con membresía `GOLD` |

### Habitaciones — `/api/habitaciones`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/habitaciones` | Listar todas |
| GET | `/api/habitaciones/{id}` | Obtener por id |
| POST | `/api/habitaciones` | Crear |
| PUT | `/api/habitaciones/{id}` | Actualizar |
| DELETE | `/api/habitaciones/{id}` | Eliminar |
| GET | `/api/habitaciones/disponibles` | Marcadas como disponibles |
| GET | `/api/habitaciones/buscar` | Disponibles por solapamiento de fechas. Query: `fechaEntrada`, `fechaSalida` (ISO `yyyy-MM-dd`) |
| GET | `/api/habitaciones/precio-promedio` | Promedio de precio por noche |

> El servicio expone también `obtenerNumeroHabitacionesPorTipo` (IDs de habitaciones disponibles por tipo); hoy **no** tiene endpoint en el controlador.

### Reservas — `/api/reservas`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/reservas` | Listar todas (DTO respuesta) |
| GET | `/api/reservas/{id}` | Detalle |
| POST | `/api/reservas` | Crear reserva (`@Valid` + `ReservaRequestDTO`) |
| PUT | `/api/reservas/{id}/cancelar` | Cancelar |
| GET | `/api/reservas/cliente/{clienteId}` | Reservas de un cliente |
| GET | `/api/reservas/ingresos` | Suma de totales en estado confirmada |
| GET | `/api/reservas/estadisticas` | Conteo por estado |
| GET | `/api/reservas/mes-actual` | Reservas del mes (no canceladas) + log en consola |
| GET | `/api/reservas/sistema/estado` | Texto con métricas del `GestorSistema` |

### Ejemplo: crear reserva

`POST /api/reservas` — `Content-Type: application/json`

```json
{
  "clienteId": 1,
  "habitacionId": 4,
  "fechaEntrada": "2026-04-10",
  "fechaSalida": "2026-04-15",
  "metodoPago": "TARJETA"
}
```

Valores permitidos para `metodoPago`: `EFECTIVO`, `TARJETA`, `TRANSFERENCIA` (mayúsculas, según validación del DTO).

## Estructura del código

```
src/main/java/com/iudigital/camilog/reservas_hotel/
├── ReservasHotelApplication.java
├── controller/          # REST
├── service/             # Lógica de negocio
├── repository/          # Spring Data JPA
├── model/               # Entidades JPA + dto/
├── exception/           # Excepciones + GlobalExceptionHandler
└── pattern/
    ├── singleton/       # GestorSistema
    └── strategy/        # PagoStrategy y implementaciones

src/main/resources/
├── application.properties
└── import.sql           # Datos iniciales (Hibernate)
```

## Tests

```bash
mvn test
```

Hay una clase de test generada (`ReservasHotelApplicationTests`); puedes ampliar pruebas de integración según necesites.

## Dependencias principales (Maven)

- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-validation`
- `h2`
- `lombok`
- `spring-boot-devtools` (runtime, opcional)

---

**Autor / contexto:** proyecto universitario IUDigital — paquete `com.iudigital.camilog.reservas_hotel`.
