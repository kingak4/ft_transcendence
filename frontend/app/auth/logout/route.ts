import { cookies } from 'next/headers';

export async function GET() {
  const cookieStore = await cookies();
  cookieStore.delete('auth_token');
  cookieStore.delete('user_id');

  return new Response(null, {
    status: 302,
    headers: {
      Location: '/',
    },
  });
}
