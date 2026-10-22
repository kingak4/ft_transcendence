// Sends one chat message as user B after a delay, with no visible window,
// so user A's window can keep focus while NVDA listens for the announcement.
import { chromium } from 'playwright';

const BASE_URL = process.env.BASE_URL ?? 'https://localhost:8443';
const FRIEND_ID = process.env.FRIEND_ID; // user A's id, as seen from B
const DELAY_S = Number(process.env.DELAY ?? 15);
const TEXT = process.env.TEXT ?? 'Hello from B';
const AUTH_FILE = 'auth-b.json';

async function openChat(browser) {
  const context = await browser.newContext({
    ignoreHTTPSErrors: true,
    storageState: AUTH_FILE,
  });
  const page = await context.newPage();
  await page.goto(`${BASE_URL}/chat?friend=${FRIEND_ID}`);
  return page.getByRole('textbox', { name: 'Message' });
}

async function main() {
  if (!FRIEND_ID) throw new Error("Set FRIEND_ID to user A's id");
  const browser = await chromium.launch();
  const input = await openChat(browser);
  console.log(`Sending in ${DELAY_S}s - focus A's message field in Chrome now.`);
  await new Promise((resolve) => setTimeout(resolve, DELAY_S * 1000));
  await input.fill(TEXT); // auto-waits until the field is enabled
  await input.press('Enter');
  await new Promise((resolve) => setTimeout(resolve, 2000)); // let STOMP deliver
  await browser.close();
  console.log('Sent.');
}

main();
