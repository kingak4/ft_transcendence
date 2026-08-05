import { useState } from 'react';

// The contract an action must satisfy to be run by this hook. Declared here
// rather than imported from a route's actions file so the hook stays free of
// any route dependency: TypeScript is structurally typed, so any action whose
// return shape matches can be passed in.
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
