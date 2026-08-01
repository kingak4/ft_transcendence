import { cookies } from 'next/headers';
import { NextResponse } from 'next/server';

export async function GET(request: Request) {
  const cookieStore = await cookies();
  cookieStore.delete('auth_token');
  cookieStore.delete('user_id');

  // Fallback to localhost:3000 if NEXT_PUBLIC_APP_URL is not set
  const baseUrl = process.env.NEXT_PUBLIC_APP_URL || new URL(request.url).origin;
  return NextResponse.redirect(new URL('/', baseUrl));
}
