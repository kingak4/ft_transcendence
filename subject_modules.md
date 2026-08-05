# ft_transcendence — Subject Modules Reference

## IV.1 Web

**A. Major:** Use a framework for both the frontend and backend.
- Use a frontend framework (React, Vue, Angular, Svelte, etc.).
- Use a backend framework (Express, NestJS, Django, Flask, Ruby on Rails, etc.).
- Full-stack frameworks (Next.js, Nuxt.js, SvelteKit) count as both if you use both their frontend and backend capabilities.

**B. Minor:** Use a frontend framework (React, Vue, Angular, Svelte, etc.).

**C. Minor:** Use a backend framework (Express, Fastify, NestJS, Django, etc.).

**D. Major:** Implement real-time features using WebSockets or similar technology.
- Real-time updates across clients.
- Handle connection/disconnection gracefully.
- Efficient message broadcasting.

**E. Major:** Allow users to interact with other users. The minimum requirements are:
- A basic chat system (send/receive messages between users).
- A profile system (view user information).
- A friends system (add/remove friends, see friends list).

**F. Major:** A public API to interact with the database with a secured API key, rate limiting, documentation, and at least 5 endpoints:
- GET /api/{something}
- POST /api/{something}
- PUT /api/{something}
- DELETE /api/{something}

**G. Minor:** Use an ORM for the database.

**H. Minor:** A complete notification system for all creation, update, and deletion actions.

**I. Minor:** Real-time collaborative features (shared workspaces, live editing, collaborative drawing, etc.).

**J. Minor:** Server-Side Rendering (SSR) for improved performance and SEO.

**K. Minor:** Progressive Web App (PWA) with offline support and installability.

**L. Minor:** Custom-made design system with reusable components, including a proper color palette, typography, and icons (minimum: 10 reusable components).

**M. Minor:** Implement advanced search functionality with filters, sorting, and pagination.

**N. Minor:** File upload and management system.
- Support multiple file types (images, documents, etc.).
- Client-side and server-side validation (type, size, format).
- Secure file storage with proper access control.
- File preview functionality where applicable.
- Progress indicators for uploads.
- Ability to delete uploaded files.

## IV.2 Accessibility and Internationalization

**A. Major:** Complete accessibility compliance (WCAG 2.1 AA) with screen reader support, keyboard navigation, and assistive technologies.

**B. Minor:** Support for multiple languages (at least 3 languages).
- Implement i18n (internationalization) system.
- At least 3 complete language translations.
- Language switcher in the UI.
- All user-facing text must be translatable.

**C. Minor:** Right-to-left (RTL) language support.
- Support for at least one RTL language (Arabic, Hebrew, etc.).
- Complete layout mirroring (not just text direction).
- RTL-specific UI adjustments where needed.
- Seamless switching between LTR and RTL.

**D. Minor:** Support for additional browsers.
- Full compatibility with at least 2 additional browsers (Firefox, Safari, Edge, etc.).
- Test and fix all features in each browser.
- Document any browser-specific limitations.
- Consistent UI/UX across all supported browsers.

## IV.3 User Management

**A. Major:** Standard user management and authentication.
- Users can update their profile information.
- Users can upload an avatar (with a default avatar if none provided).
- Users can add other users as friends and see their online status.
- Users have a profile page displaying their information.

**B. Minor:** Game statistics and match history (requires a game module).
- Track user game statistics (wins, losses, ranking, level, etc.).
- Display match history (1v1 games, dates, results, opponents).
- Show achievements and progression.
- Leaderboard integration.

  This module requires you to have implemented at least one game (see "Gaming and user experience" section). You cannot claim this module without a functional game.

**C. Minor:** Implement remote authentication with OAuth 2.0 (Google, GitHub, 42, etc.).

**D. Major:** Advanced permissions system:
- View, edit, and delete users (CRUD).
- Roles management (admin, user, guest, moderator, etc.).
- Different views and actions based on user role.

**E. Major:** An organization system:
- Create, edit, and delete organizations.
- Add users to organizations.
- Remove users from organizations.
- View organizations and allow users to perform specific actions within an organization (minimum: create, read, update).

**F. Minor:** Implement a complete 2FA (Two-Factor Authentication) system for the users.

**G. Minor:** User activity analytics and insights dashboard.

## IV.4 Artificial Intelligence

**A. Major:** Introduce an AI Opponent for games.
- The AI must be challenging and able to win occasionally.
- The AI should simulate human-like behavior (not perfect play).
- If you implement game customization options, the AI must be able to use them.
- You must be able to explain your AI implementation during evaluation.

  This module requires you to have implemented at least one game (see "Gaming and user experience" section). The AI must be able to play your game competently.

**B. Major:** Implement a complete RAG (Retrieval-Augmented Generation) system.
- Interact with a large dataset of information.
- Users can ask questions and get relevant answers.
- Implement proper context retrieval and response generation.

**C. Major:** Implement a complete LLM system interface.
- Generate text and/or images based on user input.
- Handle streaming responses properly.
- Implement error handling and rate limiting.

**D. Major:** Recommendation system using machine learning.
- Personalized recommendations based on user behavior.
- Collaborative filtering or content-based filtering.
- Continuously improve recommendations over time.

**E. Minor:** Content moderation AI (auto moderation, auto deletion, auto warning, etc.)

**F. Minor:** Voice/speech integration for accessibility or interaction.

**G. Minor:** Sentiment analysis for user-generated content.

**H. Minor:** Image recognition and tagging system.

## IV.5 Cybersecurity

