# Finance Dashboard — Distributed Microservices Platform (Full Stack In Progress)

This project is a production-grade financial system built using a microservices architecture.

## 🧩 Microservices Included
- Auth Service
- Customer Service
- Account Service (gRPC)
- Transaction Service
- Analytics Service (Kafka consumer)
- API Gateway (Spring Cloud Gateway)

## ⚙️ Technologies
Java 21 • Spring Boot 3 • PostgreSQL • Kafka • Docker • Maven • gRPC • Spring Cloud Gateway

## 🌉 Architecture Highlights
- JWT authentication & authorization
- gRPC for real-time balance validation between Account and Transaction services
- Kafka event-driven communication for Auth–Customer sync and Transaction–Analytics streaming
- Multi-stage transaction processing with rollback on failure
- Analytics service consuming Kafka streams for real-time financial insights

## 🚀 Full Stack Roadmap (In Progress)
Currently developing a **React front-end** that integrates through the API Gateway to complete the full-stack finance dashboard.

