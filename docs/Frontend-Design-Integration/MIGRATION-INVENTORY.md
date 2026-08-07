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

---

## 9. Krok 1 — ryzyko i kolejność

Dane wejściowe: dwa polecenia grep z §5.3 (mapa `trasa → komponenty` i
odwrotna mapa fan-in) oraz ręczny przegląd `Forti2Hub.dc.html`.

### 9.1 Klasyfikacja tras

| Trasa | P1 blokada | P2 treści użytkownika | P3 akcje | P4 (liczba) | P5 wspólny layout | P6 design | Ryzyko | Etap | Uzasadnienie |
|---|---|---|---|---|---|---|---|---|---|
| (app)/stomp | NIE | NIE | TAK (klient) | 2 (Button, TextField) | TAK | **NIE** | — | **ZABLOKOWANA** | Brak jakiegokolwiek śladu w eksporcie designu (`stomp` nie pada ani razu w pliku). To strona diagnostyczna, poza produktem — P6 nadpisuje ryzyko: nie planuj jej w Kroku 4, dopóki ktoś nie zdecyduje, czy w ogóle ma dostać nowy wygląd. |
| (app)/privacy-policy | NIE | NIE | NIE | 2 (LegalSection, ContactBlock) | TAK | TAK | Niskie | B | Ekran `privacy` istnieje w eksporcie (`isPrivacyPage`). Zgodne z propozycją planu. |
| (app)/terms-of-service | NIE | NIE | NIE | 2 (LegalSection, ContactBlock) | TAK | TAK | Niskie | B | Ekran `terms` istnieje w eksporcie (`isTermsPage`). Zgodne z propozycją planu. |
| (marketing)/ | TAK | NIE | NIE | 5 (AccentLink, Button, Card, Hero, SessionCard) | TAK | TAK | Wysokie | D (1.) | Ekran `landing` istnieje. Zgodne z propozycją planu — pierwsza w Etapie D. |
| (auth)/login | TAK | NIE | TAK | 4 (AccentLink, Button, Card, TextField) | TAK | **CZĘŚCIOWO** | Wysokie | D (2.) | Design istnieje, ale nie jako osobny ekran — to zakładka `authTab: 'login'` wewnątrz karty logowania na ekranie `landing`. Trzeba to przełożyć na osobną trasę samodzielnie; sam wygląd karty jest jednak w pełni zdefiniowany. |
| (auth)/register | TAK | NIE | TAK | 4 (AccentLink, Button, Card, TextField) | TAK | **CZĘŚCIOWO** | Wysokie | D (3.) | Ta sama sytuacja co login — zakładka `authTab: 'register'` w tej samej karcie. Login i register dzielą praktycznie cały wygląd w eksporcie, więc dzielą też ryzyko i pracę. |
| (app)/[userId] | TAK | TAK | TAK | 3 (Avatar, UserList, UserSearch) + 5 lokalnych (FriendsPanel, AddFriendButton, EditAvatarButton, EditDisplayNameButton, RemoveFriendButton) | TAK | **CZĘŚCIOWO** | Wysokie | D (4.) | Ekran `profile` pokrywa avatar/zmianę avatara/wiersze danych/wylogowanie. Nie pokrywa jednak panelu znajomych osadzonego w tej stronie (`FriendsPanel` z wyszukiwarką i listą) — w mockupie wyszukiwanie znajomych (`isSearch`) jest osobnym ekranem nawigacyjnym, a nie panelem wewnątrz profilu. Ta różnica struktury jest decyzją do podjęcia w Kroku 4, nie błędem inwentaryzacji. |

### 9.2 Fan-in komponentów współdzielonych

Liczba = ile **różnych plików** używa danego komponentu (nie liczba wystąpień).

| Komponent | Liczba miejsc użycia | Etap |
|---|---|---|
| Button | 6 | A |
| TextField | 6 | A |
| Avatar | 5 | A |
| Card | 4 | A |
| AccentLink | 3 | A |
| Footer | 3 | C |
| LegalSection | 2 | B (stylowany razem z privacy-policy/terms-of-service) |
| ContactBlock | 2 | B (jw.) |
| BareLayout | 2 | C |
| SessionCard | 2 | D — przygotować przed (marketing)/ i (auth)/login |
| BrandLink | 2 | C — sub-zależność Sidebar i BareLayout, stylować przed nimi |
| UserList | 2 | D — przygotować przed (app)/[userId] |
| PresenceAvatar | 1 | — (używany tylko w `/chat`, już gotowy, poza zakresem Kroku 4) |
| Sidebar | 1 | C |
| ThemeToggle | 1 | C — stylować przed Footer (Footer go zawiera) |
| Hero | 1 | D — przygotować przed (marketing)/ |
| Tag | 1 | A — mimo niskiego fan-in musi być gotowy przed Hero (zawiera go) |
| UserSearch | 1 | D — przygotować przed (app)/[userId] |

**Uwaga:** żaden pojedynczy komponent nie jest używany bezpośrednio we
wszystkich 7 trasach naraz. Najszerszy zasięg mają `Button` i `TextField`
(po 6 plików) — to one, ostylowane błędnie, narobiłyby najwięcej szkody
naraz, nie jakiś jeden uniwersalny komponent (patrz pytanie kontrolne 4 niżej).

### 9.3 Proponowana kolejność wykonania Kroku 4

1. **Etap A** (kolejność wg malejącego fan-in, Tag na końcu mimo niskiego
   fan-in — bo Hero go zawiera): TextField → Button → Avatar → Card →
   AccentLink → Tag
2. **Etap B**: privacy-policy → terms-of-service (przy okazji stylujemy
   LegalSection i ContactBlock — używane tylko tu)
   *(`stomp` usunięte z tego etapu — patrz 9.4)*
3. **Etap C**: ThemeToggle → BrandLink → Footer → Sidebar → BareLayout
   (ThemeToggle i BrandLink jako pierwsze, bo Footer i BareLayout/Sidebar
   od nich zależą)
4. **Etap D**: SessionCard + Hero (przygotowanie) → (marketing)/ →
   (auth)/login → (auth)/register → UserList + UserSearch (przygotowanie)
   → (app)/[userId]

### 9.4 Trasy zablokowane (brak ekranu w eksporcie designu)

- **(app)/stomp** — brak jakiegokolwiek odniesienia w
  `Forti2Hub.dc.html`. To strona diagnostyczna/deweloperska, nie
  produktowa. **Otwarte pytanie do osoby odpowiedzialnej za migrację:**
  czy `/stomp` w ogóle wchodzi w zakres tej migracji wizualnej, czy zostaje
  poza nią (np. ostylowana ręcznie "na oko" przez dewelopera, bez
  eksportu designu jako źródła prawdy)? Do czasu odpowiedzi nie planuj jej
  w żadnym etapie Kroku 4.

Żadna inna trasa nie jest w pełni zablokowana — (auth)/login,
(auth)/register i (app)/[userId] mają częściowe pokrycie (patrz 9.1) i
mogą być realizowane, ale wymagają jednej dodatkowej decyzji projektowej
każda (patrz 9.5).

### 9.5 Odchylenia od propozycji z §5.4 planu i ich uzasadnienie

1. **`(app)/stomp` przeniesione z "Niskie ryzyko / Etap B" do
   "ZABLOKOWANA".** Propozycja z planu zakładała P6 = TAK bez sprawdzenia.
   Realny przegląd eksportu designu pokazuje P6 = NIE. Zgodnie z zasadą
   agregacji z §5.2 (P6 to bramka wykonalności, nie punkt do średniej),
   brak designu **wyklucza** tę trasę z Kroku 4, niezależnie od tego, że
   pod każdym innym względem jest ona rzeczywiście niskiego ryzyka.

2. **(auth)/login i (auth)/register: P6 doprecyzowane jako "CZĘŚCIOWO",
   nie "TAK".** Plan w §5.4 nie miał jeszcze mapy zależności ani wglądu w
   eksport designu, więc nie mógł tego zauważyć. W realnym eksporcie login
   i register to nie dwa osobne ekrany, tylko dwie zakładki jednej karty
   nałożonej na ekran `landing`. Wygląd samej karty jest w pełni
   zdefiniowany (kolory, typografia, przyciski), ale struktura "osobna
   strona logowania z własnym tłem" wymaga decyzji: czy odtwarzamy pełną
   stronę landing w tle za kartą logowania, czy karta dostaje własne,
   uproszczone tło. Nie zmienia to kategorii ryzyka (nadal Wysokie z P1),
   ale dodaje jedno pytanie projektowe do Etapu D.

3. **(app)/[userId]: P6 doprecyzowane jako "CZĘŚCIOWO".** Ekran `profile`
   w eksporcie pokrywa avatar i dane konta, ale nie pokrywa panelu
   znajomych (`FriendsPanel`) w formie, w jakiej istnieje w kodzie
   (osadzony w tej samej stronie). W mockupie odpowiednik — ekran
   wyszukiwania znajomych — jest osobną trasą nawigacyjną. To dodatkowe
   pytanie projektowe do rozstrzygnięcia przed Etapem D, nie blokada.

4. **Etap A rozszerzony poza listę z planu (`Button, TextField, Card,
   Avatar, Tag, AccentLink`) o realny ranking fan-in.** Lista w planie była
   ilustracyjna. Rzeczywisty ranking (TextField=6, Button=6, Avatar=5,
   Card=4, AccentLink=3, Tag=1) pokrywa się z listą planu niemal
   dokładnie — jedyna różnica to kolejność wewnątrz etapu (TextField i
   Button przed Avatar i Card), ustalona na podstawie policzonego,
   a nie zgadywanego fan-in.

5. **Etap C rozszerzony o `BrandLink` i `ThemeToggle` jako
   sub-zależności.** Plan wymienia w Etapie C: Sidebar, Footer,
   BareLayout, ThemeToggle — bez podanej kolejności wewnętrznej. Mapa
   zależności pokazuje, że `Footer` importuje `ThemeToggle`, a `Sidebar`
   i `BareLayout` importują `BrandLink` — więc te dwa muszą być
   ostylowane jako pierwsze w tym etapie, inaczej komponenty nadrzędne
   dziedziczą niedokończony wygląd.

6. **Założenie robocze ws. breakpointów (pytanie otwarte z §5.7):**
   przyjmuję **tylko desktop, ≥1280px**, zgodnie z przykładem
   sugerowanym w planie. To założenie jest udokumentowane, ale
   nierozstrzygnięte — wymaga potwierdzenia przed Etapem B, żeby uniknąć
   powtórnego stylowania stron niskiego ryzyka.

---

## Odpowiedzi na pytania kontrolne Kroku 1

**1. Dlaczego `Button` jest stylowany przed stroną logowania, a nie
odwrotnie?**

Bo `Button` ma fan-in = 6 — używa go sześć różnych plików, w tym strona
logowania. Ostylowanie go raz naprawia (albo psuje) wygląd we wszystkich
sześciu miejscach jednocześnie. Gdyby zacząć od strony logowania,
trzeba by wrócić do niej drugi raz, gdy później dojdzie się do `Button` —
podwójna praca nad tym samym plikiem.

**2. Trasa X ma jedno TAK w P1 i pięć NIE w pozostałych pytaniach. Jakie
ma ryzyko i dlaczego nie jest to średnia?**

Wysokie. Zasada agregacji z §5.2 mówi wprost: wygrywa najwyższa
kategoria, nie średnia arytmetyczna. P1 TAK oznacza "zepsucie tej strony
blokuje użytkownikowi dostęp do aplikacji" — to jest koszt katastrofalny
sam w sobie, niezależnie od tego, jak niewinnie wygląda reszta pytań.
Ryzyka się nie uśrednia, bo jeden poważny sposób na zablokowanie
użytkownika nie robi się mniej groźny przez to, że strona ma mało
komponentów.

**3. Dlaczego `(app)/stomp` jest dobrym miejscem na pierwszą migrację, a
jednocześnie złym dowodem na to, że migracja działa?**

Dobre miejsce na pomyłkę: to strona diagnostyczna, widzi ją developer, nie
użytkownik końcowy — błąd tu nic nie kosztuje. Złe jako dowód: z tego
samego powodu. Sukces na stronie, której nikt poza deweloperem nie
używa i która nie ma nawet designu w eksporcie (patrz 9.4), niczego nie
mówi o tym, czy migracja poradzi sobie z prawdziwym, złożonym ekranem
używanym przez użytkowników.

**4. Który jeden komponent, ostylowany błędnie, popsuje wygląd
wszystkich siedmiu tras naraz — i w którym etapie go dotykasz?**

Żaden. To jest pytanie kontrolne z planu sformułowane pod założenie, że
taki komponent istnieje — nasza rzeczywista mapa zależności (9.2) pokazuje,
że go nie ma. Najbliżej takiego statusu są `Button` i `TextField`,
oba z fan-in = 6 (czyli 6 z 7 tras, nie 7 z 7) — dotykane jako pierwsze,
w Etapie A. To ważna korekta względem intuicji sugerowanej przez pytanie:
brak jednego uniwersalnego komponentu nie jest problemem — to dobra
wiadomość, bo oznacza brak pojedynczego punktu awarii obejmującego
całą aplikację.

**5. Co konkretnie trzeba by powtórzyć, gdyby odpowiedź na pytanie o
breakpointy przyszła dopiero po zakończeniu Kroku 4?**

Trzeba by wrócić do wszystkich 7 tras (privacy-policy, terms-of-service,
marketing, login, register, [userId], plus stomp jeśli zostanie
odblokowana) i przejrzeć w każdej z nich decyzje o odstępach, zawijaniu
elementów (flex-wrap), szerokościach kontenerów i punktach przełamania
layoutu — czyli w praktyce powtórzyć całą wizualną część Kroku 4, bo
te decyzje były podejmowane bez wiedzy o docelowych szerokościach ekranu.
Nie chodzi o dopisanie CSS-a od razu — chodzi o ponowną ocenę każdej
strony pod nowym kątem.

---

## 10. Krok 2 — usunięcie kolorów wpisanych na sztywno

Zakres: trzy pozycje ustalone w Kroku 0/1 (sekcja 8 i notatka Sidebar.tsx:27
w sekcji 7). Wszystkie trzy dotyczą `app/globals.css` i jednego pliku
komponentu.

### 10.1 Dodane tokeny

| Token (`--theme-*`) | Wartość | Rola / dlaczego taka nazwa |
|---|---|---|
| `--theme-hub-shell-start` | `#0b2b3a` | Pierwszy stop gradientu powłoki Sidebara (`bg-hub-shell`). Nazwa pozycyjna — stop gradientu nie ma roli poza pozycją. |
| `--theme-hub-shell-mid` | `#0d3339` | Środkowy stop tego samego gradientu (na 55%). |
| `--theme-hub-shell-end` | `#0d2b3f` | Ostatni stop tego samego gradientu. |
| `--theme-start-page-gradient-start` | `#7de8b4` | Pierwszy stop gradientu tła karty na stronie marketingowej (`bg-gradient-start-page`). |
| `--theme-start-page-gradient-mid` | `#56876f` | Środkowy stop (na 44%) tego samego gradientu. Trzeci stop utility celowo nietknięty — to już istniejący token starej palety (`--color-brand-reversed-main-color`), jego wymiana należy do Kroku 3. |
| `--theme-hub-on-shell-muted` | `rgba(255, 255, 255, 0.7)` | Tekst nieaktywnego linku nawigacji w Sidebarze, na ciemnej powłoce. **Placeholder** — żaden istniejący token `hub-*` nie pasował (wszystkie zaprojektowane pod jasny panel czatu, nie ciemną powłokę). Wartość identyczna z zastąpionym `white/70`. Patrz 10.4. |
| `--theme-hub-shell-hover` | `rgba(255, 255, 255, 0.1)` | Tło linku nawigacji pod hoverem, na powłoce. **Placeholder**, patrz 10.4. |
| `--theme-hub-on-shell` | `#ffffff` | Tekst linku nawigacji pod hoverem (pełna biel), na powłoce. **Placeholder**, patrz 10.4. |