**A. Major:** Implement WAF/ModSecurity (hardened) + HashiCorp Vault for secrets:
- Configure strict ModSecurity/WAF.
- Manage secrets in Vault (API keys, credentials, environment variables), encrypted and isolated.

## IV.6 Gaming and user experience

**A. Major:** Implement a complete web-based game where users can play against each other.
- The game can be real-time multiplayer (e.g., Pong, Chess, Tic-Tac-Toe, Card games, etc.).
- Players must be able to play live matches.
- The game must have clear rules and win/loss conditions.
- The game can be 2D or 3D.

**B. Major:** Remote players — Enable two players on separate computers to play the same game in real-time.
- Handle network latency and disconnections gracefully.
- Provide a smooth user experience for remote gameplay.
- Implement reconnection logic.

**C. Major:** Multiplayer game (more than two players).
- Support for three or more players simultaneously.
- Fair gameplay mechanics for all participants.
- Proper synchronization across all clients.

  This module requires you to have implemented at least one game (see "Gaming and user experience" section). You're extending your game to support three or more players.

**D. Major:** Add another game with user history and matchmaking.
- Implement a second distinct game.
- Track user history and statistics for this game.
- Implement a matchmaking system.
- Maintain performance and responsiveness.

  This module requires you to have already implemented a first game (see "Implement a complete web-based game" module above). You cannot claim this module without having a functional first game.

**E. Major:** Implement advanced 3D graphics using a library like Three.js or Babylon.js.
- Create an immersive 3D environment.
- Implement advanced rendering techniques.
- Ensure smooth performance and user interaction.

**F. Minor:** Advanced chat features (enhances the basic chat from "User interaction" module).
- Ability to block users from messaging you.
- Invite users to play games directly from chat.
- Game/tournament notifications in chat.
- Access to user profiles from chat interface.
- Chat history persistence.
- Typing indicators and read receipts.

  This module enhances the basic chat system from the "Allow users to interact" module. You cannot claim this module without having implemented the basic chat first.

**G. Minor:** Implement a tournament system.
- Clear matchup order and bracket system.
- Track who plays against whom.
- Matchmaking system for tournament participants.
- Tournament registration and management.

  This module requires you to have implemented at least one game (see "Gaming and user experience" section). You cannot have tournaments without a game to play.

**H. Minor:** Game customization options.
- Power-ups, attacks, or special abilities.
- Different maps or themes.
- Customizable game settings.
- Default options must be available.

  This module requires you to have implemented at least one game (see "Gaming and user experience" section). You're adding customization to an existing game.

**I. Minor:** A gamification system to reward users for their actions.
- Implement at least 3 of the following: achievements, badges, leaderboards, XP/level system, daily challenges, rewards
- System must be persistent (stored in database)
- Visual feedback for users (notifications, progress bars, etc.)
- Clear rules and progression mechanics

  While this is a Minor module (1 point), implementing a complete gamification system can be substantial. Focus on quality over quantity—three well-implemented features are better than six poorly done ones.

**J. Minor:** Implement spectator mode for games.
- Allow users to watch ongoing games.
- Real-time updates for spectators.
- Optional: spectator chat.

  This module requires you to have implemented at least one game (see "Gaming and user experience" section). Spectators need a game to watch.

## IV.7 Devops

**A. Major:** Infrastructure for log management using ELK (Elasticsearch, Logstash, Kibana).
- Elasticsearch to store and index logs.
- Logstash to collect and transform logs.
- Kibana for visualization and dashboards.
- Implement log retention and archiving policies.
- Secure access to all components.

**B. Major:** Monitoring system with Prometheus and Grafana.
- Set up Prometheus to collect metrics.
- Configure exporters and integrations.
- Create custom Grafana dashboards.
- Set up alerting rules.
- Secure access to Grafana.

**C. Major:** Backend as microservices.
- Design loosely-coupled services with clear interfaces.
- Use REST APIs or message queues for communication.
- Each service should have a single responsibility.

**D. Minor:** Health check and status page system with automated backups and disaster recovery procedures.

## IV.8 Data and Analytics

**A. Major:** Advanced analytics dashboard with data visualization.
- Interactive charts and graphs (line, bar, pie, etc.).
- Real-time data updates.
- Export functionality (PDF, CSV, etc.).
- Customizable date ranges and filters.

**B. Minor:** Data export and import functionality.
- Export data in multiple formats (JSON, CSV, XML, etc.).
- Import data with validation.
- Bulk operations support.

**C. Minor:** GDPR compliance features.
- Allow users to request their data.
- Data deletion with confirmation.
- Export user data in a readable format.
- Confirmation emails for data operations.

## IV.9 Blockchain

**A. Major:** Store tournament scores on the Blockchain.
- Use Avalanche and Solidity smart contracts on a test blockchain.
- Implement smart contracts to record, manage, and retrieve tournament scores.
- Ensure data integrity and immutability.

**B. Minor:** Use ICP (Internet Computer Protocol) for a backend that runs on a blockchain (incompatible with SSR).

## IV.10 Modules of choice

**A. Major:** Implement a custom module that is not listed above.
- The module must be substantial and demonstrate technical complexity.
- You must provide proper justification in your README.md explaining:
  - Why you chose this module.
  - What technical challenges it addresses.
  - How it adds value to your project.
  - Why it deserves Major module status (2 points).
- Taking shortcuts or implementing trivial features will result in rejection.
- Be creative and think outside the box.
- The module should be relevant to your project context.

**B. Minor:** Same as the major module but smaller in scope and less complex.
- Must still demonstrate technical skill and creativity.
- Should add meaningful value to your project.
- Requires justification in README.md (similar to Major, but for 1 point).
