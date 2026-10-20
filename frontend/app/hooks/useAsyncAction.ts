import { useState } from 'react';

export type AsyncActionResult =
  | { success: true }
  | { success: false; message: string };

export function useAsyncAction() {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function run(
    action: () => Promise<AsyncActionResult>,
    onSuccess?: () => void,
  ) {
    setIsLoading(true);
    setError(null);
    try {
      const result = await action();
      if (!result.success) {
        setError(result.message);
        return;
      }
      onSuccess?.();
    } finally {
      setIsLoading(false);
    }
  }

  return { isLoading, error, setError, run };
}
