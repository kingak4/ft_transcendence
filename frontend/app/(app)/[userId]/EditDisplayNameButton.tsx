'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

import { useAsyncAction } from '../../hooks/useAsyncAction';

import { updateDisplayNameAction } from './actions';

interface Props {
  displayName: string;
}

export default function EditDisplayNameButton({ displayName }: Props) {
  const router = useRouter();
  const [isEditing, setIsEditing] = useState(false);
  const [value, setValue] = useState('');
  const { isLoading, error, setError, run } = useAsyncAction();

  function handleEdit() {
    setValue(displayName);
    setError(null);
    setIsEditing(true);
  }

  function handleCancel() {
    setIsEditing(false);
    setError(null);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    const trimmed = value.trim();
    if (!trimmed) {
      setError('Display name cannot be empty.');
      return;
    }
    await run(
      () => updateDisplayNameAction(trimmed),
      () => {
        setIsEditing(false);
        router.refresh();
      },
    );
  }

  if (isEditing) {
    return (
      <form onSubmit={handleSubmit} className="mb-4 flex flex-col gap-2">
        <input
          type="text"
          value={value}
          onChange={(e) => setValue(e.target.value)}
          maxLength={32}
          autoFocus
          className="text-on-primary placeholder:text-on-primary/40 focus:ring-on-primary w-full rounded-lg bg-on-primary/10 px-3 py-1 text-3xl font-bold outline-none focus:ring-1"
        />
        {error && <p className="text-sm text-red-400">{error}</p>}
        <div className="flex gap-2">
          <button
            type="button"
            onClick={handleCancel}
            className="text-on-primary/70 rounded-lg bg-on-primary/10 px-3 py-1.5 text-sm font-medium transition-colors hover:bg-on-primary/20"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={isLoading}
            className="bg-inverse-surface text-on-inverse-surface rounded-lg px-3 py-1.5 text-sm font-bold transition-colors hover:brightness-125 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {isLoading ? 'Saving…' : 'Save'}
          </button>
        </div>
      </form>
    );
  }

  return (
    <div className="mb-4 flex items-center gap-3">
      <h1 className="text-on-primary text-3xl font-bold">
        {displayName}
      </h1>
      <button
        onClick={handleEdit}
        className="text-on-primary/50 hover:text-on-primary rounded px-2 py-0.5 text-xs font-medium transition-colors hover:bg-on-primary/10"
      >
        Edit
      </button>
    </div>
  );
}
