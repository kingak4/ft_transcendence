*This project has been created as part of the 42 curriculum by [kikwasni](https://github.com/kingak4), [alraltse](https://github.com/alrltgit), [korzecho](https://github.com/Fistxszek), [sandrzej](https://github.com/monandszy)*

<p align="center">
  <img src="docs/ft_t.png" alt="Webserv Preview" width="200"/>
</p>

## 🔒 License
This project is licensed under the GNU GPL v3 License.

➡️ [[GNU GPL v3 License](LICENSE.md)]

## 📌 Description

This application is a comprehensive web platform designed to facilitate user interaction through real-time communication and community building. Built with a robust **Java** backend, the system ensures high performance, scalability, and secure data management. 

The platform revolves around three core pillars:
*   **Secure Authentication:** A reliable user management system supporting secure registration, login, and session handling to protect user data and privacy.
*   **Real-Time Chat Module:** A dynamic messaging system that allows users to connect instantly, exchange messages, and communicate in real time.
*   **Community Forum:** A structured space for users to create discussions, post topics, share knowledge, and engage in threaded conversations.


## ⚙️ Setup

Create and configure the environment files before running the project:

- [infra/.env](infra/.env) and [infra/.env.example](infra/.env.example)
- [infra/postgres/.env](infra/postgres/.env) and [infra/postgres/.env.example](infra/postgres/.env.example)
- [infra/redis/.env](infra/redis/.env) and [infra/redis/.env.example](infra/redis/.env.example)
- [backend/.env](backend/.env) and [backend/.env.example](backend/.env.example)
- [backend/transcend/.env](backend/transcend/.env) and [backend/transcend/.env.example](backend/transcend/.env.example)

Supporting documentation:

- [Infrastructure README](infra/README.md)
- [Backend README](backend/README.md)
- [Frontend README](frontend/README.md)

## ▶️ Run the Project

From the repository root, start the stack on dev (docker) profile with `make up`. Components can be started separately (on dev or local profiles) using makefiles in infra, backend, or frontend folders.

Application will be available via nginx proxy on port 8443.

## 👥 Team Information
#### [Kinga](https://github.com/kingak4) — Project Manager & Developer
Kinga played a pivotal role in the project, driving both the management strategy and hands-on technical development while supporting the team across multiple dimensions.

*   **Project Management & Leadership:** Responsible for overall team organization, facilitating comprehensive task distribution, and coordinating regular hybrid meetings. She successfully established our shared workspace and fostered cross-functional alignment.
*   **Documentation & Knowledge Base:** Authored vital documentation and supportive technical articles within Confluence. She designed and created comprehensive visual guides and project architecture layouts using **Confluence** and **Canva**.
*   **Core Software Development:** Contributed as a developer to the implementation of the **Real-Time Chat Module**, ensuring smooth communication workflows within the application.
*   **Code Quality & Pull Requests:** Actively participated in the code evaluation process, collaborating closely with the Technical Lead to conduct thorough **Code Reviews** and manage incoming **Pull Requests** to maintain high repository standards.
*   **Compliance & Repository Care:** Fully managed the legal and structural setup of the project by drafting the platform's **Privacy Policy** and integrating the official **Open-Source License** into the repository.

## 📊 Project Management
###  Team Organization & Work Distribution
To ensure smooth collaboration and efficient progress, our team established a structured workflow from the very beginning:
* **Kick-off Meeting:** At the start of the project, the entire team gathered to brainstorm, define the core application concept, and allocate team roles based on mutual agreement.
* **Role-Based Task Distribution:** Tasks were assigned strictly in accordance with each member's designated role. Every task assignment was discussed and agreed upon collaboratively, ensuring that everyone was aligned and comfortable with their responsibilities.
* **Adaptive Meetings:** We maintained regular synchronization throughout the development lifecycle. Depending on the current needs, we held either full-team meetings or sub-team sessions focused on specific technical modules or technologies. 
* **Hybrid Collaboration:** Meetings were conducted both in person at the **42 Warsaw campus** and online, ensuring maximum flexibility. Any significant changes or pivots in the project scope were immediately communicated to all team members to keep everyone updated.

### 🛠️ Project Management & Documentation Tools
We utilized industry-standard tools to plan, track, and document our development process:
* **Jira:** Used as our primary agile project management tool. We managed tasks through a Jira board, allowing every team member to clearly see their assignments, deadlines, and the overall project status.
* **Confluence:** Served as our central knowledge base. It was used to gather comprehensive project requirements, ideas, and system architecture details. Our Project Manager (**Kinga**) and Technical Lead (**Szymon**) collaborated here to prepare detailed articles, project documentation, and visual guides (such as the forum module architecture).

### 💬 Communication Channels
To maintain constant alignment and quick feedback loops, we used a mix of professional and rapid communication tools:
* **Slack:** Our main workspace for structured project communication, official updates, and technical discussions.
* **Messenger:** Used for day-to-day, rapid communication, quick synchronization, and urgent notices.

## 🛠️ Technical Stack

## 🗄️ Database Schema

## ✨ Features List

## 🧩 Modules

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

