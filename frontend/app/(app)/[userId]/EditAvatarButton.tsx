'use client';

import Image from 'next/image';
import { useEffect, useRef, useState } from 'react';
import { useRouter } from 'next/navigation';

import { uploadAvatarAction } from './actions';

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
    setIsOpen(true);
  }

  function handleClose() {
    setIsOpen(false);
  }

  function handleFileChange(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;
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
    return () => { if (avatarPreviewRef.current) URL.revokeObjectURL(avatarPreviewRef.current); };
  }, []);

  return (
    <>
      {/* Avatar display + edit trigger */}
      <div className="relative shrink-0">
        {currentSrc ? (
          <Image
            src={currentSrc}
            alt={`${displayName}'s avatar`}
            width={96}
            height={96}
            className="h-24 w-24 rounded-full object-cover"
          />
        ) : (
          <div className="h-24 w-24 rounded-full bg-blue-200" />
        )}
        <button
          onClick={handleOpen}
          className="bg-surface text-on-surface absolute bottom-0 right-0 rounded-full p-1.5 text-xs shadow transition-colors hover:brightness-90"
        >
          Edit
        </button>
      </div>

      {/* Modal */}
      {isOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm">
          <div className="bg-inverse-surface text-on-inverse-surface w-96 rounded-2xl p-8 shadow-xl">
            <h2 className="mb-1 text-2xl font-bold">
              Change avatar
            </h2>
            <p className="text-on-inverse-surface/60 mb-6 text-sm">
              Pick a new photo for your profile.
            </p>

            <form onSubmit={handleSubmit} className="flex flex-col gap-5">
              <div className="flex flex-col items-center gap-2">
                <button
                  type="button"
                  onClick={() => fileInputRef.current?.click()}
                  className="bg-on-inverse-surface/10 relative h-24 w-24 overflow-hidden rounded-full transition-opacity hover:opacity-75"
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
                      className="h-full w-full object-cover"
                    />
                  ) : (
                    <span className="text-on-inverse-surface/40 flex h-full w-full items-center justify-center text-xs">
                      Add photo
                    </span>
                  )}
                </button>
                <p className="text-on-inverse-surface/40 text-xs">
                  Click to choose a photo
                </p>
                <input
                  ref={fileInputRef}
                  type="file"
                  accept="image/*"
                  className="hidden"
                  onChange={handleFileChange}
                />
              </div>

              {error && <p className="text-sm text-red-400">{error}</p>}

              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={handleClose}
                  className="text-on-inverse-surface/70 bg-on-inverse-surface/10 hover:bg-on-inverse-surface/20 flex-1 rounded-lg py-3 text-sm font-medium transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="bg-primary text-on-primary flex-1 rounded-lg py-3 text-sm font-bold transition-colors hover:brightness-125 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {isSubmitting ? 'Saving…' : 'Save'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </>
  );
}
