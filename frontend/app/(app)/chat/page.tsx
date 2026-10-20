import { cookies } from 'next/headers';
import { client } from '../../lib/api-clients';
import Conversation from './Conversation';
import ChatSidebar from './ChatSidebar';
import type { ChatUser } from './types';
import type { components } from '../../types/api';
import { CHAT_DICT } from './dictionary';

type FriendResult = components['schemas']['FriendResult'];
type ChatResponse = components['schemas']['ChatResponse'];

export const metadata = {
  title: 'Chat',
};

async function loadUsers(): Promise<ChatUser[]> {
  const { data, response } = await client.GET('/friends', {
    params: { query: { page: 0, size: 20 } },
  });

  if (!response.ok || !data) {
    return [];
  }

  return (data?.content ?? []).map((friend: FriendResult) => ({
    id: friend.id ?? '',
    name: friend.details?.displayName ?? 'Unknown User',
    initial: (friend.details?.displayName ?? 'U').charAt(0).toUpperCase(),
    avatarId: friend.details?.avatarId?.val ?? null,
    online: false,
    status: 'Offline',
  }));
}

async function loadChats(allUsers: ChatUser[]): Promise<{ chatId: string; user: ChatUser }[]> {
  const { data, response } = await client.GET('/chats', {
    params: { query: { page: 0, size: 20 } },
  });
  if (!response.ok || !data) return [];

  const chatsArray = data.content ?? [];

  return chatsArray.map((chat: ChatResponse) => {
    const userId = chat.otherUserId;
    let user = userId ? allUsers.find((u) => u.id === userId) : null;

    if (!user) {
      const fallbackId = userId ?? chat.chatId ?? '';
      const fallbackName = chat.displayName ?? chat.chatId ?? 'Unknown';
      user = {
        id: fallbackId,
        name: fallbackName,
        initial: fallbackName.charAt(0).toUpperCase(),
        avatarId: chat.avatarId ?? null,
        online: false,
        status: 'Offline',
      };
    }

    return { chatId: chat.chatId ?? '', user };
  });
}

export default async function ChatPage({
  searchParams,
}: {
  searchParams: Promise<{
    friend?: string | string[];
    chat?: string | string[];
  }>;
}) {
  const { friend, chat } = await searchParams;
  const userId = Array.isArray(friend) ? friend[0] : friend;
  const urlChatId = Array.isArray(chat) ? chat[0] : chat;

  const cookieStore = await cookies();
  const myUserId = cookieStore.get('user_id')?.value ?? null;

  const allUsers = await loadUsers();
  const activeChats = await loadChats(allUsers);

  let activeUser = allUsers.find((f) => f.id === userId) || null;
  let activeChatId = urlChatId || null;

  if (!activeUser && activeChatId) {
    const chatSession = activeChats.find((c) => c.chatId === activeChatId);
    if (chatSession) activeUser = chatSession.user;
  }

  if (!activeUser && userId) {
    const chatSession = activeChats.find((c) => c.chatId === userId || c.user.id === userId);
    if (chatSession) {
      activeUser = chatSession.user;
      activeChatId = chatSession.chatId;
    } else {
      const { data: userDetails, response } = await client.GET('/users/{userId}/details', {
        params: { path: { userId: userId } }
      });
      if (response.ok && userDetails) {
        activeUser = {
          id: userId,
          name: userDetails.displayName ?? 'Unknown User',
          initial: (userDetails.displayName ?? 'U').charAt(0).toUpperCase(),
          avatarId: userDetails.avatarId ?? null,
          online: false,
          status: 'Offline',
        };
      }
    }
  }

  if (activeUser && !activeChatId) {
    const chatSession = activeChats.find((c) => c.user.id === activeUser?.id);
    if (chatSession) activeChatId = chatSession.chatId;
  }

  const isFriend = activeUser ? allUsers.some(f => f.id === activeUser?.id) : false;

  return (
    <div className="border-elevated-border flex min-h-0 flex-1 overflow-hidden rounded-2xl border">
      <h1 className="sr-only">Chat</h1>
      <ChatSidebar activeChats={activeChats} allUsers={allUsers} activeUserId={activeUser?.id || ''} myUserId={myUserId} hiddenOnNarrow={!!activeUser} />
      {activeUser ? (
        <Conversation user={activeUser} initialChatId={activeChatId} myUserId={myUserId} isFriend={isFriend} />
      ) : (
        <div className="bg-sunken-surface hidden flex-1 items-center justify-center text-sm text-muted lg:flex">
          {CHAT_DICT.page.selectChatToStart}
        </div>
      )}
    </div>
  );
}
