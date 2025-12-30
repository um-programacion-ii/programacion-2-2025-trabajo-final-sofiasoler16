[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/IEOUmR9z)


# Sistema de Gestión de Eventos - Trabajo Final (sofiasoler16)
Este proyecto es una plataforma distribuida para la venta de entradas a eventos, desarrollada para la cátedra de Programación 2 - 2025. El sistema garantiza la persistencia local de datos, la gestión de sesiones de usuario concurrentes y la sincronización asincrónica con la infraestructura de la Universidad.

## 🏗️ Arquitectura del Sistema
El ecosistema utiliza una arquitectura de microservicios desacoplados:

Backend (eventosbackend): Desarrollado con Spring Boot 3 y JHipster 8. Gestiona la lógica de negocio, seguridad JWT, y persistencia en PostgreSQL.

Proxy (proxy-service): Actúa como intermediario para operaciones críticas de tiempo real, gestionando el estado de asientos en Redis y consumiendo eventos de Kafka.

Mobile App: Aplicación para el usuario final (Flutter) que permite la compra de entradas y gestión de historial personal.



```
┌─────────┐      ┌─────────┐      ┌─────────┐
│ Mobile  │─────▶│ Backend │─────▶│  Proxy  │
│  (KMP)  │◀─────│(Port    │◀─────│(Port    │
│Compose  │      │  8080 ) │      │  8081)  │
└─────────┘      └─────────┘      └─────────┘
                        │                 │
                        │                 │
                   ┌────▼─────┐      ┌─────▼───────────┐
                   │PostgreSQL│      │  Redis          │ 
                   │   (Local)│      │192.168.194.250  │
                   └──────────┘      └─────────────────┘
                                          │
                                          │
                                    ┌─────▼─────────┐
                                    │  Kafka        │
                                    │192.168.194.250│
                                    └─────┬─────────┘
                                          │
                                    ┌─────▼─────┐
                                    │  Cátedra  │
                                    │  (Externa)│
                                    └───────────┘
```

## 🚀 Inicio
Requisitos Previos
Java 21 (JDK indispensable para el backend).

Maven 3.8+.

Docker & Docker Compose.

Conectividad VPN/Local a la red de la cátedra (192.168.194.x).

1. Configuración de Red
   Para que el microservicio Proxy reconozca el cluster de Kafka, se debe mapear la IP en el archivo hosts del sistema:


```
echo "192.168.194.250 kafka" | sudo tee -a /etc/hosts
```
2. Levantar contenedores

docker compose -f src/main/docker/service.yml

3. Ejecución del Proxy
   Inicia el servicio que gestiona la mensajería y la caché:

```   
cd proxy-service
./mvnw clean spring-boot:run -Dspring-boot.run.profiles=dev
```
4. Ejecución del Backend
   Inicia la aplicación principal:

```
cd eventosBackend
./mvnw -ntp spring-boot:run
```

5. Para ejecutar los test
```
cd evemtosBackend
./mvnw test
```
## 🔐 Características Destacadas

Filtrado por Usuario: El historial de compras (/api/app/mis-ventas) utiliza el contexto de seguridad JWT para filtrar los resultados por el login del usuario actual, asegurando que un usuario no pueda ver las ventas de otro.

Sincronización Asincrónica: El Proxy actúa como bridge consumiendo mensajes de Kafka de la cátedra y notificando al backend local vía HTTP POST para disparar el EventoSyncService.

Validación de Sesiones: Gestión de estados de asientos mediante Redis externo para evitar colisiones en compras concurrentes.

Optimización de Datos
Prevención de Recursión: Se implementó @JsonIgnoreProperties en las entidades VentaLocal y AsientoVenta para evitar ciclos infinitos durante la serialización JSON, permitiendo que la App móvil procese la información correctamente.

## 📚 Documentación de la API

Endpoints Clave para la App móvil

