import type { ReactNode } from 'react';

// Was locked to the original palette on the grounds that the landing hero sits
// outside the theme system. Step 4 of the design migration reversed that for
// two reasons: the export contains no pill at all, so there is nothing here to
// stay faithful *to*; and this file is named in the step's completion criteria,
// which required it to stop referencing that palette before Step 8 could
// retire it.
//
// That left semantic tokens as the only option - the chat's parallel palette
// would just have moved the same problem into Step 8. primary/on-primary is the accent pair the export
// uses for its CTA, and the mint this replaces is the other end of the same
// mint-to-lime gradient. Weight follows the small-label row of the layout
// dictionary (MIGRATION-INVENTORY.md 12.0): 12px at 600, not 700.
export default function Tag({ children }: { children: ReactNode }) {
  return (
    <span className="bg-primary text-on-primary rounded-full px-4 py-1.5 text-xs font-semibold">
      {children}
    </span>
  );
}
