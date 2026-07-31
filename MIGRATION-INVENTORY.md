# MIGRATION-INVENTORY.md

Inwentaryzacja wykonana w ramach Kroku 0 planu migracji do designu 42Hub.
Data: 2026-07-31

---

## 1. Wszystkie trasy w aplikacji

```
app/(app)/chat/page.tsx              ← DESIGN DOCELOWY (wzorzec)
app/(app)/privacy-policy/page.tsx
app/(app)/stomp/page.tsx
app/(app)/terms-of-service/page.tsx
app/(app)/[userId]/page.tsx
app/(auth)/login/page.tsx
app/(auth)/register/page.tsx
app/(marketing)/page.tsx
```

**Opis:** 8 tras (adresów URL) w całej aplikacji, z czego jedna (`/chat`) jest
już w docelowym wyglądzie. Pozostałe 7 to kandydaci do Kroku 4 (redesign
strona po stronie).

---

## 2. Wszystkie współdzielone komponenty

```
AccentLink.tsx    Button.tsx         PresenceAvatar.tsx   TextField.tsx
Avatar.tsx        Card.tsx           SessionCard.tsx       ThemeToggle.tsx
BareLayout.tsx    ContactBlock.tsx   Sidebar.tsx           UserList.tsx
BrandLink.tsx     Footer.tsx         StompProvider.tsx     UserSearch.tsx
                  Hero.tsx           Tag.tsx
                  LegalSection.tsx
```

**Opis:** 19 plików w `app/components`. To zgadza się z liczbą "19 plików"
wspomnianą w dokumencie planu — czyli to właśnie te komponenty odpowiadają za
większość powierzchni do zmiany.

---

## 3. Pliki nadal używające STAREJ palety (`brand-*`)

```
app/components/Hero.tsx:16   text-brand-additional-color
app/components/Hero.tsx:17   text-brand-secondary-color
app/components/Hero.tsx:21   text-brand-main-color
app/components/Hero.tsx:24   text-brand-secondary-color
app/components/Tag.tsx:7     bg-brand-additional-color, text-brand-additional-color-2
```

**Opis:** Tylko **2 pliki** (`Hero.tsx`, `Tag.tsx`) bezpośrednio odwołują się
do starej palety `brand-*`.

**Wniosek:** To bardzo dobra wiadomość — zakres bezpośredniej pracy migracyjnej
"podmień starą nazwę na nową" jest mały. Nie oznacza to, że reszta App jest
gotowa (patrz sekcja 6 niżej), ale liczba plików twardo uzależnionych od
starych nazw kolorów jest minimalna.

---

## 4. Pliki poprawnie podłączone do nowych tokenów semantycznych

```
app/(app)/stomp/page.tsx                        app/components/Card.tsx
app/(app)/layout.tsx                             app/components/UserSearch.tsx
app/(app)/[userId]/EditAvatarButton.tsx          app/components/Avatar.tsx
app/(app)/[userId]/EditDisplayNameButton.tsx     app/components/Button.tsx
app/(app)/[userId]/FriendsPanel.tsx              app/components/UserList.tsx
app/(app)/[userId]/AddFriendButton.tsx           app/components/Footer.tsx
app/(app)/[userId]/RemoveFriendButton.tsx        app/components/BareLayout.tsx
app/(app)/[userId]/page.tsx                      app/components/SessionCard.tsx
app/components/TextField.tsx                     app/components/AccentLink.tsx
app/components/ThemeToggle.tsx
app/(auth)/register/page.tsx
app/(auth)/login/page.tsx
```

**Opis:** 20 plików już używa nazw tokenów typu `bg-surface`, `text-on-surface`
itd. To są komponenty "podłączone do skrzynki bezpiecznikowej" — gdy w Kroku 3
zmienimy wartości tokenów, te pliki zmienią wygląd **automatycznie**, bez
dotykania ich kodu.

**Wniosek:** Zdecydowana większość plików (20 z ok. 27 sprawdzanych) jest już
poprawnie podłączona pod tokeny. W połączeniu z wynikiem z sekcji 3 (tylko 2
pliki na starej palecie) obraz jest taki: **większość aplikacji jest już
technicznie gotowa na przełączenie palety** — brakuje głównie samego
przełączenia (Krok 3) i dociągnięcia layoutu/wyglądu do mockupu (Krok 4), a nie
masowej podmiany nazw klas.

