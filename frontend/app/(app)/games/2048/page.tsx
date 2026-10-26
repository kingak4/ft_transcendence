import { cookies } from 'next/headers';
import Game2048 from './Game2048';

export default async function Page() {
  const cookieStore = await cookies();
  const myUserId = cookieStore.get('user_id')?.value ?? null;

  return <Game2048 myUserId={myUserId} />;
}
