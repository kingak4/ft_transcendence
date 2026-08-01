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

export type Friend = {
  id: string;
  name: string;
  initial: string;
  color: string;
  avatarId?: string | null;
  online: boolean;
  status: string;
};