---

## 5. Kolory wpisane na sztywno w plikach `.tsx`

```
grep -rn --include='*.tsx' -E '#[0-9a-fA-F]{3,8}|rgb\(|hsl\(' app
→ brak wyników

grep -rn --include='*.tsx' -E '\[(#|rgb|hsl)' app
→ brak wyników
```

**Opis:** Zero wyników w obu wyszukiwaniach.

**Wniosek — to najważniejsze odkrycie Kroku 0:** W żadnym pliku `.tsx` nie ma
koloru wpisanego na sztywno (ani jako `#hex`/`rgb()`, ani jako Tailwindowy
"arbitrary value" typu `bg-[#0d2b47]`). Oznacza to, że **Krok 2 w części
dotyczącej plików `.tsx` jest w praktyce już wykonany** — nie ma "lamp
podłączonych bezpośrednio do sieci" w komponentach. Realna praca Kroku 2
sprowadza się do jednego miejsca w `globals.css` (patrz sekcja 8, linia 152).

To znacząco obniża ryzyko całej migracji (patrz R1 w rejestrze ryzyk planu —
"biały tekst na białym tle" po Kroku 3) — ten scenariusz jest tu mało
prawdopodobny, właśnie dlatego, że nie ma hardkodowanych kolorów w komponentach.

---

## 6. Pliki działające po stronie przeglądarki (`'use client'`)

```
app/(app)/stomp/page.tsx
app/(app)/[userId]/EditAvatarButton.tsx
app/(app)/[userId]/EditDisplayNameButton.tsx
app/(app)/[userId]/FriendsPanel.tsx
app/(app)/[userId]/AddFriendButton.tsx
app/(app)/[userId]/RemoveFriendButton.tsx
app/(app)/chat/Conversation.tsx
app/(app)/chat/page.tsx
app/(app)/chat/Composer.tsx
app/components/Sidebar.tsx
app/components/ThemeToggle.tsx
app/components/UserSearch.tsx
app/components/StompProvider.tsx
app/lib/theme.ts
app/(auth)/register/page.tsx
app/(auth)/login/page.tsx
app/hooks/useTheme.ts
app/hooks/HooksInstructions.md
```

**Opis:** 18 wpisów (część to pliki pomocnicze/hooki, nie tylko strony).

**Wniosek:** To lista kontrolna, nie lista do naprawy. Zapisujemy ją tu jako
punkt odniesienia — zasada z planu mówi, żeby ta lista **nie rosła** w trakcie
migracji (czyli: nie dodawaj `'use client'` do pliku, który tego jeszcze nie
miał, tylko po to, żeby "naprawić" styl).

---

## 7. Notatki TODO(design-migration) zostawione przez poprzedniego developera

```
globals.css:45    (kontekst do sprawdzenia ręcznie)
globals.css:94    → chat ignoruje ThemeToggle           → rozwiązuje KROK 5
globals.css:168   → dotyczy aktywnego elementu w Sidebar  → prawdopodobnie KROK 6/8
chat/FriendRail.tsx:19   → filtr / wyszukiwanie w rozmowach → poza zakresem designu (feature, nie kolor)
chat/Conversation.tsx:56 → `overscroll-contain`            → drobna poprawka UX, poza zakresem
chat/MessageBubble.tsx:35 → mirror rogów przy RTL          → część większego zadania i18n/RTL (KROK 7)
chat/page.tsx:63          → bug ze scrollowaniem strony     → poza zakresem designu, zanotować
chat/FriendRow.tsx:20     → `src` na sztywno `null`          → błąd danych, nie stylu — zanotować, nie naprawiać
components/Sidebar.tsx:21 → rail już renderuje się w nowym designie → informacyjne
components/Sidebar.tsx:27 → hardkodowany `white/70` jako placeholder → KROK 2/3, do podmiany na token
components/TextField.tsx:11 → `chat` jako osobny "tone" tylko dlatego że jest 2. paleta → KROK 6 (scalić z `surface`)
components/Button.tsx:15    → wariant `send` do przemyślenia względem `primary` → KROK 6
```

