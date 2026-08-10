/**
 * The export's 3px white ring on the profile hero avatar.
 *
 * Shared rather than duplicated because there are now TWO call sites, not one.
 * The 12.5 entry that chose "class at the call site" over "prop on `Avatar`"
 * argued that the ring appears on exactly one avatar in the whole design, so a
 * prop would add an API for a single user. That was true when it was written;
 * the next unit added the non-owner branch of the profile hero and it stopped
 * being true, leaving the same magic string in two files that had to stay in
 * sync by hand.
 *
 * A module constant is the smaller half of the fix. The larger one - collapsing
 * the two branches so there is a single avatar and only the edit trigger varies
 * - changes `EditAvatarButton`'s API, which §8.7 reguła 3 puts in Krok 6.
 *
 * `ring` rather than `border` because it draws outside the box and leaves the
 * 96px circle at 96px.
 */
export const AVATAR_RING_CLASSES = 'rounded-full ring-[3px] ring-white/50';
