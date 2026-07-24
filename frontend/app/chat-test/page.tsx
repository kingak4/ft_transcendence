"use client";

import { useState, useEffect } from 'react';
import { useChat, useChatSubscription, ChatEventPayload } from '@/app/hooks/useChat';
import { client } from '@/app/lib/api-clients';
import { getUserId } from '@/app/lib/getUserId';

export default function ChatTestPage() {
    interface UserDetails {
        displayName: string;
        avatarId: string | null;
    }

    const [messages, setMessages] = useState<ChatEventPayload[]>([]);
    const [userCache, setUserCache] = useState<Record<string, UserDetails>>({});
    const [inputValue, setInputValue] = useState('');
    const [myUserId, setMyUserId] = useState('');
    const [recipientId, setRecipientId] = useState('');
    const [isCreating, setIsCreating] = useState(false);
    const [chatId, setChatId] = useState('');
    const [availableChats, setAvailableChats] = useState<string[]>([]);

    // Odczytanie user_id serwerowo (jeśli ciasteczko jest HttpOnly)
    useEffect(() => {
        getUserId().then((id) => {
            if (id) setMyUserId(id);
        }).catch(err => console.error("Błąd podczas pobierania user_id z cookies", err));
    }, []);

    // Fetch available chats on mount
    useEffect(() => {
        const fetchChats = async () => {
            try {
                const { data, response } = await client.GET('/chats', {
                    params: { query: { page: 0, size: 100 } }
                });
                if (response.ok && data) {
                    setAvailableChats(data.map((chat: any) => chat.chatId));
                }
            } catch (e) {
                console.error('Błąd pobierania listy czatów:', e);
            }
        };
        fetchChats();
    }, []);

    const { sendMessage } = useChat();

    const handleCreateChat = async () => {
        if (!recipientId.trim()) return;
        setIsCreating(true);
        try {
            const { data, response } = await client.POST('/chats/{recipientId}', {
                params: { path: { recipientId } }
            });
            if (response.ok && data) {
                setChatId(data.chatId as string);
                setMessages([]); // czyścimy wiadomości przy zmianie pokoju
            } else {
                alert(`Błąd tworzenia czatu: HTTP ${response.status}`);
            }
        } catch (e) {
            alert('Wystąpił błąd przy tworzeniu czatu (sprawdź konsolę)');
            console.error(e);
        } finally {
            setIsCreating(false);
        }
    };

    // Pobieranie starych wiadomości przy wejściu do pokoju
    useEffect(() => {
        if (!chatId || chatId.length < 10) return;

        const fetchHistory = async () => {
            try {
                const { data, response } = await client.GET('/chats/{chatId}/messages', {
                    params: { path: { chatId }, query: { page: 0, size: 50 } }
                });
                if (response.ok && data) {
                    // Mapowanie odpowiedzi z backendu (MessageResponse) na format STOMP (ChatEventPayload)
                    const historyMessages: ChatEventPayload[] = data.map((msg: any) => ({
                        messageId: msg.messageId,
                        senderId: msg.senderId,
                        content: msg.content,
                        time: msg.createdAt
                    })).reverse(); // backend prawdopodobnie zwraca od najnowszych
                    setMessages(historyMessages);
                } else {
                    console.error('Nie udało się pobrać historii czatu');
                }
            } catch (e) {
                console.error('Błąd pobierania historii:', e);
            }
        };

        fetchHistory();
    }, [chatId]);

    // Nasłuchujemy na przychodzące wiadomości z danego chatId
    useChatSubscription(chatId, (newMessage) => {
        setMessages((prev) => [...prev, newMessage]);
    });

    const handleSend = (e: React.FormEvent) => {
        e.preventDefault();
        if (inputValue.trim() === '') return;

        sendMessage(chatId, inputValue);
        setInputValue('');
    };

    // Pobieranie szczegółów użytkowników, których jeszcze nie znamy
    useEffect(() => {
        const unknownUserIds = [...new Set(messages.map(m => m.senderId))].filter(id => id && id.trim().length > 10 && !userCache[id]);

        unknownUserIds.forEach(async (id) => {
            try {
                const { data, response } = await client.GET('/users/{userId}/details', {
                    params: { path: { userId: id } }
                });
                if (response.ok && data) {
                    setUserCache(prev => ({
                        ...prev,
                        [id]: {
                            displayName: data.displayName as string,
                            avatarId: data.avatarId as string | null
                        }
                    }));
                }
            } catch (e) {
                console.error(`Błąd pobierania danych usera ${id}`, e);
            }
        });
    }, [messages, userCache]);

    return (
        <div className="flex flex-col h-[calc(100vh-4rem)] max-w-4xl mx-auto p-4 md:p-8">
            <div className="mb-6 space-y-4">
                <h1 className="text-3xl font-semibold tracking-tight text-zinc-900">Testowy Panel Czatu</h1>

                <div className="flex flex-col md:flex-row gap-4 p-4 bg-zinc-50 border border-zinc-200 rounded-xl">
                    <div className="flex-1 space-y-2">
                        <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">Krok 1: Utwórz nowy czat</label>
                        <div className="flex gap-2">
                            <input
                                type="text"
                                value={recipientId}
                                onChange={(e) => setRecipientId(e.target.value)}
                                placeholder="ID Odbiorcy (UUID)"
                                className="flex-1 px-3 py-2 text-sm border border-zinc-200 rounded-lg focus:ring-2 focus:ring-zinc-900/10 outline-none"
                            />
                            <button
                                onClick={handleCreateChat}
                                disabled={isCreating || recipientId.trim() === ''}
                                className="px-4 py-2 bg-zinc-900 text-white text-sm font-medium rounded-lg hover:bg-zinc-800 disabled:opacity-50"
                            >
                                {isCreating ? 'Tworzenie...' : 'Utwórz'}
                            </button>
                        </div>
                    </div>

                    <div className="flex-1 space-y-2">
                        <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">Krok 2: Aktywny Pokój (Nasłuch)</label>
                        {availableChats.length > 0 && (
                            <select
                                value={availableChats.includes(chatId) ? chatId : ''}
                                onChange={(e) => {
                                    if (e.target.value) {
                                        setChatId(e.target.value);
                                        setMessages([]);
                                    }
                                }}
                                className="w-full px-3 py-2 text-sm border border-zinc-200 bg-white rounded-lg focus:ring-2 focus:ring-zinc-900/10 outline-none mb-2"
                            >
                                <option value="" disabled>Wybierz czat z listy</option>
                                {availableChats.map((id) => (
                                    <option key={id} value={id}>Czat: {id}</option>
                                ))}
                            </select>
                        )}
                        <input
                            type="text"
                            value={chatId}
                            onChange={(e) => {
                                setChatId(e.target.value);
                                setMessages([]); // czyścimy wiadomości przy ręcznej zmianie
                            }}
                            placeholder="Lub wklej UUID ręcznie"
                            className="w-full px-3 py-2 text-sm border border-zinc-200 bg-white rounded-lg focus:ring-2 focus:ring-zinc-900/10 outline-none font-mono mb-2"
                        />
                    </div>
                </div>
            </div>

            {/* Kontener na wiadomości */}
            <div className="flex-1 bg-white border border-zinc-200 rounded-2xl shadow-sm overflow-hidden flex flex-col">
                <div className="flex-1 p-6 overflow-y-auto flex flex-col gap-4">
                    {messages.length === 0 ? (
                        <div className="h-full flex items-center justify-center text-zinc-400 text-sm">
                            Brak wiadomości. Napisz coś jako pierwszy!
                        </div>
                    ) : (
                        messages.map((msg, index) => {
                            // Zmieniamy logikę: jeśli użytkownik podał swoje ID, sprawdzamy z nim.
                            // W przeciwnym razie wszystkie traktujemy jako 'otrzymane z serwera' (szare/lewe),
                            // żeby uniknąć pomyłek, gdy ktoś symuluje drugą osobę z innej karty.
                            const isMe = myUserId ? msg.senderId === myUserId : false;
                            const userDetails = userCache[msg.senderId];
                            const displayName = userDetails?.displayName || msg.senderId;

                            return (
                                <div
                                    key={msg.messageId || index}
                                    className={`animate-in fade-in slide-in-from-bottom-2 duration-300 w-max max-w-[75%] p-4 flex flex-col ${isMe
                                            ? 'bg-blue-600 text-white rounded-2xl rounded-tr-sm self-end'
                                            : 'bg-zinc-100 text-zinc-800 rounded-2xl rounded-tl-sm self-start'
                                        }`}
                                >
                                    <div className={`flex items-center gap-2 mb-2 ${isMe ? 'flex-row-reverse' : 'flex-row'}`}>
                                        {userDetails?.avatarId ? (
                                            <img
                                                src={`/api/users/avatar/${userDetails.avatarId}`}
                                                alt="Avatar"
                                                className="w-6 h-6 rounded-full object-cover shrink-0 bg-white"
                                            />
                                        ) : (
                                            <div className={`w-6 h-6 rounded-full shrink-0 flex items-center justify-center text-[10px] font-bold ${isMe ? 'bg-blue-500 text-blue-100' : 'bg-zinc-300 text-zinc-500'}`}>
                                                ?
                                            </div>
                                        )}
                                        <span className={`block text-xs font-medium ${isMe ? 'text-blue-200' : 'text-zinc-500'}`}>
                                            {displayName}
                                        </span>
                                    </div>
                                    <p className="text-sm leading-relaxed">
                                        {msg.content}
                                    </p>
                                </div>
                            );
                        })
                    )}
                </div>

                {/* Formularz wejściowy */}
                <form
                    onSubmit={handleSend}
                    className="p-4 border-t border-zinc-100 bg-zinc-50/50 flex gap-3"
                >
                    <input
                        type="text"
                        value={inputValue}
                        onChange={(e) => setInputValue(e.target.value)}
                        placeholder="Napisz wiadomość..."
                        className="flex-1 px-4 py-3 bg-white border border-zinc-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-zinc-900/10 focus:border-zinc-400 transition-all text-sm"
                    />
                    <button
                        type="submit"
                        disabled={inputValue.trim() === ''}
                        className="px-6 py-3 bg-zinc-900 text-white font-medium text-sm rounded-xl hover:bg-zinc-800 focus:outline-none focus:ring-2 focus:ring-zinc-900/20 disabled:opacity-50 disabled:cursor-not-allowed transition-all active:scale-95"
                    >
                        Wyślij
                    </button>
                </form>
            </div>
        </div>
    );
}
