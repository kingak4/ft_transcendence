import { cookies } from 'next/headers';
import { NextResponse } from 'next/server';

export async function GET(request: Request) {
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
