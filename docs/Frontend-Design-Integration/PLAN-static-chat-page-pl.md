# Plan — Statyczna strona czatu + tokeny palety `hub-*`

**Gałąź:** `feat/static-chat-page`
**Źródło projektu:** `42Hub UIUX design/Forti2Hub.dc.html` (eksport Claude Design)
**Data:** 2026-07-30

---

## 1. Cel

Dodać **statyczną** stronę czatu do frontendu w Next.js, wyrenderowaną w nowym języku
wizualnym 42Hub pochodzącym z eksportu Claude Design, oraz wprowadzić nową paletę marki
jako **addytywne** tokeny projektowe.

„Statyczna” oznacza: prawdziwy markup, prawdziwy układ, prawdziwa paleta, dane zastępcze.
Bez wywołań REST i bez subskrypcji STOMP. Inny developer podłączy później warstwę danych,
a ten plan jest ułożony tak, aby jego zadanie polegało na *zastąpieniu jednego modułu*,
a nie na przebudowie komponentów.

### W zakresie

- Nowe tokeny projektowe `hub-*` w `app/globals.css` (wyłącznie addytywnie)
- Nowa trasa `app/(app)/chat/` z pełnym interfejsem czatu
- Addytywne rozszerzenia komponentów `Avatar`, `TextField`, `Button`
- Nowy komponent `PresenceAvatar`
- Przestylizowany `Sidebar` zgodnie z nowym wyglądem

### Poza zakresem (odłożone — zob. §8)

- Pełna migracja palety oraz nadpisania `.mocha` / `.latte` dla `hub-*`
- Wdrożenie fontów Manrope / Noto Sans Arabic
- Pozbawiona stopki, pełnoekranowa grupa tras `(chat)`
- i18n i obsługa RTL
- Jakiekolwiek podłączenie backendu, REST lub WebSocket

---

## 2. Podjęte decyzje

| # | Decyzja | Uzasadnienie |
|---|---|---|
| 1 | **Wyłącznie tokeny addytywne.** Istniejące `--color-brand-*`, semantyczne mapowanie w `:root` oraz oba bloki Catppuccin pozostają nietknięte. | Odwrócenie `--theme-elevated-surface` z `#333333` na biały zmieniłoby ciemne panele na jasne w 19 plikach korzystających z tych tokenów. Każdy komponent, który miał wpisany na sztywno jasny tekst zamiast używać `text-on-elevated-surface`, stałby się biały na białym tle. Podejście addytywne eliminuje całą tę klasę regresji. |
| 2 | **Prefiks `hub-*`, nie `chat-*`.** | Te wartości są przyszłą paletą marki dla całej aplikacji i korzysta z nich również sidebar. Nazwa `--color-chat-panel` użyta w sidebarze byłaby myląca, a przyszła migracja nie powinna wymagać kolejnego przebiegu zmiany nazw. |
| 3 | **Nowe tokeny korzystają z warstwy pośredniej `--theme-hub-*`**, zdefiniowanej wyłącznie pod `:root`. | Odzwierciedla to wzorzec już obecny w `globals.css`. Komponenty odwołują się do nazw, nigdy bezpośrednio do wartości hex, dzięki czemu późniejsze dodanie obsługi `.mocha` / `.latte` wymaga kilku linii w jednym pliku zamiast przeglądu całego JSX. |
| 4 | **Czat renderuje się wewnątrz istniejącego layoutu `(app)`.** | Dziedziczy `Sidebar` i `Footer` bez dodatkowej pracy. Koszt: wcięcie `p-8` oraz przewijanie strony o wysokość stopki — zaakceptowane, zob. §4. |
| 5 | **Wybór znajomego przez parametr wyszukiwania `?friend=`**, a nie przez `useState`. | Dzięki temu cała strona czatu pozostaje komponentem serwerowym bez żadnego JavaScriptu po stronie klienta. Wiersze stają się elementami `<Link>`, więc obsługa klawiatury i przycisku Wstecz działa bez dodatkowej pracy, a URL można udostępnić. Koszt: jedno przejście przez serwer po każdym kliknięciu zamiast lokalnej zmiany stanu — niezauważalne przy statycznych fixture'ach. |
| 6 | **Kierunek wiadomości jest wyliczany na podstawie `senderId`**, a nie przechowywany jako flaga `from`. | Jest to zgodne z `SendMessageEventResponse` w wygenerowanych typach AsyncAPI. Skopiowanie z prototypu pola `from: 'me' \| 'them'` zmusiłoby developera podłączającego dane do przebudowania każdego fixture'a. |
| 7 | **`Sidebar` może wizualnie odbiegać od nietkniętych stron.** | Zaakceptowany kompromis. Duża część kontrastu strony czatu pochodzi z ciemnego panelu bocznego, więc jasny sidebar zafałszowałby projekt. |