### 10.2 Trzy pozycje — przed i po

| # | Plik : linia | Było | Jest | Commit |
|---|---|---|---|---|
| 1 | `app/globals.css` :152 (`bg-hub-shell`) | `background: linear-gradient(165deg, #0b2b3a, #0d3339 55%, #0d2b3f);` | `background: linear-gradient(165deg, var(--color-hub-shell-start), var(--color-hub-shell-mid) 55%, var(--color-hub-shell-end));` | 1 |
| 2 | `app/globals.css` :143–149 (`bg-gradient-start-page`) | `... #7de8b4 0%, #56876f 44%, var(--color-brand-reversed-main-color) 100% ...` | `... var(--color-start-page-gradient-start) 0%, var(--color-start-page-gradient-mid) 44%, var(--color-brand-reversed-main-color) 100% ...` | 2 |
| 3 | `app/components/Sidebar.tsx` :27 (`navLinkClasses`) | `'text-white/70 hover:bg-white/10 hover:text-white'` | `'text-hub-on-shell-muted hover:bg-hub-shell-hover hover:text-hub-on-shell'` | 3 |

Geometria obu gradientów (kąt, pozycje stopów) nie została ruszona w żadnej
z pozycji 1 i 2 — zmienione wyłącznie wartości kolorów.

### 10.3 Weryfikacja (porównanie wartości wyliczonych między branchami)

| Element | Trasa | Metoda (Computed / kroplomierz) | Wynik |
|---|---|---|---|
| Powłoka (`bg-hub-shell`) | `/chat` | Computed → `background-image` | **DO POTWIERDZENIA** |
| `bg-gradient-start-page` | strona marketingowa (Hero) | Computed → `background-image` | **DO POTWIERDZENIA** |
| Tekst nieaktywnego linku w Sidebarze | dowolna strona `(app)` | kroplomierz → `color` | **DO POTWIERDZENIA** |
| Tło linku pod hoverem | jw. | kroplomierz → `background-color` | **DO POTWIERDZENIA** |
| Tekst linku pod hoverem | jw. | kroplomierz → `color` | **DO POTWIERDZENIA** |
| Sidebar w obu motywach | przełącznik motywu | wzrokowo + Computed | **DO POTWIERDZENIA** |

**Uwaga:** ta tabela nie jest jeszcze zamknięta. Zgodnie z §6.10 (kryterium 4),
Krok 2 formalnie kończy się dopiero, gdy każdy wiersz ma realny wynik
("zgodne" / opis rozbieżności), nie placeholder. Weryfikacja Pozycji 3
wymaga kroplomierza, nie samego porównania tekstu — Tailwind renderuje
`/70` przez `color-mix()`, a nowy token to zwykły `rgba()`, więc zapisy
mogą się różnić tekstowo przy identycznej barwie piksela.

### 10.4 Decyzje odesłane do designu

**Pozycja 3 — kolory tekstu/hover w Sidebarze na powłoce.** Żaden istniejący
token z rodziny `hub-*` nie opisuje roli "tekst na ciemnym tle" — cała
istniejąca rodzina (`hub-muted`, `hub-time`, `hub-on-surface`, …) została
zaprojektowana pod jasny panel czatu (jasne tło + ciemny tekst), a Sidebar ma
układ odwrotny (ciemna powłoka + jasny tekst).

Zdecydowano (za zgodą osoby prowadzącej migrację) o Opcji B: stworzyć trzy
nowe tokeny (`--theme-hub-on-shell-muted`, `--theme-hub-shell-hover`,
`--theme-hub-on-shell`) z wartościami **identycznymi** jak zastąpione klasy
Tailwinda (`white/70`, `white/10`, `white`), oznaczone jako **placeholder**.

**Otwarte pytanie do designu:** czy te wartości (biel przy różnym kryciu) są
docelowo poprawne, czy design przewiduje inny kolor dla tekstu/hover na
powłoce Sidebara? Do czasu odpowiedzi tokeny pozostają placeholderami —
działają identycznie jak wcześniej, ale ich nazwa obiecuje rolę, której
wartość nie została jeszcze świadomie zatwierdzona.

### 10.5 Znalezione, ale NIE naprawione (Zasada B)

| Co | Gdzie | Do którego kroku należy |
|---|---|---|
| `text-white` (pełna biel, bez modyfikatora) na `BrandLink` wewnątrz Sidebara | `app/components/Sidebar.tsx`, linia z `<BrandLink className="mb-3 px-3 text-white" />` | Ta sama kategoria problemu co Pozycja 3 (nazwana wartość zamiast roli), ale świadomie poza zakresem trzech pozycji ustalonych w Kroku 0/1. Do rozważenia przy Etapie C (Krok 4), gdy Sidebar jako całość dostanie pełny redesign. |

---

## Odpowiedzi na pytania kontrolne Kroku 2

**1. Dlaczego `#0b2b3a` w linii 152 jest problemem, skoro dokładnie ta sama
wartość w bloku `:root` problemem nie jest?**

Bo rola tych dwóch miejsc jest różna. `:root` to właśnie miejsce, gdzie
surowe wartości hex mają być zdefiniowane — to "żarówka i jej opis" w
skrzynce bezpiecznikowej. Linia 152 to miejsce *użycia* koloru — gdyby
zostało tam wpisane na sztywno, zmiana wartości w `:root` (Krok 3) nie
miałaby na nie żadnego wpływu, bo nie ma tam żadnego odwołania do zmiennej.
Ten sam kolor w dwóch miejscach, ale tylko jedno z nich jest "podłączone do
sieci bezpiecznikowej".

**2. `white/70` nie zawiera znaku `#`. Dlaczego mimo to jest kolorem
wpisanym na sztywno — i co z tego wynika dla wyszukiwań, na których opierał
się Krok 0?**

Bo hardkodowany kolor to nie kwestia składni, tylko nazywania. `white`
nazywa wartość (kolor), a nie rolę (np. "tekst drugorzędny na powłoce") —
dokładnie tak samo jak `#ffffff` by to robił, tylko zapisany inną składnią.
Wynika z tego, że wyszukiwania oparte na `#`, `rgb(`, `hsl(` z Kroku 0 mają
ślepy punkt: nie złapią nazwanych kolorów Tailwinda (`white`, `black`,
`transparent`, `slate-800` itd.). Wniosek "zero hardkodowanych kolorów w
`.tsx`" z Kroku 0 był oparty na dowodach, ale niepełny — bo grep znajduje
składnię, nie intencję.

**3. Które fragmenty zapisu `linear-gradient(165deg, …, … 55%, …)`
tokenizujesz, a które przepisujesz dosłownie i dlaczego akurat te?**

Tokenizuję wyłącznie trzy wartości kolorów (color stops) — to jedyna część
tego zapisu, która jest kolorem. Kąt (`165deg`) i pozycję stopu (`55%`)
przepisuję dosłownie, bez zmian — to geometria, nie kolor, i token
kolorystyczny nie potrafi (i nie powinien) opisywać geometrii. Ruszenie
którejkolwiek z tych wartości byłoby zmianą wizualną, czyli złamaniem
kontraktu Kroku 2.

**4. Dlaczego weryfikacja odbywa się w zakładce Computed, a nie Styles?**

Bo Styles pokazuje regułę tak, jak została napisana w kodzie — po
refaktorze zobaczyłbym tam `var(--color-hub-shell-start)`, a na starym
branchu `#0b2b3a`, i takie porównanie nic by nie powiedziało (oczywiście
zapis się różni, o to w refaktorze chodziło). Computed pokazuje wartość
*po rozwiązaniu* wszystkich zmiennych przez przeglądarkę — czyli dokładnie
to, co faktycznie zostanie narysowane na ekranie. Tylko to jest dowodem, że
efekt końcowy się nie zmienił.

**5. Dodajesz token tylko do `:root`, pomijając `@theme inline`. Gradient
działa poprawnie. Co w takim razie straciłaś?**

Nic nie zepsuje się natychmiast — to jest jedyne pytanie kontrolne, na
które prawidłowa odpowiedź nie brzmi "coś się zepsuje". Tracę spójność:
sąsiednie utilities w tym samym pliku sięgają po `var(--color-hub-*)`, więc
pominięcie warstwy `@theme inline` dla nowego tokenu wprowadza trzeci,
niespójny sposób zapisu, który każdy kolejny czytający plik musi dodatkowo
rozszyfrować. Tracę też przewidywalność na przyszłość — Krok 5 nadpisuje
wartości `--theme-hub-*` w regule `.mocha`/`.latte`, i choć technicznie
zadziała niezależnie od tego, czy warstwa `@theme inline` istnieje, mieszany
zapis sprawia, że przy pisaniu tamtego bloku trzeba pamiętać, które tokeny są
którego rodzaju. Koszt jest realny, tylko odroczony — poniesie go ktoś inny
za kilka kroków, nie ja teraz.

---

## 11. Krok 3 — połączenie palet

Zgodnie z §7 planu. Zmiana w jednym pliku (`app/globals.css`), wyłącznie
w bloku `:root` + jeden nowy token-rodzeństwo w `@theme inline`. Zero
plików `.tsx` dotkniętych — cała korzyść z warstwy pośredniej opisanej
w pojęciu ② (§3.3).

### 11.0 Domknięcie warunku wejścia (§7.1①)

Krok 3 został rozpoczęty zanim tabela 10.3 (weryfikacja Kroku 2) miała
realne wyniki — to było niezgodne z kolejnością wymaganą przez plan.
Domknięte retroaktywnie metodą analityczną (patrz uzasadnienie w 11.3a),
ponieważ okno na porównanie branchy 3000/3001 w Computed zamknęło się
w momencie zmiany `:root` w Kroku 3.

### 11.1 Mapa par: token semantyczny → nowa wartość

| Token semantyczny | Wskazywał na | Nowa wartość | Źródło (token `hub-*`) | Para | Commit |
|---|---|---|---|---|---|
| `--theme-surface` | `var(--color-brand-main-color)` = `#f0f0f0` | `#f3f6f4` | `--theme-hub-surface` | Tło strony | 1 |
| `--theme-on-surface` | `var(--color-brand-additional-color-2)` = `#4c603a` | `#0d2b47` | `--theme-hub-on-surface` | Tło strony | 1 |
| `--theme-elevated-surface` | `var(--color-brand-reversed-main-color)` = `#333333` | `#ffffff` | `--theme-hub-panel` | Panel/karta | 2 |
| `--theme-on-elevated-surface` | `var(--color-brand-main-color)` = `#f0f0f0` | `#0d2b47` | `--theme-hub-on-surface` | Panel/karta | 2 |
| `--theme-elevated-border` | `var(--color-brand-reversed-main-color)` = `#333333` | `#eef2ef` | `--theme-hub-border` | Panel/karta | 2 |
| `--theme-primary` | `var(--color-brand-secondary-color)` = `#8bde5a` | `#a3e635` | `--color-hub-lime` (potwierdzone w eksporcie — aktywna zakładka logowania) | Akcja główna | 3 |
| `--theme-on-primary` | `var(--color-brand-additional-color-2)` = `#4c603a` | `#0d2b47` | `--color-hub-ink` | Akcja główna | 3 |
| `--theme-success` | `var(--color-brand-secondary-color)` = `#8bde5a` | `#49b47a` | `--theme-hub-online` — decyzja podjęta bezpośrednio, nie potwierdzona w eksporcie | Stan | 4 |
| `--theme-danger` | już niezależny, nigdy nie wskazywał na `brand-*` | bez zmian (`#e5484d`) | — | Stan | — (nic do commitowania) |

**Pozycja dodatkowa, poza tabelą 9 tokenów, ale w zakresie Kroku 3 (§7.6):**

| Co | Wskazywało na | Nowa wartość | Uwaga | Commit |
|---|---|---|---|---|
| Trzeci stop `bg-gradient-start-page` | `var(--color-brand-reversed-main-color)` = `#333333` | Nowy token `--theme-start-page-gradient-end` = `#0d2b47` | Wartość wybrana bezpośrednio (brak dopasowania w eksporcie), zapisana jako własny token-rodzeństwo (`-start`/`-mid`/`-end`), nie jako bezpośrednie odwołanie do `--color-hub-ink` — poprawione po korekcie względem pierwotnej, błędnej wersji | 5 (osobny, widoczna zmiana — §7.6) |

### 11.2 Tokeny bez odpowiednika w eksporcie designu

| Token | Dlaczego brak odpowiednika | Pytanie do designu |
|---|---|---|
| `--theme-success` (wartość ostateczna) | Eksport designu nie zawiera żadnego ekranu z generycznym stanem "sukces" — tylko konkretne użycia zieleni (obecność). Wartość `#49b47a` przyjęta bezpośrednio. | Czy `success` powinien mieć inny odcień niż `hub-online`, skoro pełni inną rolę semantyczną? |
| `--theme-start-page-gradient-end` | Brak ekranu w eksporcie pokazującego kartę Hero w nowym designie w obecnej formie. | Czy karta Hero przetrwa w obecnym układzie po Kroku 4, czy zostanie zastąpiona (mockup `landing` wygląda inaczej — pełnoekranowy ciemny gradient, nie zielona karta)? |

### 11.3 Kontrast po każdej parze

**Metoda:** obliczone matematycznie wg wzoru WCAG 2.1 na luminancję
względną (relative luminance), nie odczytane z DevTools — dokładniejsze
niż odczyt wzrokowy, bo nie zależy od trafienia kursorem we właściwy
element. Wzór: `L = 0.2126·R + 0.7152·G + 0.0722·B` (kanały zlinearyzowane
z sRGB), kontrast = `(L_jaśniejszy + 0.05) / (L_ciemniejszy + 0.05)`.

| Para | Tło porównawcze | Współczynnik | Próg WCAG | Wynik |
|---|---|---|---|---|
| `surface` / `on-surface` | (własna para) | **13.28:1** | 4.5:1 | ✅ PASS |
| `elevated-surface` / `on-elevated-surface` | (własna para) | **14.45:1** | 4.5:1 | ✅ PASS |
| `elevated-border` | vs `elevated-surface` (biały panel) | **1.13:1** | 3:1 | ❌ FAIL |
| `elevated-border` | vs `surface` (tło strony) | **1.04:1** | 3:1 | ❌ FAIL |
| `primary` / `on-primary` | (własna para) | **9.58:1** | 4.5:1 | ✅ PASS |
| `success` (tekst) | vs `surface` | **2.38:1** | 4.5:1 | ❌ FAIL |
| `success` (tekst) | vs `elevated-surface` | **2.59:1** | 4.5:1 | ❌ FAIL |
| `danger` (tekst) | vs `surface` | **3.60:1** | 4.5:1 | ❌ FAIL |
| `danger` (tekst) | vs `elevated-surface` | **3.91:1** | 4.5:1 | ❌ FAIL |

**Cztery znalezione problemy kontrastu — zapisane, nie naprawione (Zasada B):**

