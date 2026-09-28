# ✈️ yeb-trips | Cloud-Native Travel Management REST API

[![Java 21](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://www.oracle.com/java/)
[![Quarkus 3.39.1](https://img.shields.io/badge/Quarkus-3.39.1-blue?style=flat-square&logo=quarkus)](https://quarkus.io/)
[![AWS ECS Fargate](https://img.shields.io/badge/AWS-ECS%20Fargate-FF9900?style=flat-square&logo=amazonaws)](https://aws.amazon.com/ecs/)
[![AWS DynamoDB](https://img.shields.io/badge/AWS-DynamoDB-4053D6?style=flat-square&logo=amazonaws)](https://aws.amazon.com/dynamodb/)
[![JWT RS256](https://img.shields.io/badge/Security-JWT%20RS256-green?style=flat-square)](https://jwt.io/)

**`yeb-trips`** is a high-performance backend microservice for travel, itinerary, and traveler management. Built with **Quarkus 3.39.1** and **Java 21**, it demonstrates modern cloud-native practices, serverless container orchestration on AWS, NoSQL modeling with DynamoDB Enhanced, and asymmetric RS256 JWT security.

---

## 🏛️ System Architecture

                       +-----------------------------------+
                       |            HTTP Client            |
                       +-----------------------------------+
                                         | [ Bearer Token JWT ]
                                         v
                       +-----------------------------------+
                       |          AWS ECS Fargate          |
                       |       (Quarkus - yeb-trips)       |
                       +-----------------------------------+
                         /               |               \
                        v                v                v
             +-------------------+  +----------+  +-----------------+
             | SSM Parameter     |  | DynamoDB |  |  AWS ECR        |
             | Store (RSA Key)   |  | (NoSQL)  |  | (Registry)      |
             +-------------------+  +----------+  +-----------------+

---

## 🛠️ Tech Stack & Key Dependencies

* **Java 21** & **Quarkus 3.39.1**
* **Data & Persistence:** `quarkus-amazon-dynamodb-enhanced`, `quarkus-hibernate-orm-panache`
* **Security:** `quarkus-smallrye-jwt`, `quarkus-smallrye-jwt-build` (RS256 Token issuance/verification)
* **API & Observability:** `quarkus-rest-jsonb`, `quarkus-smallrye-openapi` (Swagger UI), `quarkus-smallrye-health`, `quarkus-smallrye-fault-tolerance`
* **Cloud Infrastructure:** AWS ECS Fargate, AWS ECR, AWS DynamoDB, AWS SSM Parameter Store

---

## 🗄️ NoSQL Data Model (AWS DynamoDB)

| Table Name | Partition Key (PK) | Global Secondary Index (GSI) | Description |
| :--- | :--- | :--- | :--- |
| **`Pessoa`** | `id` | - | Traveler profile management |
| **`Viagem`** | `id` | - | Trip details and participant list |
| **`Roteiro`** | `id` | `viagemId-index` (`viagem_id`) | Itinerary activities mapped to trips |

---

## 🚀 API Endpoints Summary

* **Authentication (`/api/yebTrips/auth`)**: `GET /token/user`, `GET /token/admin`
* **People (`/api/yebTrips/pessoa`)**: `POST /`, `GET /`
* **Trips (`/api/yebTrips/viagem`)**: `POST /`, `GET /`, `GET /{id}`
* **Itineraries (`/api/yebTrips/roteiro`)**: `POST /`, `GET /viagem/{viagemId}`
* **Documentation & Health**: Swagger UI at `/q/swagger-ui/`, Health checks at `/q/health/`