**Wniosek:** 12 notatek. Większość dotyczy konkretnie zaplanowanych kroków
(5, 6, 8) i nie wymaga żadnej decyzji teraz — po prostu potwierdzają, że plan
się zgadza z kodem. Kilka (`FriendRail`, `Conversation`, `page.tsx:63`,
`FriendRow.tsx:20`) to **nie są sprawy kolorystyczne** tylko błędy
funkcjonalne/UX — zgodnie z Zasadą B z planu: zanotować, nie ruszać teraz.

Jeden wpis wymaga uwagi już w Kroku 2: **`Sidebar.tsx:27`** — hardkodowana
wartość `white/70` jako tymczasowy placeholder. To jest dokładnie ten typ
"lampy podłączonej bezpośrednio do sieci", o którym mówi model skrzynki
bezpiecznikowej, mimo że nie złapały tego wcześniejsze grepy (bo `white/70`
to nazwa Tailwindowa, a nie hex).

---

## 8. Surowe kolory hex w `app/globals.css`

```
:root (oczekiwane, definicje bazowej palety):
18-23   --color-brand-*           (stara paleta, do usunięcia w Kroku 8)
53-58   --color-hub-*             (nowa paleta bazowa — OK, tu mają być surowe wartości)
89      --theme-danger: #e5484d   (OK, wartość tokenu semantycznego)
99-109  --theme-hub-*             (OK, wartości tokenów motywu czatu)

Poza :root (potencjalny problem — wymaga sprawdzenia):
144-145  gradient z surowymi hex (#7de8b4, #56876f...)
152      bg-hub-shell — gradient z surowymi hex zamiast var()  ⚠ ZNANY PRZYPADEK
```

**Opis:** Kolory w liniach 18–109 są w porządku — to jest właśnie miejsce
(`:root`), gdzie surowe wartości hex powinny się znajdować (to "żarówki" i ich
opisy w skrzynce bezpiecznikowej).

Problem dotyczy dwóch miejsc **poza** `:root`:
- **Linia 152** — to dokładnie przypadek opisany w dokumencie planu:
  `bg-hub-shell` używa surowych `#0b2b3a`, `#0d3339`, `#0d2b3f` zamiast
  `var(--color-hub-*)`, mimo że sąsiednie utilities (`bg-hub-bubble`,
  `bg-hub-cta`) robią to poprawnie.
- **Linie 144–145** — również surowe hex w gradiencie, poza `:root`. Nie było
  opisane wprost w planie, więc **wymaga sprawdzenia ręcznego**, czy to ten sam
  typ problemu (utility z hardkodowanym kolorem) — jeśli tak, dochodzi jako
  drugi punkt do Kroku 2.

**Wniosek:** Cała realna praca Kroku 2 (usuwanie hardkodowanych kolorów)
sprowadza się w praktyce do **dwóch miejsc w `globals.css`** (linia ~152, do
potwierdzenia linia ~144-145) plus jednego miejsca w `Sidebar.tsx` (`white/70`,
sekcja 7). To bardzo mały zakres w porównaniu z tym, czego można by się
spodziewać po samym opisie w planie.

---

## Podsumowanie i wnioski ogólne

| Obszar | Stan | Co to znaczy |
|---|---|---|
| Trasy | 8, w tym 1 gotowa (`/chat`) | 7 tras czeka na Krok 4 |
| Komponenty | 19 plików | zgodne z opisem w planie |
| Stara paleta `brand-*` w `.tsx` | tylko 2 pliki | mały zakres pracy |
| Tokeny semantyczne w `.tsx` | 20 plików już podłączonych | większość apki gotowa pod Krok 3 |
| Hardkodowane kolory w `.tsx` | **0** | Krok 2 w `.tsx` praktycznie zbędny |
| Hardkodowane kolory poza `.tsx` | 2 miejsca w `globals.css` + 1 w Sidebar.tsx | to jest realna praca Kroku 2 |
| TODO od poprzedniego developera | 12 | głównie potwierdzają plan, kilka to bugi spoza zakresu |

