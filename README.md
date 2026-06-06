*This project has been created as part of the 42 curriculum by [kikwasni](https://github.com/kingak4), [alraltse](https://github.com/alrltgit), [korzecho](https://github.com/Fistxszek), [sandrzej](https://github.com/monandszy)*

<p align="center">
  <img src="docs/ft_t.png" alt="Webserv Preview" width="200"/>
</p>

## 🔒 License
This project is licensed under the GNU GPL v3 License.

➡️ [[GNU GPL v3 License](LICENSE.md)]

## 📌 42hub.tech Project Description

The goal is to enable user interaction through real-time communication and community building.

The platform provides three functionalities:
*   **Secure Authentication:** A user management system supporting registration, login, and session handling.
*   **Chat:** A messaging system that allows users to connect and exchange messages in real time.
*   **Community Forums:** A space for users to create discussions, post topics, share knowledge, and engage in threaded conversations.

## ⚙️ Setup

Create and configure the environment files before running the project, either manually or by running `make env` (using the defaults):

- [infra/.env](infra/.env) and [infra/.env.example](infra/.env.example)
- [infra/postgres/.env](infra/postgres/.env) and [infra/postgres/.env.example](infra/postgres/.env.example)
- [infra/redis/.env](infra/redis/.env) and [infra/redis/.env.example](infra/redis/.env.example)
- [backend/.env](backend/.env) and [backend/.env.example](backend/.env.example)
- [backend/transcend/.env](backend/transcend/.env) and [backend/transcend/.env.example](backend/transcend/.env.example)

## ▶️ Instructions

From the repository root, start the application on dev (docker) profile with `make up`. Components can be started separately (on dev or local profiles) using makefiles in infra, backend, or frontend folders (see supporting documentation).

Supporting documentation:

- [Infrastructure README](infra/README.md)
- [Backend README](backend/README.md)
- [Frontend README](frontend/README.md)

Application will be available via nginx proxy on port 8443.

## 👥 Team Information
### [Kinga](https://github.com/kingak4) — Project Manager & Developer

Lead the project through full development lifecycle while contributing as a developer.

*   **Team Organization:** Structured task distribution and hybrid meetings, establishing the shared workspace and cross-functional coordination.
*   **Documentation:** Created Confluence articles, architecture diagrams, and visual guides (Canva) for knowledge base. Coordinated documentation and knowledge sharing across the whole team.
*   **Chat Module:** Implemented the Real-Time Chat Module feature.
*   **Code Quality:** Managed pull requests and conducted code reviews with the Technical Lead.
*   **Legal & Compliance:** Drafted Privacy Policy and integrated Open-Source License into the repository.

#### 📊 Project Management

Established structured workflow for team coordination and task execution:

*   **Kick-off Meeting:** Defined application concept and allocated roles through team brainstorming.
*   **Task Distribution:** Assigned tasks by role and agreed upon collaboratively.
*   **Synchronization:** Held full-team and sub-team meetings throughout the development lifecycle.
*   **Hybrid Meetings:** Conducted in-person sessions at 42 Warsaw campus and online.
*   **Timeline Management:** Monitored project progress and adapted scope based on time constraints, conducting concept pivots when necessary.

**Tools:** Jira (task tracking), Confluence (knowledge base & architecture), Slack (project communication), Messenger (rapid updates).

### [Szymon](https://github.com/monandszy) — Technical Lead, Software Architect
TODO

### [Alina](https://github.com/alrltgit) — DevOps, Database Engineer
TODO

### [Kacper](https://github.com/Fistxszek) — Frontend Developer, API Integration Engineer
TODO

### [Zyta](https://github.com/) — Frontend Developer & Designer
TODO


## 🛠️ Technical Stack
TODO

## 🗄️ Database Schema
TODO

## ✨ Features List
TODO

## 🧩 Modules

### ⚙️ Core Web Infrastructure

#### 1. Framework-Based Architecture (Major — 2pts)
* **Implementation:** Backend built with **Java Spring Boot**; frontend built with **Next.js** as a single-page application (SPA) using a Backend-for-Frontend (BFF) pattern with JWT based authentication.

* **Reasons:** Provides dependency injection, complete frontend-backend separation, secure token management, and enterprise-grade maintainability / future scalability.

#### 2. Microservices Architecture (Major — 2pts)
* **Implementation:** The backend was built using **Spring Modulith** with loosely-coupled modules. Services are documented with **OpenAPI** specifications for synchronous communication and **AsyncAPI** specifications for event-driven messaging. Each module contains independent services adhering to the single responsibility principle. REST endpoints are documented via OpenAPI/Swagger; asynchronous messaging patterns follow AsyncAPI standards for WebSocket and STOMP-based communication.

* **Reasons:** Reduces dependency entanglement, ensures module autonomy, enables independent scaling, facilitates team parallelization, and provides clear API contracts. Modular structure allows straightforward migration to full microservices if needed.

#### 3. Database Object-Relational Mapping (Minor — 1pt)
* **Implementation:** Used **Spring Data JPA (Hibernate)** as the ORM layer for object-to-database mapping.

* **Reasons:** Prevents SQL injection vulnerabilities, maintains type safety, abstracts database schema complexity.

---

### 💬 Communication & Social Features

#### 4. Real-Time WebSockets Module (Major — 2pts)
* **Implementation:** Built a real-time system using **Java WebSockets (STOMP protocol)**. The architecture efficiently broadcasts messages across clients, gracefully handles connection/disconnection lifecycles, and synchronizes system state for live features.

* **Reasons:** Eliminates constant HTTP polling, providing an immersive user experience essential for chatting and live updates.

#### 5. User Interaction & Core Social Systems (Major — 2pts)
* **Implementation:** Developed a suite for user interactions, including:
    * **Chat:** A real-time messaging service to send/receive messages between users.
    * **Profile Page:** Pages to view user-specific information.
    * **Friends System:** Functionality to add/remove friends and view a list of friends with real-time online/offline statuses.

* **Reasons:** Establishes the core pillars for a social platform, ensuring users can communicate with each other and build their network.

#### 6. Organization System for Forums (Major — 2pts)
* **Implementation:** Developed a forum platform, enabling users to create, edit, and delete sub-communities. It manages member lists (adding/removing users) and isolates permissions, allowing users to perform scoped CRUD actions, based on their given Role, only within an assigned organization.

* **Reasons:** Grouping users enables structured sub-communities within the forum space, organizing interactions and related content.


---

### 🔐 Security, Authentication & Access Control

#### 7. Standard User Management & Authentication (Major — 2pts)
* **Implementation:** Using **Spring Security**, implemented email and password authentication (salted and hashed passwords). Features registration, login flows, profile modifications, and avatar uploads. Incorporates form and user input validation across both the frontend and backend.

* **Reasons:** Securing user identity, preventing invalid data entry, and managing user profiles safely is critical for platform trust and data integrity.

#### 8. Secured Public API (Major — 2pts)
* **Implementation:** Developed a public REST API for database interaction, protected via JWT Tokens. It includes strict **rate limiting** and is fully documented following 'living documentation' principles.

* **Reasons:** Exposing a public API safely allows external systems to interact with the platform / build on top of it, while ensuring strict control over traffic load and unauthorized access.

#### 9. Advanced Permissions & Role Management (Major — 2pts)
* **Implementation:** Built a Role-Based Access Control (RBAC) system managing distinct forum roles (Admin, Moderator, User, Guest). Features include per-role frontend UI views and backend action restrictions.

* **Reasons:** Establishes hierarchies to prevent data tampering, enforces authorized data manipulation, and allows moderators/admins to maintain community standards.

---

### 📊 Devops, Monitoring & Observability

#### 10. Centralized Log Management - ELK Stack (Major — 2pts)
TODO

#### 11. Monitoring & Alerting - Prometheus & Grafana (Major — 2pts)
TODO

---

## 📚 Resources

This section contains the official documentation, articles, and video tutorials used during the development of this project, combining standard references with AI-assisted research.

### 📄 Documentation & Articles

#### Backend Framework & Architecture
* [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/index.html) – Foundation for the Java backend.
* [Spring Framework Reference](https://docs.spring.io/spring-framework/reference/index.html) – Documentation for core Spring concepts.
* [Spring Modulith Documentation](https://spring.io/projects/spring-modulith) – Guidelines for building and structuring modular monoliths.
* [Backend-for-Frontend (BFF) Pattern](https://en.wikipedia.org/wiki/BFF) – Architectural explanation of creating dedicated backends tailored for specific frontend applications.

#### Security & Authentication
* [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html) – Implementation guides for Web Security, authentication filters, and authorization.
* [Bcrypt Password Hashing](https://en.wikipedia.org/wiki/Bcrypt) – Details on the cryptographic hash function used for securely storing user passwords with salt.
* [JSON Web Tokens (JWT)](https://en.wikipedia.org/wiki/JSON_Web_Token) – Open standard defining the compact and self-contained way for securely transmitting information.

#### Databases & Infrastructure
* [PostgreSQL Documentation](https://www.postgresql.org/docs/) – Technical manual for managing relational data, schemas, and relational integrity.
* [Redis Documentation](https://redis.io/docs/latest/) – Reference for implementing fast, in-memory caching and session state management.
* [Docker Documentation](https://docs.docker.com/) – Official reference for containerizing the application, managing multi-container setups via Docker Compose, and deployment.

#### Compliance & Legal
* [Securiti.ai: What is a Privacy Policy?](https://securiti.ai/what-is-a-privacy-policy/) – A breakdown of data privacy compliance, regulations (like GDPR/CCPA), and data gathering practices.
* [Usercentrics: Guide to Terms of Service](https://usercentrics.com/guides/terms-of-service/) – Legal guidelines and requirements for implementing user agreements and defining platform liability limits.

### 🎥 Video Tutorials
* [What are Website Policies? (Privacy Policy, Terms of Service, Cookie Policy)](https://www.youtube.com/watch?v=tQmjyEgzrY0) – A practical video guide explaining the distinct differences between website policies and why they are critical for user management.
* [Open Source Licence Types](https://youtu.be/nFU8KoSgEmk?si=CbjTvu-DIdFZb3Tv) – An overview of the 5 main categories of open-source licenses (from public domain to copyleft/GPL), explaining how they impact downstream code and dependencies.