---

## 3. Anatomia projektu (wyciągnięta z eksportu)

Ekran czatu znajduje się w `Forti2Hub.dc.html` w liniach 325–373; jego wartości stylów są
obliczane w `renderVals()` w liniach 714–737. Występują trzy obszary:

1. **Sidebar aplikacji** — 250 px, ciemny gradient `linear-gradient(165deg,#0b2b3a,#0d3339 55%,#0d2b3f)`
2. **Panel znajomych** — 290 px, biały, prawa ramka `#eef2ef`; nagłówek, pole wyszukiwania,
   wiersze z awatarem zawierającym kolorowy inicjał + kropkę statusu online + nazwę;
   aktywny wiersz ma odcień `#eaf4fa`
3. **Panel rozmowy** — obniżone tło `#f8faf9`; biały nagłówek (awatar, nazwa, etykieta statusu),
   przewijana lista wiadomości, biały composer (pole + Send)

### Dymki wiadomości

Asymetryczne promienie zaokrąglenia kodują kierunek — narożnik `4px` jest „ogonkiem”:

| | Moje | Ich |
|---|---|---|
| Promień | `18px 18px 4px 18px` | `18px 18px 18px 4px` |
| Tło | `linear-gradient(135deg,#146b7a,#2f9bcf)` | `#ffffff` |
| Tekst | `#ffffff` | `#0d2b47` |
| Cień | brak | `0 3px 10px rgba(10,42,77,0.06)` |
| Maksymalna szerokość | `420px` | `420px` |

Znaczniki czasu: `11px`, `#9aa5a0`, grubość 600, `4px` odstępu powyżej.

Eksport odbija narożnik ogonka dla języka arabskiego RTL. RTL jest poza zakresem, ale tam,
gdzie nic to nie kosztuje, używamy logicznych utility (`ms-*`, `me-*`, `text-start`), aby
jego późniejsze dodanie pozostało tanie.

---

## 4. Ograniczenie: obecny layout nie obsługuje pełnej wysokości

`app/(app)/layout.tsx`:

```tsx
<div className="bg-surface text-on-surface flex min-h-screen">
  <Sidebar userId={userId} />
  <div className="flex flex-1 flex-col">
    <main className="flex-1 p-8">{children}</main>
    <Footer />
```

Trzy problemy dla interfejsu czatu:

1. **`min-h-screen`, a nie `h-screen`.** Wysokość procentowa lub `h-full` jest obliczana
   względem *jednoznacznie określonej* wysokości elementu nadrzędnego. `min-height` nie
   ustala jednoznacznej wysokości, więc `h-full` na głównym elemencie czatu jest obliczane
   względem `auto` i zapada się. Lista wiadomości nigdy nie przewijałaby się samodzielnie —
   przewijałaby się cała strona.
2. **Elementy flex mają domyślnie `min-height: auto`.** Nawet przy jednoznacznie określonej
   wysokości dziecko flex nie chce zmniejszyć się poniżej rozmiaru swojej zawartości, więc
   `overflow-y-auto` nigdy się nie aktywuje. `min-h-0` jest wymagane na **każdym** przodku
   flex pomiędzy jednoznacznie określoną wysokością a kontenerem przewijania. To najczęstsza
   przyczyna problemu „moja lista czatu się nie przewija”.
