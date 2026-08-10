'use client';

import Image from 'next/image';
import { useEffect, useRef, useState } from 'react';
import { useRouter } from 'next/navigation';

import Avatar from '../../components/Avatar';
import Button from '../../components/Button';
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
    return () => {
      if (avatarPreviewRef.current)
        URL.revokeObjectURL(avatarPreviewRef.current);
    };
  }, []);

  return (
    <>
      {/* Avatar display + edit trigger. The 3px white ring is the export's, and
          closes a 12.5 entry that offered two routes to it - a prop on `Avatar`
          or a class at the call site. The call site won, on the grounds that the
          ring appears on exactly one avatar in the whole design, so a prop would
          add an API for a single user.
          UPDATE: that premise expired. The very next unit added the non-owner
          branch in page.tsx and there are now two call sites, so the class moved
          to `avatarRing.ts` rather than being duplicated. The prop-versus-class
          question is genuinely reopened by that and belongs to Krok 6, where
          changing `EditAvatarButton`'s API is allowed (§8.7 reguła 3). A
          recorded rationale is a snapshot, and the unit that invalidates it is
          the one that should say so. */}
      <div className={`relative shrink-0 ${AVATAR_RING_CLASSES}`}>
        <Avatar src={currentSrc} alt={`${displayName}'s avatar`} size={96} />
        {/* The bubble stays a bubble. It reads as "this picture" purely by where
            it sits, which a labelled button under the name could not do - and
            the name's own trigger IS that labelled button, so the two edits stay
            distinguishable without reading either label. Weight and shadow come
            from the small-label and subtle-shadow rows of 12.0. */}
        <button
          onClick={handleOpen}
          className="bg-surface text-on-surface absolute bottom-0 right-0 rounded-full p-1.5 text-xs font-semibold shadow-[0_4px_14px_rgba(10,42,77,0.06)] transition-colors hover:brightness-90"
        >
          Edit
        </button>
      </div>

      {/* Modal */}
      {isOpen && (
        // `p-4` on the backdrop and `max-w-[440px]` instead of a fixed `w-96`:
        // 384px in an unpadded overlay is wider than a 360px viewport, so the
        // dialog was clipped at the narrow review width. 440px is the export's
        // card width; the padding is what keeps it off the edges below that.
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm">
          {/* No border. Cards in the export lift with shadow alone - the same
              swap UserList made at position 23 - and this one sits on a dimmed
              backdrop, so it takes the dictionary's dark-ground card shadow
              rather than the light-surface one. */}
          <div className="bg-elevated-surface text-on-elevated-surface w-full max-w-[440px] rounded-3xl p-6 shadow-[0_25px_60px_rgba(0,0,0,0.35)] lg:p-9">
            <h2 className="mb-1 text-xl font-extrabold">Change avatar</h2>
            <p className="text-on-elevated-surface/60 mb-6 text-sm font-medium">
              Pick a new photo for your profile.
            </p>

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
                    <span className="text-on-elevated-surface/40 flex h-full w-full items-center justify-center text-xs">
                      Add photo
                    </span>
                  )}
                </button>
                <p className="text-on-elevated-surface/40 text-xs">
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

              {error && <p className="text-danger text-sm">{error}</p>}

              {/* Krok 6 unit 6.7. These were bare buttons reproducing the
                  dictionary's standard-button row by hand, for the one reason
                  the previous note named exactly: `Button` had no neutral
                  variant that Cancel could use. Unit 6.6 added one, taking these
                  very values, so the duplication collapses.
                  Nothing changes visually: `Button` carries the same 12px
                  radius, the same 14px vertical padding (unfrozen by 6.4, which
                  moved BASE_CLASSES from py-3 to py-3.5) and the same 14px/700,
                  and its `primary` is the same `bg-hub-cta` Save drew by hand.
                  `flex-1` goes through `className`, which is the layout escape
                  hatch and precisely what it is for. */}
              <div className="flex gap-3">
                <Button
                  variant="neutral"
                  onClick={handleClose}
                  className="flex-1"
                >
                  Cancel
                </Button>
                <Button type="submit" disabled={isSubmitting} className="flex-1">
                  {isSubmitting ? 'Saving…' : 'Save'}
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </>
  );
}
