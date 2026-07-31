export type ChatMessage = {
  content: string;
  messageId: string;
  senderId: string;
  time?: string;
  isDeleted?: boolean;
};

export type Friend = {
  id: string;
  name: string;
  initial: string;
  color: string;
  avatarId?: string | null;
  online: boolean;
  status: string;
};