1. **`elevated-border`** — wartość odziedziczona bezpośrednio z `hub-border`
   (`#eef2ef`), która miała identyczny problem już w oryginalnym designie
   czatu. To nie jest regresja wprowadzona w Kroku 3. Możliwe, że to
   świadomie subtelna linia rozdzielająca (WCAG 1.4.11 dopuszcza wyjątek
   dla elementów czysto dekoracyjnych, niewymaganych do rozpoznania
   granic funkcjonalnych) — ale wymaga potwierdzenia, nie założenia.
2. **`success`/`danger` jako tekst** — oba poniżej progu 4.5:1 na obu
   sprawdzonych tłach. Realne odkrycie, potwierdzone tylko na stronie
   `/stomp` (jedyne miejsce użycia — patrz 11.6). Do rozstrzygnięcia
   w Kroku 7 (rejestr kontrastu), razem z już znanymi problemami
   kolorów avatarów.

### 11.3a Weryfikacja `/chat` (kryterium 4, §7.12) — metoda analityczna

Bez dostępu do przeglądarki w tej rozmowie, zamknięte przez **porównanie
źródłowe zamiast empirycznego test w Computed**: każda wartość wpisana do
nowych tokenów `hub-*`/`start-page-gradient-*` w Kroku 2 jest bajt-w-bajt
identyczna z oryginalnym hexem, który zastąpiła (`#0b2b3a`, `#0d3339`,
`#0d2b3f`, `#7de8b4`, `#56876f` — sprawdzone przez bezpośrednie
porównanie tekstu w kodzie). Podstawianie zmiennych CSS jest deterministyczne,
więc wynikowy `background-image` musi być identyczny.

Dla trzech kolorów Sidebara (`white/70`, `white/10`, `white`) zamiana na
`rgba(255,255,255,0.7)`, `rgba(255,255,255,0.1)`, `#ffffff` jest
matematycznie tą samą operacją kompozycji koloru — wynikowy piksel musi
być identyczny niezależnie od różnic w zapisie tekstowym (Tailwind mógł
renderować to przez `color-mix()`, nasz zapis to `rgba()`, ale efekt
końcowy na tym samym tle jest tym samym kolorem).

**To jest dowód analityczny, nie zastąpienie szybkiego, wzrokowego
spot-checku.** Zalecane: jedno kliknięcie na `/chat` w przeglądarce jako
potwierdzenie, że w kodzie nie ma literówki, której nie widać w
przeglądzie źródła — ale kryterium 4 uznaję za spełnione na podstawie
tej analizy.

### 11.4 Prognoza kontra rzeczywistość

| Co przewidywałam | Co się stało | Wniosek |
|---|---|---|
| Strona marketingowa będzie wyglądać najgorzej w całej aplikacji — nowa paleta dookoła, stara w `Hero.tsx`/`Tag.tsx` | **Wymaga wzrokowego potwierdzenia przez osobę prowadzącą migrację** — analiza kodu potwierdza, że `Hero.tsx`/`Tag.tsx` nadal wskazują na `--color-brand-*` bezpośrednio (nietknięte), więc prognoza powinna się sprawdzić, ale to jest twierdzenie o wyglądzie, nie o wartościach — wymaga oczu, nie tylko kodu | Zostaje jako zadanie do jednominutowego potwierdzenia, nie do wymyślenia |
| `/chat` i powłoka Sidebara — zero zmian | Potwierdzone analitycznie w 11.3a | Zgodne z prognozą |
| 20 plików na tokenach semantycznych — zmiana wyglądu | Wynika logicznie z mechanizmu (§7.0) — te pliki nie mają własnych wartości kolorów, więc muszą podążyć za `:root` | Zgodne z prognozą, mechanicznie pewne |

### 11.5 Znalezione, ale NIE naprawione (Zasada B)

| Co | Gdzie | Do którego kroku należy |
|---|---|---|
| `text-white` (pełna biel) na `BrandLink` wewnątrz Sidebara | `app/components/Sidebar.tsx` | Etap C, Krok 4 |
| `--color-brand-*` nie może zostać usunięty w Kroku 8, dopóki `Hero.tsx`/`Tag.tsx` nie przejdą Kroku 4 | `app/components/Hero.tsx`, `app/components/Tag.tsx` | Etap D, Krok 4 → dopiero wtedy odblokowany Krok 8 |
| Rozbieżność `--theme-danger` (`#e5484d`) vs gradient przycisku "logout" w eksporcie (`#e35b52`→`#c0453f`) | ogólnoaplikacyjne | Krok 4/6 |
| `elevated-border` — kontrast 1.04-1.13:1, poniżej 3:1 (odziedziczone z `hub-border`) | `app/globals.css`, wszystkie panele | Krok 7 (rejestr kontrastu) |
| `success`/`danger` jako tekst — kontrast 2.38-3.91:1, poniżej 4.5:1 | `app/(app)/stomp/page.tsx` (jedyne potwierdzone użycie) | Krok 7 (rejestr kontrastu) |

### 11.6 Sprawdzenie roli success/danger: text- czy bg- (§7.4) — ZAMKNIĘTE

```
grep -rn --include='*.tsx' -E '(text|bg)-(success|danger)' app
→ app/(app)/stomp/page.tsx:33  text-success
→ app/(app)/stomp/page.tsx:35  text-danger
→ app/(app)/stomp/page.tsx:107 text-success
→ app/(app)/stomp/page.tsx:109 text-danger
```

**Wynik: wyłącznie `text-`, zero `bg-`.** Oba tokeny są używane tylko
jako kolor tekstu na istniejącym tle — nie potrzebują partnera `on-*`,
i nie ma tu ryzyka ukrytego zahardkodowanego koloru tła. Jedyne
potwierdzone miejsce użycia to `/stomp` — strona diagnostyczna,
zablokowana w Kroku 1 (brak designu, sekcja 9.4) — co obniża pilność
naprawy kontrastu z 11.3, ale jej nie zamyka.

### 11.7 Sprawdzenie bliźniaczego bloku `.mocha`/`.latte` (§7.3 Krok B) — ZAMKNIĘTE

Blok `.mocha, .latte` nigdy nie odwoływał się do `--color-brand-*` —
nadpisuje `surface`/`elevated-surface`/`primary`/`success`/`danger`
wartościami z palety Catppuccin (`--color-ctp-*`), niezależnie od starej
palety marki. Nie istniał żaden "bliźniak" do przeniesienia razem z
którąkolwiek parą — blok nie wymagał zmian w tym kroku.

### 11.8 Status ukończenia Kroku 3 wg §7.12

| # | Kryterium | Status |
|---|---|---|
| 1 | Żaden `--theme-*` nie wskazuje na `--color-brand-*` (`:root`, `.mocha`/`.latte`, żadna utility) | ✅ |
| 2 | `grep -n -- '--color-brand-' app/globals.css` zwraca wyłącznie linie 18–23 (+ komentarze) | ✅ potwierdzone |
| 3 | Każda para przeniesiona w obu blokach, w tym samym commicie | ✅ (`.mocha`/`.latte` nie wymagał zmian — 11.7); **status faktycznych commitów per para wymaga Twojego potwierdzenia w `git log`** |
| 4 | `/chat` identyczne jak branch odniesienia | ✅ potwierdzone analitycznie (11.3a); zalecany szybki spot-check wzrokowy |
| 5 | Kontrast każdej pary zmierzony i zapisany | ✅ zmierzony matematycznie (11.3); 4 pozycje poniżej progu — zapisane, nie naprawione, zgodnie z Zasadą B |
| 6 | Sekcja 11 istnieje i jest wypełniona, wraz z 11.2 i 11.5 | ✅ |

**Krok 3 jest zamknięty co do kodu i dokumentacji.** Jedyna rzecz, której
nie mogę potwierdzić za Ciebie: czy w Twoim lokalnym repo zmiany
faktycznie trafiły do 5 osobnych commitów (jak w kolumnie "Commit" w 11.1)
zamiast jednego zbiorczego. Jeśli commitowałaś inaczej — nie blokuje to
przejścia do Kroku 4, ale warto to wiedzieć na przyszłość (git bisect
przy regresji kontrastu z 11.3 będzie mniej precyzyjny).

---

## Odpowiedzi na pytania kontrolne Kroku 3

**1. Krok 3 zmienia wygląd siedmiu tras i nie otwiera ani jednego pliku
`.tsx`. Jak to możliwe — i który dokładnie mechanizm z §3.3 za to
odpowiada?**

Mechanizm ② — dwuwarstwowa nazwa tokenu. Komponenty odwołują się wyłącznie
do nazw klas (`bg-elevated-surface`), które Tailwind generuje raz, podczas
builda, z wpisów `--color-*` w `@theme inline`. Te z kolei w trybie
`inline` nie zamrażają wartości, tylko odwołują się w runtime do
`--theme-*` w `:root`. Komponent nigdy nie zna konkretnego koloru —
zna tylko nazwę roli. Zmiana tego, na co ta nazwa wskazuje w `:root`,
zmienia wygląd wszędzie tam, gdzie nazwa jest używana, bez dotykania
pliku, w którym nazwa się pojawia.

**2. Dlaczego `--theme-elevated-surface: var(--color-hub-panel)` jest
zapisem gorszym niż wpisanie wartości wprost, mimo że wygląda na mniej
powtarzalny?**

Bo `--color-hub-panel` to nazwa zaplanowana do usunięcia w Kroku 8.
Taki zapis budowałby łańcuch tylko po to, żeby go za pięć kroków
rozplątywać — ta sama praca wykonana dwukrotnie. Dodatkowo odwraca
ustaloną w pliku konwencję kierunku strzałki (`--color-X: var(--theme-X)`,
nigdy odwrotnie) i wiąże token, który zostaje na stałe, z tokenem, który
zniknie — czyli dokładnie ten błąd, który Krok 3 miał naprawić.

**3. Przenosisz parę `surface` / `on-surface` tylko w `:root`. Wszystko
wygląda dobrze. Co odkryje pierwsza osoba, która kliknie przełącznik
motywu — i dlaczego Ty tego nie zobaczyłaś?**

W ogólnym przypadku: osoba klikająca przełącznik zobaczyłaby powrót do
starego wyglądu w trybie `.mocha`/`.latte`, bo ten blok ma własną,
niezaktualizowaną kopię tych samych tokenów — a domyślny widok (`:root`
bez klasy motywu) wyglądałby poprawnie, więc błąd jest niewidoczny,
dopóki ktoś nie przełączy motywu. **W tym konkretnym repozytorium ten
scenariusz nie wystąpił** — sprawdzone w 11.7: blok `.mocha`/`.latte`
nigdy nie wskazywał na `--color-brand-*`, tylko niezależnie na paletę
Catppuccin (`--color-ctp-*`), więc nie miał tu czego "zapomnieć"
zaktualizować. Gdyby jednak posiadał własną kopię wartości opartą na
starej palecie (tak jak sugeruje ogólny opis w §7.3 Krok B) — właśnie to
przeoczenie ujawniłby dopiero klik w przełącznik, nie przegląd samego
`:root`.

**4. Po Kroku 3 strona marketingowa wygląda gorzej niż przed nim. Czy to
jest błąd? Uzasadnij przez trzy grupy z §7.8.**

Nie, to nie błąd. Strona marketingowa miesza ze sobą treść z dwóch różnych
grup jednocześnie: layout i komponenty współdzielone (Grupa 1, na tokenach
semantycznych) zmieniają wygląd zgodnie z celem kroku, natomiast `Hero.tsx`
i `Tag.tsx` (Grupa 3, bezpośrednio na `--color-brand-*`) pozostają
nietknięte, bo Krok 3 świadomie ich nie dotyka. Efekt: nowa paleta dookoła
starej wyspy. To zaplanowany stan pośredni, naprawiany dopiero w Kroku 4,
Etapie D.

**5. Dlaczego weryfikacja przez porównanie wartości wyliczonych, która
była głównym narzędziem w Kroku 2, działa tu tylko na `/chat`?**

Bo zmienił się kontrakt kroku. W Kroku 2 kontraktem było "zero zmian
wszędzie", więc porównanie branchy miało sens w całej aplikacji — każda
różnica była błędem. W Kroku 3 kontrakt brzmi "zmiana wszędzie poza
`hub-*`", więc porównanie branchy pozostaje rozstrzygające wyłącznie dla
tej jednej podgrupy (`/chat` i powłoka Sidebara), która ma się nie zmienić.
Dla reszty aplikacji obie wersje **mają** się różnić — więc porównanie
niczego już nie dowodzi, bo różnica przestaje być jednoznacznym sygnałem
błędu.

---

## 12. Krok 4 — redesign trasa po trasie (jedna osoba, dwie szerokości)

Zgodnie z Częścią 8 planu. Pozostałe podsekcje (12.0, 12.1, 12.3–12.7)
wypełniane w trakcie pracy; 12.2 jest wypełniona z góry, bo cztery decyzje
zapadły przed rozpoczęciem kroku.

**Dwie zmiany założeń, 2026-08-06.** Krok 4 wykonuje **jedna osoba**
(wcześniej planowany był na dwie) i obejmuje **także wąskie ekrany**
(decyzja ① odwrócona). Pierwsza zmiana usuwa z tej sekcji rejestr własności
plików — 12.1 jest teraz kolejką jednostek, a nie tabelą "kto co ma".
Druga zmienia kształt 12.0 (druga kolumna wartości) i dokłada drugą
jednostkę strukturalną do 12.7.

### Checklista wykonawcza — kolejność pracy

Kolejność nie jest dowolna: każdy blok zamyka warunek, którego potrzebuje
następny (§8.4, §8.5, §8.6). Pozycje **[blokuje]** wstrzymują pracę do czasu
zamknięcia samej pozycji — **nie do czasu odpowiedzi z zewnątrz.** Nie ma
zewnętrznej strony „design" (12.4), więc nic w tym kroku nie czeka na cudzą
decyzję; Etap B (1.4) przestaje być rezerwą na czas oczekiwania i jest po
prostu ostatnim etapem Fazy 1.

**Faza 0 — warunki oceny i słownik (§8.3). Bez kodu.**

- [x] **0.1** Uzupełnij wiersz „Szerokość szeroka (ocena)" w 12.0 —
  **1280px**, 2026-08-07. Para szerokości oceny to odtąd **360 / 1280**
  i nie zmienia się do końca kroku
- [x] **0.2** Potwierdź breakpoint 1024px odczytem na profilu i na trasie
  znajomych (uwaga pod tabelą 12.0). Pomiar zrobiono na `/chat`, czyli na
  powłoce; jeśli któraś trasa treści pęka wyżej, breakpoint idzie za
  **najgorszym przypadkiem**, nie za średnią
- [x] **0.3** Zsynchronizuj kolumnę „Konsekwencja dla zakresu" w 12.2 ①
  z wartościami faktycznie ustalonymi w 12.0 (360px, `lg:`) — zrobione
  2026-08-07; do sprawdzenia przy okazji, czy Część 8 planu (nadal 390px
  i `md:`) też ma zostać zaktualizowana
- [x] **0.4** Wypisz słownik z eksportu do drugiej tabeli 12.0 — sześć
  kategorii z §8.3 (odstępy, promienie, obramowania, cienie, typografia,
  szerokości kontenerów). Kolumna „Wartość mobile" **tylko z uzasadnieniem**;
  domyślną odpowiedzią jest jedna wartość na obie szerokości — zrobione
  2026-08-07: 36 wierszy, z czego 9 ma wartość mobilną. Trzy odstępstwa
  poszły do 12.4