3. **`p-8` tworzy wcięcie paneli**, więc rezultat nie jest projektem od krawędzi do krawędzi.

### Rozwiązanie dla tej gałęzi

- Zaakceptować wcięcie. Czat staje się zaokrąglonym panelem unoszącym się na stronie, co
  wygląda akceptowalnie i nie wymaga przebudowy layoutu.
- Ustawić rozmiar głównego elementu czatu za pomocą `h-[calc(100vh-4rem)]`
  (`4rem` = górne + dolne `p-8`) oraz `overflow-hidden`.
- Zastosować `min-h-0` w całym łańcuchu flex prowadzącym do listy wiadomości.

**Znana niedoskonałość:** `Footer` znajduje się pod `main`, więc strona przewija się o
wysokość stopki. Czystym rozwiązaniem jest grupa tras `(chat)` bez stopki, z własnym
pełnoekranowym layoutem — odłożone do osobnego issue (§8).

---

## 5. Model danych

Z `app/types/asyncapi.d.ts` (wygenerowanego przez `@asyncapi/modelina`, nie edytować):

```ts
class SendMessageEventResponse {
  content?: string;
  messageId?: string;
  senderId?: string;
  time?: string;      // wstępnie sformatowany ciąg do wyświetlenia, np. "9:12 AM"
}
```

Dwie konsekwencje:

- **Kierunek jest wyliczany.** `isMine = message.senderId === currentUserId`. W protokole
  nie ma flagi kierunku.
- **Wygenerowane klasy nie mogą typować naszych fixture'ów.** Ich pola to `private
  _content` itd., a **prywatne składowe sprawiają, że typ klasy w TypeScript jest nominalny** —
  strukturalnie identyczny literał obiektu nie jest przypisywalny. Dlatego fixture'y używają
  lokalnego prostego typu, który odzwierciedla wygenerowany kształt, wraz z komentarzem
  zapisującym jego pochodzenie.

Każde wygenerowane pole jest opcjonalne, więc prawdziwe dane będą wymagały zabezpieczeń
`?? ''` na granicy renderowania. Fixture'y używają pól wymaganych; ta asymetria jest
zamierzona i należy do zadania podłączania danych.

Protokół komunikacji z (obecnie w całości zakomentowanego) `app/hooks/useChat.ts`:

- wysyłanie: `/transcend/chat/{chatId}/send`, body `{ content }`
- usuwanie: `/transcend/chat/messages/{messageId}/delete`

Obecność korzysta z osobnego kanału (`CheckPresenceRequest { userId }`,
`app/hooks/usePresence.ts`), więc flaga `online` w fixture'ach jest placeholderem dla tego
źródła danych, a nie dla źródła czatu.

---

## 6. Kroki implementacji

### Krok 1 — `app/globals.css`: dodanie tokenów

Wyłącznie addytywnie. Nic istniejącego nie jest modyfikowane ani usuwane.

**Stałe odcienie marki** (bez warstwy pośredniej motywu — to *są* kolory marki):

| Token | Wartość |
|---|---|
| `--color-hub-ink` | `#0d2b47` |
| `--color-hub-ink-deep` | `#0a3348` |
| `--color-hub-teal` | `#146b7a` |
| `--color-hub-blue` | `#2f9bcf` |
| `--color-hub-mint` | `#4ac9a0` |
| `--color-hub-lime` | `#a3e635` |

**Semantyka obsługująca motywy** — zadeklarowana w `@theme inline` jako
`var(--theme-hub-*)`, z wartościami `--theme-hub-*` zdefiniowanymi wyłącznie pod `:root`:

| Token | Wartość `:root` | Rola |
|---|---|---|
| `--color-hub-surface` | `#f3f6f4` | tło strony |
| `--color-hub-panel` | `#ffffff` | panel znajomych, nagłówek, composer |
| `--color-hub-panel-sunken` | `#f8faf9` | tło rozmowy |
| `--color-hub-field` | `#f6f9f7` | wypełnienie pola |
| `--color-hub-border` | `#eef2ef` | separatory |
| `--color-hub-on-surface` | `#0d2b47` | tekst główny |
| `--color-hub-muted` | `#7c8a92` | tekst drugorzędny |
| `--color-hub-time` | `#9aa5a0` | znaczniki czasu, placeholdery |
| `--color-hub-row-active` | `#eaf4fa` | zaznaczony wiersz znajomego |
| `--color-hub-online` | `#49b47a` | kropka obecności |
| `--color-hub-status` | `#1a8a7a` | etykieta statusu |

**Utility gradientów**, obok istniejącego `bg-gradient-start-page`:

| Utility | Wartość |
|---|---|
| `bg-hub-shell` | `linear-gradient(165deg,#0b2b3a,#0d3339 55%,#0d2b3f)` |
| `bg-hub-bubble` | `linear-gradient(135deg,#146b7a,#2f9bcf)` |
| `bg-hub-cta` | `linear-gradient(135deg,#4ac9a0,#a3e635)` |

`bg-hub-cta` nie jest używane przez stronę czatu; zostało dodane, ponieważ sposób
prezentacji głównego CTA w projekcie różni się od sposobu prezentacji przycisku Send,
a pojedyncze `--theme-primary` nie może wyrazić obu wariantów. Zdefiniowanie go tutaj
zachowuje tę informację w warstwie tokenów.

### Krok 2 — rozszerzenie trzech współdzielonych komponentów przez istniejące punkty rozszerzeń

Każdy z tych komponentów ma już punkt rozszerzenia. Każda zmiana dodaje klucz lub opcjonalny
prop i nie modyfikuje niczego istniejącego, dlatego **każde obecne miejsce użycia renderuje
się identycznie** (zasada Open/Closed w praktyce).

| Plik | Zmiana |
|---|---|
| `app/components/TextField.tsx` | Dodać ton `'chat'` do `TONE_CLASSES`. Komentarz w samym pliku mówi, że zestaw tonów „może rosnąć tylko wtedy, gdy rośnie zestaw tokenów” — Krok 1 jest wymaganym przez niego warunkiem wstępnym. |
| `app/components/Button.tsx` | Dodać wariant `'send'` wykorzystujący `bg-hub-bubble`. `hover:brightness-125` działa na gradientach bez zmian. |
| `app/components/Avatar.tsx` | Dodać opcjonalne `initial` i `color`, używane **wyłącznie** przez istniejącą gałąź bez `src`. Wybór fallbacku już należy do odpowiedzialności komponentu Avatar, więc nadal pozostaje on jednofunkcyjny. |

### Krok 3 — `app/components/PresenceAvatar.tsx` (nowy)

Opakowuje `Avatar` i nakłada kropkę statusu online. Kropka **nie** trafia do `Avatar`:
status obecności jest innym zagadnieniem niż tożsamość, dlatego stosujemy kompozycję zamiast
poszerzania odpowiedzialności istniejącego komponentu.

### Krok 4 — `app/(app)/chat/` (nowa trasa)

Wewnątrz grupy `(app)`, więc dziedziczy `Sidebar` i `Footer`.

| Plik | Odpowiedzialność |
|---|---|
| `page.tsx` | Komponent serwerowy. Odczytuje `?friend=`, rozwiązuje aktywnego znajomego i renderuje dwa panele. |
| `fixtures.ts` | Zastępczy zestaw znajomych i wiadomości oraz lokalne proste typy odzwierciedlające `SendMessageEventResponse`. **To jedyny plik, który zastępuje developer podłączający dane.** |
| `FriendRail.tsx` | Panel o szerokości 290 px: nagłówek, pole wyszukiwania, lista wierszy. |
| `FriendRow.tsx` | `PresenceAvatar` + nazwa. Element `<Link href="/chat?friend=…">`, nie przycisk. |
| `Conversation.tsx` | Nagłówek, przewijana lista wiadomości, composer. Jest właścicielem łańcucha `min-h-0`. |
| `MessageBubble.tsx` | Dymek z asymetrycznym promieniem + znacznik czasu. Przyjmuje `isMine`. |
| `Composer.tsx` | `TextField` (ton chat) + `Button` (wariant send). Nieaktywny na tej gałęzi. |

