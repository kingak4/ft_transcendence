*This project has been created as part of the 42 curriculum by [kikwasni](https://github.com/kingak4), [alraltse](https://github.com/alrltgit), [korzecho](https://github.com/Fistxszek), [sandrzej](https://github.com/monandszy), [zslowian](https://github.com/aktyz)*

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
- [frontend/.env](frontend/.env) and [frontend/.env.example](frontend/.env.example)

## ▶️ Instructions

From the repository root, start the application on dev profile with `make up`. 

Components can be started separately (on dev or local profiles) using makefiles in infra, backend, or frontend folders (see supporting documentation).

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
* **Project Documentation Repository:** Created and maintained the [Documentation](docs/) folder containing project-related documentation, including UI/UX visualizations, articles, meeting presentations, and progress tracking materials. Organized resources such as design concepts, feature descriptions, project updates, and presentation materials to provide a centralized knowledge base for the team.

  Documentation resources include:
  * [UI/UX Design Concepts & Visualizations](docs/42Hub%20UIUX%20design%20upgrade)
  * [Articles & Technical Notes](docs/Articles)
  * [Meeting Presentations](docs/Meetings-archive)
  * [UI/UX Archive](docs/UIUX-archive)
  * [Forum Archive](docs/Forum-archive)
  * [Progress Tracker](docs/README-progress-tracker)

* **Chat Module:** Implemented the Real-Time Chat Module feature.
* **UI/UX Implementation Support:** Created high-fidelity mockups for the entire application and participated in the process of translating the designed interfaces into the frontend implementation. Collaborated with the frontend team to ensure consistency between the proposed design, user flows, and the final application interface. Related documentation: [Frontend Design Integration](docs/Frontend-Design-Integration).
* **Code Quality:** Managed pull requests and conducted code reviews with the Technical Lead.
* **Legal & Compliance:** Created and integrated the Privacy Policy and Terms of Service pages, drafted the required legal documentation, and integrated the Open-Source License into the repository.

#### 📊 Project Management

Established a structured workflow for team coordination and task execution:

* **Kick-off Meeting:** Defined the application concept, project scope, and allocated team roles through collaborative brainstorming.
* **Task Distribution:** Assigned development tasks based on team members' responsibilities and agreed priorities.
* **Synchronization:** Organized regular full-team and sub-team meetings to monitor progress and resolve blockers.
* **Hybrid Meetings:** Coordinated both on-site sessions at the 42 Warsaw campus and remote meetings.
* **Timeline Management:** Monitored project progress, adapted priorities to meet deadlines, and coordinated scope adjustments when necessary.
* **Design Coordination:** Planned the application's interface and feature designs before implementation, ensuring developers followed a unified vision documented in Confluence.
* **Team Support & Collaboration:** Supported and motivated team members throughout the development process, fostering a positive and collaborative atmosphere. Encouraged open communication, knowledge sharing, and teamwork to maintain strong team engagement and effective cooperation.

**Tools:** Jira (task tracking), Confluence (project documentation, architecture & design specifications), Canva (UI/UX mockups and feature visualizations), Slack (project communication), Messenger (rapid team updates).

### [Szymon](https://github.com/monandszy) — Technical Lead, Software Architect
Managed the technical side of the project during initial stages of development. Defined the technology stack and standards.

#### Individual Contributions
- Architectured a Hexagonal spring boot backend following SOA, DDD, UseCase centric design, separation of Concerns and CQRS, enabling development consistent with the Open closed principle.
- Oversaw frontend backend JWT token integration, for stateless Rest API and STOMP websocket AsyncApi, allowing for secure token isolation and permission management with Spring Security.
- Documented the backend according to the Docs as Code, Self Documenting Code, and Living Documentation principles.
- Utilized gradle plugins and test pipelines for diagram and asciidoc generation, providing insight into the architecture and state of the backend.
- Tested the backend in accordance with TDD and BDD, utilized Spring context splitting.
- Integrated Spring WebSockets with Redis.


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
*   **Chat implementation:** Developed the chat logic based on STOMP and HTTPS backend endpoints, with sending and removing messeges, creating conversations with friends and error handling when backend connection breaks.

### [Zyta](https://github.com/) — Product Owner, Frontend Developer & Designer
Owned the application's visual language and component library, built the identity and social surfaces of the SPA (profile, friends, chat), introduced the project's CI pipeline, and took ownership of the build environments and their verification. In addition to her design and frontend engineering work, she served as Product Owner for the workstreams she led, guiding the design system, theming, and social-feature direction.

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


## 🛠️ Technical Stack

### Spring Boot Backend
was the backend of choice due to Szymon's pre-exiting knowledge of the technology.

### Next.js Frontend
was the frontend of choice due to Kacper's interest in the technology.

### PostgreSQL and Redis
were the most common open source technologies, and were the go-to choice for integration with Spring boot.

### Nginx
simplest open source router that allowed for SSL certificate configuration

### Makefile orchestration
this custom setup was made to adhere to the Separation of concerns.


## 🗄️ Database Schema
TODO

## ✨ Features List
- GetChatMessages
- GetChats
- ManageMessages
- StartChat
- GetProfile
- ManageFriends
- ReadPresence
- Register
- UpdateAvatar
- UpdatePresence
- Login
- SearchUsers
- UpdateDisplayName

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



---


## 🙋 Individual Contributions

### Kinga

As Project Manager and Developer, Kinga led the project through its full development lifecycle, structuring task distribution, organizing hybrid team meetings, and coordinating cross-functional work between sub-teams. She authored the project's Confluence documentation, architecture diagrams, and feature specifications, and maintained the repository's [Documentation](docs/) folder as the team's centralized knowledge base. She designed the application's visual identity and high-fidelity mockups in Canva, supported their translation into the frontend implementation, and implemented the Real-Time Chat Module. She also managed pull requests and code reviews with the Technical Lead, and drafted and integrated the Privacy Policy, Terms of Service, and Open-Source License.

### Zyta

As Frontend Developer, Designer, and Product Owner, Zyta owned the application's visual language, building a semantic design-token layer and a three-way runtime theme switcher (42Hub / Catppuccin Mocha / Catppuccin Latte) that made consistent re-theming possible across the platform. She extracted the shared component library and restructured the app's route architecture, and delivered the identity and social surfaces of the SPA — the live user profile page, server-side session handling, the end-to-end friends system, and the redesigned chat UI. She also introduced the project's GitHub Actions CI pipeline, established the two supported build profiles, enforced HTTPS-only backend access, and authored the automated environment-verification script, taking ownership of the build environments and their verification.

### Szymon

As Technical Lead and Software Architect, Szymon managed the technical side of the project during its initial stages and defined the technology stack and standards. He architected a Hexagonal Spring Boot backend following SOA, DDD, use-case-centric design, separation of concerns, and CQRS, and oversaw the JWT token integration between frontend and backend for the stateless REST API and STOMP WebSocket AsyncAPI. He documented the backend according to Docs as Code, Self-Documenting Code, and Living Documentation principles, used Gradle plugins and test pipelines for diagram and AsciiDoc generation, tested the backend following TDD and BDD practices with Spring context splitting, and integrated Spring WebSockets with Redis.

### Kacper

As Frontend Developer and API Integration Engineer, Kacper led the initial frontend setup, bootstrapping the Next.js application along with its linters, formatters, and initial HTTPS configuration. He developed the integration layer between the Next.js frontend and the Spring Boot backend, implementing the Backend-for-Frontend (BFF) pattern, API web clients, and error handling for the authentication flows. He integrated WebSocket (STOMP) connections on the client side to enable real-time chat and live presence updates, designed the foundational layout for the landing page and profile views, and implemented the chat logic for sending and removing messages, creating conversations with friends, and handling backend connection failures.

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

### 🤖 AI-Assisted Learning and Project Support

In line with the project's AI Instructions, AI tools were used by the team strictly as a supporting resource throughout the learning journey, not as a substitute for the team's own understanding, decision-making, or implementation work.

AI assistance was limited to:
* Supporting the team's learning process when exploring unfamiliar technologies, frameworks, and documentation.
* Helping clarify concepts and technical documentation referenced in the Resources section below.
* Supporting the organization of work and planning of tasks alongside the team's project management tools (Jira, Confluence).
* Assisting in the early ideation of interface concepts and mockups, which were then developed and refined by the team in Canva and implemented in the frontend.
* Acting as a general-purpose and domain-specific prompting aid that helped the team build technical and problem-solving skills during development.

All architectural decisions, feature design, code implementation, and integration of functionality were carried out by the project team. Any AI-assisted input was reviewed, tested, and validated by team members and discussed with peers before being relied upon, consistent with the good-practice approach of using AI to reduce repetitive or tedious tasks while ensuring the team could fully explain and take responsibility for the resulting work.