**Najważniejszy wniosek:** Aplikacja jest w znacznie lepszym stanie, niż
sugerowałby ton dokumentu planu. Nie ma masowego problemu z kolorami wpisanymi
na sztywno w komponentach — jest jedno udokumentowane miejsce w CSS (linia
152), jedno do potwierdzenia (linie 144–145) i jedno ukryte pod nazwą
Tailwindową zamiast hexem (`Sidebar.tsx:27`, `white/70`). To oznacza, że Krok 2
będzie krótki, a Krok 3 (przełączenie palety) powinno zadziałać poprawnie dla
zdecydowanej większości aplikacji od razu, bez niespodzianek typu "biały tekst
na białym tle".

Nie zmienia to kolejności kroków ani konieczności wykonania Kroku 1
(priorytetyzacja tras wg ryzyka) — tylko obniża ryzyko całego przedsięwzięcia.

---

## Odpowiedzi na 5 pytań kontrolnych

**1. Dlaczego `--color-hub-panel` wskazuje na `--theme-hub-panel`, zamiast po
prostu mieć wartość `#ffffff`?**

Bo to są dwie różne rzeczy o różnym czasie życia. `--color-hub-panel` to
*nazwa klasy* (Tailwind zamienia ją w `bg-hub-panel` podczas budowania
aplikacji — raz, na stałe). `--theme-hub-panel` to *aktualna wartość*, którą
przeglądarka odczytuje na bieżąco i może zmienić bez przebudowywania appki
(np. po kliknięciu przełącznika motywu). Gdyby `--color-hub-panel` miało
wartość wpisaną wprost, nie dałoby się jej zmienić w locie — trzeba by
przebudować całą aplikację za każdym razem, gdy ktoś zmienia motyw.

**2. Co przestałoby działać, gdyby słowo `inline` zniknęło z bloku `@theme`?**

`inline` mówi Tailwindowi: "nie zamrażaj tej wartości na stałe w plikach CSS,
tylko zostaw odwołanie do zmiennej". Bez `inline`, Tailwind wstawiłby
konkretną wartość koloru raz na zawsze podczas builda. Efekt: przełącznik
motywu przestałby działać, bo nie byłoby już żadnej zmiennej do zmienienia —
kolor byłby "zabetonowany" w CSS.

**3. Dajesz `className="bg-red-500"` do `<TextField />`, ale tło się nie
zmienia. Dlaczego i jak to naprawić?**

Bo `TextField` ma już swój własny kolor tła zdefiniowany wewnątrz komponentu.
Twoja klasa z zewnątrz i klasa wewnątrz komponentu mają tę samą "siłę"
(specyficzność) w CSS, więc o tym, która wygra, decyduje kolejność w
wygenerowanym pliku CSS — a nie kolejność, w jakiej Ty je zapisałeś. To się
może wydawać losowe. Poprawka: nie próbuj nadpisywać koloru z zewnątrz —
zmień kolor w środku komponentu `TextField.tsx`.

**4. Co jest bardziej ryzykowne: zmiana `--theme-elevated-surface` czy
`--theme-danger`?**

`--theme-elevated-surface` — bo tego tokenu używa mnóstwo różnych
komponentów w całej aplikacji (każdy "podniesiony" panel, karta, modal).
Jedna zmiana wpływa na dziesiątki miejsc naraz. `--theme-danger` jest używany
dużo węziej (komunikaty błędów, ostrzeżenia), więc zmiana ma mniejszy,
bardziej przewidywalny zasięg.

**5. Dlaczego Krok 2 musi być zrobiony przed Krokiem 3?**

Bo Krok 3 polega na zmianie *wartości* tokenów w jednym miejscu i zakładaniu,
że cała aplikacja podąży za tą zmianą automatycznie. To działa tylko wtedy,
gdy wszystkie komponenty faktycznie korzystają z tokenów, a nie mają kolorów
wpisanych na sztywno. Jeśli jakiś komponent ma hardkodowany kolor (pomija
token), to po zmianie w Kroku 3 nie zmieni wyglądu razem z resztą — i może
powstać niespójność (np. nieczytelny tekst). Krok 2 "podłącza wszystkie
lampy do skrzynki", żeby Krok 3 rzeczywiście kontrolował cały wygląd, a nie
tylko część.
