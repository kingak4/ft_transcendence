'use client';

import Image from 'next/image';
import { useEffect, useRef, useState } from 'react';
import { useRouter } from 'next/navigation';

import { updateDisplayNameAction, uploadAvatarAction } from './actions';

export default function FirstLoginSetup() {
  const router = useRouter();
  const fileInputRef = useRef<HTMLInputElement>(null);
  const avatarPreviewRef = useRef<string | null>(null);

  const [displayName, setDisplayName] = useState('');
  const [avatarFile, setAvatarFile] = useState<File | null>(null);
  const [avatarPreview, setAvatarPreview] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  function handleFileChange(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;
    if (avatarPreviewRef.current) URL.revokeObjectURL(avatarPreviewRef.current);
    const url = URL.createObjectURL(file);
    avatarPreviewRef.current = url;
    setAvatarFile(file);
    setAvatarPreview(url);
  }

  useEffect(() => {
    return () => { if (avatarPreviewRef.current) URL.revokeObjectURL(avatarPreviewRef.current); };
  }, []);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();

    const trimmed = displayName.trim();
    if (!trimmed) {
      setError('Display name is required.');
      return;
    }

    setIsSubmitting(true);
    setError(null);

    try {
      const nameResult = await updateDisplayNameAction(trimmed);
      if (!nameResult.success) {
        setError(nameResult.message);
        return;
      }

      if (avatarFile) {
        const formData = new FormData();
        formData.append('file', avatarFile);
        const avatarResult = await uploadAvatarAction(formData);
        if (!avatarResult.success) {
          setError(avatarResult.message);
          return;
        }
      }

      // Re-fetch server component data — the modal will disappear because
      // displayName is no longer the default "User".
      router.refresh();
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm">
      <div className="bg-brand-reversed-main-color w-96 rounded-2xl p-8 shadow-xl">
        <h2 className="text-brand-main-color mb-1 text-2xl font-bold">
          Welcome!
        </h2>
        <p className="text-brand-main-color/60 mb-6 text-sm">
          Set up your profile before continuing.
        </p>

        <form onSubmit={handleSubmit} className="flex flex-col gap-5">
          {/* Avatar picker */}
          <div className="flex flex-col items-center gap-2">
            <button
              type="button"
              onClick={() => fileInputRef.current?.click()}
              className="relative h-24 w-24 overflow-hidden rounded-full bg-white/10 transition-opacity hover:opacity-75"
              title="Click to choose an avatar photo"
            >
              {avatarPreview ? (
                <Image
                  src={avatarPreview}
                  alt="Avatar preview"
                  width={96}
                  height={96}
                  unoptimized
                  className="h-full w-full object-cover"
                />
              ) : (
                <span className="text-brand-main-color/40 flex h-full w-full items-center justify-center text-xs">
                  Add photo
                </span>
              )}
            </button>
            <p className="text-brand-main-color/40 text-xs">
              Avatar (optional)
            </p>
            <input
              ref={fileInputRef}
              type="file"
              accept="image/*"
              className="hidden"
              onChange={handleFileChange}
            />
          </div>

          {/* Display name */}
          <div>
            <label className="text-brand-main-color mb-1.5 block text-sm font-medium">
              Display name <span className="text-red-400">*</span>
            </label>
            <input
              type="text"
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              placeholder="Choose a display name"
              className="text-brand-main-color placeholder:text-brand-main-color/40 focus:ring-brand-secondary-color w-full rounded-lg bg-white/10 px-4 py-3 text-sm outline-none focus:ring-1"
              autoFocus
              maxLength={32}
            />
          </div>

          {error && <p className="text-sm text-red-400">{error}</p>}

          <button
            type="submit"
            disabled={isSubmitting}
            className="bg-brand-secondary-color text-brand-additional-color-2 rounded-lg py-3 text-sm font-bold transition-colors hover:brightness-125 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {isSubmitting ? 'Saving…' : 'Save profile'}
          </button>
        </form>
      </div>
    </div>
  );
}
