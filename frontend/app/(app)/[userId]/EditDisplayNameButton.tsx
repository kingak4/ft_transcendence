'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect, useRef, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';

import { useAsyncAction } from '../../hooks/useAsyncAction';
import {
  displayNameSchema,
  type DisplayNameValues,
} from '../../lib/validation/profile';
import { DISPLAY_NAME_MAX } from '../../lib/validation/rules';

import { updateDisplayNameAction } from './actions';

interface Props {
  displayName: string;
}

export default function EditDisplayNameButton({ displayName }: Props) {
  const router = useRouter();
  const [isEditing, setIsEditing] = useState(false);
  const { isLoading, error, setError, run } = useAsyncAction();
  const {
    register,
    handleSubmit,
    reset,
    setFocus,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(displayNameSchema),
    mode: 'onTouched',
    defaultValues: { displayName },
  });

  const shownError = errors.displayName?.message ?? error;
  const changeButtonRef = useRef<HTMLButtonElement>(null);
  const shouldReturnFocusRef = useRef(false);

  // Move focus with the edit mode: into the field on open, back to the button
  // on close (the form unmounts on Cancel/Save; otherwise focus falls to <body>).
  useEffect(() => {
    if (isEditing) {
      setFocus('displayName');
    } else if (shouldReturnFocusRef.current) {
      shouldReturnFocusRef.current = false;
      changeButtonRef.current?.focus();
    }
  }, [isEditing, setFocus]);

  function handleEdit() {
    reset({ displayName });
    setError(null);
    shouldReturnFocusRef.current = true;
    setIsEditing(true);
  }

  function handleCancel() {
    setIsEditing(false);
    setError(null);
  }

  function handleKeyDown(e: React.KeyboardEvent) {
    if (e.key === 'Escape') handleCancel();
  }

  async function saveDisplayName({ displayName: newName }: DisplayNameValues) {
    await run(
      () => updateDisplayNameAction(newName),
      () => {
        setIsEditing(false);
        router.refresh();
      },
    );
  }

  if (isEditing) {
    return (
      // eslint-disable-next-line jsx-a11y/no-noninteractive-element-interactions -- delegation: catches Esc bubbling up from the field and buttons
      <form
        onSubmit={handleSubmit(saveDisplayName)}
        onKeyDown={handleKeyDown}
        className="flex flex-col gap-2"
      >
        <input
          type="text"
          {...register('displayName')}
          maxLength={DISPLAY_NAME_MAX}
          aria-label="Display name"
          aria-invalid={errors.displayName ? true : undefined}
          aria-describedby={shownError ? 'display-name-error' : undefined}
          className="bg-on-accent/10 text-on-accent placeholder:text-on-accent/40 focus:ring-on-accent w-full rounded-xl px-3 py-1 text-2xl font-extrabold focus:ring-1"
        />
        {shownError && (
          <p id="display-name-error" role="alert" className="text-danger text-sm">
            {shownError}
          </p>
        )}
        <div className="flex gap-2">
          <button
            type="button"
            onClick={handleCancel}
            className="border-on-accent/60 bg-on-accent/15 text-on-accent hover:bg-on-accent/25 rounded-xl border px-5 py-2.5 text-sm font-bold transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={isLoading}
            className="bg-elevated-surface text-on-elevated-surface rounded-xl px-5 py-2.5 text-sm font-bold transition hover:brightness-95 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {isLoading ? 'Saving…' : 'Save'}
          </button>
        </div>
      </form>
    );
  }

  return (
    <>
      <p className="text-on-accent truncate text-2xl font-extrabold">
        {displayName}
      </p>
      <button
        ref={changeButtonRef}
        type="button"
        onClick={handleEdit}
        className="border-on-accent/60 bg-on-accent/15 text-on-accent hover:bg-on-accent/25 self-start rounded-xl border px-5 py-2.5 text-sm font-bold transition-colors"
      >
        Change name
      </button>
    </>
  );
}
