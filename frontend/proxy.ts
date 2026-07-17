import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

//for local development only
export function proxy(request: NextRequest) {
  const token = request.cookies.get('auth_token')?.value;

  const requestHeaders = new Headers(request.headers);

  if (token) {
    requestHeaders.set('Authorization', `Bearer ${token}`);
  }

  return NextResponse.next({
    request: {
      headers: requestHeaders,
    },
  });
}

export const config = {
  matcher: '/api/:path*',
};
