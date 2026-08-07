import { cookies } from 'next/headers';
import { client } from '../../lib/api-clients';
import Conversation from './Conversation';
import FriendRail from './FriendRail';
import type { Friend } from './types';
import type { components } from '../../types/api';

type FriendResult = components['schemas']['FriendResult'];
type ChatResponse = components['schemas']['ChatResponse'];

export const metadata = {
  title: 'Chat',
};

async function loadFriends(): Promise<Friend[]> {
  const { data, response } = await client.GET('/friends', {
    params: { query: { page: 0, size: 20 } },
  });

  if (!response.ok || !data) {
    return [];
  }

  // Map the response based on the backend structure.
  // Assuming data is a Page<FriendResult> with content array.
  // FriendResult contains id and details (displayName, avatarId).
  return (data?.content ?? []).map((friend: FriendResult) => ({
    id: friend.id ?? '',
    name: friend.details?.displayName ?? 'Unknown User',
    initial: (friend.details?.displayName ?? 'U').charAt(0).toUpperCase(),
    color: 'bg-hub-panel', // Assign a default or dynamic color
    avatarId: friend.details?.avatarId?.val ?? null,
    online: false, // We'll handle this in ChatInterface
    status: 'Offline',
  }));
}

async function loadChats(friends: Friend[]): Promise<{ chatId: string; friend: Friend }[]> {
  const { data, response } = await client.GET('/chats', {
    params: { query: { page: 0, size: 20 } },
  });
  if (!response.ok || !data) return [];

  const chatsArray = data.content ?? [];

  return chatsArray.map((chat: ChatResponse) => {
    const friendId = chat.otherUserId;
    let friend = friendId ? friends.find((f) => f.id === friendId) : null;

    if (!friend) {
      const fallbackId = friendId ?? chat.chatId ?? '';
      const fallbackName = chat.displayName ?? chat.chatId ?? 'Unknown';
      friend = {
        id: fallbackId,
        name: fallbackName,
        initial: fallbackName.charAt(0).toUpperCase(),
        color: 'bg-hub-panel',
        avatarId: chat.avatarId ?? null,
        online: false,
        status: 'Offline',
      };
    }

    return { chatId: chat.chatId ?? '', friend };
  });
}

export default async function ChatPage({
  searchParams,
}: {
  searchParams: Promise<{ friend?: string | string[]; chat?: string | string[] }>;
}) {
  const { friend, chat } = await searchParams;
  const friendId = Array.isArray(friend) ? friend[0] : friend;
  const urlChatId = Array.isArray(chat) ? chat[0] : chat;

  const cookieStore = await cookies();
  const myUserId = cookieStore.get('user_id')?.value ?? null;

  const allFriends = await loadFriends();
  const activeChats = await loadChats(allFriends);

  // Find the active friend either by friendId or by looking up the chatId
  let activeFriend = allFriends.find((f) => f.id === friendId) || null;
  let activeChatId = urlChatId || null;

  if (!activeFriend && activeChatId) {
    const chatSession = activeChats.find((c) => c.chatId === activeChatId);
    if (chatSession) activeFriend = chatSession.friend;
  }

  if (!activeFriend && friendId) {
    // Handle the case where friendId in URL is actually a chatId from the mock
    const chatSession = activeChats.find((c) => c.chatId === friendId || c.friend.id === friendId);
    if (chatSession) {
      activeFriend = chatSession.friend;
      activeChatId = chatSession.chatId;
    }
  }

  if (activeFriend && !activeChatId) {
    const chatSession = activeChats.find((c) => c.friend.id === activeFriend?.id);
    if (chatSession) activeChatId = chatSession.chatId;
  }

  if (allFriends.length === 0) {
    return (
      <div className="bg-hub-panel text-hub-muted flex h-full items-center justify-center rounded-2xl text-sm">
        No friends yet. Add friends to start chatting.
      </div>
    );
  }

  return (
    <div className="border-hub-border flex h-full overflow-hidden rounded-2xl border">
      <FriendRail activeChats={activeChats} allFriends={allFriends} activeFriendId={activeFriend?.id || ''} />
      {activeFriend ? (
        <Conversation friend={activeFriend} initialChatId={activeChatId} myUserId={myUserId} />
      ) : (
        <div className="bg-hub-panel-sunken flex flex-1 items-center justify-center text-sm text-hub-muted">
          Select a chat to start messaging
        </div>
      )}
    </div>
  );
}