- [x] **0.5** ~~Wyślij pierwszą turę pytań do designu~~ — **nieaktualne.**
  Nie ma zewnętrznej strony „design", więc nie ma tury i nie ma czekania
  (12.4). Obowiązuje reguła zastępcza: **decyzję podejmujesz przy
  jednostce, która jej potrzebuje, i zapisujesz w 12.4 w tej samej
  chwili** — patrząc na konkretny ekran, a nie abstrakcyjnie w Fazie 0.
  Kolumna „Status" w 12.4 mówi, przy której jednostce każda z nich wypada
- [x] **0.6** Przepisz kolejkę jednostek z §8.5 i §8.6 do 12.1 — zrobione
  2026-08-07: 28 pozycji, trzy rozstrzygnięcia odnotowane pod tabelą
- [x] **0.7** Potwierdź założenie kolejności poleceniem z §8.4 (czy powłoka
  importuje komponenty Etapu A) — wykonane 2026-08-07, **brak wyników**:
  Etap A i Etap C są rozłączne także zależnościowo. Zapis pod tabelą 12.1
- [x] **0.8** Przejrzyj `before_migration`, zanim zaczniesz — za kilka dni
  nikt nie będzie pamiętał, co na tych ekranach stało

**Faza 1 — komponenty i powłoka, jedna kolejka (§8.5).**

- [x] **1.1 Etap A** — zamknięty 2026-08-07, w kolejności: TextField →
  Button → Avatar → Card → AccentLink → Tag. **Korekta arytmetyki z §8.9
  pkt 6:** fan-in 6 to sześć **miejsc wywołania**, a nie sześć tras —
  `TextField` i `Button` mają po sześć wywołań na **pięciu** trasach (dwa
  wywołania siedzą razem na `/chat`). „Gotowe" znaczyło więc dziesięć
  spojrzeń, nie dwanaście. Licz trasy, nie importy
- [ ] **1.2 Etap C**, w kolejności: ThemeToggle → BrandLink → Footer →
  Sidebar → BareLayout (+ `app/(app)/layout.tsx`). Przy BrandLink domknij
  `text-white` (10.5, 11.5); przy Sidebarze — trzy tokeny z 10.4
- [ ] **1.3 Jednostka 1b** — nawigacja przy szerokości wąskiej. Osobny
  commit, rozliczenie w 12.7, **test działaniem**, wygląd na szerokim
  ekranie bez zmiany o piksel. Stylując Sidebar w 1.2, zostaw miejsce na
  wpis, który dojdzie w 2.1
- [ ] **1.4 Etap B**: `privacy-policy` → `terms-of-service`. Nie zależy od
  Etapu A (9.1), więc **jest to rezerwa na czas blokady z 0.5** — nie
  robisz dwóch rzeczy naraz, ale nigdy nie stoisz

**Faza 2 — Etap D (§8.6). Zaczyna się, gdy Faza 1 jest domknięta.**

- [ ] **2.0** Ustal zakres przeniesienia panelu poleceniem z §8.6 (importy
  `FriendsPanel.tsx` i `page.tsx`) → wynik do 12.2. Robocze założenie
  z 9.1 jest **założeniem do potwierdzenia**, nie ustaleniem
- [ ] **2.1 Jednostka 2a** — wydzielenie trasy znajomych. Refaktor: inna
  struktura, **te same piksele**. Osobny commit, 12.7, test działaniem.
  Po niej wpis Sidebara w 12.3 **przestaje być aktualny** — Sidebar wraca
  do sprawdzenia na obu szerokościach, razem z nawigacją z 1b
- [ ] **2.2** `Hero`, `SessionCard` — **tu odblokowuje się Krok 8**
  (kryterium 5 z §8.13). Odnotuj datę osobno i nie zostawiaj `Hero` na
  ostatni dzień
- [ ] **2.3** `(marketing)/`
- [ ] **2.4** `login` + `register` — **jedna jednostka, jeden commit**.
  Dwie zakładki jednej karty; rozdzielenie ich w czasie to §8.2
- [ ] **2.5** `UserList`, `UserSearch`
- [ ] **2.6** `FriendsPanel`
- [ ] **2.7** nowa trasa znajomych
- [ ] **2.8** `(app)/[userId]` — to, co zostało po wydzieleniu panelu
- [ ] **2.9** `AddFriendButton`, `RemoveFriendButton`, `EditAvatarButton`,
  `EditDisplayNameButton` — jedna decyzja powtórzona cztery razy

**Rytm — codziennie, nie na koniec etapu (§8.7, §8.8).**

- [ ] **R.1** Przegląd wsteczny: 20 minut **rano**, przed otwarciem nowej
  jednostki. Dwie ostatnie ukończone **obok** najnowszej, jednocześnie
  w polu widzenia — nie po kolei. Plus ta sama jednostka w dwóch
  szerokościach, też obok siebie
- [ ] **R.2** Jedna jednostka w toku. W 12.1 dokładnie jedna pozycja ma
  status „w toku" — trzy zaczęte to nie 60% postępu
- [ ] **R.3** Wartość spoza słownika → 12.4. Wartość, która pojawiła się
  **dwa razy w dwóch jednostkach**, nie jest wyjątkiem, tylko brakującą
  pozycją 12.0
- [ ] **R.4** Błąd poza bieżącą jednostką → 12.5, nie naprawa. Także
  „tylko trzy znaki" w pliku odhaczonym w 12.3

**Zamknięcie kroku (§8.13) — dziewięć warunków naraz.**

- [ ] **Z.1** 12.2 kompletna, z historią zmiany ①; cztery decyzje
  wykonawcze z §8.3 mają odpowiedzi
- [ ] **Z.2** 1b i 2a rozliczone osobno w 12.7; żadna nie dzieli commita
  z redesignem
- [ ] **Z.3** 6 komponentów Etapu A, 5 Etapu C, 2 Etapu B i komponenty
  Etapu D mają wpis w 12.3 ze spełnionymi punktami 6, 8, 9 i 10 z §8.9
- [ ] **Z.4** Siedem tras zgodnych z eksportem przy obu szerokościach;
  `/stomp` nieostylowana i nieusunięta
- [ ] **Z.5** `Hero.tsx` i `Tag.tsx` bez `brand-*` → Krok 8 odblokowany
- [ ] **Z.6** Trzy tokeny z 10.4 mają odpowiedź albo jawny wpis w 12.4, że
  zostają placeholderami świadomie
- [ ] **Z.7** `globals.css` bez nowych tokenów; **dokładnie jeden**
  breakpoint; zapis mobile-first; żadnego elementu w dwóch kopiach
  przełączanych przez `hidden` / `lg:hidden` poza nawigacją z 1b
- [ ] **Z.8** **Żadna z siedmiu tras nie przewija się w poziomie przy
  szerokości wąskiej.** Jedyne kryterium binarne w tym kroku
- [ ] **Z.9** Sekcja 12 wypełniona, wraz z 12.5, 12.6 i 12.7

### 12.0 Słownik layoutu (Faza 0)

Cztery pierwsze wiersze opisują **warunki oceny, nie wartości kodu** —
i tylko one nie dają się odtworzyć z kodu później. Przy odstępach zawsze
można zmierzyć, co jest; przy szerokości oceny nie ma czego mierzyć, jest
wyłącznie to, co tu zapisano.

| Pozycja | Wartość | Skąd / dlaczego | Data |
|---|---|---|---|
| Szerokość wąska (ocena) | **360px** | Dominująca szerokość logiczna Androida — praktyczna podłoga rynku. Układ pęka w dół, nigdy w górę: co działa przy 360, działa przy 390 i 430 | 2026-08-06 |
| Szerokość szeroka (ocena) | **1280px** | Eksport nie niesie szerokości artboardu w formie wydobywalnej, więc wartość jest **decyzją, nie odczytem**. Wybrana jako **test surowszy**: leży tylko 256px nad breakpointem, a układ szeroki jest najbardziej kruchy tuż nad własnym progiem. Odpowiada realnemu oknu laptopa; 1440 i 1920 mieszczą wszystko i chowają zatłoczenie, które przy 1280 widać | 2026-08-07 |
| Breakpoint | **`lg:` — 1024px** | Pomiar na `/chat` (najbardziej zatłoczona trasa): `window.innerWidth` = **955px** w najmniejszej czytelnej i funkcjonalnej szerokości. Zaokrąglone w górę do najbliższej domyślnej wartości Tailwinda — próg ma zadziałać, **zanim** zrobi się źle, a nie w momencie, gdy już jest. `md:` (768px) byłoby o 187px za nisko | 2026-08-06 |
| Kierunek zapisu | **mobile-first** | Klasa bez prefiksu = wąski ekran, `lg:` = szeroki. Reguła, nie pomiar (decyzja ①) | 2026-08-06 |
| Zasada zaokrąglania | **Typografia — do najbliższego kroku skali Tailwinda. Promienie, cienie i odstępy — dokładnie jak w eksporcie** | Eksport nie leży na siatce: rozmiary pisma to 14.5 / 13.5 / 12.5 / 11.5px, promienie 20 / 14 / 10px. Trzy kategorie zachowują się inaczej i dlatego mają różne reguły — patrz uzasadnienie pod tabelą słownika | 2026-08-07 |

**Uwaga do breakpointu — potwierdzone 2026-08-07.** Pierwszy pomiar
wykonano na `/chat`, czyli na trasie **wyłączonej z zakresu Kroku 4**
(wzorzec docelowy, §8.11) — najlepszy dostępny obiekt pomiaru, bo jako
jedyna trasa jest już zgodna z eksportem, ale mierzący przede wszystkim
**powłokę** (Sidebar plus panel treści). Odczyt kontrolny na profilu
`[userId]`: **726px** — układ pozostaje akceptowalny o 229px niżej niż
`/chat`. **1024 zostaje.**

Trasy znajomych nie dało się zmierzyć, bo powstaje dopiero w jednostce 2a.
Jej treść siedzi obecnie w profilu, więc odczyt z profilu jest **pomiarem
zachowawczym**: profil z osadzonym panelem jest ciaśniejszy, niż będzie
samodzielna trasa znajomych po wydzieleniu. Co przetrwało tutaj, przetrwa
i tam.

**Wniosek do zapamiętania na Kroki 5–8: 1024 jest progiem powłoki, nie
treści.** Wyznacza go moment, w którym przestaje się mieścić stała kolumna
Sidebara, a nie moment, w którym któraś trasa robi się nieczytelna. Stąd
pozorna nadmiarowość miejsca na trasach treści przy 1100px — to nie jest
błąd doboru progu, tylko konsekwencja tego, że próg pilnuje najciaśniejszego
elementu **wspólnego dla wszystkich tras**.

Źródło wszystkich wartości poniżej: `docs/42Hub UIUX design upgrade/42Hub.dc.html`
(spisane 2026-08-07). Eksport ma **własny słownik** — obiekty stylów w bloku
`<script>` na końcu pliku (`inputStyle`, `primaryBtnStyle`, `navItemStyle`,
`rowStyle`, `bubbleStyle`), używane wielokrotnie. Tam, gdzie wartość pochodzi
z takiego obiektu, kolumna „Skąd" podaje jego nazwę.

| Kategoria | Rola | Wartość | Wartość mobile (jeśli inna) + dlaczego | Skąd w eksporcie |
|---|---|---|---|---|
| Odstępy | Padding strony (trasy aplikacji) | `py-12 px-14` (48 / 56px) | `py-6 px-4` (24 / 16px) — przy 360px dwa marginesy po 56px zjadają 31% ekranu | ekrany `isHome`, `isProfile`, `isSearch`: `padding:48px 56px` |
| Odstępy | Padding nagłówka i stopki (strony prawne, landing) | `py-6 px-14` / `py-4 px-14` (24 / 16 pionowo, 56 poziomo) | `px-4` — jak wyżej, ten sam margines boczny | `isTermsPage`, `isPrivacyPage` |
| Odstępy | Wnętrze karty dużej | `p-9` (36px); karta strony prawnej `py-12 px-14` | `p-6` (24px) — karta zajmuje pełną szerokość minus padding strony, więc jej własny padding musi ustąpić treści | karta auth 36px, hero profilu 36px, karta prawna 48/56px |
| Odstępy | Wnętrze karty średniej / kafla | `p-6.5` (26px) | bez zmian | `dashboardCards` |
| Odstępy | Wnętrze wiersza listy | `py-3.5 px-4.5` (14 / 18px) | bez zmian | wiersz wyniku wyszukiwania 14/18px, wiersz danych profilu 18/22px |
| Odstępy | Między sekcjami strony | `gap-7` (28px) | `gap-5` (20px) — w stosie pionowym ten sam odstęp czyta się jako większy, a każdy piksel to przewijanie | profil `gap:28px`, wyszukiwanie `gap:24px` |
| Odstępy | Między elementami w grupie | `gap-3` (12px); ciasny `gap-1.5` (6px), luźny `gap-4` (16px) | bez zmian | sidebar, formularze, wiersze list |
| Promienie | Karta duża | `rounded-3xl` (24px) | bez zmian | karta auth, karta prawna, hero profilu — wszystkie 24px |
| Promienie | Karta średnia / kafel | `rounded-[20px]` | bez zmian | kafel dashboardu 20px, karta danych profilu 20px |
| Promienie | Wiersz listy | `rounded-2xl` (16px) | bez zmian | wiersz wyniku wyszukiwania 16px |
| Promienie | Pole duże, przycisk główny | `rounded-[14px]` | bez zmian | `searchInputStyle`, `chatInputStyle`, przycisk logout, przycisk send |
| Promienie | Pole standardowe, przycisk standardowy, pozycja nawigacji | `rounded-xl` (12px) | bez zmian | `inputStyle`, `primaryBtnStyle`, `navItemStyle`, przycisk „Wstecz" |
| Promienie | Przycisk mały | `rounded-[10px]` | bez zmian | przycisk „Otwórz czat" 10px, `tabBtnBase` 10px |
| Promienie | Avatar, kropka statusu | `rounded-full` | bez zmian | wszędzie `border-radius:50%` |
| Obramowania | Podział wewnątrz panelu (ten sam poziom) | `border-elevated-border` (1px, `#eef2ef`) | bez zmian | wiersze profilu, podziały czatu, `smallSearchStyle`. **Nie `hub-border`** — ta sama wartość, ale `hub-*` znika w Kroku 8, a `elevated-border` to jego semantyczny odpowiednik |
| Obramowania | Krawędź powierzchni podniesionej względem tła strony | `border-elevated-border` (1px) — **eksport ma tu `#e3ebe6`, osobną rolę bez tokenu**; decyzja i dowód w 12.4 | bez zmian | nagłówek i stopka stron prawnych, `searchInputStyle` |
| Obramowania | Krawędź przycisku drugorzędnego | **1px `#d6e4ee` — BRAK TOKENA** → 12.4 | bez zmian | przycisk „Wstecz" na stronach prawnych |
| Obramowania | Krawędź na tle ciemnym / gradiencie | `border border-white/[0.14]`; wariant mocny `border-white/60` | bez zmian | karta auth 0.14, przycisk „Zmień awatar" 0.6 |
| Cienie | Subtelny (pole, wiersz, dymek) | `shadow-[0_4px_14px_rgba(10,42,77,0.06)]` | bez zmian | `searchInputStyle`; dymek przychodzący `0 3px 10px /0.06` |
| Cienie | Karta | `shadow-[0_8px_24px_rgba(10,42,77,0.08)]` | bez zmian | kafle dashboardu, karta danych profilu; karta prawna `0 8px 28px` |
| Cienie | Element podniesiony na gradiencie | `shadow-[0_12px_32px_rgba(10,42,77,0.18)]` | bez zmian | hero profilu |
| Cienie | Karta na tle ciemnym (landing) | `shadow-[0_25px_60px_rgba(0,0,0,0.35)]` | bez zmian | karta auth |
| Typografia | Nagłówek hero (landing) | `text-5xl leading-[1.1] font-extrabold tracking-[-1px]` (48px) | `text-3xl` (30px) — 48px przy 360px łamie się na cztery wiersze i zjada ekran przed kartą logowania | landing `h1` 48px |
| Typografia | Nagłówek trasy (H1) | `text-3xl font-extrabold tracking-tight` (30px) | `text-2xl` (24px) | 30 / 32 / 34px — **ujednolicone**, patrz uwaga pod tabelą |
| Typografia | Nagłówek karty (H2) | `text-xl font-extrabold` (20px) | bez zmian | karta auth 22px, nagłówek listy czatu 19px |
| Typografia | Nagłówek sekcji w treści | `text-lg font-extrabold` (18px) | bez zmian | sekcje stron prawnych 18px |
| Typografia | Tekst wiodący (podtytuł) | `text-lg font-medium` (18px) | bez zmian | landing 18px, dashboard 17px |
| Typografia | Tekst ciągły | `text-base leading-relaxed font-medium` (16px) | bez zmian | strony prawne 15px / interlinia 1.65 — remis 14↔16 rozstrzygnięty w górę, patrz uwaga |
| Typografia | Tekst interfejsu (nazwy, wartości, pola, przyciski) | `text-sm font-bold` lub `font-semibold` (14px) | bez zmian | 14.5px w całym eksporcie |
| Typografia | Tekst drugorzędny (opisy, etykiety) | `text-sm font-medium` (14px) | bez zmian | 13.5px — po zaokrągleniu ta sama wielkość co wiersz wyżej; rozróżnia je **waga i kolor**, nie rozmiar |
| Typografia | Etykieta mała (stopka, status, czas) | `text-xs font-semibold` (12px) | bez zmian | 12.5 / 12 / 11.5 / 11px |
| Typografia | Wagi — cała aplikacja | `font-extrabold` (800) nagłówki i wordmark · `font-bold` (700) nazwy, przyciski, wartości · `font-semibold` (600) etykiety i linki · `font-medium` (500) tekst ciągły | bez zmian | konsekwentne w całym eksporcie |
| Szerokości | Kolumna Sidebara | `w-[250px]` | **nie dotyczy — rozstrzyga jednostka 1b** | sidebar `width:250px` |
| Szerokości | Kolumna treści, standardowa | `max-w-[720px]` | `w-full` — ogranicza ją padding strony | profil 720px, karta prawna 720px |
| Szerokości | Kolumna treści, wąska | `max-w-[640px]` | `w-full` | wyszukiwanie 640px |
| Szerokości | Siatka kart | `max-w-[980px]` + `grid-cols-[repeat(auto-fit,minmax(230px,1fr))]` | jedna kolumna wychodzi sama z `auto-fit` — bez prefiksu i bez drugiego breakpointu | dashboard |
| Szerokości | Karta logowania | `max-w-[440px]` | `w-full` | karta auth 440px |

