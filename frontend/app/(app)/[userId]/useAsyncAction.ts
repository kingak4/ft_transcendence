import { useState } from 'react';

import type { ActionResult } from './actions';

export function useAsyncAction() {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function run(action: () => Promise<ActionResult>, onSuccess?: () => void) {
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
