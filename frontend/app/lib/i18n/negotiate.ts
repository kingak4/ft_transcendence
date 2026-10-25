import { DEFAULT_LOCALE, isLocale, type Locale } from './config';

// "pl-PL,pl;q=0.9,en;q=0.8" -> ['pl', 'pl', 'en'], highest quality first.
function preferredLanguages(acceptLanguage: string): string[] {
  return acceptLanguage
    .split(',')
    .map((part) => {
      const [tag, ...params] = part.trim().split(';');
      const qParam = params.find((param) => param.trim().startsWith('q='));
      return {
        language: tag.split('-')[0].toLowerCase(),
        quality: qParam ? Number(qParam.trim().slice(2)) : 1,
      };
    })
    .filter(({ quality }) => quality > 0)
    .sort((a, b) => b.quality - a.quality)
    .map(({ language }) => language);
}

export function negotiateLocale(
  cookieValue: string | undefined,
  acceptLanguage: string | null,
): Locale {
  if (isLocale(cookieValue)) return cookieValue;
  if (!acceptLanguage) return DEFAULT_LOCALE;
  return preferredLanguages(acceptLanguage).find(isLocale) ?? DEFAULT_LOCALE;
}