GET http://localhost:8080/api/app/eventos : Trae los eventos
```
[
{
"id": 1,
"titulo": "Conferencia Nerd",
"resumen": "Esta es una conferencia de Nerds",
"fecha": "2026-01-10T11:00:00Z",
"imagen": "https://scontent-scl2-1.xx.fbcdn.net/v/t1.6435-9/78167441_1316997891841734_2833734909829316608_n.jpg?stp=dst-jpg_p960x960_tt6&_nc_cat=105&ccb=1-7&_nc_sid=127cfc&_nc_ohc=1EcS_p0lVGUQ7kNvwE1jrdM&_nc_oc=AdnCq7-RyHdhVpcPvW46Wehv10cjB9rcujwllkpiPP4H0OWUdbvCG0ygHh4zuXMQuIY&_nc_zt=23&_nc_ht=scontent-scl2-1.xx&_nc_gid=_fzHXJUwDiUHBphj96cIcw&oh=00_AfUm0lIje5462zK3WbGACoar67RKotbEXLqM6MeGpJdPoA&oe=68B9E878"
},
{
"id": 5,
"titulo": "Final Liga Mendocina de Basket",
"resumen": "Partido final de la liga mendocina de basket - Equipo A vs Equipo 1",
"fecha": "2026-01-14T14:00:00Z",
"imagen": "https://grupoceosa.com/wp-content/uploads/2020/11/Webp.net-resizeimage-73-1.jpg"
},
{
"id": 4,
"titulo": "Ciclo de Música Clásica Evento 2",
"resumen": "Evento musical de música clásica por vendimia - Evento 2/2",
"fecha": "2026-01-30T20:00:00Z",
"imagen": "https://media.diariouno.com.ar/adjuntos/298/migration/media/2019/04/DSC_0684-ok-700x395.jpg"
},
{
"id": 3,
"titulo": "Ciclo de Música Clásica Evento 1",
"resumen": "Evento musical de música clásica por vendimia - Evento 1/2",
"fecha": "2026-02-10T22:00:00Z",
"imagen": "https://www.unidiversidad.com.ar/cache/mg1371_608_1076.jpg"
}
]
```

GET http://localhost:8080/api/app/eventos/1 : Trae un evento por id
```
{
    "id": 1,
    "titulo": "Conferencia Nerd",
    "resumen": "Esta es una conferencia de Nerds",
    "descripcion": "Esta es una conferencia de prueba para verificar que los datos están correctos",
    "fecha": "2026-01-10T11:00:00Z",
    "direccion": "Aula magna de la Universidad de Mendoza",
    "imagen": "https://scontent-scl2-1.xx.fbcdn.net/v/t1.6435-9/78167441_1316997891841734_2833734909829316608_n.jpg?stp=dst-jpg_p960x960_tt6&_nc_cat=105&ccb=1-7&_nc_sid=127cfc&_nc_ohc=1EcS_p0lVGUQ7kNvwE1jrdM&_nc_oc=AdnCq7-RyHdhVpcPvW46Wehv10cjB9rcujwllkpiPP4H0OWUdbvCG0ygHh4zuXMQuIY&_nc_zt=23&_nc_ht=scontent-scl2-1.xx&_nc_gid=_fzHXJUwDiUHBphj96cIcw&oh=00_AfUm0lIje5462zK3WbGACoar67RKotbEXLqM6MeGpJdPoA&oe=68B9E878",
    "filasAsientos": 10,
    "columnasAsientos": 6,
    "precioEntrada": 2684.62,
    "tipoNombre": "Conferencia",
    "tipoDescripcion": "Conferencia"
}
```
GET http://localhost:8080/api/app/eventos/1/asientos?filas=10&columnas=10 : Trae la matriz de asientos de un evento