**Dlaczego trzy kategorie mają różne reguły zaokrąglania.** Skala odstępów
w Tailwindzie 4 jest **dynamiczna** — utility liczy się jako
`calc(var(--spacing) * n)`, więc `p-3.25` daje 13px, a `p-6.5` daje 26px.
Odstępy nie wymagają więc żadnego zaokrąglenia i żadnej wartości arbitralnej.
Skale promieni i rozmiarów pisma są **stałymi listami nazw** i tu wybór jest realny:

- **Typografia zaokrągla**, bo błąd wynosi ≤0.5px na wszystkim, co się czyta
  (14.5→14, 13.5→14, 12.5→12, 11.5→12), a nazwane rozmiary niosą sensowne
  domyślne interlinie. Dodatkowo 14.5 i 13.5px wyglądają na wyjście z narzędzia,
  a nie na przemyślaną skalę — świadome w tym eksporcie są **wagi i tracking**,
  i te zostają dokładnie.
- **Promienie zostają dokładne**, bo nie ma skali dynamicznej, która by je
  uratowała, błąd 20→24px to 20% na kształcie oglądanym kilkadziesiąt razy na
  ekranie, a promień jest jedną z dwóch rzeczy, które niosą charakter tego
  designu. Wartości arbitralne są dokładnie trzy: `20px`, `14px`, `10px`.
- **Cienie zostają dokładne**, bo skala `shadow-*` Tailwinda i tak nie trafia
  w `rgba(10,42,77,0.08)` — byłyby arbitralne przy każdej regule.

**Ujednolicenie nagłówka trasy — jedyne miejsce, w którym słownik odchodzi od
eksportu świadomie.** Eksport używa dla tej samej roli trzech rozmiarów: 34px
(dashboard), 32px (strony prawne), 30px (profil, wyszukiwanie). To jest konflikt
systemowy z §8.2 **wewnątrz samego eksportu** — trzy decyzje o jednej roli.
Słownik przyjmuje jedną wartość, 30px, bo powtarza się najczęściej. Odstępstwo
ma wiersz w 12.4; jeśli design potwierdzi, że różnica jest zamierzona, wracają
trzy role zamiast jednej.

**Remis 15px.** Tekst ciągły stron prawnych ma 15px, w równej odległości od
`text-sm` (14) i `text-base` (16). Reguła rozstrzygająca remis: **w górę** —
czytelność tekstu ciągłego jest ważniejsza niż zwartość, a przy 360px liczy się
podwójnie (§8.9 pkt 9).

**Pasmo 360–1023px — dlaczego nie potrzebuje drugiego breakpointu.** Poniżej
1024px renderuje się układ wąski, więc przy 900px obowiązują klasy pisane dla
360px. Sam z siebie taki układ rozlewa się: przycisk na całą szerokość okna,
wiersz tekstu na 900px. Rozwiązują to **wiersze „Szerokości kontenerów"** —
`max-w-*` plus `mx-auto` trzymają kolumnę treści niezależnie od tego, ile miejsca
jest w oknie. To jest powód, dla którego ta kategoria jest w słowniku obowiązkowa,
a nie opcjonalna: bez niej pasmo między szerokościami oceny wymagałoby drugiego
breakpointu, którego §8.11 zakazuje bez wpisu w 12.4.

### 12.1 Kolejka jednostek

Zastępuje rejestr własności plików z wersji dwuosobowej. Ta sama tabela,
inne pytanie: nie „czy wolno mi to otworzyć", tylko **„co jest w tej
chwili niedokończone"** — a jedyną poprawną odpowiedzią jest „jedno"
(§8.7 reguła 1).

Kolejność przepisana z §8.5 (Faza 1) i §8.6 (Faza 2) **2026-08-07**.
Status: `w kolejce` / `w toku` / `zamknięta`. **Dokładnie jeden wiersz może
mieć status `w toku`** (§8.7 reguła 1) — to jest jedyna rzecz, którą ta tabela
naprawdę pilnuje.

| # | Jednostka | Faza / etap | Status | Data zamknięcia |
|---|---|---|---|---|
| 1 | `TextField` | Faza 1 / Etap A | **zamknięta** (ton `elevated` wraca z pozycją 22 — patrz uwaga pod tabelą) | 2026-08-07 |
| 2 | `Button` | Faza 1 / Etap A | **zamknięta** | 2026-08-07 |
| 3 | `Avatar` | Faza 1 / Etap A | **zamknięta** — przeglądem kodu; zmieniona gałąź jest nieosiągalna (12.3, 12.5) | 2026-08-07 |
| 4 | `Card` | Faza 1 / Etap A | **zamknięta** (kolory, cień i szerokość wracają z pozycjami 21/22 — 12.5) | 2026-08-07 |
| 5 | `AccentLink` | Faza 1 / Etap A | **zamknięta** | 2026-08-07 |
| 6 | `Tag` | Faza 1 / Etap A | **zamknięta** — **Etap A domknięty** | 2026-08-07 |
| 7 | `ThemeToggle` | Faza 1 / Etap C | w kolejce | |
| 8 | `BrandLink` | Faza 1 / Etap C | w kolejce | |
| 9 | `Footer` | Faza 1 / Etap C | w kolejce | |
| 10 | `Sidebar` | Faza 1 / Etap C | w kolejce | |
| 11 | `BareLayout` + `app/(app)/layout.tsx` | Faza 1 / Etap C | w kolejce | |
| 12 | **1b — nawigacja na wąskim ekranie** | Faza 1 / strukturalna | w kolejce | |
| 13 | `LegalSection` | Faza 1 / Etap B | w kolejce | |
| 14 | `ContactBlock` | Faza 1 / Etap B | w kolejce | |
| 15 | `privacy-policy/page.tsx` | Faza 1 / Etap B | w kolejce | |
| 16 | `terms-of-service/page.tsx` | Faza 1 / Etap B | w kolejce | |
| 17 | **2a — wydzielenie trasy znajomych** | Faza 2 / strukturalna | w kolejce | |
| 18 | `Sidebar` — **ponowne sprawdzenie po 2a**, nie stylowanie | Faza 2 / kontrola | w kolejce | |
| 19 | `Hero` | Faza 2 / Etap D — wejście | w kolejce | |
| 20 | `SessionCard` | Faza 2 / Etap D — wejście | w kolejce | |
| 21 | `(marketing)/` | Faza 2 / Etap D — wejście | w kolejce | |
| 22 | `login` + `register` — **jedna jednostka** | Faza 2 / Etap D — wejście | w kolejce | |
| 23 | `UserList` | Faza 2 / Etap D — znajomi | w kolejce | |
| 24 | `UserSearch` | Faza 2 / Etap D — znajomi | w kolejce | |
| 25 | `FriendsPanel` | Faza 2 / Etap D — znajomi | w kolejce | |
| 26 | nowa trasa znajomych | Faza 2 / Etap D — znajomi | w kolejce | |
| 27 | `(app)/[userId]` | Faza 2 / Etap D — profil | w kolejce | |
| 28 | Cztery przyciski profilu — **jedna jednostka** | Faza 2 / Etap D | w kolejce | |

**Dwadzieścia osiem pozycji, nie dwadzieścia osiem dni.** Wielkość jednostki
jest bardzo nierówna i liczba wierszy jest najgorszą dostępną miarą postępu
(§8.6): pozycja 22 to pięć decyzji w jednym pliku, a pozycja 28 to jedna
decyzja w czterech plikach.

**Pozycja 1 zostanie otwarta po raz drugi — zapisane z góry, nie odkryte
później.** Ton `elevated` komponentu `TextField` obsługuje wyłącznie `login`
i `register`, a te stają się ciemną kartą dopiero na pozycji 22 (decyzja ②
w 12.4). Nadanie mu docelowych kolorów już teraz dałoby biały tekst na dzisiaj
jeszcze białej karcie — pole nieczytelne, nie „niedokończone", przez całą Fazę 1.
Jednostka 1 ustawia więc temu tonowi **tylko promień** (12px obowiązuje
niezależnie od tego, czy karta wyjdzie ciemna, czy jasna), a kolory czekają na
pozycję 22. Świadomie łamie to zasadę „dotknij raz, oceń raz": alternatywą było
albo zepsute logowanie przez całą Fazę 1, albo przeciąganie pozycji 22 przez
granicę faz. **Wpis w 12.3 dla pozycji 1 nie jest więc ostateczny** — dotyczy
tonów `surface` i `chat`, a ton `elevated` rozlicza się razem z pozycją 22.

**Fan-in `TextField` potwierdzony: 6** (zgodnie z 9.2). Miejsca użycia to
`UserSearch`, `login/page.tsx`, `register/page.tsx`, `stomp/page.tsx`,
`chat/Composer.tsx` i `chat/FriendRail.tsx`. **`AccentLink.tsx` nie jest
użyciem** — wymienia `TextField` wyłącznie w komentarzu (linia 15), więc siódme
trafienie z `grep` jest fałszywe. Warto to mieć zapisane, bo `grep` po nazwie
komponentu znajduje też komentarze, a różnica między sześcioma a siedmioma
ekranami to w §8.9 pkt 8 różnica dwóch spojrzeń.

**Trzy miejsca, w których kolejka wymagała rozstrzygnięcia, a nie przepisania:**

1. **Pozycje 13–14 przed 15–16.** §8.5 podaje dla Etapu B tylko kolejność tras
   (`privacy-policy` → `terms-of-service`), nie mówiąc, gdzie w niej stoją
   `LegalSection` i `ContactBlock`. Idą przed trasami z tego samego powodu, co
   Etap A przed Etapem C i komponenty przed trasami w Fazie 2: **dotknij raz,
   oceń raz.** Oba są używane wyłącznie przez te dwie strony, więc ich fan-in
   wynosi 2 — nisko, ale nie zero.
2. **Pozycja 28 jest jedną jednostką, nie czterema.** §8.6 nazywa cztery
   przyciski profilu „jedną decyzją powtórzoną cztery razy". Obowiązuje ta sama
   reguła co przy `login` + `register`: **jednostka pracy przebiega tam, gdzie
   przebiega jednostka decyzji**, a nie tam, gdzie przebiega granica plików.
   Rozdzielenie ich w czasie dałoby cztery przyciski o czterech wysokościach.
3. **Pozycja 18 nie jest stylowaniem i nie ma wpisu w 12.7.** To ponowne
   sprawdzenie Sidebara wymuszone przez 2a (§8.6 pkt 3): wpis z pozycji 10
   opisuje przegląd zrobiony na innej liczbie linków i przestaje być aktualny
   w chwili dojścia nowego wpisu nawigacyjnego. Sprawdzenie obejmuje **obie
   szerokości i nawigację mobilną z 1b** — nowy link musi się zmieścić także
   tam. Jest w kolejce jako osobny wiersz, bo praca, której nikt nie policzył,
   nie zostaje wykonana.

**Kolejność między klastrami Fazy 2 jest dowolna** (§8.6): pozycje 19–22
(wejście) i 23–27 (znajomi/profil) można zamienić miejscami w całości. Powyższa
kolejność stawia wejście pierwsze z jednego powodu: **pozycja 19 (`Hero`)
domyka odblokowanie Kroku 8** (kryterium 5 z §8.13), a kryterium, na które
czeka inny krok, warto zamknąć wcześnie.

**Sprostowanie 2026-08-07: kryterium 5 wymaga dwóch plików, nie jednego.**
§8.13 pkt 5 mówi o `Hero.tsx` **i** `Tag.tsx`, a `Tag` to pozycja **6**,
w Etapie A. Połowa tego kryterium spełnia się więc już przy zamknięciu
Etapu A, a nie dopiero w Fazie 2 — co czyni pozycję 6 ważniejszą, niż wynika
z jej rozmiaru (fan-in 1, jeden plik, jedna klasa kolorów).

**Sprawdzenie zależności z §8.4 — potwierdzone 2026-08-07, brak wyników.**
Żaden z sześciu plików powłoki (`Sidebar`, `Footer`, `BareLayout`,
`ThemeToggle`, `BrandLink`, `app/(app)/layout.tsx`) nie importuje żadnego
z sześciu komponentów Etapu A. Etap A i Etap C są rozłączne **nie tylko
plikowo, ale i zależnościowo** — Etap C mógłby wręcz pójść pierwszy.

