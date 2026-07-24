# WebSocket Hooks Documentation

This folder contains custom React hooks that isolate all the complex logic of communicating with the server via WebSockets (STOMP). Frontend developers and UI designers should use these hooks to easily send and receive data in real-time.

---

## General Rules for Using Hooks

1. **Client Components Only:** These hooks use state, so you must add the `'use client';` directive at the very top of your view file.
2. **Top-Level Declaration:** Always call hooks at the top level of your component. Never inside loops or conditional statements (`if`).
3. **Handle Disconnections:** Every hook provides an `isConnected` flag. Use it to disable buttons (`disabled={!isConnected}`) to prevent actions when the connection is lost.

---

## Available Hooks

### `useChat` & `useChatSubscription`
A complete set of hooks for implementing a real-time chat. To use them properly, you first need to obtain a `chatId` via the REST API.

**Step 1: Obtain a Chat ID (REST API)**
Before connecting to a WebSocket room, create or fetch a chat session via HTTP:
```tsx
// Example POST request to start a chat with another user
const res = await fetch(`/api/chats/${recipientId}`, { method: 'POST' });
const { chatId } = await res.json();
```

**Step 2: Listen for Messages (`useChatSubscription`)**
Use this hook to receive incoming messages (and deletion events) in real-time.
```tsx
const [messages, setMessages] = useState<ChatEventPayload[]>([]);

useChatSubscription(chatId, (event) => {
    // event contains: senderId, messageId, content, time
    // Note: If content is missing, it is a DeleteMessageEvent
    setMessages((prev) => [...prev, event]);
});
```

**Step 3: Send & Delete Messages (`useChat`)**
Use this hook to publish actions to the chat room.
```tsx
const { sendMessage, deleteMessage } = useChat();

// Sending a message
<button onClick={() => sendMessage(chatId, 'Hello!')}>Send</button>

// Deleting a message
<button onClick={() => deleteMessage(chatId, 'msg-id')}>Delete</button>
```

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
