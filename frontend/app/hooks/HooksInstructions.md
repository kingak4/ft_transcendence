# WebSocket Hooks Documentation

This folder contains custom React hooks that isolate all the complex logic of communicating with the server via WebSockets (STOMP). Frontend developers and UI designers should use these hooks to easily send and receive data in real-time.

---

## General Rules for Using Hooks

1. **Client Components Only:** These hooks use state, so you must add the `'use client';` directive at the very top of your view file.
2. **Top-Level Declaration:** Always call hooks at the top level of your component. Never inside loops or conditional statements (`if`).
3. **Handle Disconnections:** Every hook provides an `isConnected` flag. Use it to disable buttons (`disabled={!isConnected}`) to prevent actions when the connection is lost.

---

## Available Hooks

<!-- ### 1. `useChat`
Used to send new messages and delete existing ones in a specific chat.

**How to use:**
```tsx
const { sendMessage, deleteMessage, isConnected } = useChat();

// Sending a message
<button disabled={!isConnected} onClick={() => sendMessage('chat-id', 'Hello!')}>Send</button>

// Deleting a message
<button disabled={!isConnected} onClick={() => deleteMessage('msg-id')}>Delete</button>
``` -->

### `usePresence`
Allows you to listen to whether given users are currently Online, and manually ping the server to check their status.

**How to use:**
```tsx
// Provide an array of user IDs whose statuses you want to track
const { onlineStatus, checkPresence, isConnected } = usePresence(['user-01', 'user-02']);

// Reading from the onlineStatus dictionary
<span>Status: {onlineStatus['user-01'] ? 'Online' : 'Offline'}</span>

// Manual check (pinging the server)
<button disabled={!isConnected} onClick={() => checkPresence('user-01')}>Check Status</button>
```
