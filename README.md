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

## 🚀 Inicio Rápido
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
## 🔐 Características Destacadas

Filtrado por Usuario: El historial de compras (/api/app/mis-ventas) utiliza el contexto de seguridad JWT para filtrar los resultados por el login del usuario actual, asegurando que un usuario no pueda ver las ventas de otro.

Sincronización Asincrónica: El Proxy actúa como bridge consumiendo mensajes de Kafka de la cátedra y notificando al backend local vía HTTP POST para disparar el EventoSyncService.

Validación de Sesiones: Gestión de estados de asientos mediante Redis externo para evitar colisiones en compras concurrentes.

Optimización de Datos
Prevención de Recursión: Se implementó @JsonIgnoreProperties en las entidades VentaLocal y AsientoVenta para evitar ciclos infinitos durante la serialización JSON, permitiendo que la App móvil procese la información correctamente.

## 📚 Documentación de la API

Endpoints Clave para la App móvil
GET /api/app/mis-ventas: Historial personal de compras filtrado.

POST /api/app/bloquear: Reserva temporal de asientos en Redis.

POST /api/app/venta: Finalización de compra y persistencia local.
