'use client';

import Image from 'next/image';
import { useEffect, useRef, useState } from 'react';
import { useRouter } from 'next/navigation';

import Avatar from '../../components/Avatar';
import Button from '../../components/Button';
import Dialog from '../../components/Dialog';
import { avatarFileSchema } from '../../lib/validation/profile';
import { uploadAvatarAction } from './actions';
import { AVATAR_RING_CLASSES } from './avatarRing';

interface Props {
  avatarId: string | undefined;
  displayName: string;
}

export default function EditAvatarButton({ avatarId, displayName }: Props) {
  const router = useRouter();
  const fileInputRef = useRef<HTMLInputElement>(null);
  const avatarPreviewRef = useRef<string | null>(null);

  const [isOpen, setIsOpen] = useState(false);
  const [avatarFile, setAvatarFile] = useState<File | null>(null);
  const [avatarPreview, setAvatarPreview] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  function handleOpen() {
    setAvatarFile(null);
    setAvatarPreview(null);
    setError(null);
    if (fileInputRef.current) fileInputRef.current.value = '';
    setIsOpen(true);
  }

  function handleClose() {
    setIsOpen(false);
  }

  function handleFileChange(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;
    const result = avatarFileSchema.safeParse(file);
    if (!result.success) {
      setAvatarFile(null);
      setAvatarPreview(null);
      setError(result.error.issues[0].message);
      return;
    }
    setError(null);
    if (avatarPreviewRef.current) URL.revokeObjectURL(avatarPreviewRef.current);
    const url = URL.createObjectURL(file);
    avatarPreviewRef.current = url;
    setAvatarFile(file);
    setAvatarPreview(url);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!avatarFile) {
      setError('Please select a photo first.');
      return;
    }

    setIsSubmitting(true);
    setError(null);

    try {
      const formData = new FormData();
      formData.append('file', avatarFile);
      const result = await uploadAvatarAction(formData);

      if (!result.success) {
        setError(result.message);
        return;
      }

      setIsOpen(false);
      router.refresh();
    } finally {
      setIsSubmitting(false);
    }
  }

  const currentSrc = avatarId ? `/api/users/avatar/${avatarId}` : null;

  useEffect(() => {
    return () => {
      if (avatarPreviewRef.current)
        URL.revokeObjectURL(avatarPreviewRef.current);
    };
  }, []);

  return (
    <>
      <div className={`relative shrink-0 ${AVATAR_RING_CLASSES}`}>
        <Avatar src={currentSrc} alt={`${displayName}'s avatar`} size={96} />
        <button
          type="button"
          onClick={handleOpen}
          aria-label="Edit avatar"
          className="bg-surface text-on-surface absolute bottom-0 right-0 rounded-full p-1.5 text-xs font-semibold shadow-[0_4px_14px_var(--color-shadow-subtle)] transition hover:brightness-90"
        >
          Edit
        </button>
      </div>

      <Dialog
        open={isOpen}
        onClose={handleClose}
        title="Change avatar"
        description="Pick a new photo for your profile."
        dismissible={!isSubmitting}
      >
        <form onSubmit={handleSubmit} className="flex flex-col gap-5">
          <div className="flex flex-col items-center gap-2">
            <button
              type="button"
              onClick={() => fileInputRef.current?.click()}
              className="bg-on-elevated-surface/10 relative h-24 w-24 overflow-hidden rounded-full transition-opacity hover:opacity-75"
              title="Click to choose a photo"
            >
              {avatarPreview ? (
                <Image
                  src={avatarPreview}
                  alt="Preview"
                  width={96}
                  height={96}
                  unoptimized
                  className="h-full w-full object-cover"
                />
              ) : currentSrc ? (
                <Image
                  src={currentSrc}
                  alt="Current avatar"
                  width={96}
                  height={96}
                  unoptimized
                  className="h-full w-full object-cover"
                />
              ) : (
                <span className="text-muted flex h-full w-full items-center justify-center text-xs">
                  Add photo
                </span>
              )}
            </button>
            <p className="text-muted text-xs">Click to choose a photo</p>
            <input
              ref={fileInputRef}
              type="file"
              accept="image/*"
              className="hidden"
              onChange={handleFileChange}
            />
          </div>

          {error && (
            <p role="alert" className="text-danger text-sm">
              {error}
            </p>
          )}

          <div className="flex gap-3">
            <Button
              variant="neutral"
              onClick={handleClose}
              disabled={isSubmitting}
              className="flex-1"
            >
              Cancel
            </Button>
            <Button type="submit" disabled={isSubmitting} className="flex-1">
              {isSubmitting ? 'Saving…' : 'Save'}
            </Button>
          </div>
        </form>
      </Dialog>
    </>
  );
}
