*This project has been created as part of the 42 curriculum by [kikwasni](https://github.com/kingak4), [alraltse](https://github.com/alrltgit), [korzecho](https://github.com/Fistxszek), [sandrzej](https://github.com/monandszy)*

<p align="center">
  <img src="docs/ft_t.png" alt="Webserv Preview" width="200"/>
</p>

## 🔒 License
This project is licensed under the GNU GPL v3 License.

➡️ [[GNU GPL v3 License](LICENSE.md)]

## 📌 Description

The goal of this web application is to enable user interaction through real-time communication and community building.

The platform provides three functionalities:
*   **Secure Authentication:** A user management system supporting registration, login, and session handling.
*   **Chat:** A messaging system that allows users to connect and exchange messages in real time.
*   **Community Forums:** A space for users to create discussions, post topics, share knowledge, and engage in threaded conversations.

## ⚙️ Setup

Create and configure the environment files before running the project:

- [infra/.env](infra/.env) and [infra/.env.example](infra/.env.example)
- [infra/postgres/.env](infra/postgres/.env) and [infra/postgres/.env.example](infra/postgres/.env.example)
- [infra/redis/.env](infra/redis/.env) and [infra/redis/.env.example](infra/redis/.env.example)
- [backend/.env](backend/.env) and [backend/.env.example](backend/.env.example)
- [backend/transcend/.env](backend/transcend/.env) and [backend/transcend/.env.example](backend/transcend/.env.example)

## ▶️ Run the Project

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

####  Team Organization & Work Distribution
*   **Kick-off Meeting:** Initial team brainstorming to define the application concept and allocate roles.
*   **Role-Based Task Distribution:** Tasks assigned according to each member's role and agreed upon collaboratively.
*   **Regular Synchronization:** Full-team and sub-team meetings held throughout development based on needs.
*   **Hybrid Collaboration:** In-person meetings at 42 Warsaw campus and online sessions; scope changes communicated immediately.

#### 🛠️ Tools
*   **Jira:** Primary project management tool for task tracking and status visibility.
*   **Confluence:** Central knowledge base for requirements, architecture, and documentation.
*   **Slack:** Main workspace for project communication.
*   **Messenger:** Rapid day-to-day synchronization and urgent updates.

### [Szymon](https://github.com/monandszy) — Technical Lead, Software Architect

### [Alina](https://github.com/alrltgit) — DevOps, Data Engineer

### [Kacper](https://github.com/Fistxszek) — Frontend Developer, API Integration Engineer

### [Zyta](https://github.com/) — Forum & Social Systems Developer


## 🛠️ Technical Stack

## 🗄️ Database Schema

## ✨ Features List

## 🧩 Modules

### ⚙️ Core Web Infrastructure

#### 1. Framework-Based Architecture (Major — 2pts)
* **Justification:** Utilizing robust frameworks ensures a secure, maintainable, and scalable enterprise architecture capable of handling decoupled frontend and backend operations.
* **Implementation:** The backend is built using the **Java Spring Boot** framework, providing dependency injection and embedded server management. The frontend is built as a separate single-page application (SPA) interacting via REST APIs.

#### 2. Microservices / Modulith Architecture (Major — 2pts)
* **Justification:** Designing the system with loosely-coupled components guarantees that each service has a single responsibility, reducing dependency entanglements and allowing easier scaling.
* **Implementation:** Implemented using a **Spring Modulith / Microservices** approach. Modules communicate through clearly defined interfaces and lightweight REST APIs, ensuring high autonomy for each domain layer.

#### 3. Database Object-Relational Mapping (Minor — 1pt)
* **Justification:** An ORM abstractly maps code objects to database tables, preventing SQL injection vulnerabilities, maintaining type safety, and speeding up development.
* **Implementation:** Implemented using **Spring Data JPA (Hibernate)** as the ORM layer, managing object states and schema definitions seamlessly.

---

### 💬 Communication & Social Features

#### 4. Real-Time WebSockets Module (Major — 2pts)
* **Justification:** To provide an immersive and modern user experience, chat and updates must happen instantly without constant HTTP polling.
* **Implementation:** Built a custom event-driven system using **Java WebSockets (STOMP protocol)**. The architecture efficiently broadcasts messages across clients, handles connection/disconnection lifecycles gracefully, and keeps system state synchronized.

#### 5. User Interaction & Core Social Systems (Major — 2pts)
* **Justification:** Social connectivity is a core pillar of the platform, requiring a unified system where users can chat and stay updated on their network.
* **Implementation:** * **Chat:** A basic messaging service allowing users to send and receive direct messages.
    * **Profile Page:** A dedicated view displaying user-specific information.
    * **Friends System:** Fully operational add/remove friend workflows, including a real-time view of friends' online/offline statuses.

---

### 🔐 Security, Authentication & Access Control

#### 6. Standard User Management & Authentication (Major — 2pts)
* **Justification:** Securing user identity and managing user profiles safely is critical for platform trust and data integrity.
* **Implementation:** Powered by **Spring Security**. Users can register, log in, modify their personal profiles, and upload custom avatars (with a fallback default avatar system built-in).

#### 7. Advanced Permissions & Role Management (Major — 2pts)
* **Justification:** The forum module requires clear hierarchies to prevent data tampering and ensure moderators can maintain community standards.
* **Implementation:** Created an advanced Role-Based Access Control (RBAC) system. Administrators, Moderators, Users, and Guests have distinct UI views and action permissions. Full CRUD operations on users are restricted to specific authorized roles.

#### 8. Organization System for Forums (Major — 2pts)
* **Justification:** Grouping users into organizations allows structured sub-communities within the forum space.
* **Implementation:** Developed an organization management engine allowing authorized users to create, edit, and delete organizations, manage member lists (adding/removing users), and perform scoped CRUD actions inside specific organizational spaces.

#### 9. Secured Public API (Major — 2pts)
* **Justification:** Exposing a public API allows external systems to interact with our database securely, requiring protection against abuse.
* **Implementation:** Developed a public REST API secured via **API Keys**. It features strict **rate limiting** to prevent DDoS/abuse, and is fully documented adhering to 'self-documenting code' and 'living documentation' principles.

---

### 📊 Devops, Monitoring & Observability

#### 10. Centralized Log Management - ELK Stack (Major — 2pts)
* **Justification:** Essential for tracking system errors, security audits, and behavioral patterns across loosely-coupled modules.
* **Implementation:** *Managed by Szymon.* Implemented a complete infrastructure using **Logstash** to collect and transform application logs, **Elasticsearch** to index and store them securely, and **Kibana** to build dashboards for log visualization. Strict data retention and archiving policies were applied.

#### 11. Monitoring & Alerting - Prometheus & Grafana (Major — 2pts)
* **Justification:** Real-time visibility into system metrics (CPU, memory, request latency) allows the team to fix infrastructure strains before they impact users.
* **Implementation:** *Managed by Alina.* Configured **Prometheus** to scrape system metrics via specialized exporters. Metrics are channeled into custom-built **Grafana dashboards** equipped with automated alerting rules to flag unusual server behavior immediately. Access to Grafana is strictly secured.

---

### 🔢 Point Calculation Summary
According to the project requirements, **Major** modules are worth **2 points** each, and **Minor** modules are worth **1 point** each.

* **Total Chosen Modules:** 9 Major Modules + 1 Minor Module
* **Total Points Earned:** $(9 \times 2) + (1 \times 1) = 19 \text{ points}$

## 👨‍💻 Individual Contributions

## 📚 Resources

This section contains the official documentation, articles, and video tutorials used during the development of this project, combining standard references with AI-assisted research.

### 📄 Documentation & Articles
* [Spring Boot Getting Started Guide](https://spring.io/guides/gs/spring-boot) – Official guide for building and configuring microservices and web applications with Spring Boot.
* [Docker Documentation](https://docs.docker.com/) – Official reference for containerizing the application, managing multi-container setups, and deployment.
* [Securiti.ai: What is a Privacy Policy?](https://securiti.ai/what-is-a-privacy-policy/) – A comprehensive breakdown of data privacy compliance, regulations (like GDPR/CCPA), and privacy practices.
* [Usercentrics: Guide to Terms of Service](https://usercentrics.com/guides/terms-of-service/) – Essential legal guidelines and requirements for implementing user agreements and limiting platform liability.

### 🎥 Video Tutorials
* [What are Website Policies? (Privacy Policy, Terms of Service, Cookie Policy)](https://www.youtube.com/watch?v=tQmjyEgzrY0) – A practical video guide explaining the distinct differences between website policies and why they are critical for user management.
* [Open Source Licence Types](https://youtu.be/nFU8KoSgEmk?si=CbjTvu-DIdFZb3Tv) – An overview of the 5 main categories of open-source licenses (from public domain to copyleft/GPL), explaining how they impact downstream code and dependencies.
* [Spring Boot & Spring Security Architecture](https://www.youtube.com/watch?v=23lJ2YAnlC8&t=893s) – A deep dive into authentication filters, user management, and token handling for securing Java backend systems.
* [Microservices and Modular Architecture Patterns](https://youtu.be/aEFkWxUNAVc?si=VNngJs0vRJBH4wy0) – A guide on design principles, loosely-coupled messaging, and service boundaries in modern distributed systems.
