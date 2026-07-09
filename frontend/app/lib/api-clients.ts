import createClient from 'openapi-fetch';
import type { paths } from '../types/api';

export const client = createClient<paths>({
  baseUrl: typeof window === 'undefined' ? process.env.BACKEND_URL : '/api',
  headers: {
    Accept: 'application/json',
  },
});

client.use({
  async onRequest({ request }) {
    if (typeof window === 'undefined') {
      try {
        // Import cookies dynamically — only available on the server
        const { cookies } = await import('next/headers');
        const cookieStore = await cookies();
        const token = cookieStore.get('auth_token')?.value;

        if (token) {
          request.headers.set('Authorization', `Bearer ${token}`);
        }
      } catch {
        console.warn('Cookies not available in this context');
      }
    }
    return request;
  },
});
