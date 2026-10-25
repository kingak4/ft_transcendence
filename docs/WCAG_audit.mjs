import { chromium } from 'playwright';
import AxeBuilder from '@axe-core/playwright';
import { readFile, writeFile } from 'node:fs/promises';

const BASE_URL = process.env.BASE_URL ?? 'https://localhost:8443';
const ROUTES_FILE = process.env.ROUTES ?? 'routes.public.json';
const AUTH_FILE = process.env.AUTH; // unset = scan as a logged-out visitor
const THEME = process.env.THEME ?? 'brand'; // brand | mocha | latte
const THEME_CLASSES = ['mocha', 'latte']; // mirrors lib/theme.ts THEME_CLASSES
const LOCALE = process.env.LOCALE ?? 'en'; // en | pl | fr | ar - same cookie the language switcher sets
const EXPECTED_DIR = LOCALE === 'ar' ? 'rtl' : 'ltr'; // mirrors lib/i18n/config.ts RTL_LOCALES
const SHOW_REVIEW = Boolean(process.env.REVIEW); // also list what axe could not decide
const WCAG_TAGS = [
  'wcag2a',
  'wcag2aa',
  'wcag21a',
  'wcag21aa',
  ...(process.env.BEST_PRACTICE ? ['best-practice'] : []), // landmarks, heading order
];

function reportFileName() {
  const set = ROUTES_FILE.replace(/^routes\./, '').replace(/\.json$/, '');
  return `report-${set}-${THEME}-${LOCALE}.json`;
}

async function loadRoutes() {
  return JSON.parse(await readFile(ROUTES_FILE, 'utf8'));
}

// Same effect as applyTheme() in hooks/useTheme.ts: swap the class on <html>.
async function applyTheme(page) {
  await page.evaluate(
    ({ theme, themeClasses }) => {
      const { classList } = document.documentElement;
      classList.remove(...themeClasses);
      if (theme !== 'brand') classList.add(theme);
    },
    { theme: THEME, themeClasses: THEME_CLASSES },
  );
  await page.waitForTimeout(300); // let transition-colors settle
}

async function scanRoute(page, route) {
  await page.goto(BASE_URL + route, { waitUntil: 'load' });
  await page.waitForTimeout(1500); // let client components hydrate
  await applyTheme(page);
  const { pathname, search } = new URL(page.url());
  const finalPath = pathname + search;
  const title = await page.title();
  const { lang, dir } = await page.evaluate(() => ({
    lang: document.documentElement.lang,
    dir: document.documentElement.dir,
  }));
  const { violations, incomplete } = await new AxeBuilder({ page })
    .withTags(WCAG_TAGS)
    .analyze();
  return { route, finalPath, title, lang, dir, violations, incomplete };
}

function summarise({ route, finalPath, title, lang, dir, violations, incomplete }) {
  const redirect = finalPath !== route ? ` (redirected to ${finalPath}!)` : '';
  // WCAG 3.1.1: the page must declare the language it is actually shown in.
  const langMismatch =
    lang !== LOCALE || dir !== EXPECTED_DIR
      ? ` (expected ${LOCALE}/${EXPECTED_DIR}!)`
      : '';
  const header = `${route} [title: "${title}"] [${lang}/${dir}]${langMismatch}${redirect}`;
  const lines = violations.map(
    (v) => `  [${v.impact}] ${v.id} x${v.nodes.length}: ${v.help}`,
  );
  const review = SHOW_REVIEW
    ? incomplete.map((v) => `  [review] ${v.id} x${v.nodes.length}: ${v.help}`)
    : [];
  return [`${header}: ${violations.length} failing rule(s)`, ...lines, ...review].join('\n');
}

async function main() {
  const browser = await chromium.launch();
  const context = await browser.newContext({
    ignoreHTTPSErrors: true, // self-signed cert behind nginx
    storageState: AUTH_FILE,
  });
  await context.addCookies([{ name: 'NEXT_LOCALE', value: LOCALE, url: BASE_URL }]);
  const page = await context.newPage();
  const results = [];
  for (const route of await loadRoutes()) results.push(await scanRoute(page, route));
  await browser.close();
  await writeFile(reportFileName(), JSON.stringify(results, null, 2));
  console.log(`== ${ROUTES_FILE} / ${THEME} / ${LOCALE} ==\n`);
  console.log(results.map(summarise).join('\n\n'));
}

main();
