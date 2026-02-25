# 🏢 API para la gestión de propiedades en alquiler

Una API RESTful creada con Spring Boot y PostgreSQL para gestionar propiedades en alquiler, inquilinos, contratos y pagos.

Este proyecto está diseñado para pequeños propietarios y agencias inmobiliarias que necesitan un mejor control sobre las operaciones de alquiler.

---

## 🚀 Features

- 🏠 Gestión de propiedades (CRUD)
- 👤 Gestión de inquilinos
- 📄 Gestión del ciclo de vida de los contratos
- 💰 Seguimiento de los pagos mensuales
- 📊 Resumen del panel de control (ingresos, pagos pendientes, contratos activos)
- 🔐 Autenticación basada en roles (administrador, propietario, inquilino)
- 🔑 Seguridad basada en JWT (disponible en la siguiente fase)
  
---

## 🛠️ Tech Stack

- Java 17+
- Spring Boot
- Spring Data JPA
- Spring Security
- PostgreSQL
- Maven
- Lombok
- Swagger (OpenAPI)

---

## 📦 Estructura del Proyecto

- controller/
- service/
- repository/
- entity/
- dto/
- mapper/
- security/
- config/
- exception/

---

## 🗄️ Diseño de la base de datos

Entidades principales:

- Usuario
- Propiedad
- Inquilino
- Contrato
- Pago
- Mantenimiento

Relaciones:

- Una propiedad → Varios contratos
- Un contrato → Varios pagos
- Un inquilino → Varios contratos

---

## 🔐  Roles

- ADMINISTRADOR
- PROPIETARIO
- INQUILINO 

---

## 📊 Aspectos destacados de la lógica empresarial

- Una propiedad no puede tener varios contratos activos.
- Los pagos no se pueden duplicar para el mismo período.
- El estado de la propiedad se actualiza automáticamente cuando un contrato se activa.

---

## 🚀 Mejoras futuras

- Generación de contratos en PDF
- Recordatorios de pago por correo electrónico
- Integración de pagos en línea
- Implementación de Docker
- Implementación en la nube (AWS / Render)

---

## 📌 Author

Desarrollado como un proyecto práctico de backend para simular un sistema real de gestión de alquileres SaaS.