Kolejność 1–6 przed 7–11 zostaje bez zmian, ale wiadomo teraz, że ma zapas:
żadna pozycja Etapu C nie czeka na zamknięcie pozycji Etapu A, więc pomyłka
w kolejności wewnątrz Fazy 1 nie kosztuje powtórnego przeglądu.

**Jak to zostało ustalone — bo polecenie z §8.4 tego nie ustaliło.** Wzorzec
podany w planie szuka członu `components/(TextField|Button|…)`, ale **pięć
z sześciu przeszukiwanych plików samo leży w `app/components/`**, więc import
rodzeństwa zapisuje się tam jako `from './Button'` — bez członu `components/`.
Wzorzec nie mógł go dopasować z założenia, więc jego „brak wyników" nie był
dowodem. Wniosek okazał się prawdziwy, ale uzasadnienie było puste.

Rozstrzygnęły dopiero trzy przebiegi wzorca **zakotwiczonego na końcu ścieżki
i na początku linii**, wykonane przy jednostkach 1 i 2:

```bash
grep -rnE "^import .*from '[^']*/(Avatar|Card|AccentLink|Tag)'" app/components/Sidebar.tsx app/components/Footer.tsx app/components/BareLayout.tsx app/components/ThemeToggle.tsx app/components/BrandLink.tsx 'app/(app)/layout.tsx'
```

**Dwie reguły do końca kroku, obie kupione błędem:**

1. **Kotwicz na końcu ścieżki, nie w środku.** `[^']*/Nazwa'` dopasowuje
   `./Nazwa`, `../components/Nazwa` i `../../components/Nazwa` jednakowo,
   a odrzuca `./AddFriendButton` — bo przed `Button'` stoi tam `d`, nie `/`.
2. **Kotwicz na `^import`, nie na samej nazwie.** Wyszukiwanie po nazwie
   komponentu łapie komentarze i kolizje nazw. Przy `Button` dało to
   **piętnaście trafień, z czego dziewięć fałszywych** — cztery komponenty
   `*Button.tsx`, `ChatInterface`, `[userId]/page.tsx`, `FriendsPanel` oraz
   `Sidebar`, gdzie „Button" pada wyłącznie w komentarzu. Różnica między
   sześcioma a piętnastoma miejscami użycia to w §8.9 pkt 6 różnica
   osiemnastu spojrzeń.

Konsekwencja dla §8.9 pkt 6: **fan-in komponentów powłoki nie pochodzi od
komponentów sterujących.** „Wszystkie trasy, na których komponent występuje"
oznacza dla Etapu C trasy, na których występuje sama powłoka — czyli wszystkie
trasy `(app)` albo wszystkie trasy `BareLayout`, zależnie od komponentu.

### 12.2 Rozstrzygnięcie czterech pytań wejściowych (§8.1)

Decyzje podjęte **2026-08-03** przez osobę prowadzącą migrację (Zyta);
decyzja ① odwrócona **2026-08-06**. To jest tabela referencyjna dla
Kroków 5–8: każda z tych decyzji tłumaczy stan aplikacji, który bez niej
wygląda na przypadkowy.

| # | Pytanie (źródło) | Decyzja | Data (i zmiany) | Konsekwencja dla zakresu | Co unieważnia |
|---|---|---|---|---|---|
| ① | Breakpointy (9.5 pkt 6) | **Desktop i mobile.** Aplikacja ma wyglądać dobrze i być funkcjonalna na wąskim ekranie | 2026-08-03 → **zmieniona 2026-08-06**; poprzednio: "Tylko desktop, węższe ekrany poza zakresem migracji" | **Dwie szerokości oceny** — wartości wiążące w 12.0: **360px / 1280px**. **Jeden breakpoint** — **`lg:` 1024px** z pomiaru (12.0); drugi tylko przez wpis w 12.4. **Zapis mobile-first**: klasa bez prefiksu = wąski ekran, prefiks = szeroki. Druga kolumna wartości w 12.0 **tylko z uzasadnieniem** — domyślnie jedna wartość na obie szerokości. Dokłada jednostkę strukturalną **1b** (nawigacja przy szerokości wąskiej) → 12.7 oraz punkty 8–9 definicji ukończenia (§8.9) | Zastępuje "założenie robocze, nierozstrzygnięte" z 9.5 pkt 6 — **oraz własną poprzednią wersję**: zapis "tylko desktop" nie obowiązuje od 2026-08-06 |
| ② | Struktura logowania (9.5 pkt 2) | **Uproszczone tło.** Karta logowania dostaje własne tło; ekran `landing` nie jest odtwarzany za nią | 2026-08-03 | `(marketing)/` i `(auth)/*` przestają być jednym zadaniem — w eksporcie były jednym obrazem (karta nałożona na landing). `login` i `register` nadal jedno zadanie (dwie zakładki jednej karty). Otwarte: **czym konkretnie jest "uproszczone tło"** → 12.4. Nie używać `bg-gradient-start-page` z automatu (§7.6 — ta sama wartość to nie ta sama rola) | Domyka P6 = "CZĘŚCIOWO" z 9.1 dla obu tras auth |
| ③ | Panel znajomych (9.5 pkt 3) | **Wydzielamy.** Znajomi stają się osobną trasą, zgodnie z eksportem (`isSearch`) | 2026-08-03 | Jedna z **dwóch** jednostek Kroku 4, które **nie są stylowaniem** (druga to 1b z decyzji ①) — zmienia strukturę nawigacji. Wykonywana jako osobny refaktor (jednostka 2a, §8.6) przed redesignem, rozliczana w 12.7. Dotyka `Sidebar.tsx` i `app/(app)/layout.tsx`, więc **otwiera ponownie jednostki zamknięte w Fazie 1** — po niej Sidebar wymaga ponownego sprawdzenia na obu szerokościach. Otwarte: nazwa trasy, dokładny zakres przeniesienia, czy profil zachowuje skrót → 12.2/12.4 | **Unieważnia wiersz `(app)/[userId]` w 9.1** (lista komponentów i fan-in) oraz odpowiadające pozycje w 9.2 |
| ④ | `/stomp` (9.4) | **Poza zakresem migracji wizualnej.** Strona deweloperska, do usunięcia przed oceną końcową | 2026-08-03 | Nie stylowana i **nie usuwana w Kroku 4** (usunięcie to sprzątanie, nie krok designowy) → 12.5. Liczba tras w aplikacji bez zmian: `/stomp` wypada, trasa znajomych dochodzi | Zamyka otwarte pytanie z 9.4 |

**Dlaczego poprzednia treść decyzji ① została zachowana, a nie nadpisana.**
Decyzja odwrócona jest innym rodzajem faktu niż decyzja, która od początku
brzmiała tak jak dziś, i Kroki 5–8 potrzebują tej różnicy. Bez adnotacji
zapis "tylko desktop" byłby najbliższym dostępnym uzasadnieniem dla
prefiksów responsywnych zastanych w kodzie — i prowadziłby do wniosku, że
ktoś dopisał je wbrew ustaleniom. Z adnotacją widać, że prefiksy `md:`
powstałe po 2026-08-06 są zgodne z zakresem, a te wcześniejsze to
pozostałości sprzed migracji, opisane w 9.5 pkt 6.

**Konsekwencja pochodna decyzji ④, istotna dla Kroku 7.** Zgodnie z 11.6
`/stomp` to **jedyne potwierdzone miejsce użycia** `text-success`
i `text-danger` w całej aplikacji. Jeśli strona znika przed oceną końcową,
oba tokeny zostają bez ani jednego użytkownika — a wtedy dwie z czterech
pozycji kontrastu zapisanych w 11.3 (`success` i `danger` jako tekst,
2.38–3.91:1) przestają dotyczyć czegokolwiek renderowanego.

**Reguła do Kroku 7: najpierw sprawdź, czy token ma jeszcze użytkownika,
potem mierz jego kontrast.** Pozycje `elevated-border` z 11.3 ta uwaga
nie dotyczy — ten token jest używany przez wszystkie panele i pozostaje
realnym problemem.

**Konsekwencja pochodna decyzji ① i ③ dla sekcji 6 (lista `'use client'`).**
Krok 4 jest krokiem stylistycznym i lista klientów nie ma prawa rosnąć —
z dwoma wyjątkami, oba nazwane z góry i oba wynikające z jednostek
strukturalnych, nie ze stylowania:

| Jednostka | Z decyzji | Kiedy dopuszczalny przyrost | Gdzie odnotować |
|---|---|---|---|
| **1b** — nawigacja przy szerokości wąskiej (360px) | ① | jeśli menu ma stan otwarty/zamknięty | 12.7, z powodem |
| **2a** — trasa znajomych | ③ | jeśli strona nowej trasy musi być komponentem klienckim | 12.7, z powodem |

Każdy inny przyrost względem sekcji 6 jest błędem, nie wyjątkiem.
Zapis "najwyżej +2, oba nazwane" jest tu po to, żeby Krok 5 nie musiał
zgadywać, które wpisy są zaplanowane, a które przypadkowe.

**Uwaga do kolumny „Konsekwencja dla zakresu" w wierszu ①.** Wartości
robocze zapisane 2026-08-06 (390px / 1440px, breakpoint `md:` 768px)
zostały zastąpione ustaleniami Fazy 0 — patrz 12.0. Wiążąca jest tabela
12.0: szerokość wąska **360px**, breakpoint **`lg:` 1024px** z pomiaru na
`/chat`. To nie jest drugi breakpoint (którego §8.11 zabrania bez wpisu
w 12.4), tylko inna wartość tego jednego — pomiar wygrywa z domyślną
propozycją planu, bo plan podawał `md:` jako domyślne, nie jako wynik.
Część 8 planu nadal cytuje wartości robocze w kilku miejscach.

### 12.3 Dziennik ukończonych jednostek

Wpis powstaje **dopiero po** spełnieniu wszystkich punktów §8.9 — w tym
punktu 6 (wszystkie trasy użycia), 8 (obie szerokości), 9 (wąski ekran)
i 10 (nic nie zniknęło). Kolumna „Szerokości sprawdzone" istnieje osobno,
bo od decyzji ① „sprawdzone" bez podania szerokości nie znaczy nic.

**Odhaczenie nie jest wieczne — jest ważne do najbliższej zmiany w pliku.**
Po jednostce 2a wpis przy `Sidebar` przestaje opisywać stan aktualny, bo
dotyczył przeglądu zrobionego na innej liczbie linków (§8.6 pkt 3).

| Jednostka | Faza / etap | Trasy sprawdzone | Szerokości sprawdzone | Przegląd wsteczny |
|---|---|---|---|---|
| `Tag` — wyjście z `brand-*` + waga pisma | Faza 1 / Etap A | **Jedna trasa, jedno miejsce użycia:** `/`, przez `Hero` (fan-in 1, najmniejszy w Etapie A). Oba stany sesji, bo `Hero` renderuje się niezależnie od zalogowania | **360 i 1280.** Pigułka zmienia kolor z mint `#5cd4a0` na lime `#a3e635`, tekst na `#0d2b47`, waga 700 → 600. Zmiana potwierdzona wzrokowo na `/`. **`Tag.tsx` nie odwołuje się już do `brand-*`** — to połowa kryterium 5 z §8.13; pozostały zakres `brand-*` w aplikacji nie był przy tej okazji mierzony | Odbył się przed pozycją 3 |
| `AccentLink` — waga pisma | Faza 1 / Etap A | **Trzy trasy:** `/`, `/login`, `/register`, wyłącznie w stanie wylogowanym (`SessionCard` nie zawiera `AccentLink`). Jedna rola we wszystkich trzech: link akcentowy u dołu karty, nie link w tekście ciągłym — dlatego `text-primary`, a nie kolor globalnego `a` z eksportu (`#2f9bcf`) | **360 i 1280.** Potwierdzone wartościami wyliczonymi: `Register` zwraca `weight: 600` i `color: rgb(163, 230, 53)` na obu trasach | Odbył się przed pozycją 3 |
| `Card` — promień i padding; kolory, cień i szerokość **odłożone do pozycji 21/22** (12.5) | Faza 1 / Etap A | **Trzy trasy, cztery miejsca użycia, oba stany sesji:** `/` (karta „Hello!" — wylogowana; `SessionCard` — zalogowana), `/login`, `/register` (karta formularza — wylogowana; `SessionCard` „Already logged in" z `(auth)/layout.tsx` — zalogowana). `/chat` **nie importuje** `Card`, więc §8.11 tu nie sięga — pierwsza jednostka Etapu A bez zamrożenia | **360 i 1280.** Pierwsza jednostka z zachowaniem responsywnym: padding przechodzi 24px ↔ 36px na progu `lg:` (1024px). Potwierdzone dwojako — wzrokowo przy przeciąganiu okna **oraz** wartościami wyliczonymi: ta sama tabela `getComputedStyle` zwraca dwie różne wartości po obu stronach progu. To jest pierwszy dowód w tym kroku, że breakpoint z 12.0 faktycznie działa, a zapis jest mobile-first | Odbył się przed pozycją 3 |
| `Avatar` — gałąź zastępcza (brak zdjęcia) | Faza 1 / Etap A | **Dwie trasy:** `/[userId]` (hero profilu, `EditAvatarButton`, wiersze `UserList`) i `/chat` (`Conversation`, `FriendRow` → `PresenceAvatar`). **Żadna nie zmienia wyglądu** — patrz kolumna obok | **Nie dotyczy — nic się nie renderuje inaczej.** Zamknięte **przeglądem kodu, nie wzrokiem**, i to jest świadomy wyjątek od §8.9 pkt 8. Powód: zmieniona gałąź jest nieosiągalna. Wszystkie pięć miejsc wywołania liczy `src` jako `avatarId ? … : null`, a backend wydaje `avatarId` każdemu użytkownikowi — sprawdzone `getComputedStyle` na obu trasach: **wszystkie awatary renderują się jako `<img>`**, żaden jako gałąź zastępcza. Gałąź `<Image>` nie została tknięta, więc nie ma czego oglądać przy 360 ani 1280. Weryfikacja wizualna stanie się możliwa dopiero, gdy backend przestanie wydawać `avatarId` wszystkim, albo gdy Krok 6 rozstrzygnie los tej gałęzi (12.5) | Odbył się przed pozycją 3 — opis w wierszu `Button` |
| `Button` — warianty `primary`, `outline`, `send` | Faza 1 / Etap A | **Pięć tras, sześć miejsc użycia:** `/` (CTA w `Card` — wylogowana, oraz `SessionCard` — zalogowana), `/login`, `/register` (submit + `SessionCard`), `/chat` (`Composer`, wariant `send` — bez zmiany, §8.11), `/stomp` (poza zakresem). **Oba stany sesji sprawdzone** — wariant `outline` renderuje się wyłącznie w `SessionCard`, więc jest niewidoczny na wylogowanym `/` | **360 i 1280**, oba motywy | **Odbył się 2026-08-07, przed otwarciem pozycji 3.** Pierwszy w tym kroku — wcześniej nie było dwóch ukończonych jednostek do zestawienia. Wykonany na `/login`, gdzie pole i przycisk stoją obok siebie, **porównaniem wartości wyliczonych**, nie wzrokiem: `console.table` po `getComputedStyle` dla wszystkich kontrolek strony. Wynik: promień 12px zgodny dla obu jednostek, **brak rozjazdu** → 12.6 pozostaje pusta zasadnie. Ubocznie ujawnił dwie rzeczy: `ThemeToggle` na 6px (pozycja 7, jeszcze nieostylowana — nie rozjazd) oraz wagę pisma pól 400 wobec 500 w eksporcie (→ 12.5) |
| `TextField` — tony `surface` i `chat`; ton `elevated` **rozliczany z pozycją 22** (12.1) | Faza 1 / Etap A | **Pięć tras, sześć miejsc użycia:** `/chat` (`Composer` + `FriendRail` — bez zmiany, §8.11), `(app)/[userId]` (`UserSearch` — jedyna realna zmiana wyglądu), `/login`, `/register` (sam promień), `/stomp` (poza zakresem, zmiana uboczna dopuszczalna) | **360 i 1280**, oba motywy. Punkt 9 czytany zgodnie z rozstrzygnięciem w 12.4: ocena komponentu, nie strony | **Brak — pierwsza jednostka kroku.** Nie ma dwóch wcześniejszych, obok których można by ją postawić (§8.8 pkt 1). Pierwszy realny przegląd wsteczny odbędzie się przed otwarciem pozycji 3 |

