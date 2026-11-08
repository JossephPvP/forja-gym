# FORJA Gym

Sistema web para mejorar la atención a clientes de un gimnasio.
Arquitectura **Spring Boot + MVC en capas**, vistas con **Thymeleaf**, base de datos **MySQL** (o H2 sin instalar nada).

## Qué hace

| Módulo | Para qué sirve |
|---|---|
| Inicio | Ingreso rápido por DNI, cifras del día y membresías que vencen en 7 días |
| Control de ingreso | Valida la membresía del cliente al llegar y guarda la visita |
| Clientes | Registrar, buscar, editar y dar de baja |
| Membresías | Registrar o renovar una membresía y cobrarla (caso de uso principal) |
| Pagos | Historial de cobros e ingresos del mes |
| Clases y reservas | Crear clases con cupo y reservar por DNI |
| Planes | Mensual, trimestral, etc., con duración y precio |

## Requisitos (Ubuntu)

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk maven mysql-server
java -version
mvn -version
```

## Base de datos MySQL

```bash
sudo systemctl start mysql
sudo mysql
```

```sql
CREATE DATABASE forja_gym CHARACTER SET utf8mb4;
CREATE USER 'gym_user'@'localhost' IDENTIFIED BY 'gym1234';
GRANT ALL PRIVILEGES ON forja_gym.* TO 'gym_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

Si usas otro usuario o clave, cámbialos en `src/main/resources/application.properties`.
Las tablas se crean solas la primera vez.

## Ejecutar

```bash
mvn spring-boot:run
```

Abre http://localhost:8080

Sin MySQL (guarda los datos en la carpeta `data/`):

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

La primera vez se cargan 5 planes, 3 clientes y 3 clases de ejemplo.
Para no cargar los ejemplos, pon `forja.datos-demo=false` en `application.properties`.

DNI de ejemplo para probar el ingreso:

- `70123456` Lucía: membresía vigente
- `70234567` Diego: vence en 3 días
- `70345678` Camila: sin membresía

## Cómo se organiza (MVC en capas)

```
Navegador → Controller → Service → Repository → Base de datos
                ↓
        Vista (Thymeleaf)
```

```
src/main/java/com/forja/gimnasio/
├── model/        Entidades: Cliente, Plan, Membresia, Pago, Asistencia, Clase, Reserva
├── repository/   Acceso a datos (Spring Data JPA)
├── service/      Reglas del negocio
├── controller/   Reciben las peticiones y eligen la vista
├── config/       Datos iniciales
├── exception/    Errores de negocio
└── util/         Fechas en hora de Lima

src/main/resources/
├── templates/    Vistas (Thymeleaf)
└── static/       css/ y js/
```

## Reglas del negocio

- El DNI debe tener 8 dígitos y no puede repetirse.
- Una membresía termina `duración - 1` días después de su inicio (un plan de 30 días que empieza el 1 termina el 30).
- Un cliente no puede tener dos membresías que se crucen en fechas. Al renovar, la nueva empieza al día siguiente de que termina la actual.
- Registrar una membresía crea su pago en el mismo paso.
- Una membresía está "por vencer" cuando le quedan 7 días o menos.
- Para reservar una clase el cliente necesita membresía vigente el día de la clase, y la clase debe tener cupo.

## Pendiente (ideas para ampliar)

- Inicio de sesión por roles (administrador y recepcionista) con Spring Security.
- Reportes en PDF o Excel.
- Pruebas automáticas de los servicios.
