import { clearAuthCookies } from '@/app/lib/logout';

export async function GET() {
  await clearAuthCookies();

  return new Response(null, {
    status: 302,
    headers: {
      Location: '/',
    },
  });
}
