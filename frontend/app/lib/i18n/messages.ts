import ar from '@/messages/ar.json';
import en from '@/messages/en.json';
import fr from '@/messages/fr.json';
import pl from '@/messages/pl.json';

import type { Locale } from './config';

// English is the source of truth: a translation missing a key fails `tsc`.
export type Messages = typeof en;

export const MESSAGES = { en, pl, fr, ar } satisfies Record<Locale, Messages>;
