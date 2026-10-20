export type ChatMessage = {
  content: string;
  messageId: string;
  senderId: string;
  time?: string;
  isDeleted?: boolean;
};

export interface BackendFriendResponse {
  id?: string;
  details?: {
    displayName?: string;
    avatarId?: { val?: string };
  };
}

export interface BackendChatMessage {
  messageId?: string;
  senderId?: string;
  content?: string;
  createdAt?: string;
}

export interface BackendChatResponse {
  chatId: string;
  otherUserId?: string;
  displayName?: string;
  avatarId?: string;
}

export type ChatUser = {
  id: string;
  name: string;
  initial: string;
  // Optional since Krok 8. Every loader used to fill it with a Tailwind class
  // name, which Avatar's inline `backgroundColor` silently ignored (14.3).
  // Leaving it unset lets Avatar fall back to its own primary/on-primary pair.
  color?: string;
  avatarId?: string | null;
  online: boolean;
  status: string;
};