Ten podział jest celowy: gdy pojawią się prawdziwe dane, `fixtures.ts` zostaje zastąpiony,
a `page.tsx` zmienia źródło danych. Żaden komponent prezentacyjny nie jest modyfikowany.

### Krok 5 — przestylizowanie `app/components/Sidebar.tsx`

Na końcu i w sposób samodzielny — Kroki 1–4 działają bez niego.

- `bg-surface` → `bg-hub-shell`
- `w-52` (208 px) → `w-[250px]`, aby dopasować projekt
- Aktywny stan nawigacji → `bg-hub-cta` z `text-hub-ink`; nieaktywny → jasny tekst na
  ciemnym panelu; hover → subtelna biała nakładka

**Zaakceptowana konsekwencja:** `Sidebar` renderuje się na każdej stronie `(app)`, więc
profil, terms, privacy i stomp będą pokazywać ciemny panel obok treści w starej palecie,
dopóki nie zostanie wdrożone issue migracyjne.

---

## 7. Ryzyka

| Ryzyko | Dotkliwość | Ograniczenie ryzyka |
|---|---|---|
| Strona czatu nie reaguje na `ThemeToggle` (mocha/latte) — jest stałą jasną wyspą | Średnia | Nieodłączny skutek addytywnego zakresu. Ograniczony przez warstwę pośrednią `--theme-hub-*`: późniejsze dodanie nadpisań wymaga jednego bloku w `globals.css`, a nie przeglądu JSX. |
| Strona przewija się o wysokość stopki | Niska | Zaakceptowane; issue z layoutem `(chat)` naprawia to właściwie. |
| Wizualne odchylenie `Sidebar` na nietkniętych stronach | Niska | Jawnie zaakceptowane (§2.7). |
| `h-[calc(100vh-4rem)]` przestaje działać po zmianie paddingu w `(app)/layout.tsx` | Niska | Magiczna liczba jest udokumentowana w komentarzu przy miejscu użycia, ze wskazaniem na `layout.tsx`. |
| Typy fixture'ów rozjeżdżają się z `asyncapi.d.ts` po zmianach backendu | Niska | Komentarz o pochodzeniu w `fixtures.ts`; typy są regenerowane przez skrypt codegen projektu. |

---

## 8. Odłożone do issue na GitHubie

1. Wdrożenie Manrope jako fontu nagłówkowego 42Hub (przez `next/font/google`, a nie
   `@import` z eksportu)
2. Pełna migracja palety + nadpisania `.mocha` / `.latte` dla `hub-*`
3. Grupa tras `(chat)` bez stopki dla prawdziwie pełnoekranowego layoutu
4. i18n + obsługa RTL (eksport zawiera EN/PL/CS/FR/AR ze stylowaniem uwzględniającym kierunek)

---

## 9. Weryfikacja

Do uruchomienia przez właściciela repozytorium z katalogu `frontend/`:

```bash
npx tsc --noEmit
npm run lint
npx prettier --write "app/**/*.{ts,tsx,css}"
npm run dev
```

Następnie odwiedzić `/chat` oraz `/chat?friend=grzes` i porównać z ekranem `isChat`
w eksporcie projektu.

Kontrole ręczne:

- Lista wiadomości przewija się wewnętrznie; panel znajomych i composer pozostają nieruchome
- Zaznaczony wiersz znajomego ma odpowiedni odcień i utrzymuje się po przeładowaniu strony
  (stan zapisany w URL)
- Narożniki ogonków dymków wskazują właściwy kierunek dla każdej strony rozmowy
- Kropka statusu online pojawia się wyłącznie przy znajomych oznaczonych jako online
- Istniejące strony (`/[userId]`, `/terms-of-service`, `/privacy-policy`) pozostają bez zmian
  poza sidebarem