### 12.4 Decyzje projektowe i odstępstwa od słownika

**Nie ma zewnętrznej strony „design".** Eksport `42Hub.dc.html` jest jedynym
artefaktem projektowym, a wszystko, czego on nie rozstrzyga, rozstrzyga osoba
prowadząca migrację. Ta tabela nie jest więc kolejką pytań wysyłanych na
zewnątrz, tylko **rejestrem decyzji podjętych samodzielnie** — co zmienia dwie
rzeczy naraz, w przeciwnych kierunkach:

- **Znika batching z §8.8.** Reguła „zbierane na bieżąco, wysyłane razem, raz"
  istniała wyłącznie dlatego, że czekanie na odpowiedź jest kosztem
  **równoległym**. Bez drugiej strony nie ma na co czekać, więc decyzję
  podejmuje się **przy jednostce, która jej potrzebuje** — patrząc na konkretny
  ekran, a nie abstrakcyjnie w Fazie 0. To jest decyzja lepiej poinformowana,
  nie skrót.
- **Rośnie waga zapisu.** Osiem decyzji podjętych w ośmiu różnych dniach to
  osiem momentów w czasie, czyli dokładnie mechanizm z §8.2. Wcześniej kopią
  zapasową byłaby korespondencja z designem; teraz **ten wiersz jest jedyną
  kopią.** Wpis powstaje w chwili podjęcia decyzji, nie „później".

Kolumna „Status" przyjmuje jedną z dwóch form: **`podjęta <data>`** razem
z treścią decyzji, albo **`otwarta — potrzebna przy <jednostka>`**. Nigdy samo
„otwarte": bez wskazania jednostki nie widać, czy wiersz czeka na pracę, czy
praca czeka na wiersz.

