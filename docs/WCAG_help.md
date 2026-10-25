# WCAG 2.1 AA — testing guide

How to check the frontend against **WCAG 2.1 AA**: an automated scan (axe-core + Playwright), contrast checks by hand where tools can't measure, and a manual screen-reader run with NVDA.

> Automated tools catch only ~30–40 % of WCAG problems. A clean scan is necessary, not sufficient — the keyboard and NVDA checks below are what prove the app works with assistive technology.

## Contents

- [Quick reference](#quick-reference)
- [Automated scan](#automated-scan)
  - [Setup](#setup)
  - [Running the scan](#running-the-scan)
  - [Investigating failures](#investigating-failures)
  - [What the scan cannot see](#what-the-scan-cannot-see)
- [Contrast on gradients and transparent backgrounds](#contrast-on-gradients-and-transparent-backgrounds)
- [Manual checklist](#manual-checklist)
- [NVDA scenario](#nvda-scenario)
- [Rules that keep the app AA](#rules-that-keep-the-app-aa)

---

## Quick reference

**Full scan** (both page sets × 3 themes × 4 languages, prints only failures):

```bash
cd ~/a11y-audit && for l in en pl fr ar; do for t in brand mocha latte; do LOCALE=$l THEME=$t node WCAG_audit.mjs; LOCALE=$l ROUTES=routes.app.json AUTH=auth.json THEME=$t node WCAG_audit.mjs; done; done 2>&1 | grep -v ': 0 failing' | grep -v '^$'
```

A `(expected ar/rtl!)` line means the page did not render in the requested language — a WCAG 3.1.1 failure even if axe reports 0.

**Lint and type check:**

```bash
cd <repo>/frontend && npx eslint app 2>&1 | tail -60
cd <repo>/frontend && npx tsc --noEmit 2>&1 | tail -40
```

Silence from all three = clean.

---

## Automated scan

The scripts' **source of truth is the repo** (`docs/`). They **run** from a workspace outside the repo, `~/a11y-audit`, which holds everything that must not be committed:

- `node_modules` (Playwright, axe) — kept out of the frontend's dependencies
- `auth*.json` — live session cookies, effectively passwords
- `routes.app.json` — user IDs from your local database
- `report-*.json` — throwaway output

> [!IMPORTANT]
> Run the scripts from the workspace copy, not from `docs/`. ES modules resolve `import 'playwright'` from the **script file's own folder** upwards, so `node <repo>/docs/WCAG_audit.mjs` fails with *Cannot find package 'playwright'* — even when run from `~/a11y-audit`. Copy them in instead ([step 1](#1-install-the-tools-once)).

### Files

| File | Purpose |
|---|---|
| `WCAG_audit.mjs` | The scanner (axe tags `wcag2a`, `wcag2aa`, `wcag21a`, `wcag21aa`) — source in `docs/` |
| `WCAG_send-as-b.mjs` | Sends a chat message as user B after a delay, for the NVDA announcer test — source in `docs/` |
| `routes.public.json` | Pages scanned logged out |
| `routes.app.json` | Pages scanned logged in — **contains user IDs**, see [step 3](#3-put-the-ids-into-routesappjson) |
| `auth.json` | Saved login session of user A |
| `auth-b.json` | Saved login session of user B |
| `report-<set>-<theme>-<locale>.json` | Full results of the last run (`violations` + `incomplete`), overwritten each run |

### Environment variables

| Variable | Default | Meaning |
|---|---|---|
| `BASE_URL` | `https://localhost:8443` | Where the app runs |
| `ROUTES` | `routes.public.json` | Which route list to scan |
| `AUTH` | *(unset)* | Session file; unset = logged-out visitor |
| `THEME` | `brand` | `brand` \| `mocha` \| `latte` — switched the same way as `applyTheme()` in `hooks/useTheme.ts` |
| `LOCALE` | `en` | `en` \| `pl` \| `fr` \| `ar` — sets the `NEXT_LOCALE` cookie, like the footer language switcher; the output shows each page's `[lang/dir]` |
| `REVIEW` | *(unset)* | `1` = also print `[review]` lines: elements axe **could not decide** (gradients, images) |
| `BEST_PRACTICE` | *(unset)* | `1` = also run axe best-practice rules (landmarks, heading order) |

Each route's output line includes its page title, e.g. `/friends [title: "Friends · 42Hub"]`, so duplicate or generic titles (WCAG 2.4.2) are visible at a glance.

### Setup

#### When to redo it

A full Docker rebuild (containers **and** volumes) wipes the database:

| What breaks | Redo |
|---|---|
| Users A and B no longer exist | [step 2](#2-test-data-two-users-and-a-conversation) |
| Their IDs change | [step 3](#3-put-the-ids-into-routesappjson) |
| `auth.json` / `auth-b.json` point at dead sessions | [step 4](#4-save-the-login-sessions) |

Independently of Docker: whenever `docs/WCAG_*.mjs` change in the repo, copy them into the workspace again (the `cp` line from step 1).

Symptoms in the scan output:

- `(redirected to /login!)` → session expired → step 4
- profile or chat route gives unexpected results / 404 → IDs changed → steps 2–3

Step 1 is needed only once per machine.

#### 1. Install the tools (once)

```bash
mkdir -p ~/a11y-audit && cd ~/a11y-audit && npm init -y && npm i playwright @axe-core/playwright && npx playwright install --with-deps chromium
```

| Part | What it does |
|---|---|
| `npm init -y` | Creates `package.json` (doesn't touch existing files) |
| `npm i …` | Installs Playwright and the axe integration |
| `playwright install --with-deps chromium` | Downloads headless Chromium plus the Linux libraries it needs |

Then copy the scripts from the repo into the workspace (repeat whenever they change):

```bash
cp <repo>/docs/WCAG_audit.mjs <repo>/docs/WCAG_send-as-b.mjs ~/a11y-audit/
```

Create `~/a11y-audit/routes.public.json`:

```json
["/", "/login", "/register", "/privacy-policy", "/terms-of-service"]
```

`routes.app.json` comes in [step 3](#3-put-the-ids-into-routesappjson).

#### 2. Test data: two users and a conversation

In **Windows Chrome**. Why two users: message bubbles, timestamps and the "Online" label only exist when there is a real chat. Without them, axe never sees — and never checks — those elements.

1. Normal window: `https://localhost:8443/register` → register **user A**
2. Incognito window (`Ctrl+Shift+N`): `/register` → register **user B**
   *(separate windows = separate cookies = both logged in at once)*
3. As A: **Friends** → search for B → **Add friend**
4. As A: open the chat with B, send 2–3 messages
5. As B: reply with 2–3 messages *(both bubble styles: mine / theirs)*
6. As A: delete **one** of A's messages *(so the "(message deleted)" style is checked too)*
7. Keep B's incognito window **open** while scanning, so B shows as "Online"

User A is the scanner's user; user B only exists to be chatted with.

#### 3. Put the IDs into `routes.app.json`

- **A's ID** — as A, open your profile and copy the last part of the URL: `https://localhost:8443/<A_ID>`
- **Chat URL** — as A, open the chat with B and copy the path: `/chat?friend=<B_ID>`

```json
[
  "/chat",
  "/chat?friend=<B_ID>",
  "/friends",
  "/<A_ID>"
]
```

> [!WARNING]
> Never add `/auth/logout`. It's a route handler that logs you out — every page after it would silently be scanned as `/login`.

#### 4. Save the login sessions

A browser window opens (WSLg). Log in, then **close the window** — the session cookies are written to the file. Ignore the code-recorder panel.

```bash
cd ~/a11y-audit && npx playwright codegen --ignore-https-errors --save-storage=auth.json https://localhost:8443/login
```

Log in as **A**. Then, for the NVDA announcer test only:

```bash
cd ~/a11y-audit && npx playwright codegen --ignore-https-errors --save-storage=auth-b.json https://localhost:8443/login
```

Log in as **B**.

### Running the scan

**Everything, full output:**

```bash
cd ~/a11y-audit && for t in brand mocha latte; do THEME=$t node WCAG_audit.mjs; ROUTES=routes.app.json AUTH=auth.json THEME=$t node WCAG_audit.mjs; done 2>&1
```

| Part | Meaning |
|---|---|
| `for t in brand mocha latte; do …; done` | Repeat for each theme; `$t` is the theme name |
| `THEME=$t node WCAG_audit.mjs` | Public pages, logged out |
| `ROUTES=… AUTH=… THEME=$t node …` | App pages, logged in as A |
| `2>&1` | Show errors together with normal output |

**Failures only** (quiet when clean) — append:

```bash
… 2>&1 | grep -v ': 0 failing' | grep -v '^$'
```

`grep -v PATTERN` drops matching lines; `'^$'` is an empty line.

**Undecided items** (gradients etc.) — see [Contrast on gradients](#contrast-on-gradients-and-transparent-backgrounds):

```bash
cd ~/a11y-audit && for t in brand mocha latte; do REVIEW=1 THEME=$t node WCAG_audit.mjs; done 2>&1
```

**Best-practice rules** (landmarks, heading order, unique titles):

```bash
cd ~/a11y-audit && BEST_PRACTICE=1 node WCAG_audit.mjs && BEST_PRACTICE=1 ROUTES=routes.app.json AUTH=auth.json node WCAG_audit.mjs
```

**Goal:** every page `0 failing rule(s)` in every theme.

### Investigating failures

The summary only gives counts. This reads the saved reports (no new scan) and prints one line per failing element:

```bash
cd ~/a11y-audit && node -e '
for (const f of process.argv.slice(1))
  for (const r of require("./" + f))
    for (const v of r.violations)
      for (const n of v.nodes) {
        const d = n.any[0]?.data ?? {};
        console.log([f, r.route, v.id, n.target.join(" "), d.fgColor, d.bgColor, d.contrastRatio + "/" + d.expectedContrastRatio, d.fontSize, n.html.slice(0, 160)].join(" | "));
      }
' report-*.json
```

Columns: report file · route · rule · CSS selector · text colour · background · actual/required ratio · font size · start of the HTML. Colour columns are empty for non-contrast rules — that's normal.

**Same for undecided items** — swap `violations` for `incomplete`; `messageKey` is axe's reason (`bgGradient`, `bgOverlap`, `emptyValue` …):

```bash
cd ~/a11y-audit && node -e '
for (const r of require("./report-public-brand-en.json"))
  for (const v of r.incomplete ?? [])
    for (const n of v.nodes) {
      const d = n.any[0]?.data ?? {};
      console.log([r.route, n.target.join(" "), d.fgColor, d.messageKey, n.html.slice(0, 110)].join(" | "));
    }
'
```

#### Contrast thresholds (WCAG 1.4.3 / 1.4.11)

| What | Minimum |
|---|---|
| Normal text | **4.5 : 1** |
| Large text — ≥ 24 px, or ≥ 18.66 px **bold** | **3 : 1** |
| UI parts — borders, icons, focus indicators | **3 : 1** |
| Disabled controls | exempt |

### What the scan cannot see

It checks each page **as it loads**. It doesn't open dialogs, menus or dropdowns, and doesn't type or press keys. Check these with the **axe DevTools** / **WAVE** extensions in Windows Chrome:

- Edit avatar dialog and the display-name editor (open them first, then scan)
- Chat-sidebar user search with results showing
- `ConnectionBanner` while visible (stop the backend container to trigger it)

---

## Contrast on gradients and transparent backgrounds

axe **cannot compute contrast against a gradient or a semi-transparent background**. Those elements land in `incomplete` ("needs review"), **not** in `violations` — so a page can show `0 failing` while text on it is unreadable. Run with `REVIEW=1` to see them.

Affected areas and how they were verified by hand (worst case = the lightest spot under the text):

| Area | Background | Solution |
|---|---|---|
| Footer on the start page | Brand gradient — even white is only ≈ 3.1 : 1 at the blue end `#2f9bcf` | Solid band in the gradient's darkest stop (`.footer-on-start-page` in `globals.css`); white on `#0a3348` ≈ 13 : 1 |
| Hero paragraph | Brand gradient — white ≈ 4.0 : 1 near `#1f8f7a` | Made **large text** (`text-xl font-bold`), so 3 : 1 applies; worst ≈ 3.1 : 1 |
| Login / register / home card | `bg-card` is 55–60 % opaque over the gradient; `opacity-60` text ≈ 3.7 : 1, `/50` ≈ 3.15 : 1 | Full `text-on-card` everywhere on the card |
| Accent links on the latte card | Light card | Latte `primary` = `mauve-700` (`#7731d2`) ≈ 5.0 : 1 on the darkest card area |

The `[review]` lines for these elements will keep appearing — that's expected. Re-check by hand whenever the gradient, the card or text colours on the start page change.

---

## Manual checklist

| Check | How | Pass when |
|---|---|---|
| Keyboard only | `Tab` / `Shift+Tab` / `Enter` / `Space` / `Esc` through every page and dialog | Focus always visible, never trapped, logical order |
| Screen reader | [NVDA scenario](#nvda-scenario) below | Every section matches |
| Zoom | 200 % browser zoom, and a 320 px-wide window | No horizontal scrolling, no clipped text |
| Themes | Switch in the footer, repeat the visual checks | Same results in brand, mocha, latte |
| Languages | Switch in the footer; repeat zoom + keyboard in PL and FR (longest strings) and AR | No clipped or overlapping text; tab title translated |
| RTL (Arabic) | Narrow window + drawer, chat with B, profile | Drawer slides in from the right; own bubbles on the left; the back arrow points right and the send icon points up-left; Latin names keep their punctuation in place; switching EN↔AR keeps focus on the pressed button |

---

## NVDA scenario

Windows Chrome, `https://localhost:8443`.

### NVDA basics

| Action | Keys |
|---|---|
| Start | `Ctrl+Alt+N` |
| Quit | `Insert+Q`, then `Enter` |
| Mute speech | `Insert+S` (cycles talk → beeps → off) |
| No autostart | `Insert+N` → Preferences → Settings → General → untick *Start NVDA after I sign in to Windows* |
| Speech log | `Insert+N` → Tools → **Speech Viewer** — shows everything NVDA says as copyable text |

| Key (browse mode) | What it does |
|---|---|
| `H` / `Shift+H` | Next / previous heading |
| `D` | Next landmark (main, nav, region "Messages") |
| `F` | Next form field |
| `K` | Next link |
| `X` | Next checkbox |
| `Tab` | Next interactive element |
| `Insert+F7` | List of links, headings, landmarks |
| `Insert+Space` | Toggle browse mode ↔ focus mode |

**Browse vs focus mode.** Browse mode = reading the page; single letters jump (`H`, `D`, `F`, `K` …). Focus mode = typing into a field; letters type text. NVDA switches automatically when you Tab into a field. `Esc` in a text field leaves focus mode **first** — you may need to press it twice (once for NVDA, once for the app).

### Before you start

1. [Setup](#setup) steps 2–4 are done (users A and B, a chat with messages, both sessions saved).
2. Start NVDA and open the **Speech Viewer**.
3. Note where each section starts in Speech Viewer; afterwards copy the text for that section.

Expected phrases are approximate — NVDA's wording varies by version. What matters is the **meaning**: name, role, state.

A step **fails** if NVDA:

- says nothing,
- says only "button" / "edit" with no name,
- reads something that should be hidden, or
- focus jumps somewhere unexpected (e.g. to the top of the page).

> [!NOTE]
> "clickable" before landmarks is noise, not a failure: React attaches one click listener at the page root (event delegation), and NVDA announces any element with a listener.

### 1 · Login page, logged out — `/login`

| Action | Expect |
|---|---|
| Load the page | "Login · 42Hub" |
| `Tab` | "Skip to main content, link" — link appears top-left |
| `Enter` | Focus moves **to `<main>`** itself; NVDA reads the main content |
| `Tab` | First interactive element **inside** main — not the logo |
| `Insert+F7` → Landmarks | banner, main, content info |
| `H` | "heading level 1, Login" |
| `F` | "Email, edit" — the label is visible above the field |
| `F` | "Password, edit, protected" |
| `Tab` to Login, `Enter` (empty form) | Errors read ("Email cannot be blank"); focus on the first invalid field |

> [!TIP]
> Test on `/login`, not on `/` — the home page has no form, so `F` there correctly says "no next form field".

### 2 · Register page — `/register`

| Action | Expect |
|---|---|
| Load the page | "Register · 42Hub" |
| `F` repeatedly | "Email", "Password", "Confirm password" — each with a name |
| `X` | "I agree to the Terms of Service and Privacy Policy, check box, not checked" |
| `Space` | "checked" |
| `K` ×2 | "Terms of Service, link", "Privacy Policy, link" |

### 3 · Legal page, logged out — `/terms-of-service`

| Action | Expect |
|---|---|
| Load the page | "Terms of Service · 42Hub" |
| `H` repeatedly | "heading level 1, Terms of Service", then "1. Introduction" … |
| `Tab` | "Skip to main content, link" |
| `Tab` | "main landmark" — the scrollable area got focus |
| `Down` / `Page Down` | Reading continues; the page scrolls |

### 4 · Own profile, logged in as A — `/<A_ID>`

| Action | Expect |
|---|---|
| Load the page | "My Profile · 42Hub" |
| `Insert+F7` → Landmarks | "Main" navigation, main, content info |
| `Insert+F7` → Links | "My Profile, link, current page" |
| Browse past the details card | "Username" and the name read as **separate** items, not "UsernameZyta" |
| `Tab` to the avatar "Edit" | "Edit avatar, button" |
| `Enter` | "Change avatar, dialog, Pick a new photo for your profile." — focus **inside** |
| `Tab` repeatedly | Only dialog controls (and Chrome's own UI — that's correct, see below) |
| `Esc` | Dialog closes; "Edit avatar, button" |
| `Tab` to "Change name", `Enter` | "Display name, edit, ‹your name›" |
| Delete the whole name, `Enter` | Error read as an alert |
| `Esc` (×2) | "Change name, button" |

> [!NOTE]
> Tab reaching Chrome's tabs and address bar while the dialog is open is **correct**. A modal dialog blocks the page behind it, but WCAG 2.1.2 *No Keyboard Trap* requires that the user can always leave — the browser UI is that exit.

### 5 · Friends — `/friends`

| Action | Expect |
|---|---|
| Load the page | "Friends · 42Hub" |
| `H` | "heading level 1, Friends" |
| `F` | "Search users, search edit" |
| `Tab` through a friend row | Every control has a name: "Chat, link", "Remove, button" |
| *(more than one page of friends)* | "Page 1 of N"; "Next ›, link" |

### 6 · Chat — `/chat?friend=<B_ID>`

| Action | Expect |
|---|---|
| Load the page | "Chat · 42Hub" |
| `D` repeatedly | … "Chats, complementary", "Chats, navigation", "Messages, region" … |
| `K` in the Chats list | "‹B›, link, current page" — **without** "graphic" (avatars are decorative) |
| `Down` inside "Messages" | Every bubble starts with its sender: "You: …" / "‹B›: …"; deleted: "You: (message deleted)"; own messages end with "Sent, graphic" |
| `Tab` onto one of **your** messages | "Delete message, button" — and the trash icon becomes **visible** |
| `Tab` to the message field | "Message, edit" |

**New-message announcement.** NVDA only speaks live regions in the **window that has focus**. Typing as B in a second window puts A's window in the background, so nothing is spoken — that's a test-setup artefact, not a bug. Use the script instead:

1. In Chrome as A, open the chat with B and put focus in the message field.
2. In WSL (`FRIEND_ID` is **A's** ID — from B's side, A is the friend):

   ```bash
   cd ~/a11y-audit && FRIEND_ID=<A_ID> node WCAG_send-as-b.mjs
   ```

3. Within 15 s, click back into A's message field and wait.

| Expect |
|---|
| "New message from ‹B›: Hello from B" — **without** focus moving |
| Your own sent messages are **not** announced (by design) |

Optional overrides: `DELAY=30` (seconds), `TEXT="…"`.

**Optional — connection error:** stop the backend container, then type and send a message → "Failed to send message: Not connected to server" read as an alert.

### 7 · Mobile navigation drawer — any logged-in page

Make the Chrome window narrow (< 1024 px) until the hamburger appears.

| Action | Expect |
|---|---|
| `Tab` to it | "Open navigation, button, collapsed" |
| `Enter` | Focus moves to the first nav link ("My Profile") |
| `Esc` | Drawer closes; "Open navigation, button, collapsed" |

> [!NOTE]
> Known limitation: while the drawer is open, `Tab` can still reach the page behind it (the drawer is not modal). Not a WCAG failure.

### 8 · Footer theme switch — any page

| Action | Expect |
|---|---|
| `Tab` to the theme buttons | "Theme, grouping"; "42Hub, toggle button, pressed"; "Mocha, toggle button, not pressed" |
| `Space` on "Mocha" | "pressed"; the page switches to the dark theme |

### 9 · Footer language switch — any page

Turn on NVDA → Preferences → Settings → Speech → **Automatic language switching** first; otherwise NVDA reads every language with the English voice.

| Action | Expect |
|---|---|
| `Tab` to the language buttons | "Language, grouping"; "English, toggle button, pressed" |
| `Tab` to "Polski" | Read with the **Polish** voice (`lang="pl"` on the button) |
| `Space` on "Polski" | "pressed"; focus stays on the button; the page text is Polish |
| `H` | "nagłówek poziom 1, …" — NVDA now speaks Polish |
| `Space` on "العربية" | Page mirrors right-to-left; NVDA switches to the Arabic voice |

### Reporting results

For each section: the section number + the Speech Viewer text. For any step that didn't match, one line with what you pressed and what happened instead, e.g. `S4 Esc: dialog closed but NVDA said nothing`.

---

## Rules that keep the app AA

Patterns established during the WCAG work — follow them in new code:

| Rule | Why |
|---|---|
| **No `opacity-*` or `/NN` on text** — use a colour token (`text-muted`, `text-on-card` …) | Transparency makes the real colour depend on whatever is behind it, so contrast can't be checked in advance |
| **One token = one background.** Text on `bg-cta` uses `text-on-cta`; on `bg-primary` uses `text-on-primary` | Sharing one token across two backgrounds means fixing one breaks the other |
| **Check new colours in all 3 themes** | Each theme has its own surfaces; a pass in brand says nothing about latte |
| **Dialogs use `components/Dialog`** (native `<dialog>` + `showModal()`) | Focus trap, `Esc`, focus return and the dialog role come from the browser |
| **Form fields use `FormField` with `label`** (required by its type) and `autoComplete` for personal data | WCAG 3.3.2 labels; 1.3.5 input purpose |
| **Status updates go through `components/Announcer`** (`aria-live="polite"`) | Live regions must already exist in the DOM before their content changes |
| **Errors use `role="alert"`**, which may be rendered conditionally | Exception to the rule above: screen readers announce an `alert` when it is inserted |
| **Every page sets `metadata.title`** (template `%s · 42Hub` in the root layout) | WCAG 2.4.2 — tab titles identify the page |
| **Every shell renders `<main id="main">`** | Target of the skip link; landmark for `D` in NVDA |
| **Decorative images get `alt=""`** — e.g. an avatar next to the visible name | Otherwise the name is read twice |
| **Scrollable regions are focusable** (`tabIndex={0}` + a name) | Keyboard users must be able to scroll (2.1.1); allowed in `eslint.config.mjs` for `main` and `region` |
| **Never remove focus outlines** (`outline-none`) without an equally visible replacement | Chrome's default two-colour ring works on every background (2.4.7) |
| **No literal UI text** — every string, `aria-label`, `alt` and `placeholder` comes from `messages/*.json` via `useTranslations` / `getTranslations` | Untranslated text is read with the wrong voice and breaks 3.1.1; `react/jsx-no-literals` catches JSX text, attributes need review |
| **Visible text stays inside the accessible name in every language** — e.g. PL "Edytuj" / "Edytuj awatar" | WCAG 2.5.3 Label in Name; check it when translating `aria-label`s |
| **Logical classes only** — `ms-`/`me-`, `ps-`/`pe-`, `start-`/`end-`, `rounded-s`/`-e`; never `ml-`, `left-`, `text-left` | They flip automatically under `dir="rtl"` |
| **User-written text in `<bdi>` or `dir="auto"`** — names, messages, search fields | A Latin name in an Arabic page (or the reverse) otherwise moves its punctuation to the wrong side |
| **Directional icons use `<Icon mirrorInRtl />`** — arrows, send; never checkmarks, clocks, logos | An arrow must point along the reading direction |
