import { client } from './api-clients';

export async function assertValidSession(): Promise<boolean> {
  const { response } = await client.GET('/auth/session');
  
  if (response.status === 401 || response.status === 403) {
    return false;
  }
  
  return true;
}