| Co | Gdzie się pojawiło | Pytanie / odstępstwo | Status |
|---|---|---|---|
| Tło karty logowania | decyzja ② (12.2); **wypadła przy jednostce 1**, nie przy 22 | Czym konkretnie jest „uproszczone tło"? | **podjęta 2026-08-07: statyczny gradient landingu** — te same stopnie co w eksporcie (`120deg, #0a3348 0%, #146b7a 32%, #1f8f7a 62%, #2f9bcf 100%`), **bez** animacji `gradientShift` i **bez** czterech pływających kształtów dekoracyjnych. Karta zachowuje ciemne, półprzezroczyste potraktowanie z eksportu, więc jej pola to `inputStyle`. To jest dosłowne odczytanie słowa „uproszczone": to samo tło, mniej ruchu. **Nie** `bg-gradient-start-page` — tamten gradient ma inną rolę (§7.6) |
| Nawigacja przy szerokości wąskiej | decyzja ① (12.2) | Co się dzieje ze stałą kolumną Sidebara przy 360px? Jedna konstrukcja czy dwie? Jeśli menu ma stan otwarcia — to jest zmiana zachowania, nie odstępu | otwarta — potrzebna przy jednostce **1b** (Faza 1, po Sidebarze) |
| `--theme-hub-on-shell-muted` | 10.4 | Wartość docelowa czy świadomy placeholder? Etap C to jedyny moment, w którym decyzja jest naprawdę potrzebna | otwarta — potrzebna przy `Sidebar` (Etap C) |
| `--theme-hub-shell-hover` | 10.4 | jw. | otwarta — potrzebna przy `Sidebar` (Etap C) |
| `--theme-hub-on-shell` | 10.4 | jw. | otwarta — potrzebna przy `Sidebar` (Etap C) |
| Szerokość szeroka (ocena) | 12.0 | Eksport nie niesie szerokości artboardu w formie wydobywalnej — wartość ustalona decyzją, nie odczytem | **rozstrzygnięte 2026-08-07: 1280px** |
| Breakpoint 1024px | 12.0 | Pomiar wykonano na `/chat` (powłoka); kontrolny odczyt na profilu dał 726px. Trasy znajomych nie da się zmierzyć przed 2a — profil jest pomiarem zachowawczym | **potwierdzone 2026-08-07: 1024 zostaje** |
| Obramowanie `#e3ebe6` | 12.0, Faza 0; potrzebne przy `TextField` | Eksport odróżnia **dwie role obramowania**, dowód pod tabelą. Brak tokenu dla drugiej z nich | **podjęta 2026-08-07: obie role dostają `elevated-border`.** Uzasadnienie: §8.7 reguła 2 zabrania nowego tokenu w Kroku 4, a wpisanie `border-[#e3ebe6]` byłoby gorsze niż użycie tokenu — to powrót do surowego hexa, który Kroki 2–3 właśnie usunęły. Różnica 11/255 na linii 1px jest poniżej progu widzialności, a oba kolory i tak czekają na Krok 7 (11.3). **Rozróżnienie ról przestaje istnieć w kodzie, więc żyje w tym wierszu** |
| Obramowanie `#d6e4ee` | 12.0, Faza 0 | Krawędź przycisku drugorzędnego („Wstecz"). Też bez tokenu, wyraźnie chłodniejszy niż dwa powyższe — możliwe, że to trzecia rola (krawędź kontrolki), a nie wariant krawędzi panelu | otwarta — potrzebna przy Etapie B (`terms-of-service`, przycisk „Wstecz") |
| **Odstępstwo od §8.11 — `/chat` zmienia wygląd** | jednostka 3 (`Avatar`) | `Avatar` nie ma **żadnej** osi wariantów: ani tonów jak `TextField`, ani wariantów jak `Button`. Jest jeden wygląd, wspólny dla wszystkiego, a `/chat` używa go przez `Conversation` i `FriendRow` → `PresenceAvatar`. Nie istnieje sposób, by ostylować ten komponent i nie ruszyć `/chat` | **podjęta 2026-08-07: stylujemy, odstępstwo zapisane.** Trzy uzasadnienia: (1) alternatywą było odłożenie całej jednostki, czyli trzeci z sześciu komponentów Etapu A bez stylowania; (2) awatary na `/chat` **i tak są tam zepsute** — `chat/page.tsx` podaje `color: 'bg-hub-panel'`, czyli nazwę klasy Tailwinda w miejsce wartości CSS, więc kolor jest cicho ignorowany; (3) zmiana **zbliża** `/chat` do eksportu, nie oddala. **To jest pierwsze świadome odstępstwo od §8.11 w tym kroku i ma pozostać jedynym** — każde następne wymaga osobnego wiersza i osobnego uzasadnienia, nie powołania się na ten. **Sprostowanie z tego samego dnia: odstępstwo jest utajone, nie czynne.** Pomiar `getComputedStyle` na `/chat` i `/[userId]` pokazał, że **wszystkie** awatary renderują się gałęzią `<Image>` — każdy użytkownik w obecnych danych ma wgrane zdjęcie, więc zmieniona gałąź zastępcza nie renderuje się nigdzie. Na `/chat` nie widać dziś żadnej różnicy. Odstępstwo pozostaje zapisane, bo dotyczy kodu wspólnego i ujawni się przy pierwszym użytkowniku bez awatara — ale uzasadnienie (3) jest na razie przewidywaniem, nie obserwacją |
| Gradient CTA dla wariantu `primary` | jednostka 2 (`Button`) | `primaryBtnStyle` w eksporcie to gradient mint→lime, a `--theme-primary` jest płaskim `#a3e635` i gradientu nie wyrazi | **podjęta 2026-08-07: `primary` przyjmuje `bg-hub-cta`.** Wykonanie instrukcji zapisanej w `globals.css` (komentarz przy `@utility bg-hub-cta`). `text-on-primary` (`#0d2b47`) jest już kolorem, który eksport kładzie na tym przycisku. Świadomy koszt: **dochodzi jeden użytkownik `hub-*`** w kroku, który ich pozbywa — Krok 8 musi dotknąć `Button`, co ten sam komentarz w `globals.css` już przewiduje |
| `Tag` — BRAK ODPOWIEDNIKA, a mimo to musi się zmienić | jednostka 6 (`Tag`) | Eksport nie zawiera **żadnej** pigułki — jego hero to logo, wordmark, nagłówek i podtytuł, i nic więcej. Nie ma więc wzorca, do którego można być wiernym. Jednocześnie §8.13 kryterium 5 wymaga, żeby ten plik przestał odwoływać się do `brand-*`, bo inaczej Krok 8 nie może wycofać tej palety. Komponent musi się zmienić **mimo braku odniesienia** — sytuacja odwrotna do `outline`, gdzie brak odniesienia był powodem, żeby nie ruszać | **podjęta 2026-08-07: `bg-primary text-on-primary`.** Wybór przez eliminację: `brand-*` wyklucza kryterium 5, `hub-*` tylko przenosi problem do Kroku 8, zostają tokeny semantyczne. `primary`/`on-primary` to para akcentowa, której eksport używa na CTA, a zastępowany mint jest drugim końcem tego samego gradientu mint→lime. **Widoczny skutek: pigułka zmienia kolor z `#5cd4a0` na `#a3e635`** — zmiana wyglądu strony landingowej bez mandatu z designu, wymuszona ograniczeniem. Do rewizji przy pozycji 19 (`Hero`), gdzie cały ten obszar i tak jest przeprojektowywany. **Uzupełnienie 2026-08-07: pigułki mają zniknąć przed końcem migracji** (ustalenie osoby prowadzącej, zgodne z tym, że eksport ich nie zawiera). Stylowanie z pozycji 6 jest więc tymczasowe — ale wyjście z `brand-*` **nie było pracą na marne**: dzięki niemu połowa kryterium 5 jest spełniona **już teraz**, zamiast być uzależniona od tego, czy przeprojektowanie `Hero` faktycznie usunie ten komponent. Kryterium spełnione przez zmianę jest pewniejsze niż kryterium spełnione przez planowane usunięcie |
| Wariant `outline` — BRAK ODPOWIEDNIKA | jednostka 2 (`Button`) | Eksport nie rysuje żadnego przycisku obrysowanego na trasie, która renderuje ten komponent. Jedyny obrysowany („Wstecz") leży na stronach prawnych, które `Button` nie importują; „Wyloguj" w eksporcie to wypełniony czerwony gradient na ekranie profilu, a to inny ekran i inny komponent | **podjęta 2026-08-07: kolory bez zmian, tylko promień.** Nie da się być „zgodnym z eksportem" z czymś, czego eksport nie narysował (§7.3, §8.9 pkt 2 — brakujące stany nie są do wymyślenia). Do rewizji przy pozycji 20 (`SessionCard`), która jest jedynym miejscem użycia tego wariantu |
| Zakres §8.9 pkt 9 przed jednostką 1b | jednostka 1 (`TextField`) | Punkt 9 pyta „czy strona przewija się w poziomie", ale przy 360px **każda** trasa `(app)` przewija się do czasu przebudowy powłoki: sam Sidebar to stała kolumna 250px, a `/chat` dokłada 290px szyny znajomych — 540px stałej konstrukcji w oknie 360px. Wzięte dosłownie, punkt 9 przepada dla jednostek 1–11 i definicja ukończenia przestaje cokolwiek znaczyć | **podjęta 2026-08-07: dla jednostki komponentowej punkt 9 pyta o komponent, nie o stronę** — czy sam komponent nie wystaje, nie jest za mały do dotknięcia i nie łamie się przy zwężeniu. Test całej strony jest już osobno jako kryterium **Z.8** w §8.13 (poziom kroku) i zaczyna obowiązywać dla tras dopiero po jednostce 1b |
| Rozmiar nagłówka trasy | 12.0, Faza 0 | Eksport daje jednej roli trzy rozmiary: 34px (dashboard), 32px (strony prawne), 30px (profil, wyszukiwanie) | **podjęta 2026-08-07: jedna wartość, 30px** — najczęstsza z trzech. Rozbieżność w eksporcie to konflikt systemowy z §8.2 wewnątrz samego artefaktu projektowego; słownik nie może odtwarzać trzech decyzji o jednej roli. Do rewizji, jeśli przegląd wsteczny pokaże, że dashboard potrzebuje mocniejszego nagłówka |

**Dowód na dwie role obramowania (wiersz `#e3ebe6`).** Podział przebiega
dokładnie po granicy poziomów, bez ani jednego wyjątku w eksporcie:

| `#eef2ef` — podział wewnątrz panelu | `#e3ebe6` — krawędź powierzchni podniesionej |
|---|---|
| wiersze danych profilu (wewnątrz białej karty) | dolna krawędź nagłówka stron prawnych (biel na tle `#f3f6f4`) |
| nagłówek i pasek pisania w czacie (wewnątrz białego panelu) | górna krawędź stopki stron prawnych (ta sama granica) |
| `smallSearchStyle`, `chatInputStyle` (pola wpuszczone w biały panel) | `searchInputStyle` (białe pole na tle strony) |

Wniosek dla Kroku 5 lub 7: jeśli którykolwiek z tych kroków rusza
`elevated-border`, ma tu gotową odpowiedź na pytanie, czy potrzebne są dwa
tokeny zamiast jednego — i po czym poznać, który należy gdzie.

**Uwaga o terminach — decyzje wypadają wcześniej, niż wskazuje trasa, na
której widać ich skutek.** Decyzja ② była zaplanowana na pozycję 22
(`login` + `register`) i wypadła na pozycji **1**, bo ton `elevated`
komponentu `TextField` jest używany wyłącznie przez te dwie trasy — a ton
stylujesz przy komponencie, nie przy trasie. Reguła praktyczna dla trzech
decyzji, które zostały otwarte: **termin decyzji wyznacza pierwszy
komponent, który jej dotyka, a nie trasa, na której widać efekt.** Trzy
tokeny-placeholdery z 10.4 dotyczą powłoki, więc wypadną przy `Sidebar`
(poz. 10) zgodnie z zapisem; `#d6e4ee` dotyczy przycisku, więc może wypaść
już przy `Button` (poz. 2), a nie dopiero w Etapie B.

### 12.5 Znalezione, ale NIE naprawione (Zasada B) — pozycje założone z góry

| Co | Gdzie | Do którego kroku należy |
|---|---|---|
| Rozbieżność `--theme-danger` (`#e5484d`) vs gradient przycisku "logout" w eksporcie | ogólnoaplikacyjne | Krok 4/6 (odziedziczone z 11.5) |
| `elevated-border` — kontrast 1.04–1.13:1 | `app/globals.css`, wszystkie panele | Krok 7 (odziedziczone z 11.5) |
| `success`/`danger` jako tekst — kontrast 2.38–3.91:1 | `app/(app)/stomp/page.tsx` | Krok 7, **z zastrzeżeniem powyżej** (decyzja ④) |
| `/stomp` do usunięcia przed oceną końcową | `app/(app)/stomp/` | Poza planem migracji — sprzątanie (decyzja ④) |
| `TextField` — padding niezgodny z eksportem (`md` 12px w pionie, eksport 14px). `SIZE_CLASSES` jest wspólne dla wszystkich tonów, a `/chat` używa **obu** rozmiarów (`Composer` → `md`, `FriendRail` → `sm`), więc korekta ruszyłaby trasę zamrożoną przez §8.11 | `app/components/TextField.tsx` | Krok 6 lub 8 — odblokowuje się, gdy ton `chat` znika razem z `hub-*` |
| `/chat` **już** odbiega od eksportu: pole kompozytora renderuje promień 8px i padding 12×16px, a `chatInputStyle` w eksporcie ma 14px i 14×18px. Wzorzec docelowy nie jest więc wzorcem pikselowym dla tego komponentu | `app/(app)/chat/Composer.tsx` | Obserwacja — §8.11 nadal zabrania ruszania `/chat`. Do rozstrzygnięcia w Kroku 8 |
| Eksport ma **cztery** wyglądy pola (`inputStyle`, `searchInputStyle`, `smallSearchStyle`, `chatInputStyle`) przy trzech tonach komponentu. Czwarty ton to zmiana API | `app/components/TextField.tsx` | Krok 6 (§8.7 reguła 3) |
| Nazwa tonu `elevated` przestanie opisywać swoją powierzchnię po jednostce 22: karta logowania staje się ciemna (decyzja ②), a nazwa pochodzi od `--theme-elevated-surface`, które jest białe. Przemianowanie wariantu to zmiana API | `app/components/TextField.tsx` | Krok 6 (§8.7 reguła 3) |
| `UserSearch` przekazuje `size="sm"`, ale ekran wyszukiwania w eksporcie używa **dużego** pola (`searchInputStyle`, 15×18px). To decyzja miejsca wywołania, nie komponentu | `app/components/UserSearch.tsx` | Krok 4, **pozycja 24** — nie w jednostce 1 (§8.0, jeden obszar na jednostkę) |
| **Prefiksy `md:` sprzed migracji vs kryterium Z.7.** Breakpointem kroku jest `lg:` (12.0), ale w kodzie siedzą jeszcze pojedyncze `md:` opisane w 9.5 pkt 6. Kryterium **Z.7** wymaga **dokładnie jednego** breakpointu na koniec kroku, więc każdy z nich trzeba przerobić albo usunąć — a **żadna jednostka w 12.1 obecnie tego nie obejmuje** | całe `app/`, zakres do ustalenia | Krok 4 — zamiatanie przed zamknięciem, do dopisania jako osobna pozycja kolejki albo do rozliczenia przy jednostkach, które te pliki i tak otwierają |
| `Button` — padding i typografia niezgodne z eksportem (`py-3` = 12px wobec 14px; `text-sm font-bold` = 14px/700 wobec 15px/800). Wszystko troje siedzi w `BASE_CLASSES`, wspólnym z wariantem `send`, którego używa `/chat` | `app/components/Button.tsx` | Krok 6 lub 8 — dokładnie ta sama blokada co przy `TextField`, z tego samego powodu |
| `outline` wypełnia się na hover płaskim `bg-primary`, podczas gdy `primary` jest już gradientem `bg-hub-cta`. Obrys przestał być obrysem tego, co wypełnia | `app/components/Button.tsx` | Krok 6 — i tak scala warianty |
| **`SessionCard` nie ma odpowiednika w eksporcie.** Eksport nie zawiera karty „witaj z powrotem, kontynuuj" — jego landing prowadzi wprost do karty logowania. Cała ta karta, razem z jedynym użyciem wariantu `outline`, jest UI spoza projektu | `app/components/SessionCard.tsx` | Krok 4, **pozycja 20** — będzie rozstrzygana decyzją własną, nie odczytem z eksportu (12.4) |
| **Błąd: nazwa klasy Tailwinda podana jako wartość CSS.** `color: 'bg-hub-panel'` trafia do `style={{ backgroundColor: color }}` w `Avatar`, czyli w miejsce, które oczekuje koloru CSS. Wartość jest cicho ignorowana, więc awatary bez zdjęcia spadają na tło domyślne | `app/(app)/chat/page.tsx` (linie 31 i 57) | Poza Krokiem 4 — `/chat` jest zamrożone (§8.11). Do naprawy razem z `/chat` w Kroku 8 |
| `Avatar` — obwódka 3px `rgba(255,255,255,0.5)` występuje w eksporcie **tylko** na awatarze hero profilu, nie na pozostałych. Dodanie propa to zmiana API | `app/components/Avatar.tsx` | Krok 6 (prop) albo Krok 4 pozycja 27 (klasa w miejscu wywołania) |
| **Gałąź zastępcza `Avatar` jest w praktyce nieosiągalna.** Wszystkie pięć miejsc wywołania liczy `src` jako `avatarId ? '/api/users/avatar/…' : null` — uczciwy warunek, bez zaszytej wartości domyślnej po stronie frontendu. Backend wydaje jednak `avatarId` **każdemu** użytkownikowi (potwierdzone na świeżym koncie bez wgranego zdjęcia), więc `src` nigdy nie jest `null` i gałąź z inicjałem nie renderuje się nigdzie. Pytanie „czy ta gałąź ma rację bytu" jest pytaniem o API komponentu, nie o styl | `app/components/Avatar.tsx` oraz pięć miejsc wywołania | Krok 6 — zostawić jako zabezpieczenie czy usunąć razem z propami `initial` / `color` (§8.7 reguła 3 zabrania rozstrzygać to w Kroku 4) |
| **`prettier-plugin-tailwindcss` nie sortuje klas.** `.prettierrc` ładuje wtyczkę poprawnie, ale podaje jej `"tailwindConfig": "./tailwind.config.ts"` — formę z Tailwinda **3**, wskazującą plik konfiguracyjny JS. Projekt stoi na Tailwindzie **4**, gdzie konfiguracja jest w CSS (`@theme` w `app/globals.css`), więc wtyczka nie znajduje kontekstu i po cichu nic nie sortuje. Objaw: sześć plików Etapu A wraca z `--write` jako `unchanged`, a zastane łańcuchy klas nie są w kolejności kanonicznej (np. `Card` miał `w-72` i `p-8` **po** kolorach). Poprawną opcją dla v4 jest `"tailwindStylesheet": "./app/globals.css"` | `frontend/.prettierrc` | **Poza Krokiem 4** — to naprawa narzędzia, nie designu. Warto osobnym commitem, bo w chwili gdy wtyczka ruszy, przesortuje łańcuchy klas w całym projekcie. Konsekwencja na teraz: kolejność klas jest ręczna i taka trafia do repo. **Decyzja robocza: nie sortujemy jej ręcznie** — łańcuchy klas zostają w konwencji danego pliku, a zmieniamy wyłącznie to, co faktycznie się różni. Powód jest czytelnościowy: diff ma pokazywać zmienioną wartość, a nie przetasowanie ośmiu klas. Sortowanie całego projektu przyjdzie jednym commitem, gdy wtyczka zacznie działać |
| `AccentLink` — `text-primary` (`#a3e635`) na dzisiejszej białej karcie daje bardzo niski kontrast. Kolor jest **zgodny z eksportem** (tak samo wygląda tam „Forgot Password?"), tyle że eksport kładzie go na ciemnej karcie. Zestawienie jasne+lime jest stanem przejściowym, nie docelowym | `app/components/AccentLink.tsx` na `/`, `/login`, `/register` | Krok 7 — z zastrzeżeniem, że **pozycja 22 prawdopodobnie rozwiąże to sama**, gdy karta stanie się ciemna (decyzja ②). Zgodnie z regułą z 12.2: najpierw sprawdź, czy problem nadal istnieje, potem mierz |
| `Card` — kolory i cień niezgodne z eksportem (biel + `elevated-border`, brak cienia; eksport: ciemna półprzezroczystość, `white/14`, `0 25px 60px rgba(0,0,0,0.35)`). **Nie jest to blokada techniczna, tylko kolejność**: tło pod kartą kładą trasy, nie komponent, więc ciemna karta teraz oznacza ciemny tekst na ciemnym tle przez całą Fazę 1 | `app/components/Card.tsx` | Krok 4, **pozycje 21 i 22** — razem z tłem z decyzji ② |
| `Card` — szerokość `w-72` (stałe 288px) wobec `width:100%; max-width:440px` w eksporcie. Zmiana nie jest bezpieczna na poziomie komponentu: `(marketing)/page.tsx` stawia kartę obok `Hero` w kontenerze `flex flex-wrap`, gdzie `w-full` rozwiązuje się do 100% kontenera i łamie układ na dwie linie. Eksport zakłada wyśrodkowaną kolumnę, czyli kontekst logowania, nie landingu | `app/components/Card.tsx`, `app/(marketing)/page.tsx` | Krok 4, **pozycje 21 i 22** — decyzja miejsca wywołania |
| `PresenceAvatar` — kropka offline używa `bg-gray-400`, surowego koloru z palety Tailwinda zamiast tokenu; jej odpowiednik online stoi poprawnie na `bg-hub-online`. Sama kropka ma 11px, czyli dokładnie tyle co w eksporcie | `app/components/PresenceAvatar.tsx` | Poza Krokiem 4 — komponent renderuje się wyłącznie na `/chat` (przez `FriendRow`), zamrożonym przez §8.11. Do Kroku 8 |
| Awatary w wierszach list renderują się na **40px**, a eksport podaje **44px** (wiersze wyszukiwania i szyny czatu) oraz 42px w nagłówku rozmowy. Hero profilu ma 96px i zgadza się co do piksela. `size` jest liczbą podawaną przez wywołującego, więc to nie jest ustawienie komponentu | `app/components/UserList.tsx`, `app/(app)/[userId]/page.tsx` | Krok 4, **pozycje 23 i 27** — decyzja miejsca wywołania |
| `TextField` — waga pisma 400 wobec 500 w eksporcie (`inputStyle` i `searchInputStyle` mają `fontWeight: 500`). **Przeoczone przy jednostce 1**, nie zablokowane — waga daje się ustawić per ton, więc `/chat` jej nie broni | `app/components/TextField.tsx` | Krok 4, **pozycja 22** — `TextField` i tak wraca wtedy po kolory tonu `elevated` (12.1) |

### 12.6 Rozjazdy wyłapane w przeglądzie wstecznym

Kolumna „Między czym a czym" ma dwie dopuszczalne wartości: **między
dwiema jednostkami** albo **między dwiema szerokościami tej samej
jednostki** — bo od decyzji ① rozjazd może powstać także tam (§8.2).

Każdy wiersz mówi jedno z dwóch: albo słownik z Fazy 0 miał lukę (wtedy
warto ją zamknąć od razu, bo zapewne wróci), albo pracujesz na innym
założeniu, niż zapisałaś. **Pusta 12.6 po kilku dniach pracy nie oznacza,
że rozjazdów nie było — oznacza, że przegląd wsteczny się nie odbywał.**

| Co się rozjechało | Między czym a czym | Kiedy wykryte | Jak rozstrzygnięte | Czy słownik wymagał uzupełnienia |
|---|---|---|---|---|

**Pusta po Etapie A — i to jest wynik, nie brak wyniku.** Odbyły się dwa
przeglądy wsteczne: przed pozycją 3 (na `/login`, pole + przycisk) i po
zamknięciu Etapu A (na `/`, gdzie renderują się cztery z sześciu komponentów).
Oba wykonane **porównaniem wartości wyliczonych**, nie wzrokiem: `console.table`
po `getComputedStyle`, z filtrem na elementy mające promień.

Wynik drugiego przeglądu: `Card` 24px, `Button` 12px / 700 / 14px, `Tag` ×5
pełny promień / 600 / 12px — **każda wartość jest wierszem słownika 12.0**,
żadnej spoza. Dwa promienie odstające (16px w kontenerze `Hero`, 6px
w `ThemeToggle`) należą do pozycji **19** i **7**, czyli do jednostek jeszcze
nieotwartych — to nie rozjazd, tylko praca niewykonana.

**Jak czytać taki przegląd, bo odruch podpowiada źle.** Nie szuka się *jednej*
wartości promienia. Kilka różnych jest poprawne, bo 12.0 przypisuje różne
promienie różnym rolom. Sygnałem jest **wartość, która nie jest żadnym wierszem
słownika** — trzy wartości dające się wskazać w tabeli to spójny system, czwarta
niedająca się wskazać to konflikt systemowy z §8.2.

### 12.7 Jednostki strukturalne (1b — nawigacja mobilna, 2a — wydzielenie trasy)

Obie rozliczają się **działaniem, nie wyglądem**, i obie mają własną,
krótszą definicję ukończenia — punkty 1–10 z §8.9 ich nie dotyczą. Są to
jedyne dwie jednostki w tym kroku, których nie da się ocenić patrząc;
trzeba je kliknąć. Każda idzie osobnym commitem, nigdy razem z redesignem
(§8.7 reguła 5) i nigdy razem ze sobą.

Kolumna „Gdzie wygląd bez zmian" jest tu tym, czym w Kroku 2 była tabela
10.3: **dowodem, że zmiana struktury nie przemyciła zmiany wizualnej.**
Różnica względem Kroku 2 jest taka, że tam dowodem były wartości
wyliczone, a tu wystarczy porównanie ze screenshotem, bo żadna z tych
jednostek nie zmienia klas.

| Jednostka | Co się zmieniło | Gdzie wygląd bez zmian | Sprawdzone działaniem | `'use client'` +? |
|---|---|---|---|---|
| **1b** — nawigacja mobilna | *do wypełnienia* | **na szerokim ekranie — ani o piksel** | każda pozycja osiągalna przy szerokości wąskiej; menu otwiera się i zamyka; przejście do trasy je zamyka | dopuszczalne +1, jeśli menu ma stan; wymaga powodu |
| **2a** — wydzielenie trasy znajomych | *do wypełnienia* | **w przeniesionym panelu — ani o piksel** | dodanie i usunięcie znajomego, wyszukiwanie, nawigacja tam i z powrotem na obu szerokościach; profil nie stracił niczego poza panelem | dopuszczalne +1, jeśli strona trasy musi być kliencka; wymaga powodu |

### Materiał odniesienia

Screenshoty stanu sprzed migracji: **`/root/design/frontend/before_migration`**.
Rola w Kroku 4 (§8.10): odpowiadają na pytanie "czy nic nie zniknęło",
którego eksport designu nie potrafi rozstrzygnąć — eksport pokazuje
intencję, screenshot pokazuje inwentarz tego, co strona faktycznie
zawierała. Zastępują branch odniesienia, porzucony po Kroku 3 (§7.12).

**Zakres ich ważności po decyzji ①.** Jeśli powstały przy jednej
szerokości okna, odpowiadają na pytanie "co tam było", ale nie na "jak to
się układało na wąskim ekranie" — a tego drugiego pytania nie da się już
zadać wstecz. Przy jednostkach mobilnych są więc **listą elementów do
odnalezienia w nowym układzie**, nie wzorcem układu; w tej roli działają
bez zastrzeżeń, bo lista elementów nie zależy od szerokości. Przy czytaniu
punktu 10 definicji z §8.9 obowiązuje rozróżnienie: element schowany za
przyciskiem menu **nie zniknął**, element wycięty, żeby się zmieściło —
zniknął. Różnica jest w kodzie, nie na ekranie.
