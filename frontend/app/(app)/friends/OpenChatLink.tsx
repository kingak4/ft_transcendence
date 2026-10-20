import Link from 'next/link';

export default function OpenChatLink({ friendId }: { friendId: string }) {
  return (
    <Link
      href={`/chat?friend=${friendId}`}
      className="bg-primary text-on-primary px-4.5 py-2.25 rounded-[10px] text-sm font-bold transition hover:brightness-125"
    >
      Chat
    </Link>
  );
}
