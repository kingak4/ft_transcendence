*This project has been created as part of the 42 curriculum by [kikwasni](https://github.com/kingak4), [alraltse](https://github.com/alrltgit), [korzecho](https://github.com/Fistxszek), [sandrzej](https://github.com/monandszy), [zslowian](https://github.com/aktyz)*

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

Lead the project through the full development lifecycle while contributing as a developer.

* **Team Organization:** Structured task distribution and hybrid meetings, establishing the shared workspace and cross-functional coordination.
* **Documentation:** Created Confluence articles, architecture diagrams, feature specifications, and visual guides for the project knowledge base. Coordinated documentation and knowledge sharing across the entire team.
* **UI/UX Design:** Designed the application's visual identity and user interface, creating high-fidelity mockups and feature visualizations in Canva. Planned the layout, user flows, and overall user experience to ensure a consistent design across all modules.
* **Design Documentation:** Documented UI concepts, workflows, and feature specifications in Confluence, providing implementation guidelines and maintaining design consistency throughout development.
* **Project Documentation Repository:** Created and maintained the - [Documentation](docs/) folder containing project-related documentation, including UI/UX visualizations, articles, meeting presentations, and progress tracking materials. Organized resources such as design concepts, feature descriptions, project updates, and presentation materials to provide a centralized knowledge base for the team.
* **Chat Module:** Implemented the Real-Time Chat Module feature.
* **Code Quality:** Managed pull requests and conducted code reviews with the Technical Lead.
* **Legal & Compliance:** Drafted the Privacy Policy and integrated the Open-Source License into the repository.

#### 📊 Project Management

Established a structured workflow for team coordination and task execution:

* **Kick-off Meeting:** Defined the application concept, project scope, and allocated team roles through collaborative brainstorming.
* **Task Distribution:** Assigned development tasks based on team members' responsibilities and agreed priorities.
* **Synchronization:** Organized regular full-team and sub-team meetings to monitor progress and resolve blockers.
* **Hybrid Meetings:** Coordinated both on-site sessions at the 42 Warsaw campus and remote meetings.
* **Timeline Management:** Monitored project progress, adapted priorities to meet deadlines, and coordinated scope adjustments when necessary.
* **Design Coordination:** Planned the application's interface and feature designs before implementation, ensuring developers followed a unified vision documented in Confluence.
* **Team Support & Collaboration:** Supported and motivated team members throughout the development process, fostering a positive and collaborative atmosphere. Encouraged open communication, knowledge sharing, and teamwork to maintain strong team engagement and effective cooperation.

### [Szymon](https://github.com/monandszy) — Technical Lead, Software Architect
TODO

### [Alina](https://github.com/alrltgit) — DevOps, Database Engineer

Designed and maintained the project's infrastructure and database layer

#### DevOps

*   **Containerization:** Containerized the backend and frontend applications using Docker.
*   **Infrastructure:** Configured PostgreSQL and the Docker Compose environment for local development.
*   **Environment Management:** Established a shared, idempotent Docker network and coordinated environment configuration across Docker Compose and Makefiles.
*   **Build & Compatibility:** Improved the Docker build process.

#### Database Engineer

*   **Database Design:** Designed and updated the database schema as new features were added.
*   **Persistence:** Integrated PostgreSQL with the backend using Spring Data JPA and implemented the repositories.
*   **Transactions:** Researched and applied transaction management to keep data consistent.
*   **Migrations:** Set up database migrations and managed schema changes over time.
*   **Testing:** Wrote integration tests for the repository layer.
*   **Documentation:** Created and maintained database documentation, including the entity-relationship diagram.


### [Kacper](https://github.com/Fistxszek) — Frontend Developer, API Integration Engineer

Led the initial frontend setup and established core integrations for REST API and WebSocket communication.

*   **Frontend Architecture:** Bootstrapped the Next.js application and development environment, including linters, formatters, and initial HTTPS configuration.
*   **API Integration:** Developed the integration layer between the Next.js frontend and the Spring Boot backend. Implemented the BFF (Backend-for-Frontend) pattern, API web clients, and robust error handling for authentication flows (login/register).
*   **Real-Time Communication:** Integrated WebSocket (STOMP) connections on the client side, enabling real-time chat functionality and live user presence (online/offline) updates.
*   **UI/UX Design:** Designed and developed the foundational layout and aesthetics for the platform's landing page and user profile views.

### [Zyta](https://github.com/aktyz) — Frontend Developer & Designer
Owned the application's visual language and component library, built the identity and social surfaces of the SPA (profile, friends, chat), introduced the project's CI pipeline, and took ownership of the build environments and their verification.

#### 🎨 Design System & Theming

*   **Design System:** Built a semantic design-token layer (`surface`, `on-surface`, `primary`, `elevated-*`, `success`, `danger`) so pages and components consume intention-revealing theme roles instead of hardcoded palette colors — the change that made re-theming possible at all.
*   **Theming:** Implemented a three-way runtime theme switcher (42Hub / Catppuccin Mocha / Catppuccin Latte) driven by a single source of truth in `lib/theme.ts`, with Tailwind's `dark` variant rebound from `prefers-color-scheme` to the theme class and a blocking pre-hydration script that applies the stored flavour before first paint. Adding a fourth flavour is one array entry plus one CSS block — no component changes.
*   **Component Library:** Extracted the component library the tokens imply — primitives (`Button`, `Card`, `TextField`, `AccentLink`, `Tag`, `Avatar`, `PresenceAvatar`) and composites (`SessionCard`, `ContactBlock`, `LegalSection`, `Hero`, `Footer`, `BareLayout`, `ThemeToggle`) — reducing pages to composition and auditing every page to route colors through the token layer.
*   **Route Architecture:** Restructured the app into route groups so unauthenticated routes stop rendering the app sidebar, and inverted dependencies in shared components (`UserSearch` parameterised over its search and action renderers) so they are reusable beyond the flow they were born in.
*   **Visual Identity:** Introduced the 42Hub palette as an additive brand-token layer with dedicated gradient utilities, applied across the app shell, navigation, and CTAs.

#### 👤 Identity & Social Features

*   **User Profile:** Replaced the hardcoded mock profile with a live per-user page — a server component fetching real user details with 401/403/404 handling, inline display-name editing, and avatar upload with modal, file picker, and preview via Next.js Server Actions.
*   **Session Handling:** Wired the authenticated session through the UI entirely server-side (no client-side cookie reads): login/logout flows, an auth-layout guard for already-authenticated visitors, and session-aware sidebar and landing page.
*   **Friends System:** Built the friends feature end-to-end — user search with pagination, add/remove friendships, and a friends list with avatars, names, and live rendering — using Server Actions over the typed `openapi-fetch` client, with profile ownership derived server-side so visitors never see the owner's controls.
*   **Chat UI:** Delivered the chat screen in the new design language (friend rail, conversation, message bubbles, composer) as a presentational split with all placeholder data isolated in a single fixtures module, so the data layer is wired in by replacing one file rather than restructuring components.
*   **State Handling:** Introduced a shared `useAsyncAction` hook to manage loading and error state consistently across friend and profile actions.

#### ⚙️ Build, CI & Environment Engineering

*   **Continuous Integration:** Introduced the project's GitHub Actions CI pipeline, splitting frontend and backend into separate jobs for sound technical reasons: the frontend job brings up the full Docker stack because API types are generated at CI time from live OpenAPI/AsyncAPI endpoints, while the backend job uses native service containers to enable Gradle caching. Added Docker Buildx layer caching and healthcheck-gated startup to remove race-condition flakiness.
*   **Dual Build Profiles:** Established the two supported builds as single-command flows — the Docker dev/eval build (`make`, fully nginx-fronted over HTTPS on port 8443) and the local frontend-development build (`make frontend-local`, host-run `next dev` against a loopback-only backend).
*   **HTTPS Enforcement:** Closed the backend's host-published port so it is reachable exclusively through nginx's TLS termination, satisfying the HTTPS-only requirement.
*   **nginx Routing:** Introduced dynamic upstream resolution and explicit prefix rewriting in nginx, breaking a startup circular dependency between nginx, the frontend image build, and API type generation.
*   **Build Reliability:** Repaired the root-to-service Makefile chain and added content-based staleness tracking, so `make up` rebuilds images only when their build context actually changed. Made the shared Docker network creation idempotent and fixed the environment strategy (`.env` + optional `.env.local`) so local and containerized runs stay consistent.
*   **Automated Verification:** Authored `docs/env_verification.sh` — an end-to-end check of both builds covering HTTPS routes, closed backend ports, loopback isolation, and cross-build leak checks.

## 🔄 Development Lifecycle & Practices

### Workflow
All feature development followed a structured, API-first approach that enabled parallel execution across different technology layers:
* **Requirements & Core Domain:** Feature development began with business requirements defined by the Project Manager (**Kinga**). The Technical Lead (**Szymon**) then implemented the core domain models and use cases in Java, exposing the functionality via REST APIs.
* **Parallel Integration:** Once the API contracts were established, the team split into simultaneous tracks to prevent bottlenecks:
  * **Persistence Layer (Infra):** Implementing database repositories and integrating infrastructure (**Alina**).
  * **Client Layer (Frontend):** Designing the UI components (**Zyta**) and consuming the exposed backend endpoints (**Kacper**).

### Code Quality & Version Control Standards
Integration standards were enforced through an automated CI pipeline combined with team policies and repository configuration:

* **Continuous Integration (GitHub Actions):** Every push and pull request targeting `main` runs a three-job pipeline that goes beyond compilation checks:
  * **Backend tests** — the Gradle test suite executed inside Docker against real PostgreSQL and Redis containers, not mocks.
  * **Dev/eval build verification** — brings up the full containerized stack and asserts the security topology: REST and STOMP endpoints must answer through nginx's HTTPS proxy while the backend's direct ports are confirmed closed. It then generates API types from the live OpenAPI/AsyncAPI specs, lints and builds the Next.js frontend, and verifies the homepage is served end-to-end through nginx.
  * **Local build verification** — validates the frontend developer workflow: the backend must be published on loopback only (`127.0.0.1:5001`, invisible to the network) with health and STOMP endpoints reachable for a host-run dev server.
  * Docker layer, Gradle, and Next.js caches keep full pipeline runs to a few minutes.
* **Automated Formatters & Linters:** Integrated static analysis tools to eliminate stylistic debates, catch logical bugs before runtime, and enforce framework best practices. This allowed peer reviews to focus entirely on architecture and business logic.
* **Strict GitHub Flow:** Adopted an isolated feature-branching strategy. Developers frequently synced with the main branch to preempt massive merge conflicts, submitting all work exclusively through Pull Requests.
* **Conventional Commits:** Enforced structured commit messaging. This provided instant context for changes, streamlined debugging, and prepped the repository for automated release changelogs.
* **Repository Protection & Linear History:** Configured GitHub branch protection rules to disable pushes to main and require peer approvals. All PRs were integrated using "Squash and Merge," ensuring the main branch maintained a clean, readable, and chronological history of deployable features.


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

---

### 🔐 Security, Authentication & Access Control

#### 6. Standard User Management & Authentication (Major — 2pts)
* **Implementation:** Using **Spring Security**, implemented email and password authentication (salted and hashed passwords). Features registration, login flows, profile modifications, and avatar uploads. Incorporates form and user input validation across both the frontend and backend.

* **Reasons:** Securing user identity, preventing invalid data entry, and managing user profiles safely is critical for platform trust and data integrity.

#### 7. Secured Public API (Major — 2pts)
* **Implementation:** Developed a public REST API for database interaction, protected via JWT Tokens. It includes **rate limiting** and is documented following 'living documentation' principles.

* **Reasons:** Exposing a public API safely allows external systems to interact with the platform / build on top of it, while ensuring strict control over traffic load and unauthorized access.

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