```
{
    "eventoId": 1,
    "filas": 10,
    "columnas": 10,
    "matriz": [
        [
            "VENDIDO",
            "VENDIDO",
            "VENDIDO",
            "VENDIDO",
            "VENDIDO",
            "VENDIDO",
            "LIBRE",
            "LIBRE",
            "LIBRE",
            "LIBRE"
        ],
        [
            "BLOQUEADO",
            "VENDIDO",
            "BLOQUEADO",
            "VENDIDO",
            "VENDIDO",
            "LIBRE",
            "LIBRE",
            "LIBRE",
            "LIBRE",
            "LIBRE"
        ],
 ...
```

GET http://localhost:8080/api/app/mis-ventas : Historial personal de compras
```
[
    {
        "eventoId": 2,
        "ventaId": 1643,
        "fechaVenta": "2025-12-26T00:02:00.651703Z",
        "resultado": true,
        "descripcion": "Venta realizada con exito",
        "precioVenta": 4506.01,
        "cantidadAsientos": 1
    },
    {
        "eventoId": 2,
        "ventaId": 1656,
        "fechaVenta": "2025-12-26T11:02:24.845089Z",
        "resultado": true,
        "descripcion": "Venta realizada con exito",
        "precioVenta": 4517.4,
        "cantidadAsientos": 1
    },
    {
        "eventoId": 2,
        "ventaId": 1694,
        "fechaVenta": "2025-12-26T18:10:58.746576Z",
        "resultado": false,
        "descripcion": "Venta rechazada. Alguno de los asientos no se encontraban bloqueados para la venta.",
        "precioVenta": 4517.4,
        "cantidadAsientos": 0
    },
```
POST http://localhost:8080/api/app/bloquear : Reserva temporal de asientos

POST http://localhost:8080/api/app/venta: Compra del asiento

## EStructura del proyecto

```
.
├── eventosBackend/               # Núcleo del Sistema (Spring Boot + JHipster)
│   ├── src/
│   │   ├── main/
│   │   │   ├── docker/
│   │   │   ├── java/com/um/eventosbackend/
│   │   │   │   ├── config/           # Seguridad (SecurityConfiguration) y filtros
│   │   │   │   ├── domain/           # Entidades (VentaLocal, AsientoVenta con @JsonIgnoreProperties)
│   │   │   │   ├── repository/       # Consultas SQL (Filtro de "mis-ventas")
│   │   │   │   ├── service/
│   │   │   │   │   ├── app/     
│   │   │   │   │   ├── catedra/      # EventoSyncService (Motor de sincronización)
│   │   │   │   │   └── dto/
│   │   │   │   └── web/rest/         # Controladores API
│   │   │   │       ├── app/
│   │   │   │       │   ├── SesionAppResource/   # Endpoints para la App Móvil
│   │   │   │       │   └── EventoAppResource/   # Endpoints 
│   │   │   └── EventoNotifyResource  # Recibe avisos del Proxy (/api/public/...)
│   │   │   └── resources/            # application-dev.yml (Configuración DB y Cátedra)
│   │   └── test/                     # Tests de integración (EventoSyncServiceDbTest)
│   ├── pom.xml
│   └── src/main/docker/              # Configuración de PostgreSQL local
│
├── proxy-service/                # Intermediario de Mensajería y Caché
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/um/proxy/
│   │   │   │   ├── broker/           
│   │   │   │   │   └── ProxyEventoKafkaConsumer  #  (Escucha a la cátedra)
│   │   │   │   ├── config/           # ProxyProperties y KafkaConfiguration (Resiliencia)
│   │   │   │   └── service/          # Lógica de comunicación
│   │   │   │       └── BackendNotifyService/ # Notifica al Backend por vía pública
│   │   │   └── resources/            # application.yml (Configuración Kafka/Redis)
│   └── pom.xml
│
├── mobile/                       # Aplicación Móvil
│   ├── ComposeApp/                      
│        └── src/commonMain/kotlin/com.um.eventosmovil
│               ├── data
│               ├── service
│               ├── ui
│               │    ├── eventos
│               │    └── login
│               └── viewModel      
│
└── README.md                     # Documentación técnica del sistema completo
```