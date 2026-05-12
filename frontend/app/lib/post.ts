"use server";

import { cookies } from 'next/headers';

export async function postData<TResponse, TBody>(url: string, body: TBody): Promise<TResponse> {
  let response: Response | null = null;
  try {
    response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    });
  }
  catch (error: unknown) {
    if (error instanceof Error)
      throw new Error('Fetch failed.');
  }
  if (response == null)
    throw new Error('Fetch failed.');

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}));
    const errorMessage = errorBody.message || `HTTP error! status: ${response.status}`;

    const error = new Error(errorMessage);
    (error as any).status = response.status;
    throw error;
  };

  return await response.json() as TResponse;
}
