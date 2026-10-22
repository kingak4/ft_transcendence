'use client';

import { useEffect, useId, useRef, type ReactNode } from 'react';

interface Props {
  open: boolean;
  onClose: () => void;
  title: string;
  description?: string;
  dismissible?: boolean;
  children: ReactNode;
}

export default function Dialog({
  open,
  onClose,
  title,
  description,
  dismissible = true,
  children,
}: Props) {
  const dialogRef = useRef<HTMLDialogElement>(null);
  const titleId = useId();
  const descriptionId = useId();

  // showModal() gives focus trap, Esc-to-close and focus return for free.
  useEffect(() => {
    const dialog = dialogRef.current;
    if (!dialog) return;
    if (open && !dialog.open) dialog.showModal();
    if (!open && dialog.open) dialog.close();
  }, [open]);

  return (
    <dialog
      ref={dialogRef}
      // Best-effort: Chromium ignores preventDefault on a repeated Esc without fresh user activation.
      onCancel={(e) => {
        if (!dismissible) e.preventDefault();
      }}
      onClose={onClose}
      aria-labelledby={titleId}
      aria-describedby={description ? descriptionId : undefined}
      className="bg-elevated-surface text-on-elevated-surface m-auto w-[calc(100%-2rem)] max-w-[440px] rounded-3xl p-6 shadow-[0_25px_60px_rgba(0,0,0,0.35)] backdrop:bg-black/60 backdrop:backdrop-blur-sm lg:p-9"
    >
      <h2 id={titleId} className="mb-1 text-xl font-extrabold">
        {title}
      </h2>
      {description && (
        <p id={descriptionId} className="text-muted mb-6 text-sm font-medium">
          {description}
        </p>
      )}
      {children}
    </dialog>
  );
}
