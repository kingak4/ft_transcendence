import { client } from './api-clients';

export async function assertValidSession(): Promise<boolean> {
  try {
    const { response } = await client.GET('/auth/session');

    if (response.status === 401 || response.status === 403) {
      return false;
    }

    return true;
  } catch {
    // If the backend is down (e.g. ECONNREFUSED), fetch will throw an exception.
    // We do not want to log the user out due to a temporary server outage.
    // We return true to render the page and let the ConnectionBanner
    // show a "no connection" message on the client side.
    return true;
  }
}
