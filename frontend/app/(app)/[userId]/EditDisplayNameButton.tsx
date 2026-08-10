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
      // Colours moved from `on-primary` to white at unit 27, because the hero
      // had become the export's teal-to-mint gradient where dark navy text
      // would be unreadable. Step 5 took the next step: the hero gradient now
      // themes, so "white" is no longer a safe stand-in for "legible on the
      // hero" - `hub-on-accent` is the role that means that, and it inverts
      // with the fill instead of assuming it. Every alpha below is unchanged;
      // only what the alpha is taken off has moved.
      // The sizing matches the display name beside it: 26px at 800, not 30px at 700.
      <form onSubmit={handleSubmit} className="flex flex-col gap-2">
        <input
          type="text"
          value={value}
          onChange={(e) => setValue(e.target.value)}
          maxLength={32}
          autoFocus
          className="bg-hub-on-accent/10 text-hub-on-accent placeholder:text-hub-on-accent/40 focus:ring-hub-on-accent w-full rounded-xl px-3 py-1 text-2xl font-extrabold outline-none focus:ring-1"
        />
        {error && <p className="text-danger text-sm">{error}</p>}
        {/* Same geometry as the trigger they replace - 10x20px at 12px radius,
            14px/700 - so the row does not change shape when editing starts.
            Only the colour roles differ: Cancel keeps the trigger's translucent
            treatment, Save takes a solid fill to read as the committing action. */}
        <div className="flex gap-2">
          <button
            type="button"
            onClick={handleCancel}
            className="border-hub-on-accent/60 bg-hub-on-accent/15 text-hub-on-accent hover:bg-hub-on-accent/25 rounded-xl border px-5 py-2.5 text-sm font-bold transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={isLoading}
            className="bg-elevated-surface text-on-elevated-surface rounded-xl px-5 py-2.5 text-sm font-bold transition-colors hover:brightness-95 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {isLoading ? 'Saving…' : 'Save'}
          </button>
        </div>
      </form>
    );
  }

  return (
    // No longer an `h1`: the route took that at unit 27, and a page with two
    // level-one headings has no outline. The export renders this as a plain
    // 26px/800 line inside the hero - a value, not a section title.
    //
    // The trigger below it is the export's "Change avatar" button, borrowed for
    // the name: 10x20px, 12px radius, 13.5px at 700, on a 15% fill with a 60%
    // edge - which the dictionary already carries as the STRONG variant of
    // "krawędź na tle ciemnym", sourced from this very button. The percentages
    // are the dictionary's; what they are taken off is now `hub-on-accent`
    // rather than `white`, for the reason given in the editing branch above.
    // The avatar keeps its own bubble trigger, so the two affordances stay
    // visually distinct rather than competing.
    //
    // A fragment, not a wrapper: the hero column already spaces its children by
    // 10px, exactly the export's gap between the name and this button.
    <>
      <p className="text-hub-on-accent truncate text-2xl font-extrabold">
        {displayName}
      </p>
      <button
        onClick={handleEdit}
        className="border-hub-on-accent/60 bg-hub-on-accent/15 text-hub-on-accent hover:bg-hub-on-accent/25 self-start rounded-xl border px-5 py-2.5 text-sm font-bold transition-colors"
      >
        Change name
      </button>
    </>
  );
}
