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
- [x] **1.2 Etap C**, w kolejności: ThemeToggle → BrandLink → Footer →
  Sidebar → BareLayout (+ `app/(app)/layout.tsx`). Przy BrandLink domknij
  `text-white` (10.5, 11.5); przy Sidebarze — trzy tokeny z 10.4
- [x] **1.3 Jednostka 1b** — nawigacja przy szerokości wąskiej. Osobny
  commit, rozliczenie w 12.7, **test działaniem**, wygląd na szerokim
  ekranie bez zmiany o piksel. Stylując Sidebar w 1.2, zostaw miejsce na
  wpis, który dojdzie w 2.1
- [x] **1.4 Etap B**: `privacy-policy` → `terms-of-service`. Nie zależy od
  Etapu A (9.1), więc **jest to rezerwa na czas blokady z 0.5** — nie
  robisz dwóch rzeczy naraz, ale nigdy nie stoisz

**Faza 2 — Etap D (§8.6). Zaczyna się, gdy Faza 1 jest domknięta.**

- [x] **2.0** Ustal zakres przeniesienia panelu poleceniem z §8.6 (importy
  `FriendsPanel.tsx` i `page.tsx`) → wynik do 12.2. Robocze założenie
  z 9.1 jest **założeniem do potwierdzenia**, nie ustaleniem
  — *zrobione 2026-08-09 czytaniem plików, nie poleceniem: `FriendsPanel`
  okazał się lokalny dla trasy (`[userId]/`, nie `components/`), więc wzorzec
  z §8.6 szukałby go w złym miejscu. **Założenie z 9.1 było błędne** —
  `UserSearch` i `UserList` nie przenoszą się, bo są już niezależne od trasy;
  przenoszą się za to trzy pliki, o których 9.1 nie wspomina
  (`AddFriendButton`, `RemoveFriendButton`, `actions.ts`)*
- [x] **2.1 Jednostka 2a** — wydzielenie trasy znajomych. Refaktor: inna
  struktura, **te same piksele**. Osobny commit, 12.7, test działaniem.
  Po niej wpis Sidebara w 12.3 **przestaje być aktualny** — Sidebar wraca
  do sprawdzenia na obu szerokościach, razem z nawigacją z 1b
  — *zamknięte 2026-08-09; wpis Sidebara faktycznie unieważniony, pozycja 18*
- [x] **2.2** `Hero`, `SessionCard` — **tu odblokowuje się Krok 8**
  (kryterium 5 z §8.13). Odnotuj datę osobno i nie zostawiaj `Hero` na
  ostatni dzień
- [x] **2.3** `(marketing)/` — *zamknięta 2026-08-09. Największa pojedyncza
  zmiana wizualna kroku: gradient landingu na `BareLayout`, `Hero` bez karty,
  układ pionowy zamiast obok siebie. Otworzyła ponownie pozycje 9, 11 i 19 —
  wszystkie trzy przewidziane w 12.5. Jedyny nowy token Kroku 4 (12.4)*
- [x] **2.4** `login` + `register` — **jedna jednostka, jeden commit**.
  Dwie zakładki jednej karty; rozdzielenie ich w czasie to §8.2
- [x] **2.5** `UserList`, `UserSearch`
- [x] **2.6** `FriendsPanel`
- [x] **2.7** nowa trasa znajomych
- [x] **2.8** `(app)/[userId]` — to, co zostało po wydzieleniu panelu
- [x] **2.9** `AddFriendButton`, `RemoveFriendButton`, `EditAvatarButton`,
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

- [x] **Z.1** 12.2 kompletna, z historią zmiany ①; cztery decyzje
  wykonawcze z §8.3 mają odpowiedzi
- [x] **Z.2** 1b i 2a rozliczone osobno w 12.7; żadna nie dzieli commita
  z redesignem — *plus **2b**, jednostka strukturalna dopisana 2026-08-10,
  której §8.5/§8.6 nie przewidywały; też w 12.7, też osobnym commitem*
- [x] **Z.3** 6 komponentów Etapu A, 5 Etapu C, 2 Etapu B i komponenty
  Etapu D mają wpis w 12.3 ze spełnionymi punktami 6, 8, 9 i 10 z §8.9
- [x] **Z.4** Siedem tras zgodnych z eksportem przy obu szerokościach;
  `/stomp` nieostylowana i nieusunięta
- [x] **Z.5** `Hero.tsx` i `Tag.tsx` bez `brand-*` → **Krok 8 odblokowany**
  (pozycje 6 i 19; potwierdzone `grep`em — w warstwie komponentów nie ma
  ani jednego użycia)
- [x] **Z.6** Trzy tokeny z 10.4 mają odpowiedź — wszystkie trzy
  **potwierdzone jako wartości docelowe** na pozycji 10, wpisy w 12.4
- [~] **Z.7** **Spełnione warunkowo — jeden nowy token, świadomie.**
  *Breakpoint:* dokładnie jeden (`lg:`), potwierdzone `grep`em; jedyne
  trafienia na `sm:`/`md:` to klucze obiektu `SIZE_CLASSES`, nie prefiksy.
  *Zapis mobile-first:* tak, wszystkie 12 wartości responsywnych kroku.
  *Kopie przełączane przez `hidden`:* brak — 1b, 2b i modal ukrywają
  **ten sam** element, nie renderują drugiego.
  *Nowe tokeny:* **jeden — `--theme-start-page-gradient-mid-2`**, wyjątek
  od §8.7 reguły 2 z pełnym uzasadnieniem w 12.4. Kryterium mówi „bez
  nowych tokenów", więc dosłownie **nie** jest spełnione; zostaje
  odhaczone warunkowo, bo wyjątek został podjęty świadomie, zapisany
  przed wykonaniem i jest jedyny w całym kroku
- [x] **Z.8** **Żadna z siedmiu tras nie przewija się w poziomie przy
  szerokości wąskiej.** Potwierdzone pomiarem `scrollWidth − clientWidth`
  na każdej z nich. **Siódma trasa (`/chat`) wymusiła jednostkę 2b** —
  kryterium binarne zadziałało dokładnie tak, jak miało: nie dało się go
  „prawie" spełnić
- [x] **Z.9** Sekcja 12 wypełniona, wraz z 12.5, 12.6 i 12.7

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
| Odstępy | Między elementami w grupie | `gap-3` (12px); ciasny `gap-1.5` (6px), luźny `gap-4` (16px), **bardzo luźny `gap-5` (20px)** | bez zmian | sidebar, formularze, wiersze list; **20px z linków stopki (landing i strony prawne) — dopisane 2026-08-10 na wniosek z pozycji 9** |
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
| Cienie | **Wiersz listy** | `shadow-[0_6px_18px_rgba(10,42,77,0.07)]` | bez zmian | wiersze wyników wyszukiwania — **dopisane 2026-08-10 na wniosek z pozycji 23.** Leży między „subtelnym" a „kartą" i nie jest żadnym z nich; wiersz listy **unosi się cieniem zamiast mieć obramowanie**, co jest w eksporcie regułą, nie wyjątkiem |
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

**Uwaga pomiarowa 2026-08-09: szerokości 1280px nie da się na tej maszynie
uzyskać przez zmaksymalizowanie okna.** Pomiar przy jednostce 7 zwrócił
`devicePixelRatio` **1,25** (skalowanie Windows 125%) i `window.innerWidth`
**1164px** przy oknie rozciągniętym na cały ekran. Szerokość szeroką ze
słownika trzeba więc ustawiać w trybie urządzenia w DevTools (Ctrl+Shift+M,
wpisana szerokość 1280), a nie rozmiarem okna — inaczej „1280" jest życzeniem,
nie pomiarem.

Dla pozycji 1–6 nie zmienia to żadnego wniosku: **1164 i 1280 leżą po tej samej
stronie progu `lg:` (1024px)**, więc każda klasa responsywna rozwiązuje się
tam identycznie i padding `Card` przechodzący 24 ↔ 36px został zaobserwowany
poprawnie. Zapisy „sprawdzone przy 1280" w 12.3 opisują jednak faktycznie
~1164px. Różnica zacznie mieć znaczenie przy pierwszej jednostce, której
wartość zmienia się **powyżej** 1024 — czyli przy wierszach `max-w-*` ze
słownika, gdzie 1164 i 1280 mogą dać dwa różne układy.

**Skutek uboczny DPR 1,25, istotny dla Kroku 7:** obramowanie `1px` renderuje
się jako **0,8px** (Chrome przycina krawędź do pełnego piksela urządzenia).
Szczegóły przy wierszu o obramowaniach włosowych w 12.5.

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
| 7 | `ThemeToggle` | Faza 1 / Etap C | **zamknięta** (obramowanie tymczasowe — schodzi przy relokacji, patrz 12.5; trzecia jednostka otwierana świadomie po raz drugi, po 1 i 4) | 2026-08-09 |
| 8 | `BrandLink` | Faza 1 / Etap C | **zamknięta** (brak koloru w `BareLayout` wraca z pozycjami 21/22 — 12.5) | 2026-08-09 |
| 9 | `Footer` | Faza 1 / Etap C | **zamknięta** (na trasach `(app)` stopka nie ma odpowiednika — likwidacja strukturalna na pozycjach 10/11, 12.5; krawędź na ciemnym tle wraca z 21/22) | 2026-08-09 |
| 10 | `Sidebar` | Faza 1 / Etap C | **zamknięta** — stylowanie tego, co istnieje; skład raila i linki prawne przebudowuje pozycja 11, która ten plik otwiera ponownie (12.5) | 2026-08-09 |
| 11 | `BareLayout` + `app/(app)/layout.tsx` | Faza 1 / Etap C | **zamknięta** — **Etap C domknięty** (w Fazie 1 zostają jeszcze 1b i Etap B). Zakres okrojony decyzją z 2026-08-09: przebudowa strukturalna powłoki przeniesiona do Kroku 6 (12.5). `BareLayout.tsx` bez zmian | 2026-08-09 |
| 12 | **1b — nawigacja na wąskim ekranie** | Faza 1 / strukturalna | **zamknięta** — rozliczona w 12.7, nie w 12.3 (ocena działaniem). Osobny commit, §8.7 reguła 5. **Odblokowuje Z.8 dla tras `(app)`** | 2026-08-09 |
| 13 | `LegalSection` | Faza 1 / Etap B | **zamknięta** — kolor nagłówka `#155e8f` świadomie nieodtworzony (12.4); `mt-4` akapitów sumuje się z nowym gapem do czasu pozycji 15/16 (12.5) | 2026-08-09 |
| 14 | `ContactBlock` | Faza 1 / Etap B | **zamknięta** — jedna zmiana: rytm wewnętrzny 8px → 10px, zgodny z gapem `LegalSection` | 2026-08-09 |
| 15 | `privacy-policy/page.tsx` | Faza 1 / Etap B | **zamknięta** — wykonana **razem z 16**, jedną decyzją (patrz uwaga pod tabelą) | 2026-08-09 |
| 16 | `terms-of-service/page.tsx` | Faza 1 / Etap B | **zamknięta** — **Etap B i cała Faza 1 domknięte** | 2026-08-09 |
| 17 | **2a — wydzielenie trasy znajomych** | Faza 2 / strukturalna | **zamknięta** — rozliczona w 12.7. Osobny commit, §8.7 reguła 5. **Unieważnia wpis `Sidebar` w 12.3** (rail ma teraz cztery linki nawigacyjne, nie trzy) → pozycja 18 | 2026-08-09 |
| 18 | `Sidebar` — **ponowne sprawdzenie po 2a**, nie stylowanie | Faza 2 / kontrola | **zamknięta** — nowy wpis w 12.3 zastępuje przekreślony z pozycji 10. Wykryła i naprawiła defekt 1b (nieprzewijalna szuflada) oraz zapisała łamanie etykiety STOMP w 12.5 | 2026-08-09 |
| 19 | `Hero` | Faza 2 / Etap D — wejście | **zamknięta, świadomie częściowa** — typografia i wyjście z `brand-*`; struktura, szerokość i gradient wracają z pozycją 21 (12.5). **Domyka kryterium 5 z §8.13 — `brand-*` nie ma już ani jednego użycia w komponentach, Krok 8 odblokowany** | 2026-08-09 |
| 20 | `SessionCard` | Faza 2 / Etap D — wejście | **zamknięta** — typografia i odstępy ze słownika, bo eksport nie ma odpowiednika. Rewizja wariantu `outline` wykonana, wynik: bez zmian do 21/22 (12.4) | 2026-08-09 |
| 21 | `(marketing)/` | Faza 2 / Etap D — wejście | **zamknięta** — gradient landingu, `Hero` bez karty, układ pionowy. **Otwiera ponownie 9, 11 i 19**; `Card` świadomie zostaje biała do pozycji 22 (12.5). Jedyny nowy token kroku (12.4) | 2026-08-09 |
| 22 | `login` + `register` — **jedna jednostka** | Faza 2 / Etap D — wejście | **zamknięta** — ciemna karta i ton `elevated` w jednym commicie. **Domyka cztery wiersze 12.5** (kolory `Card`, `w-72`, waga `TextField`, kontrast `AccentLink`) i wiersz `outline` w 12.4 | 2026-08-09 |
| 23 | `UserList` | Faza 2 / Etap D — znajomi | **zamknięta** — zamiana obramowania na cień, geometria wiersza ze słownika, awatar 40 → 44px. **Domyka wiersz 12.5 o awatarach w listach** | 2026-08-09 |
| 24 | `UserSearch` | Faza 2 / Etap D — znajomi | **zamknięta** — `size="sm"` → `"md"` (12.5 domknięte częściowo, padding nadal zamrożony), `text-red-400` → `text-danger`, odstęp i przycisk „Load more" ze słownika | 2026-08-09 |
| 25 | `FriendsPanel` | Faza 2 / Etap D — znajomi | **zamknięta** — dwie akcje w wierszu zamiast jednej (ustalenie osoby prowadzącej), przyciski w geometrii z eksportu, `Remove` przejmuje jego potraktowanie. Dwa nowe wiersze w 12.5: gradient bez tokenów i `?friend=` nieczytany przez `/chat` | 2026-08-09 |
| 26 | nowa trasa znajomych (`/friends`) | Faza 2 / Etap D — znajomi | **zamknięta** — kolumna 640px, nagłówek trasy, odstęp sekcji. Nagłówek `h2` przeniesiony tu z `FriendsPanel` (drobne ponowne otwarcie pozycji 25). Warstwa dekoracyjna pominięta — **decyzja należy do pozycji 27** (12.5) | 2026-08-09 |
| 27 | `(app)/[userId]` | Faza 2 / Etap D — profil | **zamknięta** — nagłówek trasy, hero z gradientem eksportu, **nowa karta informacyjna**, wylogowanie przeniesione i przestylowane, obwódka awatara. Domyka licznik znajomych odłożony przez 2a oraz wiersz 12.5 o obwódce. `Email` nie powstał — brak danych, nie brak stylu | 2026-08-09 |
| **2b** | **`/chat` — układ jednopanelowy przy szerokości wąskiej** | **poza pierwotną kolejką / strukturalna** | **zamknięta 2026-08-10** — rozliczona w 12.7. Dopisana tego samego dnia. Nie wynika z §8.5 ani §8.6, tylko z **Z.8**: po domknięciu 28 pozycji `/chat` został jedyną trasą przewijającą się w poziomie przy 360px. Odstępstwo od §8.11 zapisane w 12.4, rozliczenie w 12.7 | |
| 28 | Cztery przyciski profilu — **jedna jednostka** | Faza 2 / Etap D | **zamknięta — KOLEJKA DOMKNIĘTA, 28/28.** Cztery przyciski `EditAvatarButton` plus samo okno dialogowe. Naprawiony błąd: modal był szerszy niż wąska szerokość oceny (12.5) | 2026-08-10 |

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

**I tak się stało — 2026-08-09.** Wiersz `Sidebar` jest przekreślony, bo opisuje
rail z **trzema** linkami nawigacyjnymi, a 2a dołożyła czwarty („Friends"). Nie
jest usuwany: opisuje przegląd, który naprawdę się odbył, i pokazuje, na czym
został wykonany. Zastąpi go wpis z **pozycji 18**, wykonany na obu szerokościach
**oraz w szufladzie mobilnej z 1b** — czego wersja z pozycji 10 nie mogła objąć,
bo szuflada wtedy nie istniała. To jest ta sama mechanika, przed którą ostrzega
§8.2: nie zmienił się kod, który sprawdzano, tylko warunki, w których był
prawdziwy.

| Jednostka | Faza / etap | Trasy sprawdzone | Szerokości sprawdzone | Przegląd wsteczny |
|---|---|---|---|---|
| **Cztery przyciski profilu + okno dialogowe awatara** — plakietka na awatarze, wybór zdjęcia, Cancel, Save | Faza 2 / Etap D | **Jedna trasa, wyłącznie własny profil** — `EditAvatarButton` nie renderuje się na cudzym. Dwa stany: zamknięty (sama plakietka) i otwarty (okno dialogowe), i **to rozróżnienie okazało się istotne**, bo cała zawartość okna była dotąd poza zasięgiem jakiegokolwiek pomiaru | **360 i 1280, w obu stanach.** Okno: promień 24px, padding **24 ↔ 36px**, bez obramowania, cień `0 25px 60px` (wiersz „karta na tle ciemnym" — okno leży na przyciemnionej nakładce, więc to właściwy wiersz, nie „karta"). Nagłówek 20px/800. Przyciski: `12px | 14px | 14px | 700`, `sameGeom` potwierdzone, Save na gradiencie `hub-cta`. **Szerokość okna 440 ↔ 327px przy `left` 420 ↔ 16px** — przed tą jednostką było to stałe 384px w oknie 360px, czyli przycięcie po obu stronach | **Odbył się 2026-08-10 na własnym profilu, jako ostatni przegląd kroku.** Wynik pusty. Promienie na jednym ekranie: 24px (hero, okno), 20px (karta informacyjna), 14px (wylogowanie), 12px (przyciski okna, „Change name") — cztery wiersze słownika przypisane po roli. **Znalezisko metodyczne, nie wartościowe:** błąd szerokości okna przetrwał do przedostatniej jednostki, bo element `fixed` nie powiększa `scrollWidth` dokumentu, więc Z.8 go nie widzi, a wszystkie dotychczasowe tabele mierzyły stan po wejściu na trasę, nie po otwarciu warstwy → 12.5 |
| **`(app)/[userId]` — nagłówek trasy, hero z gradientu eksportu, nowa karta informacyjna, wylogowanie na dole** | Faza 2 / Etap D — profil | **Jedna trasa, dwa warianty:** własny profil (edycje, licznik znajomych, wylogowanie) i cudzy (awatar, nazwa, jeden wiersz karty). Oba sprawdzone — wariant cudzy ma karty o jednym wierszu, bo `GET /friends` zwraca znajomych **wywołującego**, nie oglądanego | **360 i 1280, oba motywy.** Hero: gradient `135deg` z trzech stopni, promień 24px, padding **24 ↔ 36px**, gap 28px, cień `0 12px 32px`. Karta informacyjna: promień 20px, padding 8px, wiersze `18×22px`, etykieta `rgb(124,138,146)`. Kolumna 720px, odstęp sekcji **20 ↔ 28px**. `overflow` zero przy 360 — mimo że hero trzyma tam 96px awatara i nazwę obok siebie w kolumnie o szerokości 328px. **Pod Mocha zmienia się karta informacyjna, hero nie** — gradient jest wartością dowolną i motywu nie zna; to samo dotyczy `hub-muted` w etykietach | **Odbył się 2026-08-09 na własnym profilu.** Wynik pusty. **Warta odnotowania jest jedna zbieżność:** `hub-muted` okazał się dokładnie tym `#7c8a92`, którego eksport używa na etykiety — **jedyny drugorzędny kolor Etapu D, który miał już token**, wobec `#155e8f`, `#3d4a44`, `#d6e4ee`, `#eaf6fb`/`#e9f9f0` i pięciu stopni dwóch gradientów, które go nie mają. Typografia: nagłówek trasy 30/24px, nazwa 26px/800, wiersze karty 14px — trzy wiersze słownika |
| `FriendsPanel` + `AddFriendButton` + `RemoveFriendButton` + nowy `OpenChatLink` — **dwie akcje w wierszu zamiast jednej** | Faza 2 / Etap D — znajomi | **Jedna trasa, dwa konteksty:** `/friends` — wiersze znajomych (Chat + Remove) i wyniki wyszukiwania (Add). Wszystkie trzy przyciski dostały tę samą geometrię wiersza, więc czytają się jako jedna rodzina kontrolek niezależnie od listy | **360 i 1280, wartości identyczne.** Geometria z eksportu: `9×18px`, promień 10px, 14px/700, `sameGeom` między akcją akcentową a drugorzędną potwierdzone programowo. `Chat` na płaskiej limonce z `on-primary`, `Remove` na bladym gradiencie z `hub-teal` (`rgb(20,107,122)`). Odstęp między akcjami 12px. **`overflow` zero także przy 360** — a to najciaśniejszy stan, jaki ten wiersz miał: 44px awatara, nazwa i dwa przyciski z 18px paddingu każdy. Dalsze zagęszczanie wymagałoby ikon, co świadomie odłożono do osobnego zgłoszenia | **Odbył się 2026-08-09 na `/friends`.** Wynik pusty. Promienie na jednym ekranie: 16px (wiersz listy), 14px (pole `surface`), 10px (trzy przyciski akcji i pager) — trzy wiersze słownika, przypisane po roli, nie po wielkości. Zniknęła też rozbieżność wskazana w przeglądzie przy pozycji 23: przyciski akcji miały wtedy 8px i były jedyną wartością spoza tabeli na tym ekranie. **Świadome odstępstwo od eksportu, zapisane tutaj, bo nie jest błędem:** eksport ma w wierszu **jedną** akcję, my mamy dwie — ustalenie osoby prowadzącej z 2026-08-09. „Chat" zostaje akcją akcentową, `Remove` przejmuje potraktowanie, które eksport dawał tamtej. Wiersz nie jest więc odtworzeniem, tylko rozszerzeniem o jedną akcję w już istniejącym języku wizualnym | **Jedna trasa, jedno miejsce użycia:** `/friends`, przez `FriendsPanel`. Sprawdzone z wpisanym zapytaniem, żeby pole i wyniki były widoczne naraz | **Zmierzone przy 1151px (`lg` czynne).** Pole: `16×12px` (było `12×8px`), promień 14px, tło białe, **waga 500** — ta ostatnia to ton `surface` odbierający wagę dodaną na pozycji 22, w jednostce, która tego pola nie dotykała. Odstęp do wyników 28px. `overflow` zero. **Wartość wąska niezmierzona:** `gap-5` (20px) jest jedyną liczbą w tej jednostce potwierdzoną tylko po jednej stronie progu. Ryzyko oceniono jako znikome — ten sam mechanizm `lg:` potwierdzono pomiarem na paddingu (`Card`, `Footer`, `main`), rozmiarze pisma i interlinii (`Hero`) oraz szerokości kolumny — ale zapis ma to odnotowywać, a nie zaokrąglać | **Nie odbył się osobno.** Jednostka zmienia jeden komponent na tej samej trasie, którą przegląd z pozycji 23 objął dzień wcześniej i w tym samym stanie; drugi przegląd tego samego ekranu odpowiadałby na pytanie już zadane. Najbliższy realny przegląd to pozycja 25, gdzie dochodzą przyciski akcji i pager |
| `UserList` — obramowanie zamienione na cień, geometria wiersza, awatar 44px, nazwa 14px/700 | Faza 2 / Etap D — znajomi | **Jedna trasa, dwa konteksty:** `/friends`, gdzie ten sam komponent renderuje **listę znajomych** i **wyniki wyszukiwania**. Sprawdzone z wpisanym zapytaniem, żeby oba były na ekranie naraz — pięć wierszy w sumie. To pierwsza jednostka od czasu 2a, której zasięg **zmalał**: przed wydzieleniem trasy te same wiersze renderowały się na `/[userId]` | **360 i 1280.** Wartości identyczne: `listGap 12px`, `radius 16px`, `18×14px`, `innerGap 16px`, **`border 0px`**, cień `0 6px 18px rgba(10,42,77,0.07)`, awatar 44, nazwa 14px/700. **`overflow` zero także przy 360** — istotne, bo w tej jednostce **każdy wymiar urósł** (awatar 40→44, padding 12→18/14, odstępy 8→12 i 12→16), i to akurat przy szerokości, gdzie jest najmniej miejsca. Kontrolnie `/chat`: bez zmian, więc nic nie przeciekło przez `PresenceAvatar`/`FriendRow` na trasę zamrożoną | **Odbył się 2026-08-09 na `/friends`.** Wynik: promienie 16px (wiersz), 14px (pole `surface`) i 8px (przyciski akcji) — pierwsze dwa to wiersze słownika, **trzeci nie**: 12.0 daje małemu przyciskowi `rounded-[10px]`. **To praca niewykonana, nie rozjazd** — `AddFriendButton` i `RemoveFriendButton` należą do klastra pozycji 25, tak samo jak 6px `ThemeToggle` należało do pozycji 7 w przeglądzie przy pozycji 2. Eksport daje temu przyciskowi własne potraktowanie (gradient `#eaf6fb→#e9f9f0`, tekst `#146b7a`, 13,5px/700), więc pozycja 25 ma co odtwarzać |
| **`login` + `register` — ciemna karta i ton `elevated` w jednym commicie.** Jednostka, na którą czekały cztery wiersze 12.5 i jeden 12.4 | Faza 2 / Etap D — wejście | **Dwie trasy jednostki plus dwie kontrolne.** `/login` i `/register` w stanie wylogowanym; obie także zalogowane, gdzie `(auth)/layout.tsx` podstawia `SessionCard`. Kontrolnie **`/[userId]` i `/privacy-policy`** — nie po to, żeby coś sprawdzić, tylko żeby udowodnić, że **nic się tam nie zmieniło**: obie używają `on-elevated-surface` bezpośrednio, z pominięciem `Card`, i token pozostał nietknięty. Trzecia trasa uboczna to `/` — „Hello!" i `SessionCard` też siedzą w `Card` | **1280 i 360.** Karta: `max-w-440px`, gradient `160deg` z dwóch półprzezroczystych stopni, `blur(18px)`, krawędź `white/0.14`, cień `0 25px 60px`. Pole: `white/0.08` na `white/0.15`, tekst biały, **waga 500**, promień 12px. Akcent `rgb(163,230,53)`. `overflow` zero | **Odbył się 2026-08-09 na `/login`** — ostatni przegląd Etapu D „wejście". Wynik pusty. **Ujawnił natomiast defekt, którego nie widać w żadnej klasie:** pola wypełnione autouzupełnianiem Chrome'a renderują się jasnoniebiesko z czarnym tekstem, bo `:-webkit-autofill` omija kaskadę. Pierwszy pomiar tonu `elevated` był więc pomiarem stylu przeglądarki, nie naszego; czysty odczyt wykonano na trzecim polu `/register`. → 12.5 |
| **`(marketing)/` — gradient landingu, `Hero` bez karty, układ pionowy.** Największa zmiana wizualna kroku; dotyka pięciu plików i otwiera ponownie trzy zamknięte jednostki | Faza 2 / Etap D — wejście | **Trasa `/` w obu stanach sesji — ale skutek sięga dalej.** Gradient trafił do `BareLayout`, więc zmienia się także `/login` i `/register`; sprawdzone wszystkie trzy. **Czwarta trasa sprawdzana była po to, żeby się NIE zmieniła:** `Footer` renderuje się w obu powłokach, a jego kolory przestały być nazwane i zaczęły być dziedziczone — `/[userId]` potwierdza, że na jasnej powłoce wygląda dokładnie jak przed zmianą | **360 i 1280, oba stany sesji, oba motywy.** Potwierdzone wartościami: gradient `120deg` z **czterema** stopniami (`rgb(10,51,72) 0%`, `rgb(20,107,122) 32%`, `rgb(31,143,122) 62%`, `rgb(47,155,207) 100%`), kolumna hero `max-w 720px` / `gap 14px`, nagłówek `max-w 600px` i `margin-bottom 0` (stary `mb-4` nie sumuje się już z gapem), krawędź stopki `oklab(… / 0.1)` z `currentColor`, linki stopki `opacity 0.6`, `overflow 0`. **Układ przestał zależeć od szerokości** — poprzedni `flex-wrap` sam zwijał się w stos poniżej ~800px, więc obie szerokości oceny pokazywały dwie różne kompozycje; teraz pokazują tę samą i różni je wyłącznie odstęp | **Odbył się 2026-08-09 na `/`.** Wynik pusty pod względem wartości. Ważniejsze, że **trzy wiersze 12.5 zamknęły się same, bez dopisywania kodu pod nie**: kolor `BrandLink` (dziedziczy biel, czyli wartość z eksportu), krawędź stopki na ciemnym tle (`border-current/10` ≈ `rgba(255,255,255,0.12)` z eksportu) i kolor linków stopki. Wszystkie trzy były zapisane jako „utajone, uaktywnią się przy 21/22" i uaktywniły się dokładnie tam, gdzie zapowiedziano |
| `SessionCard` — nagłówek 24px/700 → 20px/800, waga podtytułu, odstęp przycisków, usunięty zawieszony margines | Faza 2 / Etap D | **Dwie trasy, dwa miejsca użycia, wyłącznie w stanie zalogowanym:** `/` (`(marketing)/page.tsx:31`, obok `Hero`) i `/login` + `/register` (`(auth)/layout.tsx:19`, samodzielnie). Sprawdzone na `/` i `/login` — **`headingTag` wraca odpowiednio `H2` i `H1`**, czyli prop `headingLevel` nadal robi to, co deklaruje jego komentarz: poziom nagłówka zależy od tego, co jeszcze jest na stronie, a nie od wyglądu | **360 i 1280.** Wartości identyczne przy obu szerokościach — komponent nie ma klas responsywnych; responsywna jest pod nim `Card` i to widać w jedynej liczbie, która się zmienia: **`gapToCardEdge` 25px ↔ 37px**, czyli padding `Card` (24 ↔ 36px) plus jej 1px obramowania. To uboczne potwierdzenie, że usunięcie zawieszonego `mb-4` nie przestrzeliło: odstęp do dolnej krawędzi jest teraz dokładnie tym, co karta deklaruje, a nie tym plus 16px | **Odbył się 2026-08-09 na `/` w stanie zalogowanym**, jedynym ekranie, gdzie `Hero` i `SessionCard` stoją obok siebie. Wynik pusty: nagłówek hero 48px/800, nagłówek karty 20px/800, przycisk 14px/700, pigułka 12px/600 — cztery wiersze słownika, żadnej wartości spoza. **Rewizja `outline` z pozycji 2 wykonana przy tej okazji** i zakończona świadomym „bez zmian" — powód w 12.4 |
| `Hero` — typografia hero i wyjście z `brand-*`; struktura i tło **odłożone do pozycji 21** (12.5) | Faza 2 / Etap D | **Jedna trasa, jedno miejsce użycia:** `/`, przez `(marketing)/page.tsx:27`. Oba stany sesji, bo `Hero` renderuje się niezależnie od zalogowania | **360 i 1280, oba motywy.** Nagłówek przechodzi **30px ↔ 48px** na progu `lg:` — **piąta responsywna wartość migracji i pierwsza, która zmienia interlinię razem z rozmiarem** (33px ↔ 52,8px, bo `leading-[1.1]` jest względne). Reszta bez zmian przy obu szerokościach: waga 800, tracking −1px, podtytuł 18px/500, wyrównanie do środka. Pod Mocha zmienia się **wyłącznie** kolor akcentu (limonka → mauve) — tło i tekst zostają, co jest zapisane w 12.5 jako świadoma mieszanka | **Odbył się 2026-08-09 na `/`**, gdzie `Hero` stoi obok `Card`/`SessionCard` i `Tag`. Wynik pusty pod względem wartości: nagłówek hero, tekst wiodący i mała etykieta pigułki to trzy różne wiersze słownika 12.0 i każdy się zgadza. **Znalezisko nie-wartościowe, zapisane w 12.5:** komponent stał się w połowie motywowany, a jego własny komentarz twierdził, że nie jest — komentarz poprawiono w tej samej jednostce |
| **`Sidebar` — ponowne sprawdzenie po 2a (pozycja 18).** Zastępuje przekreślony wpis z pozycji 10. Nie jest stylowaniem: jedyna zmiana w kodzie to `overflow-y-auto overscroll-contain`, naprawa defektu 1b wykrytego przy tej okazji | Faza 2 / kontrola | **Wszystkie trasy `(app)`**, mierzone na `/friends` — trasie, która przy pozycji 10 jeszcze nie istniała. Rail ma teraz **siedem** odnośników: wordmark plus sześć nawigacyjnych, o jeden więcej niż opisywał tamten wpis | **1280 oraz 360 — a tym razem także dwie wysokości okna: pełna i ~270px.** Geometria identyczna we wszystkich przebiegach: rail 250px, pozycje 46px, promień 12px, `active: true` dokładnie raz i na właściwym linku. Dwa znaleziska: etykieta `Dev: STOMP WebSocket Test` łamie się na dwie linie (66px, → 12.5, nie naprawiamy — `/stomp` znika) oraz **szuflada nie dawała się przewijać** przy niskim oknie, co odcinało dolne linki. Po naprawie: przy 360×270 `vOverflow` 170px jest **osiągalne**, a przy 1280 `vOverflow` wynosi 0 i klasa jest bezczynna — element `static` bez ograniczenia wysokości nie tworzy kontenera przewijania, więc rail przewija się razem ze stroną, jak poprzednio | **Nie dotyczy — to jest przegląd, nie jednostka.** Pozycja 18 sama jest kontrolą; jej rolą jest unieważnić starszy wpis i postawić nowy na aktualnych warunkach |
| **Strony prawne — pozycje 15 i 16 jako jedna jednostka.** Usunięcie zagnieżdżonego `<main>` i martwej klasy `text`, karta z eksportu, nagłówek trasy, `Last updated`, listy, koniec sumowania `mt-4` z gapem | Faza 1 / Etap B | **Dwie trasy, po jednym pliku każda.** Zmiana jest w obu identyczna co do klasy — dlatego wykonane razem, tą samą regułą co `login` + `register` z pozycji 22: **jednostka pracy biegnie tam, gdzie biegnie jednostka decyzji.** §8.5 podaje je jako dwie pozycje i tak zostają w kolejce, żeby zachować ślad, ale rozliczają się jednym wpisem | **360 i 1280, obie trasy.** Karta: `max-w-[720px]`, `rounded-3xl`, `p-6` ↔ `lg:px-14 lg:py-12`, biel `elevated-surface`, cień `0 8px 24px`. Nagłówek `text-2xl` ↔ `lg:text-3xl` — **czwarta responsywna wartość migracji i pierwsza w typografii**, wcześniej tylko odstępy. Dwa naprawione defekty niewidoczne w słowniku: zagnieżdżony `<main>` (dwa punkty orientacyjne w dokumencie) i `className="text"`, które nie jest żadnym utility | **Odbył się 2026-08-09 na `/terms-of-service`** — ostatni przegląd Fazy 1, więc na jednym ekranie stoi teraz **dziewięć** zamkniętych jednostek: rail, wordmark, stopka, toggle, karta, sekcje, blok kontaktowy, nagłówek trasy i listy. Wynik pusty: każda wartość daje się wskazać w słowniku 12.0, `h1` przestał być jedynym odstępstwem z poprzedniego przeglądu. **Pozostaje jedna świadoma strata, zapisana dwa razy w 12.5:** nagłówki sekcji i tekst ciągły mają ten sam kolor, bo `#155e8f` i `#3d4a44` nie mają tokenów, a Krok 4 nie może ich dodać |
| `ContactBlock` — rytm wewnętrzny 8px → 10px | Faza 1 / Etap B | **Dwie trasy, dwa miejsca użycia:** `/privacy-policy` (z `className="mt-4"`) i `/terms-of-service` (bez). Sprawdzone na obu — na pierwszej blok stoi pod akapitem, więc szew jest widoczny; na drugiej jest jedynym dzieckiem sekcji i szwu być nie może | **360, obie trasy.** `realGap` wynosi **10** i zgadza się z `sectionGap` — o tę zgodność w tej jednostce chodziło. `overflow` zero na obu. Różnica `blockMarginTop` między trasami (16px vs 0px) to zderzenie marginesu z gapem opisane w 12.5, należące do pozycji 15/16, nie do tej | **Odbył się 2026-08-09 na `/privacy-policy`.** Wynik pusty. **Zmienił natomiast metodę pomiaru na resztę kroku:** przy tej jednostce dwa razy pod rząd odczytano niewłaściwą krawędź — najpierw `marginBottom` akapitu zamiast `marginTop`, potem `marginTop` zamiast `marginBottom`, bo **Tailwind 4 zmienił `space-y-*`** z `margin-top` na kolejnych dzieciach (v3) na `margin-block-end` na wszystkich poza ostatnim (v4). Wniosek: **mierzyć geometrię, nie własności** — `getBoundingClientRect().top − .bottom` daje odstęp niezależnie od tego, która własność go produkuje, i nie wymaga znajomości wersji frameworka |
| `LegalSection` — separacja sekcji linią zamiast marginesem; nagłówek 24px/600 → 18px/800 | Faza 1 / Etap B | **Dwie trasy, siedemnaście miejsc użycia** (`/privacy-policy` ×8, `/terms-of-service` ×9). Wysoka liczba wywołań, ale **jedna rola** — pkt 6 liczy trasy, nie wystąpienia, więc to nadal dwa spojrzenia, nie siedemnaście. Obie trasy leżą w `(app)`, więc dochodzi do nich powłoka z pozycji 10–12 | **360 i 1280, obie trasy.** Wartości identyczne: `padY 22px`, `borderTop 1px rgb(238,242,239)`, `gap 10px`, nagłówek `18px / 800`. **Z.8 sprawdzone i spełnione przy 360px** (`scrollWidth − clientWidth = 0`) — pierwsza jednostka rozliczana już po wygaśnięciu złagodzenia pkt 9 z 12.4. **Poniżej szerokości oceny strona się przewija:** przy 277px `scrollWidth` wynosi 412px. Przyczyną jest podwójny padding z zagnieżdżonego `<main>` (12.5), a nie ta jednostka; 277px leży poza kontraktem z 12.0 i nie jest kryterium | **Odbył się 2026-08-09 na `/terms-of-service`.** Wynik pusty. Nagłówek sekcji (18px/800) staje obok linków stopki (12px/600), nawigacji raila (14px/700) i wordmarku (20px/800) — cztery wielkości, każda ze swojego wiersza słownika 12.0, plus wspólna krawędź `elevated-border` w `LegalSection` i w `Footer`. Jedyna wartość spoza słownika na tej stronie to `h1` (`text-4xl font-bold`, 36px/700 wobec 30px/800) — **to praca niewykonana, nie rozjazd**, bo nagłówek trasy należy do pozycji 15/16, tak samo jak 6px `ThemeToggle` należało do pozycji 7 w przeglądzie przy pozycji 2 |
| `BareLayout` + `app/(app)/layout.tsx` — **padding strony na trasach aplikacji**; reszta zakresu przeniesiona do Kroku 6 | Faza 1 / Etap C | **Wszystkie trasy `(app)`** — zmiana leży w layoucie grupy, więc dotyczy każdej trasy w niej bez wyjątku (§8.9 pkt 6 w najczystszej postaci: nie ma tu „miejsc użycia" do policzenia, jest grupa tras). `BareLayout.tsx` **nie został zmieniony** — jego dwa elementy, `BrandLink` i `Footer`, rozliczyły się na pozycjach 8 i 9, a sam kontener nie ma wartości ze słownika | **Do sprawdzenia przy obu szerokościach:** `main` przechodzi z płaskiego `p-8` (32px) na **`py-6 px-4` ↔ `py-12 px-14`** (24/16 ↔ 48/56px) na progu `lg:`. Trzecia responsywna wartość migracji, po `Card` i `Footer`, i pierwsza w pliku trasy zamiast w komponencie | **Nie dotyczy — jednostka nie zmienia żadnego komponentu.** Przegląd wsteczny porównuje jednostki komponentowe; tu zmienia się kontener trasy, który nie stoi obok niczego, z czym można go zestawić. Najbliższy realny przegląd to **Z.8** na poziomie kroku |
| ~~`Sidebar`~~ — **wpis nieaktualny od 2026-08-09**, patrz uwaga nad tabelą. Padding raila, geometria i waga pozycji nawigacji; **trzy tokeny on-rail potwierdzone** (12.4) | Faza 1 / Etap C | **Wszystkie trasy `(app)`:** `/[userId]`, `/chat`, `/terms-of-service`, `/privacy-policy`, `/stomp`. Zmierzone na `/[userId]` i `/chat` — dwie trasy, na których stan aktywny wypada na **różnych** pozycjach, co przy okazji potwierdziło, że `usePathname()` rozpoznaje go poprawnie na obu. `/chat` po raz trzeci dotknięte przez powłokę, zgodnie z doprecyzowaniem §8.11 w 12.4, nie jako odstępstwo | **277px i 1547px, obie trasy.** Rail nie ma klas responsywnych i wszystkie osiem wartości wraca identycznie po obu stronach progu — czego się oczekiwało. Przy 277px rail zajmuje 250 z 277px, ale punkt 9 czytany jest zgodnie z rozstrzygnięciem z 12.4 (ocena komponentu, nie strony); pełny test trasy to **Z.8**, po jednostce 1b | **Odbył się 2026-08-09 na `/[userId]`.** Wynik pusty i zasadnie: na railu stoją obok siebie `BrandLink` (20px / 800) i pozycje nawigacji (14px / 700 / promień 12px), a poziomy padding różni się celowo — 10px w nagłówku, 14px w nawigacji — dokładnie tak, jak różni się w eksporcie. Każda wartość daje się wskazać w słowniku 12.0. **Ubocznie zamknięte ryzyko techniczne:** `py-3.25` renderuje się jako **13px**, więc dynamiczna skala odstępów Tailwinda 4 przyjmuje mnożniki ułamkowe i reguła „odstępy nie wymagają zaokrąglania" spod tabeli 12.0 jest potwierdzona pomiarem, a nie tylko dokumentacją |
| `Footer` — padding, krawędź, typografia i kolor linków wg słownika 12.0 | Faza 1 / Etap C | **Wszystkie trasy aplikacji, obie powłoki** (`BareLayout.tsx:17`, `app/(app)/layout.tsx:34`) — trzeci raz z rzędu, więc od pozycji 7 jest to stała cecha Etapu C, nie ustalenie jednostkowe. Zmierzone na `/login` (powłoka `BareLayout`) i `/[userId]` (powłoka `(app)`). **Na trasach `(app)` ta stopka nie ma odpowiednika w eksporcie** i nie została z nich usunięta — to zmiana strukturalna, pozycje 10/11 (12.5) | **277px i 1405/1539px, obie powłoki, oba motywy.** Druga responsywna wartość w całej migracji po `Card` i pierwsza na innej własności: `padX` przechodzi **56px ↔ 16px** na progu `lg:`, potwierdzone po obu stronach na obu trasach. Reszta identyczna przy obu szerokościach, zgodnie z zamiarem | **Odbył się 2026-08-09 na `/login`.** Wynik niepusty → 12.6, wiersz drugi: doprowadzenie krawędzi stopki do słownika (`elevated-border`) zostawiło limonkowe obramowanie `ThemeToggle` jako jedyny akcent wśród neutralnych. Nie naprawiane — dostarcza natomiast argumentu za zapisaną już decyzją o zdjęciu tej krawędzi przy relokacji. **Ubocznie potwierdzony pomiarem wybór tokenu zamiast dokładnego heksa:** `text-on-surface/60` zwraca `oklab(0,2827 … / 0,6)` w motywie domyślnym i `oklab(0,8787 … / 0,6)` w Mocha, a `elevated-border` `#eef2ef` → `#45475a`. `hub-time` (`#9aa5a0`, dokładna wartość z eksportu) nie ma nadpisania `.mocha`/`.latte` i dałby stały jasny szary na ciemnym tle — §8.9 pkt 5 rozstrzyga tu na korzyść tokenu semantycznego, mimo że eksport podaje inną liczbę |
| `BrandLink` — usunięcie kursywy, waga 700 → 800, rozmiar 30 → 20px | Faza 1 / Etap C | **Wszystkie trasy aplikacji, obie powłoki — tym razem przez dwa różne miejsca wywołania, nie jedno.** `BareLayout` (`(auth)` + `(marketing)`, wywołanie `absolute left-6 top-6`) oraz `Sidebar` (grupa `(app)`, wywołanie `mb-3 px-3 text-white`). Zmierzone na `(marketing)`, `/login` i `/[userId]` — po jednej z każdej grupy tras | **277px i 1405px**, jak przy pozycji 7 i z tego samego powodu (komponent bez klas responsywnych). Wartości identyczne na wszystkich trzech trasach: `20px / 800 / normal / tracking normal`. **Kolor jest jedyną rzeczą, która się różni** — `rgb(13, 43, 71)` pod `BareLayout`, `rgb(255, 255, 255)` pod `Sidebarem` — i to jest teza tej jednostki potwierdzona pomiarem: komponent trzyma kształt, miejsce wywołania trzyma kolor. Gdyby rozmiar albo waga też się różniły, podział byłby błędny | **Odbył się 2026-08-09 na `/login`** (wszystkie sześć ostylowanych komponentów na jednej stronie). **Pierwszy przegląd w tym kroku z wynikiem niepustym** → 12.6: `BrandLink` ląduje na wadze 800, `Button` stoi na 700, a eksport daje 800 obu. Rozjazd nie powstał tu i nie jest tu do naprawienia — waga `Button` jest zablokowana przez §8.11 (12.5) — ale dopiero zestawienie obu jednostek na jednym ekranie zamieniło zapisaną liczbę w widoczną niespójność. Reszta bez zmian: promienie nadal stopniowane zgodnie ze skalą eksportu (8/12/14px), żadna wartość spoza słownika 12.0 |
| `ThemeToggle` — geometria i typografia wg `langBtnStyle`; **obramowanie tymczasowe** (12.5) | Faza 1 / Etap C | **Wszystkie trasy aplikacji, obie powłoki.** Fan-in wynosi 1 (`Footer`), ale zasięg tras jest całkowity: `Footer` renderuje się w `BareLayout.tsx:17` **i** `app/(app)/layout.tsx:34`. Zmierzone na czterech trasach reprezentatywnych — po obu powłokach i obu stanach sesji: `/`, `/login`, `/chat`, `/[userId]`. Wszystkie cztery zwróciły **identyczne** tabele. `/chat` włącznie — to nie odstępstwo od §8.11, tylko doprecyzowanie jego zakresu (12.4) | **Zmierzone przy 277px i 1405px** w trybie urządzenia DevTools, nie dokładnie 360/1280 — obie wartości leżą jednak po właściwych stronach jedynego progu (`lg:` 1024) i **obejmują szerokości oceny z zewnątrz** (277 < 360, 1405 > 1280). Dla komponentu bez ani jednej klasy responsywnej jest to dowód mocniejszy niż trafienie w wartości nominalne, a identyczność obu tabel jest tu właśnie tezą do udowodnienia. **Brak zawijania potwierdzony liczbą, nie okiem:** wysokość 30px to jedna linia — dwie dałyby ~46px przy `line-height` 16px — i to przy 277px, czyli węziej niż nominalne 360. Uwaga do 12.0 o nieosiągalności 1280 przez rozmiar okna powstała przy tej jednostce | **Odbył się 2026-08-09 na `/login`**, gdzie toggle stoi na jednej stronie z `Button` i `TextField`. Trzy promienie na jednym ekranie: **8px** (toggle), **12px** (`Button`), **14px** (`TextField`, ton `surface`). **To nie jest rozjazd** — eksport stopniuje promień według roli kontrolki (`langBtnStyle` 9, `tabBtnBase` 10, `primaryBtnStyle`/`inputStyle` 12, `searchInputStyle` 14), więc odtwarzamy jego skalę, zamiast łamać własną. Przegląd z pozycji 2 zapisał toggle na 6px jako „nie rozjazd, bo jeszcze nieostylowany"; teraz jest ostylowany i ląduje dokładnie tam, gdzie kładzie go eksport. **12.6 pozostaje pusta zasadnie po raz trzeci.** Ubocznie potwierdził mechanizm przycinania obramowań: ten sam przycisk ma 30px w trybie urządzenia (DPR 1) i 29,59px na wyświetlaczu (DPR 1,25) |
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
| Nawigacja przy szerokości wąskiej | decyzja ① (12.2) | Co się dzieje ze stałą kolumną Sidebara przy 360px? Jedna konstrukcja czy dwie? Jeśli menu ma stan otwarcia — to jest zmiana zachowania, nie odstępu | **podjęta 2026-08-09: dwie konstrukcje.** Przy szerokości wąskiej rail zwija się do **menu hamburgera** — przycisk z trzema poziomymi kreskami otwiera szufladę z tą samą nawigacją; powyżej progu `lg:` zostaje dotychczasowa stała kolumna 250px, **bez zmiany o piksel**. Menu ma więc stan otwarcia, czyli jest to zmiana zachowania i rozlicza się w 12.7, nie w 12.3. **Przyrost `'use client'` wynosi jednak zero, wbrew dopuszczeniu z 12.2:** `Sidebar.tsx` jest komponentem klienckim od zawsze, bo używa `usePathname()`, więc `useState` nie dokłada nowego wpisu do listy z sekcji 6 |
| `--theme-hub-on-shell-muted` | 10.4; rozstrzygnięta przy jednostce 10 | Wartość docelowa czy świadomy placeholder? Etap C to jedyny moment, w którym decyzja jest naprawdę potrzebna | **podjęta 2026-08-09: placeholder zostaje wartością docelową — `rgba(255,255,255,0.7)`.** Eksport podaje dla nieaktywnej pozycji nawigacji `rgba(255,255,255,0.75)`; różnica 0,05 kanału alfa jest poniżej progu widzialności, a zmiana wartości w `globals.css` to ruch w warstwie tokenów, który §8.7 reguła 2 każe w tym kroku minimalizować. **To ta sama arytmetyka co przy obramowaniu `#e3ebe6` vs `#eef2ef`** — różnica realna, ale mniejsza niż koszt jej zapisania. **Uwaga: rail ma w eksporcie dwa poziomy wyciszenia**, nie jeden — 0,75 dla nawigacji i **0,4 dla linków prawnych** na dole. Ten token opisuje wyłącznie pierwszy; drugi pojawi się dopiero z relokacją linków (pozycja 11) i **nie wolno go załatwiać tym samym tokenem** |
| `--theme-hub-shell-hover` | 10.4; rozstrzygnięta przy jednostce 10 | jw. | **podjęta 2026-08-09: zostaje `rgba(255,255,255,0.1)` jako decyzja własna, nie odczyt.** Eksport jest statyczny i **nie rysuje żadnego stanu hover** dla pozycji nawigacji — nie ma z czym porównywać (§7.3, §8.9 pkt 2: brakujących stanów się nie wymyśla, ale też nie usuwa działającego zachowania). Ta sama sytuacja co wariant `outline` w `Button` |
| `--theme-hub-on-shell` | 10.4; rozstrzygnięta przy jednostce 10 | jw. | **podjęta 2026-08-09: wartość docelowa, `#ffffff` — zgodność dokładna.** Eksport używa czystej bieli dla wordmarku na railu (linia 220) i dla tekstu pozycji aktywnej nie używa jej wcale (tam `#0d2b47` na gradiencie mint→lime, co kod już ma poprawnie jako `text-hub-ink`) |
| Szerokość szeroka (ocena) | 12.0 | Eksport nie niesie szerokości artboardu w formie wydobywalnej — wartość ustalona decyzją, nie odczytem | **rozstrzygnięte 2026-08-07: 1280px** |
| Breakpoint 1024px | 12.0 | Pomiar wykonano na `/chat` (powłoka); kontrolny odczyt na profilu dał 726px. Trasy znajomych nie da się zmierzyć przed 2a — profil jest pomiarem zachowawczym | **potwierdzone 2026-08-07: 1024 zostaje** |
| Obramowanie `#e3ebe6` | 12.0, Faza 0; potrzebne przy `TextField` | Eksport odróżnia **dwie role obramowania**, dowód pod tabelą. Brak tokenu dla drugiej z nich | **podjęta 2026-08-07: obie role dostają `elevated-border`.** Uzasadnienie: §8.7 reguła 2 zabrania nowego tokenu w Kroku 4, a wpisanie `border-[#e3ebe6]` byłoby gorsze niż użycie tokenu — to powrót do surowego hexa, który Kroki 2–3 właśnie usunęły. Różnica 11/255 na linii 1px jest poniżej progu widzialności, a oba kolory i tak czekają na Krok 7 (11.3). **Rozróżnienie ról przestaje istnieć w kodzie, więc żyje w tym wierszu** |
| **Trasa znajomych — nazwa, zakres przeniesienia, punkty wejścia** | decyzja ③ (12.2); potrzebna przy jednostce **2a** (pozycja 17) | Trzy pytania zostawione otwarte w Fazie 0, bo **eksport ich nie rozstrzyga**: pokazuje gotowy ekran `isSearch`, ale nie to, jak się na niego wchodzi ani co zostaje na profilu | **podjęta 2026-08-09, trzy odpowiedzi.** **(1) Nazwa: `/friends`** — słownictwo, które kod już ma (`FriendsPanel`, `UserList`, licznik „Friends" na profilu). Eksport tytułuje ten ekran „Search Users", ale wyszukiwanie jest tam **czynnością**, nie tematem; trasa nazwana tematem przeżyje dodanie zaproszeń czy blokad, nazwana czynnością — nie. **(2) Zakres: przenosi się wszystko, co dotyczy znajomych** — pole wyszukiwania, wyniki i lista znajomych. Profil zostaje „to jestem ja": awatar, nazwa, e-mail, **licznik** znajomych, wylogowanie. Alternatywa (przenieść samo wyszukiwanie) rozdzieliłaby dodawanie znajomego i usuwanie go na dwie trasy. **(3) Punkty wejścia: link w Sidebarze oraz kafel na dashboardzie**, jak w eksporcie. **Zastrzeżenie wykonawcze: kafla nie da się dziś zbudować** — aplikacja nie ma trasy „Home", a eksport wchodzi na wyszukiwanie właśnie z dashboardu. Do czasu jej powstania **jedynym czynnym wejściem jest link w railu** — i to on wymusza pozycję 18 (§8.6 pkt 3). **Profil świadomie nie dostaje linku:** licznik zostaje liczbą, nie odnośnikiem |
| **WYJĄTEK od §8.7 reguły 2 — nowy token `--theme-start-page-gradient-mid-2`** | jednostka 21 (`(marketing)/`) | Gradient landingu z decyzji ② ma **cztery** stopnie: `120deg, #0a3348 0%, #146b7a 32%, #1f8f7a 62%, #2f9bcf 100%`. Trzy pierwsze obeszły się bez nowych nazw — istniejące `--theme-start-page-gradient-start/mid/end` **zmieniły tylko wartość**, a 12.4 zapisywało wprost, że stary `end` był „wartością wybraną bezpośrednio, brak dopasowania w eksporcie designu", czyli placeholderem czekającym na tę chwilę. Czwarty stopień, `#1f8f7a`, nie ma odpowiednika: `hub-mint` to `#4ac9a0`, inna barwa | **podjęta 2026-08-09: token dochodzi, reguła 2 złamana świadomie.** To **jedyny nowy token w całym Kroku 4** i ma nim pozostać — każdy następny wymaga osobnego wiersza, nie powołania się na ten. Uzasadnienie: alternatywą było zbudowanie gradientu z trzech stopni, czyli **utrata zielonkawego przejścia w środku** — a to nie jest detal, tylko tło dwóch grup tras i pierwsza rzecz widoczna po wejściu do aplikacji. Wartość dowolna wpisana wprost w `globals.css` byłaby gorsza: ten sam wyłom, ale bez nazwy i bez śladu. Token nazwany **pozycją w gradiencie** (`mid-2`), nie barwą — ta sama reguła, którą Krok 2 przyjął dla pozostałych stopni. **Bez nadpisania `.mocha`/`.latte`**, jak trzy pozostałe; czy gradient landingu w ogóle się motywuje, rozstrzyga Krok 5 |
| **ODSTĘPSTWO od §8.11 — `/chat` dostaje układ jednopanelowy przy szerokości wąskiej** | jednostka **2b** (nowa, poza kolejką 28); wymuszona przez Z.8 | Po 1b Sidebar zwija się w hamburger, ale na `/chat` zostaje **druga** stała kolumna: szyna znajomych, 290px z 360px. Trasa przewija się w poziomie. **Z.8 i §8.11 są tu w bezpośredniej sprzeczności** — Z.8 mówi „żadna z **siedmiu** tras", nazywa to jedynym kryterium binarnym kroku, a `/chat` jest siódmą; §8.11 zamraża `/chat` jako wzorzec docelowy. Jedno z dwóch musi ustąpić | **podjęta 2026-08-10: ustępuje §8.11, nie Z.8.** Przy szerokości wąskiej `/chat` pokazuje **albo** listę znajomych, **albo** rozmowę — `/chat` to lista, `/chat?friend=<id>` to rozmowa z powrotem do listy; powyżej `lg:` oba panele bez zmian. Trzy powody: **(1)** Z.8 jest jedynym kryterium binarnym kroku i wynika wprost z decyzji ①, która zobowiązała aplikację do działania przy 360px — kryterium, które omija najtrudniejszą trasę, nie jest kryterium; **(2)** wzorzec jednopanelowy z nawigacją wstecz jest standardem każdego komunikatora przy szerokości telefonu, więc nie wymaga nauki; **(3)** rozwiązanie jest **sterowane adresem, nie stanem** — a to znaczy, że `?friend=`, który `OpenChatLink` wysyła od pozycji 25, wreszcie coś robi, i **domyka wiersz 12.5 o nieczytanym parametrze**. **Powód czwarty, podany przez osobę prowadzącą i najmocniejszy z czterech: eksport nie zawiera widoku 360px dla żadnego ekranu.** „Wzorzec docelowy" z §8.11 jest wzorcem **jednej szerokości** — artboardu desktopowego. Nie da się złamać wierności wobec projektu w wymiarze, którego projekt nie opisuje; zamrożenie chroni tu nie decyzję projektanta, tylko jej brak. Ta sama logika stała za 1b: Sidebar również nie miał w eksporcie żadnego zachowania przy wąskim ekranie i również trzeba je było wymyślić. §8.11 pozostaje w mocy dla **wyglądu** `/chat`: kolory, typografia i geometria obu paneli bez zmian, zmienia się wyłącznie to, który z nich jest widoczny. Rozliczana w **12.7**, osobnym commitem, testem działaniem — jak 1b i 2a |
| **Przycisk „Change avatar" z eksportu użyty jako „Change name"** | jednostka 27 (`/[userId]`) | Eksport **nie ma żadnej edycji nazwy** — w hero profilu jest jeden przycisk, `Change avatar`. Aplikacja ma dwie edycje: nazwy i awatara. Dotychczasowy wyzwalacz nazwy był drobnym „Edit" obok tekstu, wyglądającym na niedokończony, a wyzwalacz awatara jest plakietką na samym awatarze i **działa dobrze** (ocena osoby prowadzącej) | **podjęta 2026-08-09: potraktowanie z `Change avatar` przechodzi na „Change name", plakietka awatara zostaje.** Przycisk bierze wartości z eksportu co do jednej: `10×20px`, promień 12px, 13,5px/700, tło `white/15`, obrys `white/60` — a ten obrys **jest już w słowniku** jako „wariant mocny" wiersza „krawędź na tle ciemnym", wpisany tam właśnie z tego przycisku. Etykieta skrócona do dwóch słów, równolegle do „Change avatar". **Dwa różne kształty dla dwóch różnych edycji są tu zaletą, nie niespójnością** — plakietka mówi „ten awatar", przycisk pod nazwą mówi „ta nazwa"; identyczne kontrolki wymagałyby czytania etykiet. Cancel/Save w trybie edycji dostały tę samą geometrię, żeby wiersz nie zmieniał kształtu po wejściu w edycję |
| Kolor `#155e8f` — tekst akcentowy na tle jasnym | jednostka 13 (`LegalSection`); ta sama barwa wraca przy przycisku „Wstecz" | Eksport używa jej **dwa razy, w dwóch rolach**: nagłówki sekcji stron prawnych (18px/800) i tekst przycisku drugorzędnego. Nie odpowiada jej żaden token — `hub-blue` (`#2f9bcf`) i `hub-teal` (`#146b7a`) to inne odcienie, nie jaśniejszy/ciemniejszy wariant tego samego | **podjęta 2026-08-09: nagłówek dziedziczy `on-elevated-surface`, koloru nie odtwarzamy.** §8.7 reguła 2 zabrania nowego tokenu w Kroku 4, a `text-[#155e8f]` byłoby powrotem do surowego heksa, który Kroki 2–3 właśnie usunęły — ten sam argument co przy `#e3ebe6`. **Różnica jest tu jednak widoczna, w przeciwieństwie do tamtej.** Eksport celowo odróżnia nagłówki sekcji (niebieskie) od tekstu ciągłego (`#3d4a44`); u nas oba będą ciemnogranatowe i rozróżni je wyłącznie rozmiar i waga. To realna strata wizualna, przyjęta świadomie. **Do Kroku 7:** ta barwa jest najlepszym kandydatem na token „tekst akcentowy na powierzchni jasnej", bo ma już **dwie** czekające role, a nie jedną |
| Obramowanie `#d6e4ee` | 12.0, Faza 0 | Krawędź przycisku drugorzędnego („Wstecz"). Też bez tokenu, wyraźnie chłodniejszy niż dwa powyższe — możliwe, że to trzecia rola (krawędź kontrolki), a nie wariant krawędzi panelu | otwarta — potrzebna przy Etapie B (`terms-of-service`, przycisk „Wstecz") |
| **Odstępstwo od §8.11 — `/chat` zmienia wygląd** | jednostka 3 (`Avatar`) | `Avatar` nie ma **żadnej** osi wariantów: ani tonów jak `TextField`, ani wariantów jak `Button`. Jest jeden wygląd, wspólny dla wszystkiego, a `/chat` używa go przez `Conversation` i `FriendRow` → `PresenceAvatar`. Nie istnieje sposób, by ostylować ten komponent i nie ruszyć `/chat` | **podjęta 2026-08-07: stylujemy, odstępstwo zapisane.** Trzy uzasadnienia: (1) alternatywą było odłożenie całej jednostki, czyli trzeci z sześciu komponentów Etapu A bez stylowania; (2) awatary na `/chat` **i tak są tam zepsute** — `chat/page.tsx` podaje `color: 'bg-hub-panel'`, czyli nazwę klasy Tailwinda w miejsce wartości CSS, więc kolor jest cicho ignorowany; (3) zmiana **zbliża** `/chat` do eksportu, nie oddala. **To jest pierwsze świadome odstępstwo od §8.11 w tym kroku i ma pozostać jedynym** — każde następne wymaga osobnego wiersza i osobnego uzasadnienia, nie powołania się na ten. **Sprostowanie z tego samego dnia: odstępstwo jest utajone, nie czynne.** Pomiar `getComputedStyle` na `/chat` i `/[userId]` pokazał, że **wszystkie** awatary renderują się gałęzią `<Image>` — każdy użytkownik w obecnych danych ma wgrane zdjęcie, więc zmieniona gałąź zastępcza nie renderuje się nigdzie. Na `/chat` nie widać dziś żadnej różnicy. Odstępstwo pozostaje zapisane, bo dotyczy kodu wspólnego i ujawni się przy pierwszym użytkowniku bez awatara — ale uzasadnienie (3) jest na razie przewidywaniem, nie obserwacją |
| Gradient CTA dla wariantu `primary` | jednostka 2 (`Button`) | `primaryBtnStyle` w eksporcie to gradient mint→lime, a `--theme-primary` jest płaskim `#a3e635` i gradientu nie wyrazi | **podjęta 2026-08-07: `primary` przyjmuje `bg-hub-cta`.** Wykonanie instrukcji zapisanej w `globals.css` (komentarz przy `@utility bg-hub-cta`). `text-on-primary` (`#0d2b47`) jest już kolorem, który eksport kładzie na tym przycisku. Świadomy koszt: **dochodzi jeden użytkownik `hub-*`** w kroku, który ich pozbywa — Krok 8 musi dotknąć `Button`, co ten sam komentarz w `globals.css` już przewiduje |
| `Tag` — BRAK ODPOWIEDNIKA, a mimo to musi się zmienić | jednostka 6 (`Tag`) | Eksport nie zawiera **żadnej** pigułki — jego hero to logo, wordmark, nagłówek i podtytuł, i nic więcej. Nie ma więc wzorca, do którego można być wiernym. Jednocześnie §8.13 kryterium 5 wymaga, żeby ten plik przestał odwoływać się do `brand-*`, bo inaczej Krok 8 nie może wycofać tej palety. Komponent musi się zmienić **mimo braku odniesienia** — sytuacja odwrotna do `outline`, gdzie brak odniesienia był powodem, żeby nie ruszać | **podjęta 2026-08-07: `bg-primary text-on-primary`.** Wybór przez eliminację: `brand-*` wyklucza kryterium 5, `hub-*` tylko przenosi problem do Kroku 8, zostają tokeny semantyczne. `primary`/`on-primary` to para akcentowa, której eksport używa na CTA, a zastępowany mint jest drugim końcem tego samego gradientu mint→lime. **Widoczny skutek: pigułka zmienia kolor z `#5cd4a0` na `#a3e635`** — zmiana wyglądu strony landingowej bez mandatu z designu, wymuszona ograniczeniem. Do rewizji przy pozycji 19 (`Hero`), gdzie cały ten obszar i tak jest przeprojektowywany. **Uzupełnienie 2026-08-07: pigułki mają zniknąć przed końcem migracji** (ustalenie osoby prowadzącej, zgodne z tym, że eksport ich nie zawiera). Stylowanie z pozycji 6 jest więc tymczasowe — ale wyjście z `brand-*` **nie było pracą na marne**: dzięki niemu połowa kryterium 5 jest spełniona **już teraz**, zamiast być uzależniona od tego, czy przeprojektowanie `Hero` faktycznie usunie ten komponent. Kryterium spełnione przez zmianę jest pewniejsze niż kryterium spełnione przez planowane usunięcie |
| Wariant `outline` — BRAK ODPOWIEDNIKA | jednostka 2 (`Button`) | Eksport nie rysuje żadnego przycisku obrysowanego na trasie, która renderuje ten komponent. Jedyny obrysowany („Wstecz") leży na stronach prawnych, które `Button` nie importują; „Wyloguj" w eksporcie to wypełniony czerwony gradient na ekranie profilu, a to inny ekran i inny komponent | **podjęta 2026-08-07: kolory bez zmian, tylko promień.** Nie da się być „zgodnym z eksportem" z czymś, czego eksport nie narysował (§7.3, §8.9 pkt 2 — brakujące stany nie są do wymyślenia). Do rewizji przy pozycji 20 (`SessionCard`), która jest jedynym miejscem użycia tego wariantu. **Rewizja wykonana 2026-08-09 — wynik: nadal bez zmian, i tym razem z mocniejszym powodem.** Limonkowy `text-primary` na dzisiejszej białej karcie daje kontrast ~1,7:1, czyli praktycznie nieczytelny — ale karta **staje się ciemna** na pozycjach 21/22 (decyzja ②), a limonka na ciemnym tle jest wartością z eksportu. Naprawianie tego teraz oznaczałoby dobranie koloru pod tło, które za dwie jednostki przestanie istnieć. **Dokładnie ta sama logika i ten sam termin co przy `AccentLink` w 12.5:** najpierw sprawdź, czy problem nadal istnieje po zmianie tła, potem mierz. Jedyny obrysowany przycisk w eksporcie („Wstecz") i tak jest nie do odtworzenia — obie jego barwy, `#d6e4ee` i `#155e8f`, nie mają tokenów |
| Zakres §8.9 pkt 9 przed jednostką 1b | jednostka 1 (`TextField`) | Punkt 9 pyta „czy strona przewija się w poziomie", ale przy 360px **każda** trasa `(app)` przewija się do czasu przebudowy powłoki: sam Sidebar to stała kolumna 250px, a `/chat` dokłada 290px szyny znajomych — 540px stałej konstrukcji w oknie 360px. Wzięte dosłownie, punkt 9 przepada dla jednostek 1–11 i definicja ukończenia przestaje cokolwiek znaczyć | **podjęta 2026-08-07: dla jednostki komponentowej punkt 9 pyta o komponent, nie o stronę** — czy sam komponent nie wystaje, nie jest za mały do dotknięcia i nie łamie się przy zwężeniu. Test całej strony jest już osobno jako kryterium **Z.8** w §8.13 (poziom kroku) i zaczyna obowiązywać dla tras dopiero po jednostce 1b. **Uzupełnienie 2026-08-09: 1b zamknięta, więc to złagodzenie wygasło dla tras `(app)`.** Od pozycji 13 punkt 9 czytamy tam znowu dosłownie — strona ma się nie przewijać w poziomie przy 360px, i przy zamknięciu 1b faktycznie nie przewijała się (`scrollWidth - innerWidth = 0`). Dla tras `BareLayout` złagodzenie nigdy nie było potrzebne: tam nie ma stałej kolumny |
| **Zakres §8.11 — czy zamrożenie `/chat` obejmuje powłokę, czy tylko treść** | jednostka 7 (`ThemeToggle`) | Jedynym importerem `ThemeToggle` jest `Footer`, a `Footer` renderuje się w **obu** powłokach: `BareLayout.tsx:17` i `app/(app)/layout.tsx:34`. `/chat` leży w grupie `(app)`, więc ostylowanie toggla **zmienia wygląd na `/chat`** — potwierdzone pomiarem 2026-08-09 (te same wartości co na `/`, `/login`, `/[userId]`). Pytanie brzmi: czy to drugie odstępstwo od §8.11 | **podjęta 2026-08-09: nie jest to odstępstwo, tylko doprecyzowanie zakresu. §8.11 zamraża treść `/chat`, nie chrom wokół niej.** Dowód z samego planu: pozycje 9, 10 i 11 to `Footer`, `Sidebar` i `app/(app)/layout.tsx` — plan **planowo** przebudowuje powłokę opakowującą `/chat`, więc czytanie §8.11 jako zamrożenia powłoki czyniłoby cały Etap C niewykonalnym. Zgodne z zapisem z 12.1: fan-in komponentu powłoki to trasy, na których występuje powłoka. **Zamrożone pozostają komponenty własne `/chat`** — `ChatInterface`, `Composer`, `FriendRail`, `FriendRow`, `PresenceAvatar`, bąbelki wiadomości. **Ten wiersz obejmuje pozycje 7, 9, 10 i 11 łącznie** i nie wolno się na niego powoływać przy ruszaniu treści `/chat` — tam nadal obowiązuje osobny wiersz na osobne uzasadnienie, jak przy `Avatar` |
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
| `UserSearch` przekazuje `size="sm"`, ale ekran wyszukiwania w eksporcie używa **dużego** pola (`searchInputStyle`, 15×18px). To decyzja miejsca wywołania, nie komponentu | `app/components/UserSearch.tsx` | Krok 4, **pozycja 24** — nie w jednostce 1 (§8.0, jeden obszar na jednostkę). **ZAMKNIĘTE 2026-08-09: `size="md"`.** Rozstrzygnięcie jest częściowe i takie musi zostać: eksport chce 15×18px, a `md` daje 16×12px, bo `SIZE_CLASSES` pozostaje wspólne z tonem `chat` i zamrożone przez §8.11. **Miejsce wywołania mogło wybrać spośród dwóch dostępnych rozmiarów i wybrało właściwy** — dokładna wartość odblokuje się dopiero, gdy ton `chat` zniknie razem z `hub-*` |
| **Prefiksy `md:` sprzed migracji vs kryterium Z.7.** Breakpointem kroku jest `lg:` (12.0), ale w kodzie siedzą jeszcze pojedyncze `md:` opisane w 9.5 pkt 6. Kryterium **Z.7** wymaga **dokładnie jednego** breakpointu na koniec kroku, więc każdy z nich trzeba przerobić albo usunąć — a **żadna jednostka w 12.1 obecnie tego nie obejmuje** | całe `app/`, zakres do ustalenia | Krok 4 — zamiatanie przed zamknięciem, do dopisania jako osobna pozycja kolejki albo do rozliczenia przy jednostkach, które te pliki i tak otwierają |
| `Button` — padding i typografia niezgodne z eksportem (`py-3` = 12px wobec 14px; `text-sm font-bold` = 14px/700 wobec 15px/800). Wszystko troje siedzi w `BASE_CLASSES`, wspólnym z wariantem `send`, którego używa `/chat` | `app/components/Button.tsx` | Krok 6 lub 8 — dokładnie ta sama blokada co przy `TextField`, z tego samego powodu |
| `outline` wypełnia się na hover płaskim `bg-primary`, podczas gdy `primary` jest już gradientem `bg-hub-cta`. Obrys przestał być obrysem tego, co wypełnia | `app/components/Button.tsx` | Krok 6 — i tak scala warianty |
| **`SessionCard` nie ma odpowiednika w eksporcie.** Eksport nie zawiera karty „witaj z powrotem, kontynuuj" — jego landing prowadzi wprost do karty logowania. Cała ta karta, razem z jedynym użyciem wariantu `outline`, jest UI spoza projektu | `app/components/SessionCard.tsx` | Krok 4, **pozycja 20** — będzie rozstrzygana decyzją własną, nie odczytem z eksportu (12.4) |
| **Błąd: nazwa klasy Tailwinda podana jako wartość CSS.** `color: 'bg-hub-panel'` trafia do `style={{ backgroundColor: color }}` w `Avatar`, czyli w miejsce, które oczekuje koloru CSS. Wartość jest cicho ignorowana, więc awatary bez zdjęcia spadają na tło domyślne | `app/(app)/chat/page.tsx` (linie 31 i 57) | Poza Krokiem 4 — `/chat` jest zamrożone (§8.11). Do naprawy razem z `/chat` w Kroku 8 |
| `Avatar` — obwódka 3px `rgba(255,255,255,0.5)` występuje w eksporcie **tylko** na awatarze hero profilu, nie na pozostałych. Dodanie propa to zmiana API | `app/components/Avatar.tsx` | Krok 6 (prop) albo Krok 4 pozycja 27 (klasa w miejscu wywołania). **Rozstrzygnięte na 27 klasą w miejscu wywołania — i przesłanka tej decyzji wygasła w tej samej pozycji.** Uzasadnienie brzmiało „obwódka jest na dokładnie jednym awatarze w całym projekcie, więc prop dodawałby API dla jednego użytkownika". Pozycja 27 dobudowała jednak gałąź cudzego profilu, a ta też rysuje obwódkę: miejsc wywołania są **dwa**, nie jedno, i ten sam magiczny łańcuch stał w dwóch plikach do ręcznej synchronizacji. **Naprawione przy przeglądzie PR 2026-08-10:** łańcuch mieszka w `app/(app)/[userId]/avatarRing.ts` i oba miejsca go importują. Pytanie „prop czy klasa" jest przez to **ponownie otwarte** — argument „jeden użytkownik" już nie działa — ale rozstrzyga je Krok 6, bo zwinięcie obu gałęzi w jedną zmienia API `EditAvatarButton` (§8.7 reguła 3). **Wniosek metodyczny: zapisane uzasadnienie jest zdjęciem stanu, a jednostka, która je unieważnia, jest tą, która ma je poprawić** — tutaj unieważniła je jednostka o numer dalej, więc wpis przeżył niecały dzień |
| **Gałąź zastępcza `Avatar` jest w praktyce nieosiągalna.** Wszystkie pięć miejsc wywołania liczy `src` jako `avatarId ? '/api/users/avatar/…' : null` — uczciwy warunek, bez zaszytej wartości domyślnej po stronie frontendu. Backend wydaje jednak `avatarId` **każdemu** użytkownikowi (potwierdzone na świeżym koncie bez wgranego zdjęcia), więc `src` nigdy nie jest `null` i gałąź z inicjałem nie renderuje się nigdzie. Pytanie „czy ta gałąź ma rację bytu" jest pytaniem o API komponentu, nie o styl | `app/components/Avatar.tsx` oraz pięć miejsc wywołania | Krok 6 — zostawić jako zabezpieczenie czy usunąć razem z propami `initial` / `color` (§8.7 reguła 3 zabrania rozstrzygać to w Kroku 4) |
| **`prettier-plugin-tailwindcss` nie sortuje klas.** `.prettierrc` ładuje wtyczkę poprawnie, ale podaje jej `"tailwindConfig": "./tailwind.config.ts"` — formę z Tailwinda **3**, wskazującą plik konfiguracyjny JS. Projekt stoi na Tailwindzie **4**, gdzie konfiguracja jest w CSS (`@theme` w `app/globals.css`), więc wtyczka nie znajduje kontekstu i po cichu nic nie sortuje. Objaw: sześć plików Etapu A wraca z `--write` jako `unchanged`, a zastane łańcuchy klas nie są w kolejności kanonicznej (np. `Card` miał `w-72` i `p-8` **po** kolorach). Poprawną opcją dla v4 jest `"tailwindStylesheet": "./app/globals.css"` | `frontend/.prettierrc` | **Poza Krokiem 4** — to naprawa narzędzia, nie designu. Warto osobnym commitem, bo w chwili gdy wtyczka ruszy, przesortuje łańcuchy klas w całym projekcie. Konsekwencja na teraz: kolejność klas jest ręczna i taka trafia do repo. **Decyzja robocza: nie sortujemy jej ręcznie** — łańcuchy klas zostają w konwencji danego pliku, a zmieniamy wyłącznie to, co faktycznie się różni. Powód jest czytelnościowy: diff ma pokazywać zmienioną wartość, a nie przetasowanie ośmiu klas. Sortowanie całego projektu przyjdzie jednym commitem, gdy wtyczka zacznie działać |
| `AccentLink` — `text-primary` (`#a3e635`) na dzisiejszej białej karcie daje bardzo niski kontrast. Kolor jest **zgodny z eksportem** (tak samo wygląda tam „Forgot Password?"), tyle że eksport kładzie go na ciemnej karcie. Zestawienie jasne+lime jest stanem przejściowym, nie docelowym | `app/components/AccentLink.tsx` na `/`, `/login`, `/register` | Krok 7 — z zastrzeżeniem, że **pozycja 22 prawdopodobnie rozwiąże to sama**, gdy karta stanie się ciemna (decyzja ②). Zgodnie z regułą z 12.2: najpierw sprawdź, czy problem nadal istnieje, potem mierz. **ZAMKNIĘTE 2026-08-09 — i rozwiązało się dokładnie samo, bez ani jednej zmiany w `AccentLink`.** Karta jest ciemna, limonka `#a3e635` leży na niej tak, jak kładzie ją eksport („Forgot Password?"), a plik z pozycji 5 nie został przy tym otwarty. **Reguła „najpierw sprawdź, czy problem nadal istnieje" zaoszczędziła tu całą jednostkę pracy** |
| `Card` — kolory i cień niezgodne z eksportem (biel + `elevated-border`, brak cienia; eksport: ciemna półprzezroczystość, `white/14`, `0 25px 60px rgba(0,0,0,0.35)`). **Nie jest to blokada techniczna, tylko kolejność**: tło pod kartą kładą trasy, nie komponent, więc ciemna karta teraz oznacza ciemny tekst na ciemnym tle przez całą Fazę 1 | `app/components/Card.tsx` | Krok 4, **pozycja 22 — doprecyzowane 2026-08-09, nie 21.** Pozycja 21 postawiła ciemne tło i **świadomie zostawiła kartę białą**: biała karta na ciemnym gradiencie jest w pełni czytelna i jest poprawnym stanem pośrednim. Przyciemnienie jej na 21 zepsułoby logowanie, bo ton `elevated` w `TextField` to dziś `bg-on-elevated-surface/10` z ciemnym tekstem — na ciemnej karcie dałoby ciemne na ciemnym. **Karta i ton `elevated` muszą pociemnieć w jednej jednostce**, i tą jednostką jest 22. **ZAMKNIĘTE 2026-08-09.** Karta dostała gradient `160deg, rgba(20,40,45,0.55) → rgba(15,26,38,0.6)`, `backdrop-blur-[18px]`, `border-white/[0.14]` i cień `0 25px 60px rgba(0,0,0,0.35)`; wszystkie cztery miejsca wywołania przeszły na tekst biały w tym samym commicie. **Token `on-elevated-surface` nietknięty** — nadal maluje wiersze `UserList`, kartę stron prawnych i ton `surface`, potwierdzone kontrolnie na `/[userId]` i `/privacy-policy` |
| `Card` — szerokość `w-72` (stałe 288px) wobec `width:100%; max-width:440px` w eksporcie. Zmiana nie jest bezpieczna na poziomie komponentu: `(marketing)/page.tsx` stawia kartę obok `Hero` w kontenerze `flex flex-wrap`, gdzie `w-full` rozwiązuje się do 100% kontenera i łamie układ na dwie linie. Eksport zakłada wyśrodkowaną kolumnę, czyli kontekst logowania, nie landingu | `app/components/Card.tsx`, `app/(marketing)/page.tsx` | Krok 4, **pozycje 21 i 22** — decyzja miejsca wywołania. **ZAMKNIĘTE 2026-08-09: `w-full max-w-[440px]`, dokładnie jak w eksporcie.** Blokada zniknęła sama: powodem był `flex-wrap` na landingu, a pozycja 21 zastąpiła go stosem pionowym. **Wiersz wart zapamiętania jako wzorzec** — ograniczenie nie zostało obejście ani przegłosowane, tylko przestało istnieć, gdy zmienił się układ, który je tworzył |
| `PresenceAvatar` — kropka offline używa `bg-gray-400`, surowego koloru z palety Tailwinda zamiast tokenu; jej odpowiednik online stoi poprawnie na `bg-hub-online`. Sama kropka ma 11px, czyli dokładnie tyle co w eksporcie | `app/components/PresenceAvatar.tsx` | Poza Krokiem 4 — komponent renderuje się wyłącznie na `/chat` (przez `FriendRow`), zamrożonym przez §8.11. Do Kroku 8 |
| Awatary w wierszach list renderują się na **40px**, a eksport podaje **44px** (wiersze wyszukiwania i szyny czatu) oraz 42px w nagłówku rozmowy. Hero profilu ma 96px i zgadza się co do piksela. `size` jest liczbą podawaną przez wywołującego, więc to nie jest ustawienie komponentu | `app/components/UserList.tsx`, `app/(app)/[userId]/page.tsx` | Krok 4, **pozycje 23 i 27** — decyzja miejsca wywołania. **ZAMKNIĘTE dla wierszy list 2026-08-09 na pozycji 23: `size={44}`.** Jedno miejsce wywołania wystarczyło, bo `UserList` obsługuje **oba** konteksty — listę znajomych i wyniki wyszukiwania — a eksport daje im tę samą wielkość. Część przypisana pozycji 27 jest bezprzedmiotowa: hero profilu ma 96px i zgadzał się co do piksela od początku. Zostaje wyłącznie 42px w nagłówku rozmowy, zamrożone przez §8.11 |
| `TextField` — waga pisma 400 wobec 500 w eksporcie (`inputStyle` i `searchInputStyle` mają `fontWeight: 500`). **Przeoczone przy jednostce 1**, nie zablokowane — waga daje się ustawić per ton, więc `/chat` jej nie broni | `app/components/TextField.tsx` | Krok 4, **pozycja 22** — `TextField` i tak wraca wtedy po kolory tonu `elevated` (12.1). **ZAMKNIĘTE 2026-08-09:** `font-medium` dopisane do tonów `elevated` i `surface`, czyli do obu pól, którym eksport daje 500; `chat` zostaje przy swojej wadze, bo §8.11. **Najdłużej otwarty wiersz w tej tabeli** — przeoczenie z jednostki 1, wykryte tabelą `getComputedStyle` przy pozycji 2, zamknięte przy 22 |
| **Eksport nie zawiera ani jednego przycisku z obramowaniem.** Wszystkie cztery style przyciskowe — `primaryBtnStyle`, `tabBtnBase`, `navItemStyle`, `langBtnStyle` — mają `border: 'none'`, bez wyjątku; obramowania w eksporcie występują wyłącznie na polach i panelach (`#e3ebe6`, `#eef2ef`). Aplikacja ma dziś **dwa** obramowane przyciski: wariant `outline` w `Button` i `ThemeToggle`. Oba obramowania są **tymczasowe i zapisane jako takie**, ale z różnych powodów: `outline` nie ma w eksporcie odpowiednika w ogóle (12.4), a `ThemeToggle` odpowiednik ma (`langBtnStyle`) i różni się od niego wyłącznie tym jednym atrybutem. **Ustalenie osoby prowadzącej 2026-08-09: gdy toggle trafi na swoje miejsce w nowym projekcie, obramowanie schodzi.** Dziś zostaje, bo komponent siedzi w `Footer` na tle tego samego tokenu `surface`, którym sam jest wypełniony — bez obramowania nie byłoby go widać. To jest zależność od kontekstu, nie od komponentu: obramowanie znika w chwili, gdy tło pod przyciskiem przestaje być `surface` | `app/components/ThemeToggle.tsx`, `app/components/Button.tsx` **Krok 6 — rozstrzygnięte 2026-08-09 przy pozycji 11.** Relokacja okazała się niewykonalna w Kroku 4 z powodu, którego przy pozycji 7 nie było widać: eksport **parametryzuje swoją odpowiedniczkę tej kontrolki po powłoce** — `langBtnStyle = (active, isSidebar) => …` (linia 650) daje przezroczyste tło na railu i `rgba(255,255,255,0.9)` na landingu. `ThemeToggle` musiałby więc dostać oś wariantów, a to zmiana API (§8.7 reguła 3). Do tego czasu obramowanie **zostaje** — nie dlatego, że jest poprawne, tylko dlatego, że bez niego przycisk znika na tle `surface` |
| **Konflikt wewnątrz naszej własnej definicji ukończenia: §8.9 pkt 9 vs słownik 12.0.** `ThemeToggle` renderuje się na **29,59px** wysokości (zmierzone 2026-08-09: 6 + 16 + 6, plus dwa obramowania po 0,79px). Punkt 9 oczekuje celu dotykowego ~44px. Podniesienie paddingu do 44px oznaczałoby jednak odejście od `langBtnStyle`, który sam ma ~29px — **to nie jest rozjazd z eksportem, tylko rozjazd między dwoma naszymi regułami**, i rozstrzyga go fakt, że WCAG 2.2 na poziomie **AA** wymaga 24×24px (spełnione z zapasem), a 44px to poziom AAA / wytyczna Apple. Ta sama arytmetyka dotyczy każdej małej kontrolki, którą eksport rysuje w tej skali | `app/components/ThemeToggle.tsx`; potencjalnie wszystkie kontrolki ze słownika 12.0 z wiersza „mała kontrolka" | Krok 7 (dostępność) — **nie Krok 4.** Rozstrzygnięcie wymaga wyboru poziomu zgodności dla całej aplikacji, a nie korekty jednego przycisku; korekta pojedynczej kontrolki dałaby jeden przycisk niezgodny ze słownikiem i nie zbliżyła do zgodności AAA nigdzie indziej |
| **Obramowania włosowe renderują się cieńsze, niż podaje CSS.** `border` (1px) wraca z `getComputedStyle` jako **0,8px** — Chrome przycina krawędź do pełnego piksela urządzenia, więc przy DPR ≈ 1,26 zostaje ~0,79px CSS. Nie jest to błąd w kodzie i nie ma czego naprawiać w Kroku 4, ale **ma znaczenie dla pomiaru kontrastu**: `elevated-border` jest już zapisane z kontrastem 1,04–1,13:1, a na tym wyświetlaczu ta linia jest dodatkowo o ~20% cieńsza, niż zakłada wartość w CSS | `app/globals.css`, wszystkie panele i pola z obramowaniem | Krok 7 — uwaga metodyczna do wiersza `elevated-border` powyżej: kontrast mierzony na cieńszej linii jest w praktyce gorszy niż wynikający z samych kolorów |
| **Wordmark w eksporcie nigdy nie występuje sam — jest połową logotypu.** Wszystkie cztery wystąpienia „42Hub" stoją przy kafelku z gradientem (`135deg,#1a7a8a,#2f9bcf 55%,#4ac9a0`) zawierającym wbudowany SVG: 88px z promieniem 26px na landingu (kafelek **nad** napisem), 32px/10px w nagłówkach stron prawnych, 36px/11px w Sidebarze. `BrandLink` jest wyłącznie tekstem, więc **największa różnica w tej jednostce jest różnicą, której nie zamykamy** — dorobienie znaku to nowa treść i nowy zasób, a nie restylowanie | `app/components/BrandLink.tsx`, oba miejsca wywołania | Krok 4 — **jako decyzja miejsca wywołania**: pozycja 19 (`Hero`, kafelek 88px) i Etap B (nagłówki stron prawnych, 32px), ewentualnie osobny komponent znaku w Kroku 6. Nie mieści się w pozycji 8, bo jednostka komponentowa nie dokłada treści |
| **Eksport używa dwóch rozmiarów wordmarku dla dwóch ról; komponent umie jeden.** 26px w hero landingu wobec 19px w chromie (Sidebar, nagłówki prawne). Pozycja 8 wybiera **19px dla obu miejsc wywołania**, bo oba są dziś umiejscowione jak chrom (`absolute left-6 top-6`, nagłówek Sidebara), a hero landingu i tak nie odtwarza układu z eksportem. Osobno: **`BareLayout` nie podaje `BrandLink` żadnego koloru** i napis dziedziczy to, na czym akurat leży — `Sidebar` podaje `text-white` i robi to poprawnie, bo eksport zmienia ten kolor wraz z tłem (biel na ciemnym, `#0d2b47` na białym nagłówku). Kolor **słusznie** zostaje w miejscu wywołania; brakuje go tylko po jednej stronie. **Pomiar 2026-08-09: dziś to nie boli, ale zaboli na pozycjach 21/22.** Na `(marketing)` odziedziczony kolor wychodzi jako `rgb(13, 43, 71)`, czyli **dokładnie `#0d2b47`** — ta sama wartość, którą eksport daje wordmarkowi w jasnym nagłówku stron prawnych. Zbieżność jest jednak przypadkowa i **nietrwała**: `BareLayout` obsługuje wyłącznie `(auth)` i `(marketing)`, a **obie te grupy stają się ciemne** — `(auth)` przez decyzję ② na pozycji 22, `(marketing)` na pozycji 21. W chwili gdy tło pociemnieje, `#0d2b47` na ciemnym gradiencie jest tą samą pułapką co ton `elevated` w `TextField` i kolory `Card` | `app/components/BrandLink.tsx`; `app/components/BareLayout.tsx:15` | Oś rozmiaru → **Krok 6** (prop = zmiana API, §8.7 reguła 3). Brakujący kolor → **pozycje 21 i 22**, nie pozycja 11: kolor jest tu funkcją tła, a tło kładą trasy. Pozycja 11 (`BareLayout`) może go najwyżej przekazać, ale nie ma z czego wybrać, dopóki tło nie jest rozstrzygnięte |
| **Światło międzyliterowe z eksportu nie jest odtworzone.** Wordmark ma `letter-spacing:-0.3px` (chrom) i `-0.4px` (hero). Najbliższa nazwana wartość Tailwinda, `tracking-tight` (−0,025em), daje przy 19px **−0,475px**, czyli przestrzeliwuje o połowę; wartość dowolna `tracking-[-0.3px]` byłaby wpisaniem liczby z eksportu wprost. Skutek łączny na pięciu znakach „42Hub" to ~1,5px zwężenia — poniżej progu widzialności, ta sama klasa decyzji co 11/255 różnicy na obramowaniu (12.4) | `app/components/BrandLink.tsx` | Krok 6 lub 8 — tylko jeśli słownik 12.0 w ogóle dorobi kategorię światła międzyliterowego; dziś jej nie ma i pojedyncza wartość dowolna byłaby wyłomem, nie wiernością |
| **Eksport nie ma stopki na trasach aplikacji — i nie jest to przeoczenie.** Ekrany `isApp` nie zawierają żadnego elementu stopki; linki „Privacy Policy" i „Terms of Service" siedzą **na dole Sidebara** (linie 246–249), w stosie pionowym, 11,5px / 600 / `rgba(255,255,255,0.4)`. Nasz `app/(app)/layout.tsx:34` renderuje tam pełną stopkę poziomą. Osobno: **GitHub w ogóle nie jest linkiem chromu** — w eksporcie to jeden z czterech kafli dashboardu (`dashCards.github`), a u nas pierwszy link stopki. Przeniesienie linków do Sidebara i kafelka na dashboard to **zmiana struktury, nie stylowania**, więc pozycja 9 jej nie robi | `app/components/Footer.tsx`, `app/(app)/layout.tsx:34`, `app/components/Sidebar.tsx` **Krok 6** dla usunięcia stopki i przeniesienia linków; **pozycja 21** dla kafla GitHub. **Przesunięte z pozycji 11 decyzją z 2026-08-09.** Powód jest jeden i wyszedł dopiero przy czytaniu `BareLayout`: usunięcie stopki z tras `(app)` zabiera `ThemeToggle` **jedynego gospodarza**, jakiego tam ma, a przeniesienie go na rail wymaga osi wariantów (patrz wiersz o obramowaniach). Trzy rzeczy — stopka, linki i toggle — okazały się jedną zmianą, a ta jedna zmiana zawiera zmianę API. **Koszt zapisany świadomie: `/chat` i `/[userId]` renderują do Kroku 6 stopkę, której design nie ma** |
| **Górna krawędź stopki na ciemnym tle nie jest jeszcze rozstrzygnięta.** Słownik 12.0 ma dwa wiersze: „krawędź powierzchni podniesionej" (`elevated-border`, stopka stron prawnych, `#e3ebe6`) i „krawędź na tle ciemnym / gradiencie" (`border-white/[0.14]`, landing — eksport ma tam `rgba(255,255,255,0.12)`). Pozycja 9 bierze pierwszy, bo dziś wszystkie trasy są jasne. `BareLayout` ciemnieje jednak na pozycjach 21/22 — **dokładnie ta sama zależność co przy kolorze `BrandLink`**, w tym samym pliku-rodzicu i z tym samym terminem | `app/components/Footer.tsx` | Krok 4, **pozycje 21 i 22** — rozliczyć razem z kolorem `BrandLink`, bo obie decyzje zależą od jednego tła i obie dotyczą `BareLayout` |
| **Odstęp między linkami stopki (20px) nie występuje w słowniku.** Wiersz „między elementami w grupie" podaje 12px, ciasny 6px i luźny 16px, a jego kolumna „Skąd" wymienia sidebar, formularze i wiersze list — **stopki nie badano**. Pozycja 9 wpisuje `gap-5` (dokładne 20px), bo skala odstępów jest dynamiczna i nie wymaga zaokrąglania (reguła spod tabeli 12.0), ale formalnie jest to wartość spoza słownika | 12.0, wiersz „Odstępy / między elementami w grupie" | Krok 4 — **uzupełnić słownik przy najbliższej okazji**, dopisując 20px jako czwartą wartość tego wiersza. Wersja alternatywna (zaokrąglić do `gap-4`) byłaby zaokrąglaniem odstępu, czego reguła 12.0 wprost nie przewiduje |
| **Cień wiersza listy (`0 6px 18px rgba(10,42,77,0.07)`) to czwarta wartość, której słownik nie zna.** 12.0 ma trzy wiersze cieni: subtelny `0 4px 14px /0.06`, karta `0 8px 24px /0.08` i karta na ciemnym `0 25px 60px /0.35`. Cień wiersza wyników z eksportu leży **między pierwszym a drugim** i nie jest żadnym z nich. Pozycja 23 wpisuje wartość dokładną, zgodnie z regułą spod tabeli 12.0 („cienie zostają dokładne, bo skala `shadow-*` Tailwinda i tak nie trafia") — ale formalnie to wartość spoza słownika, **drugi taki przypadek po odstępie 20px w stopce** | 12.0, kategoria „Cienie"; `app/components/UserList.tsx` | Krok 4 — **uzupełnić słownik razem z wierszem o `gap-5` wyżej.** Oba znaleziska mówią to samo: tabela 12.0 powstała z przeglądu ekranów, których wtedy nie budowaliśmy, więc pojedyncze role wypadły z niej po cichu. Warto dopisać obie wartości jednym ruchem, zamiast odkrywać trzecią przy pozycji 27 |
| **Skład i układ Sidebara odbiega od eksportu strukturalnie, nie stylistycznie.** Eksport ma na railu: logotyp (kafelek 36px + wordmark), **przełącznik języka**, **trzy** pozycje nawigacji (Home / My Profile / Chat), rozpychacz `flex:1` z ozdobnym SVG przy `opacity:0.5` i **linki prawne przyklejone do dołu** (11,5px / 600 / `rgba(255,255,255,0.4)`, w stosie pionowym). U nas linki prawne są **zwykłymi pozycjami nawigacji** w środku listy, nie ma rozpychacza, nie ma trasy „Home", a przełącznika języka nie ma bo aplikacja nie ma i18n. Pozycja 10 ostylowała to, co istnieje, i **nie przestawiała niczego** | `app/components/Sidebar.tsx` **Krok 6 — jako jedna skoordynowana zmiana** (przesunięte z pozycji 11 decyzją z 2026-08-09). Relokacja linków prawnych na dół raila, usunięcie stopki z `app/(app)/layout.tsx` i przeniesienie `ThemeToggle` to **jedna operacja**: zrobione osobno dałyby okno, w którym te same linki renderują się dwa razy na jednej stronie. Do Kroku 4 nie weszła, bo jej częścią jest oś wariantów `ThemeToggle` (§8.7 reguła 3). Zmiana **ponownie otworzy `Sidebar.tsx`**, czyli plik zamknięty na pozycji 10 — tak samo jak robi to 2a przed pozycją 18. Brak trasy „Home" i brak i18n to braki funkcjonalne, nie designowe: poza migracją |
| **Stary TODO w `Sidebar.tsx` twierdzi, że `/chat` nie jest w nawigacji — a jest.** Komentarz nad tablicą `navItems` proponuje dopisać tam `{ label: 'Chat', href: '/chat' }`, tymczasem link do czatu renderuje się kilkanaście linii niżej, w bloku `userId && …`, obok „My Profile". TODO opisuje więc stan sprzed dodania tego linku i czytany dosłownie prowadziłby do zduplikowania pozycji | `app/components/Sidebar.tsx` — komentarz nad `navItems` | Poza Krokiem 4 — porządek w komentarzach, nie design. Warto jednak zdjąć przy pozycji 11, która i tak przebudowuje skład tej listy, żeby nie przenosić nieaktualnej instrukcji przez refaktor |
| **Zagnieżdżony `<main>` — błąd HTML, nie stylu.** `app/(app)/layout.tsx` renderuje `<main>`, a **obie** strony prawne renderują wewnątrz drugi `<main className="text mx-auto max-w-4xl px-6 py-12">`. Dwa punkty orientacyjne `main` w jednym dokumencie są niepoprawne i mylą czytniki ekranu. Skutek uboczny jest też wizualny: **padding poziomy nakłada się** — `px-4` z layoutu plus `px-6` ze strony daje 40px z każdej strony przy szerokości wąskiej, zamiast 16px ze słownika. Przy okazji: `className="text"` nie jest żadnym utility Tailwinda — to martwy napis, prawdopodobnie resztka po `text-…` | `app/(app)/privacy-policy/page.tsx:6`, `app/(app)/terms-of-service/page.tsx:6` | Krok 4, **pozycje 15 i 16** — wewnętrzny `<main>` zamienia się w `<div>` albo znika, a padding zostaje wyłącznie w layoucie |
| **Strony prawne nie mają karty, którą eksport im daje.** Eksport kładzie treść na białej karcie: `max-width:720px`, `border-radius:24px`, `padding:48px 56px`, `box-shadow:0 8px 28px rgba(10,42,77,0.08)`, wyśrodkowanej na tle `#f3f6f4`. U nas treść leży bezpośrednio na tle strony, a szerokość to `max-w-4xl` (**896px** wobec 720px ze słownika). Nagłówek `text-4xl font-bold` (36px/700) wobec wiersza „nagłówek trasy" (30px/800), a `Last updated` jest zwykłym akapitem, gdy eksport daje mu 13,5px/600/`#8b968f`. Listy mają `pl-6` (24px) wobec 20px | `app/(app)/privacy-policy/page.tsx`, `app/(app)/terms-of-service/page.tsx` | Krok 4, **pozycje 15 i 16** — to jest właśnie ich zakres, wypisany tu z góry, żeby nie odkrywać go ponownie |
| **Odstęp między akapitami urósł z 16px do 26px — sprawcą jest pozycja 13.** Akapity w obu stronach mają `mt-4`, a `LegalSection` dostał na pozycji 13 `gap-2.5` (10px); margines i gap **sumują się**, bo gap nie zastępuje marginesów, tylko dokłada się do nich. Eksport chce 10px. **Przeoczone przy weryfikacji pozycji 13**, bo sprawdzano `marginBottom` akapitu (0px) zamiast `marginTop` — pomiar odpowiedział na źle zadane pytanie | `app/(app)/privacy-policy/page.tsx`, `app/(app)/terms-of-service/page.tsx` | Krok 4, **pozycje 15 i 16** — usunąć `mt-4` z akapitów i zostawić odstęp gapowi z `LegalSection`. Regresja jest widoczna do tego czasu i jest to świadomy koszt kolejności „komponent przed trasą" |
| **Adres e-mail jest najdłuższym nieprzełamywalnym ciągiem w aplikacji.** `Email: kinga.kwasniak5@gmail.com` to ~25 znaków bez spacji po dwukropku, czyli ~200px przy 16px pisma. Poniżej szerokości oceny, gdzie podwójny padding zostawia ~197px treści (12.5 wyżej), jest to **najpoważniejszy kandydat na źródło przewijania poziomego przy 277px**. Przy 360px problem nie występuje i Z.8 przechodzi. Eksport nie stosuje tu żadnego łamania — renderuje ten sam ciąg jako zwykły akapit — więc dodanie `break-words` byłoby wartością spoza słownika i spoza eksportu | `app/components/ContactBlock.tsx` | **Nie naprawiamy w Kroku 4.** Kontraktem z 12.0 jest 360px i przy tej szerokości nic nie wystaje. Gdyby kontrakt kiedyś zszedł niżej, właściwą naprawą jest usunięcie podwójnego paddingu (pozycje 15/16), a dopiero potem łamanie ciągu — najpierw odzyskać 24px na stronę, potem kaleczyć typografię |
| **`/privacy-policy` nie ma wiersza „Last updated", a `/terms-of-service` ma.** Eksport daje `lastUpdated` **obu** stronom prawnym (13,5px / 600 / `#8b968f`, tuż pod nagłówkiem). To brak treści, nie brak stylu — pozycja 15 nie dopisuje zdania, którego nikt nie zatwierdził, tak samo jak pozycja 6 nie wymyślała pigułki, której eksport nie zawiera | `app/(app)/privacy-policy/page.tsx` | Poza Krokiem 4 — decyzja redakcyjna. Styl jest już gotowy i czeka: wystarczy skopiować akapit z `terms-of-service` |
| **Kolor tekstu ciągłego `#3d4a44` też nie ma tokenu — spłaszczenie jest teraz podwójne.** Eksport rozróżnia na stronach prawnych **trzy** poziomy: nagłówki sekcji `#155e8f`, tekst ciągły `#3d4a44`, tekst pomocniczy `#8b968f`. Po pozycjach 13 i 15/16 mamy dwa: `on-elevated-surface` dla nagłówków i treści, `on-elevated-surface/60` dla „Last updated". Nagłówek sekcji i akapit pod nim mają więc **identyczny kolor** i rozróżnia je wyłącznie rozmiar i waga | `app/components/LegalSection.tsx`, obie strony prawne | Krok 7 — razem z `#155e8f` (12.4). Te dwie barwy warto rozstrzygać **jedną decyzją**: albo paleta dostaje parę „akcent + treść" na powierzchni jasnej, albo świadomie zostajemy przy jednym kolorze tekstu i różnicowaniu przez skalę |
| **Błąd sprzed migracji: wyszukiwarka proponuje „Add" przy osobie, która już jest znajomym — jeśli leży na innej stronie paginacji.** `FriendsPanel` buduje `excludedIds` z `friends`, a `friends` to **jedna strona** wyników (`FRIENDS_PAGE_SIZE`). Znajomi z pozostałych stron nie trafiają do zbioru wykluczeń, więc `UserSearch` pokazuje ich jako obcych. Wykryte 2026-08-09 przy weryfikacji 2a, na `FRIENDS_PAGE_SIZE` zmniejszonym do 2 dla testu — **nie jest to regresja 2a**: identyczny kod działał wcześniej na `/[userId]`, tyle że trzeba było mieć jedenastu znajomych, żeby to zobaczyć. Źródłem jest niedopasowanie kontraktu: `excludedIds` zakłada, że wywołujący **zna wszystkich** znajomych, a przy paginacji nie zna | `app/(app)/friends/FriendsPanel.tsx`, `app/components/UserSearch.tsx` | **Poza migracją wizualną — i poza możliwościami frontendu.** Trzy drogi, wszystkie poza Krokiem 4: (1) właściwa — `/users/search` zwraca flagę „już jest znajomym" albo sam wyklucza, czyli **zmiana API**; (2) frontend dociąga **wszystkie** identyfikatory znajomych, co jest nieograniczonym zapytaniem i psuje sens paginacji; (3) `AddFriendButton` obsługuje odpowiedź błędu z backendu jako „już znajomy", co leczy objaw. Rekomendacja: (1). Pozycje 24 i 25 w kolejce dotyczą **wyglądu** tych komponentów i tego nie naprawią |
| **Etykieta `Dev: STOMP WebSocket Test` łamie się na dwie linie.** Rail ma 250px, po odjęciu `px-5` i `px-3.5` pozycji zostaje **182px** na tekst 14px/700 — 25 znaków się nie mieści. Zmierzone na pozycji 18: ta pozycja ma **66px** wysokości wobec 46px wszystkich pozostałych. Nie jest to rozjazd ze słownikiem ani z eksportem — eksport nie ma tak długiej etykiety, bo nie ma takiej pozycji. To po prostu za długa treść | `app/components/Sidebar.tsx`, tablica `navItems` | **Nie naprawiamy.** Decyzja ④ usuwa `/stomp` przed oceną końcową, więc skracanie etykiety byłoby pracą nad czymś, co znika. Element pozostaje czytelny i klikalny — łamanie nie psuje niczego poza rytmem listy |
| **Hero w eksporcie nie jest kartą — jest wyśrodkowaną kolumną na gradiencie strony.** Eksport buduje go z czterech elementów w stosie `gap:14px`, `max-width:720px`, wyśrodkowanych na ciemnym tle landingu: kafelek logo 88px (`rounded-[26px]`, cień `0 12px 28px rgba(0,0,0,0.3)`), wordmark 26px/800, nagłówek 48px `max-width:600px` i podtytuł 18px `max-width:520px`. **Nie ma tam ani karty, ani obramowania, ani pigułek.** U nas jest to pudełko `max-w-md` (448px) z własnym gradientem `bg-gradient-start-page` i promieniem 16px. Pozycja 19 zmieniła w nim typografię i kolory, ale **struktury nie ruszała**: ten gradient jest dziś jedyną ciemną powierzchnią pod tym tekstem, a stronę na ciemno przestawia dopiero pozycja 21 | `app/components/Hero.tsx`, `app/(marketing)/page.tsx` | Krok 4, **pozycja 21** — tło, rozkarcenie, szerokości kolumn i ewentualny kafelek logo (ten ostatni razem z wierszem o logotypie wyżej). **Pozycja 19 jest więc świadomie częściowa** i jest czwartą jednostką otwieraną po raz drugi, po 1, 4 i 7 |
| **Gradient landingu z decyzji ② nie ma kompletu tokenów.** Eksport podaje `120deg, #0a3348 0%, #146b7a 32%, #1f8f7a 62%, #2f9bcf 100%`. Trzy stopnie mają odpowiedniki (`hub-ink-deep`, `hub-teal`, `hub-blue`), **czwarty — `#1f8f7a` — nie ma żadnego**; `hub-mint` to `#4ac9a0`, inna barwa. Zbudowanie tego gradientu wymaga więc albo nowego tokenu, albo wartości dowolnej wpisanej wprost, albo nowego `@utility` w `globals.css` — a każda z tych dróg to zmiana warstwy tokenów, której §8.7 reguła 2 zabrania w Kroku 4 | `app/globals.css`, `app/components/Hero.tsx` | Krok 4, **pozycja 21** — tam decyzja jest nieunikniona, bo tło landingu trzeba wtedy postawić. Do rozstrzygnięcia wtedy: czy `#1f8f7a` dostaje token (i wtedy jest to wyjątek od reguły 2, wymagający wiersza w 12.4), czy gradient powstaje z trzech istniejących stopni zamiast czterech |
| **`Hero` jest po pozycji 19 w połowie stały, w połowie motywowany.** Gradient karty stoi na `--theme-start-page-gradient-*`, które **nie mają nadpisania** w `.mocha`/`.latte`, więc tło zostaje mint→granat w każdym motywie. Tekst jest stałym `white`. Ale oba akcentowane słowa dostały `text-primary`, a to pod Mocha daje **mauve `rgb(209, 178, 248)`** — pomiar 2026-08-09. Wychodzi więc fioletowy akcent na miętowym gradiencie. **Wybór jest mimo to świadomy:** `Tag` renderuje się wewnątrz tej karty i wziął `bg-primary` na pozycji 6, więc `hub-lime` w `Hero` rozjechałby pigułki z wyróżnionymi słowami pod Mocha. Zgodność w dziwnym wyglądzie jest lepsza niż niezgodność | `app/components/Hero.tsx`, `app/components/Tag.tsx`, `app/globals.css` | **Krok 5** decyduje, czy gradient landingu w ogóle się motywuje — dziś nie, bo brak mu bloku `.mocha`/`.latte`, tak samo jak tokenom raila. **Pozycja 21** przebudowuje ten obszar i może problem unieważnić: jeśli tło stanie się ciemnym gradientem z eksportu, limonkowy akcent na nim jest wartością z eksportu, a nie kompromisem |
| **Pola wypełnione autouzupełnianiem przeglądarki ignorują ton `elevated`.** Pomiar przy pozycji 22 zwrócił dla pola hasła `background-color: rgb(232, 240, 254)` i `color: rgb(0, 0, 0)` zamiast `rgba(255,255,255,0.08)` i bieli. To **styl `:-webkit-autofill` Chrome'a**, nakładany poza normalną kaskadą i nieusuwalny zwykłym `background-color`. Na ciemnej karcie daje jasnoniebieskie pole z czarnym tekstem — odwrotność tego, co projekt zakłada. Obramowanie i waga pisma przechodzą poprawnie, bo autouzupełnianie ich nie dotyka. **Widoczne tylko u osób z zapisanymi danymi logowania**, co jest też powodem, dla którego łatwo to przeoczyć: na czystym profilu przeglądarki problem nie istnieje | `app/components/TextField.tsx`, ton `elevated`; `/login` i `/register` | **Poza Krokiem 4.** Obejście wymaga niestandardowych reguł `-webkit-autofill` (`box-shadow: inset 0 0 0 1000px <kolor>` plus `-webkit-text-fill-color`), czyli hacka pod jeden silnik, a nie wartości ze słownika. Eksport nie ma na ten stan odpowiedzi, bo jest statycznym HTML-em bez działającego formularza. **Krok 6 lub 7** — razem z pozostałymi stanami pól, których projekt nie narysował (§8.9 pkt 2) |
| **Gradient przycisku wiersza (`#eaf6fb → #e9f9f0`) wpisany jako wartość dowolna — dwie z trzech barw nie mają tokenów.** Przycisk wiersza w eksporcie (`isSearch`, linia 318) to `linear-gradient(135deg,#eaf6fb,#e9f9f0)` z tekstem `#146b7a`. Tekst dostał token, bo `hub-teal` **jest** tym `#146b7a`; oba stopnie gradientu odpowiednika nie mają i zostały wpisane wprost. Uzasadnienie to samo co przy karcie na pozycji 22: żadna skala Tailwinda nie wyraża dwustopniowego bladego gradientu, a reguła spod tabeli 12.0 każe takie wartości zostawiać dokładne. **Osobno hover:** eksport przechodzi na płaskie `#d2e9f6`, a z gradientu nie da się animować do koloru płaskiego — zastąpione `brightness-95`, które ciemnieje w tę samą stronę, ale nie w ten sam odcień | `app/(app)/friends/RemoveFriendButton.tsx` | Krok 7 — razem z `#155e8f` i `#d6e4ee` (12.4). **To trzecia rola koloru bez tokenu na powierzchni jasnej**, a wszystkie trzy są bladymi błękitami z rodziny `hub-teal`/`hub-blue`. Warto rozstrzygnąć je **jedną** decyzją o parze „tło drugorzędne + tekst drugorzędny", a nie trzema osobnymi |
| **Warstwy dekoracyjne z eksportu nie są odtwarzane na żadnym ekranie aplikacji.** Eksport kładzie na ekranach `isHome` i `isSearch` statyczne, absolutnie pozycjonowane koła z gradientem promienistym — 160px `rgba(47,155,207,0.10)` na dashboardzie, 110px `rgba(163,230,53,0.10)` przy wyszukiwaniu — a na landingu cztery pływające kształty, te ostatnie animowane. Landingowe odpadły decyzją ② („uproszczone tło"), ale **te dwa nie są animowane i decyzja ② ich nie obejmuje**; pozycja 26 pominęła je mimo to, bez osobnego ustalenia | `app/(app)/friends/page.tsx` | **Sprostowanie 2026-08-09: dotyczy jednego ekranu, nie dwóch.** Pierwotnie zapisano, że pytanie wróci przy pozycji 27 — nieprawda: ekran `isProfile` (linia 281) **nie ma żadnej warstwy dekoracyjnej**, wchodzi wprost w nagłówek. Koła są tylko na `isHome` i `isSearch`, a `isHome` to dashboard, którego aplikacja nie ma. **Jedynym miejscem, gdzie ta decyzja cokolwiek zmienia, jest `/friends`.** **Rozstrzygnięte 2026-08-09: nie odtwarzamy, świadomie i ostatecznie.** Powód podany przez osobę prowadzącą, mocniejszy niż argumenty projektowe: **w samym eksporcie tego koła nie widać, dopóki ktoś nie wskaże, gdzie patrzeć.** Dekoracja przy 10% krycia, której uważny obserwator nie zauważa, nie niesie intencji projektowej — niesie szum. Argument dodatkowy: pojedynczy taki akcent na jednym z dziewięciu ekranów czytałby się jak przypadek, a nie jak system; eksportowi uchodzi to dlatego, że ma jeszcze wersję na dashboardzie, którego aplikacja nie ma. **Gdyby kiedyś powstała trasa „Home", oba koła warto dodać razem albo wcale** |
| **`/chat` przewijał się w pionie, bo liczył wysokość chromu zamiast ją dostawać.** Trasa miała `h-[calc(100vh-4rem)]`, a `4rem` odpowiadało dokładnie `p-8` (2×32px) elementu `main` w `app/(app)/layout.tsx`. **Pozycja 11 zmieniła ten padding** na `pt-20 pb-6` / `lg:py-12` i liczba przestała być prawdziwa — kontener robił się o 40px za wysoki przy 360px i o 32px przy 1280px. Objaw był mylący: po wejściu w rozmowę strona „skakała" do pola wiadomości, bo przeglądarka przewijała do elementu, który dostał fokus. **Żaden pomiar Z.8 tego nie widział — Z.8 mierzy wyłącznie przewijanie poziome** | `app/(app)/chat/page.tsx`, `app/(app)/layout.tsx` | **Naprawione przy 2b, 2026-08-10.** `main` dostaje `flex flex-col`, trasa `flex-1 min-h-0`: wysokość **pochodzi** z layoutu, zamiast być o nim założeniem. Wszystkie strony `(app)` i tak otwierały się od `flex flex-1 flex-col`, co przy blokowym `main` nic nie robiło. **Wniosek szerszy: `calc()` na wymiarze rodzica to zależność, której nic nie pilnuje** — zmiana paddingu w innym pliku unieważniła ją bez jednego ostrzeżenia z `tsc`, ESLinta ani Z.8 |
| **Modal zmiany awatara był szerszy niż wąska szerokość oceny.** `w-96` to 384px, a nakładka `fixed inset-0` nie miała paddingu — przy 360px okno dialogowe wystawało poza ekran po obu stronach. **Nie jest to znalezisko projektowe, tylko błąd, którego żaden wcześniejszy pomiar nie mógł wyłapać:** kryterium Z.8 sprawdza `scrollWidth` strony, a element `fixed` nie powiększa przewijania dokumentu — po prostu zostaje przycięty. Naprawione na pozycji 28 (`w-full max-w-[440px]` plus `p-4` na nakładce) | `app/(app)/[userId]/EditAvatarButton.tsx` | **Naprawione w Kroku 4, pozycja 28.** Wniosek metodyczny do 12.7: **elementy `fixed` wymykają się obu naszym testom szerokości** — Z.8 ich nie widzi, a tabele `getComputedStyle` mierzyliśmy dotąd na tym, co widać po wejściu na trasę, a nie po otwarciu warstwy. To druga taka luka po nieprzewijalnej szufladzie z pozycji 18, i obie dotyczą elementów `fixed` |
| **Karta informacyjna profilu ma dwa wiersze zamiast trzech — brakuje `Email`, bo brakuje danych.** Eksport pokazuje `Email / Username / Friends`. `Username` bierzemy z `displayName`, `Friends` z `totalElements` (pozycja 27 wraca po tę liczbę do `/friends` z `size=1`), ale **`/users/{userId}/details` zwraca wyłącznie `{ displayName, avatarId }`** — potwierdzone błędem `tsc` na wygenerowanych typach, nie domysłem. Wiersz został **usunięty, a nie wypełniony myślnikiem**: placeholder sugerowałby, że pole istnieje i jest puste, podczas gdy backend go nie oddaje | `app/(app)/[userId]/page.tsx`; kontrakt `/users/{userId}/details` | **Poza Krokiem 4 — to brak danych, nie brak stylu.** Wiersz jest gotowy i czeka: wystarczy dodać `<InfoRow label="Email" …>`, gdy endpoint zacznie zwracać adres. Ta sama sytuacja co „Last updated" na `/privacy-policy` — styl istnieje, treści nie ma. **Uwaga uboczna: na cudzym profilu karta ma jeden wiersz**, bo licznik znajomych dotyczy wyłącznie zalogowanego użytkownika (`GET /friends` ignoruje ścieżkę) |
| **Przycisk „Change avatar" z eksportu nie jest odtworzony — u nas to plakietka na awatarze, nie przycisk obok nazwy.** Eksport stawia w hero profilu awatar, a **obok niego** kolumnę: nazwa 26px/800 i pod nią przycisk `Change avatar` (obrys `rgba(255,255,255,0.6)`, tło `rgba(255,255,255,0.15)`, 12px promienia, 13,5px/700). U nas wyzwalacz „Edit" jest małą plakietką pozycjonowaną absolutnie w rogu awatara. Pozycja 27 zbudowała układ hero, ale tej zamiany nie zrobiła: przycisk i modal siedzą w jednym komponencie (`EditAvatarButton`), więc przeniesienie wyzwalacza obok nazwy to rozdzielenie komponentu, a nie zmiana klasy | `app/(app)/[userId]/EditAvatarButton.tsx` | Krok 4, **pozycja 28** — tam i tak rozstrzyga się potraktowanie wszystkich przycisków profilu. **Ta sama uwaga dotyczy wyzwalacza „Edit" przy nazwie**, którego eksport nie ma wcale: tam nazwę zmienia się przez ten sam przycisk `Change avatar`, bo w makiecie nie ma osobnej edycji nazwy |
| **Gradient wylogowania (`#e35b52 → #c0453f`) i gradient hero profilu (`#146b7a → #2f9bcf → #4ac9a0`) wpisane jako wartości dowolne.** Żaden z pięciu stopni nie ma tokenu. Dla wylogowania jest to szczególnie widoczne, bo **token `--theme-danger` istnieje** (`#e5484d`) i jest bliski pierwszemu stopniowi, ale rola wymaga gradientu, którego pojedyncza zmienna nie wyrazi — dokładnie ta sama sytuacja co przy CTA na pozycji 2, gdzie `--theme-primary` nie wyraził gradientu mint→limonka i rozwiązaniem był `@utility bg-hub-cta`. Rozbieżność `danger` vs gradient wylogowania jest zresztą zapisana w 12.5 od 11.5, jako odziedziczona | `app/(app)/[userId]/page.tsx`; `app/globals.css` | Krok 7 lub 8 — **jeśli te gradienty mają dostać nazwy, wzorcem jest `bg-hub-cta`**: `@utility` zbudowane z tokenów, nie surowe heksy w komponencie. Pozycja 27 tego nie robi, bo utworzenie dwóch nowych `@utility` plus pięciu tokenów byłoby największą zmianą w warstwie tokenów w całym Kroku 4, a §8.7 reguła 2 dopuściła dokładnie jeden wyjątek (12.4) |
| ~~**`/chat` nie czyta parametru `?friend=`**~~ — **WPIS BŁĘDNY, wycofany 2026-08-10.** Twierdził, że `/chat` ignoruje `?friend=`, bo jest „statyczną makietą na danych testowych". **Nieprawda i to podwójnie:** trasa jest komponentem serwerowym pobierającym `/friends` i `/chats`, czyta `?friend=` **oraz** `?chat=` (linie 73–99) i rozwiązuje z nich aktywną rozmowę, a `FriendRow` od dawna jest `<Link>` na `/chat?friend=` z komentarzem tłumaczącym, że wybór jest stanem adresu. `OpenChatLink` działał więc poprawnie od chwili powstania na pozycji 25 | `app/(app)/chat/page.tsx`, `app/(app)/chat/FriendRow.tsx` | **Nic do zrobienia — wpis powstał z nieaktualnych notatek o `/chat`, nie z odczytu pliku.** Zostaje przekreślony, nie usunięty, bo pokazuje realny koszt §8.11: trasa była zamrożona, więc przez cały krok opisywaliśmy ją z pamięci zamiast z kodu, i **opis rozjechał się z rzeczywistością bez żadnego sygnału**. Ta sama przyczyna stoi za nieaktualnym TODO w `Sidebar.tsx` (wiersz wyżej). Wniosek do Kroku 8: **zamrożenie pliku nie zamraża wiedzy o nim** |
| **Surowe `text-red-500` zostaje w dwóch plikach `/chat`, gdy pięć innych przeszło na `text-danger`.** Krok 4 wymienił surowy kolor na token w `UserSearch`, `AddFriendButton`, `RemoveFriendButton`, `EditAvatarButton` i `EditDisplayNameButton`, ale `Composer.tsx:16` i `MessageBubble.tsx:81` nadal trzymają `text-red-500` / `hover:text-red-500`. Oba pliki są w diffie Kroku 4 — dotknął ich przelot Prettiera — więc **z zewnątrz wygląda to na przeoczenie, a nie na granicę**. Zgłoszone w przeglądzie PR 2026-08-10 dokładnie z tym pytaniem | `app/(app)/chat/Composer.tsx`, `app/(app)/chat/MessageBubble.tsx` | **Nie naprawiamy — to granica §8.11, nie niedokończona praca.** `/chat` jest implementacją odniesienia i jest zamrożone; przeformatowanie pliku nie jest zgodą na zmianę jego kolorów. Sytuacja jest identyczna z wierszem o `bg-gray-400` w `PresenceAvatar` wyżej i idzie tam, gdzie tamten: **do Kroku 8**, razem z wycofaniem palety surowej. Wpis istnieje po to, żeby następny czytelnik nie zgłosił tego drugi raz — Prettier zostawia w diffie ślad nieodróżnialny od zmiany merytorycznej |
| **`opacity` mnoży się w dół drzewa, alfa koloru nie — więc technika `Footer` nie uogólnia się na tekst zagnieżdżony.** Pozycja 22 przestawiła `Card` na `text-white`, a jego konsumenci nazywali tę samą biel drugi raz z obniżoną alfą (`text-white/60`, `/50`). Przegląd PR 2026-08-10 słusznie wskazał, że `Footer` rozwiązał ten problem lepiej — kolor **dziedziczony i modulowany**, nie nazwany — i że kolor pierwszego planu `Card` był zadeklarowany w pięciu miejscach. Zamiana przeszła jednak **tylko na pięciu z dziesięciu miejsc**: `opacity` działa na całe poddrzewo, więc tam, gdzie akapit obejmuje dziecko z własnym kolorem, mnoży się z nim. Dwa wzorce, w których jest to błąd: `<span text-white/60>` obejmujący linki `text-white/80` w zgodzie na regulamin (0,6 × 0,8 = **0,48**, czyli link ciemniejszy od zdania, w którym stoi) oraz akapity „Don't have an account?" obejmujące `AccentLink` z własnym `text-primary` (limonka przygaszona do połowy) | `app/(auth)/login/page.tsx`, `app/(auth)/register/page.tsx` | **Rozstrzygnięte 2026-08-10, świadomie niejednolicie.** Węzły liściowe (`Welcome back!`, `Nice to meet you!`, `Forgot password?`, podtytuł `SessionCard`, podtytuł landingu) dziedziczą przez `opacity-*`. Trzy miejsca z kolorowanym dzieckiem **zostają na alfie nazwanej** i mają komentarz mówiący dlaczego. Alternatywą było owinięcie każdego fragmentu tekstu własnym `<span>`, żeby modulować wyłącznie liście — to jednak dokładanie znaczników pod regułę stylistyczną, a nie pod czytelnika. **Wniosek ogólniejszy: „dziedzicz i moduluj" jest właściwe dla liścia, a nie dla kontenera** — `Footer` był liściem, dlatego działało tam bez zastrzeżeń |

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
| **Waga pisma 800 wobec 700 na jednej stronie.** `BrandLink` renderuje się z wagą **800**, `Button` z wagą **700** — a eksport daje **800 obu** (`primaryBtnStyle` ma `fontWeight: 800`, wordmark tak samo). Na `/login` oba elementy są widoczne jednocześnie | **Między dwiema jednostkami:** pozycja 2 (`Button`) i pozycja 8 (`BrandLink`) | Przegląd wsteczny przed zamknięciem pozycji 8, **2026-08-09**, na `/login` | **Nierozstrzygnięty w Kroku 4 — i to jest rozstrzygnięcie.** Waga `Button` siedzi w `BASE_CLASSES`, wspólnym z wariantem `send`, którego używa zamrożone `/chat` (§8.11); różnica jest już zapisana w 12.5. `BrandLink` **zostaje na 800** — cofnięcie go do 700 dla zgodności z zablokowanym sąsiadem byłoby propagowaniem blokady, nie spójnością, i zostawiłoby dwa komponenty niezgodne z eksportem zamiast jednego. Odblokowuje się razem z `Button` w Kroku 6 lub 8 | **Nie.** 800 i 700 są **oba** wierszami słownika 12.0, więc nie ma luki do zamknięcia. Wartość spoza słownika nie padła |

| **Obramowanie `ThemeToggle` przestało do czegokolwiek pasować.** Do pozycji 9 stopka miała `border-primary/20`, więc limonkowa krawędź toggla współgrała z limonkową krawędzią stopki. Pozycja 9 przeniosła stopkę na `elevated-border` (neutralny `#eef2ef`, w Mocha `#45475a`) — zgodnie ze słownikiem — i limonka toggla została **jedynym akcentem w całej stopce**, otoczona samymi neutralnymi | **Między dwiema jednostkami:** pozycja 7 (`ThemeToggle`) i pozycja 9 (`Footer`) | Przegląd wsteczny przed zamknięciem pozycji 9, **2026-08-09**, na `/login` | **Bez zmian w kodzie — rozjazd jest dowodem, nie problemem do naprawienia teraz.** Obramowanie toggla jest już zapisane w 12.5 jako tymczasowe, do zdjęcia przy relokacji (ustalenie z 2026-08-09). Ten wiersz **dostarcza argumentu za tamtą decyzją**: dopóki stopka też była limonkowa, obramowanie dawało się czytać jako element systemu; po doprowadzeniu stopki do słownika widać, że nim nie było. Zdejmowanie go teraz zostawiłoby jednak przycisk niewidoczny na tle `surface` — kolejność pozostaje: najpierw relokacja, potem krawędź | **Nie** — obie wartości są wierszami słownika. Rozjazd polega na sąsiedztwie ról, nie na wartości spoza tabeli |

**Czego dowodzi pierwszy wiersz tej tabeli.** Przez trzy przeglądy 12.6 była
pusta zasadnie, a wpis pojawił się nie tam, gdzie go wyglądano. Nie powstał
z niedbałości ani z braku słownika — **powstał z blokady**. Jednostka zamknięta
całkowicie poprawnie, w granicach tego, co §8.11 pozwalał ruszyć, rozjechała się
z jednostką zamkniętą sześć pozycji później. To jest mechanizm §8.2 w czystej
postaci: dwie decyzje w dwóch momentach, obie słuszne osobno.

Praktyczny wniosek na resztę kroku: **każda pozycja zapisana w 12.5 jako
„zablokowana przez §8.11" jest przyszłym wierszem 12.6**, który uaktywni się
w chwili, gdy jakaś późniejsza jednostka dojdzie do swojej wartości docelowej
bez blokady. Warto je czytać jako listę oczekujących rozjazdów, nie jako
zamknięte notatki.

**Pusta po Etapie A — i to był wynik, nie brak wyniku.** Odbyły się dwa
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
wyliczone, a tu wystarczy porównanie ze screenshotem.

**Sprostowanie 2026-08-09: założenie „żadna z tych jednostek nie zmienia klas"
nie przetrwało zetknięcia z 1b.** Dwie konstrukcje wymagają klas — i to nowych
elementów, nie tylko nowych utility na istniejących. Porównanie ze screenshotem
pozostaje właściwym dowodem, ale **tylko dla szerokiej szerokości**, gdzie teza
brzmi „nic się nie zmieniło". Dla wąskiej nie ma z czym porównywać: tam wcześniej
nie było żadnej działającej nawigacji, więc dowodem jest wyłącznie kolumna
„Sprawdzone działaniem".

| Jednostka | Co się zmieniło | Gdzie wygląd bez zmian | Sprawdzone działaniem | `'use client'` +? |
|---|---|---|---|---|
| **1b** — nawigacja mobilna | **Dwie konstrukcje, zapis mobile-first** (decyzja z 12.4, 2026-08-09). Bez prefiksu `<aside>` jest `fixed inset-y-0 left-0 z-40` i stoi wysunięty poza kadr (`-translate-x-full`), a otwiera się przez `translate-x-0`; `lg:static lg:translate-x-0` przywraca poprzednią kolumnę. Dochodzą trzy elementy, wszystkie `lg:hidden`: **przycisk hamburgera** (`fixed left-4 top-4 z-50`, chowany gdy szuflada otwarta, żeby nie leżeć na jej nagłówku), **przesłona** `bg-black/50 z-30` zrobiona jako `<button>` z etykietą, nie `<div>` — realny cel kliknięcia ma być fokusowalny i opisany — oraz zamykanie klawiszem **Escape**, nasłuchiwane wyłącznie gdy otwarte. Poza komponentem jedna zmiana: `main` w `app/(app)/layout.tsx` dostaje `pt-20` przy szerokości wąskiej. To **rezerwacja miejsca pod przycisk, nie decyzja o odstępie**, dlatego znika przy `lg:` | **na szerokim ekranie — ani o piksel.** Trzy nowe elementy są `lg:hidden`, a `lg:static lg:translate-x-0` cofa oba narzucone zachowania. `inset-y-0 left-0 z-40` zostają w łańcuchu klas, ale na elemencie `static` nie robią nic — offsety i `z-index` dotyczą wyłącznie elementów pozycjonowanych. Jedyne realne ryzyko to `pt-20` w `main`, dlatego `lg:pt-12` i `lg:pb-12` zapisane są **rozdzielnie**, zamiast polegać na kolejności `py-*` względem `pt-*` w wygenerowanym CSS **Sprawdzone 2026-08-09 — sześć na sześć.** Hamburger widoczny, rail schowany; kliknięcie wysuwa szufladę i chowa przycisk; wszystkie pięć pozycji osiągalnych; **kliknięcie pozycji przechodzi do trasy i zamyka szufladę**; Escape zamyka; kliknięcie przesłony zamyka. Punkt czwarty był jedynym niezależnym od CSS — zależy od efektu na `pathname`, nie od klasy. **Pomiar stanu spoczynkowego przy 288px:** `left: -250px`, `right: 0px`, czyli prawa krawędź szuflady leży dokładnie na lewej krawędzi okna — schowana, nie tylko niewidoczna. **Z.8 spełnione po raz pierwszy na trasach `(app)`:** `scrollWidth - innerWidth = 0` przy obu szerokościach. **Uzupełnienie 2026-08-09 — „sześć na sześć" było niepełne.** Pozycja 18 pokazała, że kryterium „każda pozycja osiągalna przy szerokości wąskiej" przechodziło wyłącznie dlatego, że sprawdzano je na **wysokim** oknie. Szuflada jest `fixed inset-y-0`, ma więc dokładnie wysokość okna, a jej treść ma ~430px; przy oknie ~270px ostatnie linki znalazły się pod krawędzią **bez możliwości przewinięcia** — widoczny pasek przewijania należał do strony pod przesłoną. Dołożone `overflow-y-auto overscroll-contain`. **Wniosek metodyczny: element `inset-y-0` trzeba sprawdzać na obu osiach okna, nie tylko na szerokości** — słownik 12.0 zna dwie szerokości oceny i ani jednej wysokości | **+0 — dopuszczenie z 12.2 okazało się niepotrzebne.** `Sidebar.tsx` jest komponentem klienckim od zawsze, bo używa `usePathname()`, więc `useState` i `useEffect` nie dokładają wpisu do listy z sekcji 6. Limit „najwyżej +2, oba nazwane" zostaje z zapasem jednego |
| **2b** — `/chat` przy szerokości wąskiej | **Jeden panel naraz poniżej `lg:`, oba powyżej.** Trasa czytała `?friend=` **jeszcze przed** tą jednostką, więc wybór rozmowy był już stanem adresu, a nie komponentu — całą zmianę dało się wyrazić **trzema utility widoczności**: szyna `hidden lg:flex` gdy rozmowa jest wybrana, zastępnik „Select a chat" `hidden lg:flex` zawsze (poniżej `lg:` listą **jest** ekran), szyna `w-full lg:w-[290px] lg:border-e`. Doszedł jeden element: strzałka powrotu w nagłówku rozmowy, `lg:hidden`, prowadząca na `/chat` bez parametru. Naprawiona przy okazji **regresja z pozycji 11**: `h-[calc(100vh-4rem)]` kodowało `p-8` elementu `main` z innego pliku i przestało być prawdą, gdy padding się zmienił (12.5) | **powyżej `lg:` — ani o piksel.** Szyna 290px z krawędzią, oba panele, zastępnik widoczny. Wszystkie trzy klasy są `lg:`-warunkowe albo `lg:hidden`; żaden element nie istnieje w dwóch kopiach, więc Z.7 nienaruszone | **Sprawdzone 2026-08-10.** Przy 360px: lista pełnej szerokości, wejście w rozmowę, powrót strzałką, powrót przyciskiem przeglądarki, wejście z `/friends` prosto w rozmowę, wyszukiwarka w szynie działa. **`hOverflow` i `vOverflow` zerowe przy 360 i 1280** — Z.8 spełnione na siódmej trasie, tej, dla której powstała ta jednostka | **+0.** Żadnego nowego komponentu klienckiego: `page.tsx` pozostaje serwerowy, `FriendRail` i `Conversation` były klienckie wcześniej. **Wszystkie trzy jednostki strukturalne kroku zamknęły się bez ani jednego nowego wpisu** do listy z sekcji 6 |
| **2a** — wydzielenie trasy znajomych | **Nowa trasa `/friends`** (decyzja z 12.4). Przeniesione `git mv`, bez zmiany treści: `FriendsPanel.tsx`, `AddFriendButton.tsx`, `RemoveFriendButton.tsx`. **`actions.ts` nie został przeniesiony, tylko rozdzielony** — trzymał akcje profilu (nazwa, awatar) razem z akcjami znajomych; profilowe zostały, pozostałe trzy odeszły. Nowy `friends/page.tsx` przejął `loadFriendsPage`, `FRIENDS_PAGE_SIZE`, `parsePageParam` i przekierowanie z pustej strony. Pager celuje teraz w `/friends?page=N`. Rail dostał czwarty link. **`UserSearch` i `UserList` NIE zostały ruszone** — leżą w `app/components/` i były już niezależne od trasy, co `UserSearch` deklaruje we własnym komentarzu; robocze założenie z 9.1 mówiło inaczej i **okazało się nietrafne**. Nowa lokalizacja ma tę samą głębokość co stara, więc żaden import `../../…` nie wymagał korekty | **w przeniesionym panelu — ani o piksel.** Żadna klasa nie została zmieniona w trzech przeniesionych plikach; jedyne zmiany treści to dwa adresy w pagerze i komentarz przy propie `currentUserId`. Na szerokim ekranie rail różni się wyłącznie **jednym dodanym linkiem**, co jest zamierzone i jest przedmiotem pozycji 18 | **Sprawdzone 2026-08-09, wszystkie punkty.** `/friends` ładuje się i wyszukuje; dodanie i usunięcie znajomego działa; pager prowadzi na `/friends?page=1`, nie na `/<userId>?page=1`; profil zachował baner, edycję nazwy i awatara oraz wylogowanie, a stracił wyłącznie panel — to jest dowód, że `actions.ts` rozdzielono w dobrym miejscu; cudzy profil renderuje się normalnie; rail podświetla „Friends" na trasie aktywnej. **Znaleziono przy okazji błąd sprzed migracji** (wykluczenia w wyszukiwarce obejmują tylko bieżącą stronę znajomych) — zapisany w 12.5, nieusuwany, bo właściwa naprawa jest po stronie API | **+0 — dopuszczenie z 12.2 znowu niewykorzystane.** `friends/page.tsx` jest komponentem serwerowym, `friends/actions.ts` to `'use server'`, a trzy przeniesione pliki były klienckie już wcześniej. **Obie jednostki strukturalne zamknęły się bez ani jednego nowego wpisu** do listy z sekcji 6; budżet „najwyżej +2" pozostał nietknięty |

## 13. Krok 5 — przywrócenie obsługi motywów (plan, prefill 2026-08-10)

### 13.0 Cel, warunki wejścia i to, czego plan z §Krok 5 nie przewidywał

**Cel:** żadna powierzchnia aplikacji nie ignoruje `ThemeToggle` przez
przeoczenie. Powierzchnie, które motywu **nie** przyjmują, mają być wyliczone
i uzasadnione, a nie zostać takimi dlatego, że nikt nie dopisał bloku.

**Warunki wejścia — spełnione:** Krok 4 zamknięty, `brand-*` bez użyć
w komponentach, dokładnie jeden breakpoint, `globals.css` z jednym świadomym
nowym tokenem.

**Czego §Krok 5 nie mógł przewidzieć.** Plan opisuje ten krok jako jeden blok
nadpisań dla czatu — „wystarczy dodać tutaj jeden blok i nie wykonywać żadnych
zmian w JSX". **To już nieprawda.** Krok 4 dołożył trzy rzeczy, których wtedy
nie było:

1. **Rail** dostał na pozycji 10 potwierdzone wartości trzech tokenów on-shell,
   ale wciąż bez bloku `.mocha`/`.latte` — komentarz w `Sidebar.tsx` wprost
   odsyła to do Kroku 5.
2. **Gradient landingu i auth** (pozycja 21) stoi na czterech tokenach, również
   bez nadpisań. 12.5 zapisuje, że czy on się motywuje, rozstrzyga Krok 5.
3. **Cztery gradienty wpisane jako wartości dowolne** (karta auth, hero profilu,
   przycisk wylogowania, przycisk wiersza listy). **Tych nie da się zmotywować
   bez uprzedniej tokenizacji** — nie mają nazw, więc nie ma czego nadpisać.

Krok 5 jest więc szerszy niż „jeden blok" i zaczyna się od decyzji, nie od kodu.

### 13.1 Inwentarz — co dziś przyjmuje motyw, a co nie

Stan `app/globals.css` na 2026-08-10. Nadpisania w regule `.mocha, .latte`
istnieją **wyłącznie** dla siedmiu tokenów semantycznych plus `success`/`danger`
per odmiana. Wszystko poniżej jest zdefiniowane tylko na `:root`.

| Grupa | Tokeny | Ile | Dziś | Gdzie widać |
|---|---|---|---|---|
| **A — semantyka czatu** | `hub-surface`, `hub-panel`, `hub-panel-sunken`, `hub-field`, `hub-border`, `hub-on-surface`, `hub-muted`, `hub-time`, `hub-row-active`, `hub-online`, `hub-status` | 11 | stałe | `/chat` w całości; `hub-muted` także w etykietach karty profilu |
| **B — gradient raila** | `hub-shell-start`, `hub-shell-mid`, `hub-shell-end` | 3 | stałe | Sidebar na wszystkich trasach `(app)` |
| **C — role on-shell** | `hub-on-shell-muted`, `hub-shell-hover`, `hub-on-shell` | 3 | stałe | nawigacja raila |
| **D — gradient landingu** | `start-page-gradient-start`, `-mid`, `-mid-2`, `-end` | 4 | stałe | `BareLayout` — `/`, `/login`, `/register` |
| **E — stałe barwy marki** | `hub-ink`, `hub-ink-deep`, `hub-teal`, `hub-blue`, `hub-mint`, `hub-lime` | 6 | stałe **z założenia** | bąbelki czatu, CTA, `Remove` |
| **F — wartości dowolne, nie tokeny** | gradient karty auth, hero profilu, wylogowania, przycisku wiersza; cienie `rgba` | 4 + cienie | **nietokenizowalne bez zmiany kodu** | `/login`, `/register`, `/[userId]`, `/friends` |

**Grupa E jest jedyną, która ma dziś uzasadnienie w kodzie** — komentarz
w `globals.css` mówi wprost, że to *są* barwy marki i nie biorą pośrednictwa
motywu. Grupy A–D są stałe **przez zaległość, nie przez decyzję**.

### 13.2 Decyzja D1 — granica między „marką" a „motywem"

**To jedyna decyzja tego kroku, z której wynika cała reszta**, i musi zapaść
przed pierwszą linią kodu. Pytanie brzmi: **które powierzchnie aplikacji są
znakiem firmowym, a które są ubraniem?**

Trzy spójne odpowiedzi, każda do obrony:

- **D1-a „wszystko się motywuje poza barwami marki".** Grupy A–D dostają
  nadpisania, E zostaje. Najbliższe intencji `ThemeToggle`; najdroższe, bo
  wymaga tokenizacji grupy F albo świadomej zgody, że cztery gradienty
  pozostaną jasne w Mocha.
- **D1-b „chrom jest marką, treść się motywuje".** Rail (B, C) i gradient
  landingu (D) zostają stałe jako sygnatura wizualna; czat (A) się motywuje.
  Najtańsza; ryzyko: ciemny rail obok jasnego Latte może wyglądać na
  niedokończony — dokładnie ten zarzut, który §Krok 5 stawia dziś czatowi.
- **D1-c „motywuje się tylko to, co i tak było semantyczne".** Grupa A tak,
  B–D nie, i zapisujemy to jako trwałe. Uczciwe minimum; wymaga jawnego wpisu,
  że rail i landing **nigdy** nie odpowiedzą na przełącznik.

**Rekomendacja: D1-b**, z jednym zastrzeżeniem do sprawdzenia pomiarem przed
podjęciem — czy ciemny rail obok Latte faktycznie razi. To pytanie ma
odpowiedź empiryczną, nie teoretyczną: wystarczy tymczasowo nadpisać trzy
tokeny grupy B i zobaczyć obie wersje obok siebie.

### 13.3 Kolejka jednostek Kroku 5

| # | Jednostka | Rodzaj | Zależy od | Status |
|---|---|---|---|---|
| 5.0 | **Macierz odniesienia** — pomiar wszystkich tras × 3 motywy **przed** zmianami | pomiar | — | w kolejce |
| 5.1 | **Decyzja D1** na podstawie 5.0 i próbnego nadpisania grupy B | decyzja | 5.0 | w kolejce |
| 5.2 | **Grupa A — semantyka czatu.** Blok z TODO: `panel → ctp-surface0`, `surface → ctp-base`, `border → ctp-surface1`, `muted/time → ctp-subtext0/overlay1`; pozostałe sześć tokenów **nie ma podanego odpowiednika i wymaga wyboru** | tokeny | 5.1 | w kolejce |
| 5.3 | **Grupy B + C — rail** | tokeny | 5.1 | w kolejce |
| 5.4 | **Grupa D — gradient landingu i auth** | tokeny | 5.1 | w kolejce |
| 5.5 | **Grupa F — cztery gradienty dowolne**: tokenizować (`@utility` wzorem `bg-hub-cta`) czy zapisać jako trwale stałe | decyzja + kod | 5.1 | w kolejce |
| 5.6 | **Kontrast per odmiana** — pomiar, bez naprawy; naprawa należy do Kroku 7 | pomiar | 5.2–5.5 | w kolejce |
| 5.7 | **Macierz końcowa** — te same sondy co 5.0, porównanie | pomiar | wszystkie | w kolejce |

**Uwaga do 5.2.** TODO podaje mapowanie dla **pięciu** ról z jedenastu.
Bez odpowiednika zostają `hub-panel-sunken`, `hub-row-active`, `hub-online`,
`hub-status` oraz `hub-surface`. Cztery pierwsze niosą **znaczenie**
(zaznaczenie, obecność, status), więc ich odpowiedniki w Catppuccin trzeba
wybrać, a nie odczytać — i zapisać w 13.4 jak każdą inną decyzję.

### 13.4 Decyzje Kroku 5

| Co | Gdzie się pojawiło | Pytanie | Status |
|---|---|---|---|
| **D1 — granica marka / motyw** | 13.2 | Które grupy tokenów przyjmują nadpisania? | otwarta — potrzebna przy 5.1, blokuje 5.2–5.5 |
| Sześć ról czatu bez mapowania | 5.2 | `hub-surface`, `hub-panel-sunken`, `hub-row-active`, `hub-online`, `hub-status` — jakie odpowiedniki Catppuccin? | otwarta — potrzebna przy 5.2 |
| Cztery gradienty dowolne | 5.5 | Tokenizować czy uznać za trwale stałe? | otwarta — potrzebna przy 5.5 |

### 13.5 Znalezione, ale NIE naprawione (Krok 5)

*(do wypełnienia w trakcie)*

### 13.6 Metoda weryfikacji — macierz motywów

Krok 4 sprawdzał **trasę po jednostce**. Krok 5 zmienia jedną rzecz naraz
w warstwie tokenów, a skutek widać wszędzie — więc jednostką pomiaru jest
**cała aplikacja w trzech motywach**, nie komponent.

Dla każdej z siedmiu tras zbieramy ten sam zestaw sond (tło strony, tło panelu,
tekst główny, tekst wyciszony, krawędź, akcent) w motywie domyślnym, Mocha
i Latte, i sprawdzamy dwie rzeczy naraz:

- role **motywowane** muszą się różnić między odmianami — jeśli nie, brakuje
  nadpisania;
- role **markowe** muszą być identyczne — jeśli się różnią, nadpisanie sięgnęło
  za daleko.

**Trzy wnioski z Kroku 4 wchodzą tu jako wymagania, nie sugestie:**

1. **Mierzymy stany, nie tylko trasy.** Trzy z trzech defektów znalezionych
   późno w Kroku 4 (nieprzewijalna szuflada, za szeroki modal, zła wysokość
   czatu) siedziały w stanach, w które skryptowy przebieg nie wchodził.
   Macierz musi obejmować: **otwartą szufladę nawigacji, otwarty modal awatara,
   rozwiniętą wyszukiwarkę w railu, tryb edycji nazwy** i `/chat` w obu
   wariantach `?friend=`.
2. **Mierzymy geometrię, nie własności.** `space-y-*` w Tailwindzie 4 przeniosło
   się z `margin-top` na `margin-block-end` i dwa pomiary z rzędu odpowiedziały
   na źle zadane pytanie. Odstęp mierzymy przez `getBoundingClientRect()`.
3. **Mierzymy przy obu wysokościach okna**, nie tylko szerokościach — słownik
   12.0 zna dwie szerokości oceny i ani jednej wysokości.

### 13.7 Definicja ukończenia Kroku 5

- [ ] **T.1** D1 podjęta i zapisana w 13.4 **przed** pierwszą zmianą w `globals.css`
- [ ] **T.2** Każdy token z grup A–D ma albo nadpisanie `.mocha`/`.latte`, albo
      wiersz w 13.4 mówiący, że zostaje stały świadomie. **Zero tokenów bez
      jednego z dwóch**
- [ ] **T.3** Żadna zmiana w JSX poza tymi, które wymusza 5.5 (tokenizacja
      gradientów). Warstwa pośrednia z Kroku 2 istnieje właśnie po to
- [ ] **T.4** Macierz 5.7 pokazuje różnicę tam, gdzie D1 ją przewiduje, i brak
      różnicy tam, gdzie D1 jej zabrania
- [ ] **T.5** Żadna para tekst/tło w żadnej z trzech odmian nie jest
      nieczytelna — pomiar, nie ocena wzrokowa. Wartości poniżej AA idą do 13.5
      i do Kroku 7, ale **nieczytelność** jest blokerem tego kroku
- [ ] **T.6** Stany z 13.6 pkt 1 sprawdzone w każdej odmianie
- [ ] **T.7** Sekcja 13 wypełniona

---

## 14. Krok 6 — scalenie zduplikowanych wariantów (plan, prefill 2026-08-10)

### 14.0 Cel i warunek, bez którego nie wolno zaczynać

**Cel:** jedna paleta znaczy jeden wariant. Trzy komponenty mają dziś wariant
równoległy istniejący **wyłącznie** dlatego, że istniała równoległa paleta.

**Charakter kroku: refaktoryzacja. Nic nie może zmienić się wizualnie** — i to
jest jednocześnie metoda weryfikacji (14.4).

**Warunek wejścia, twardy:** Krok 5 zamknięty. `TextField` tone `chat` wolno
scalić z `surface` **dopiero wtedy, gdy oba opisują to samo** — a dziś nie
opisują, bo `surface` się motywuje, a `chat` nie. Scalenie ich przed Krokiem 5
nie byłoby refaktoryzacją, tylko cichą zmianą wyglądu `/chat`.

### 14.1 Kolejka jednostek Kroku 6

Trzy pozycje pochodzą z §Krok 6, **dziewięć dołożył Krok 4** — każda ma już
wiersz w 12.4 albo 12.5 z uzasadnieniem, dlaczego wtedy jej nie zrobiono.

| # | Jednostka | Skąd | Odblokowuje |
|---|---|---|---|
| 6.1 | **`TextField`: tone `chat` → `surface`** | §Krok 6 | padding `SIZE_CLASSES` (12.5, zablokowany od pozycji 1 przez §8.11) |
| 6.2 | **`TextField`: czwarty ton** — eksport ma **cztery** wyglądy pola przy trzech tonach | 12.5, pozycja 1 | wierność `UserSearch` (15×18px zamiast 16×12px) |
| 6.3 | **`TextField`: nazwa tonu `elevated`** opisuje dziś **ciemną** kartę, a pochodzi od `--theme-elevated-surface`, które jest białe | 12.5, pozycja 1 | czytelność API |
| 6.4 | **`Button`: variant `send` → `primary`** — §Krok 6 ostrzega, że to **realne pytanie**, nie mechaniczne usunięcie; sprawdzić eksport przed scaleniem | §Krok 6 | padding i typografia `Button` (12.5: `py-3` wobec 14px, `text-sm/700` wobec 15px/800) |
| 6.5 | **`bg-hub-cta` → wariant `primary`** | §Krok 6 | — |
| 6.6 | **`Button`: wariant neutralny** — dziś nie ma żadnego, przez co modal awatara i trzy inne miejsca powielają przyciski ręcznie | 12.5, pozycja 28 | 6.7 |
| 6.7 | **Przyciski modala awatara → `Button`** | 12.5, pozycja 28 | — |
| 6.8 | **`ThemeToggle`: oś wariantów powłoki** — eksport parametryzuje swoją odpowiedniczkę po powłoce (`langBtnStyle(active, isSidebar)`) | 12.5, pozycja 7 | 6.9 |
| 6.9 | **Przebudowa powłoki `(app)`, jedną zmianą:** stopka znika z tras `(app)`, linki prawne schodzą na dół raila, `ThemeToggle` się przenosi i **traci obramowanie** | 12.5, pozycje 9/10/11 | zamknięcie wiersza o obramowaniu |
| 6.10 | **`Avatar`: gałąź zastępcza** — nieosiągalna, bo backend wydaje `avatarId` każdemu. Zostawić jako zabezpieczenie czy usunąć razem z propami `initial`/`color`? | 12.5, pozycja 3 | — |
| 6.11 | **`ActionResult` zdublowany** w dwóch plikach akcji po 2a | 12.5 | — |
| 6.12 | **Autouzupełnianie przeglądarki** na ciemnej karcie — `:-webkit-autofill` omija kaskadę | 12.5, pozycja 22 | *alternatywnie Krok 7* |

**Kolejność wymuszona zależnościami:** 6.1 → 6.2 → 6.3, potem 6.4 → 6.5,
potem 6.6 → 6.7, potem 6.8 → 6.9. Pozostałe są niezależne.

**6.9 jest strukturalna, nie stylistyczna** — rozlicza się jak 1b, 2a i 2b:
osobnym commitem, testem działaniem, wpisem w 14.5, nie w tabeli wyglądu.

### 14.2 Decyzje Kroku 6

| Co | Gdzie | Pytanie | Status |
|---|---|---|---|
| `send` vs `primary` | 6.4 | Czy design **naprawdę** traktuje Send inaczej niż pozostałe CTA? §Krok 6 każe sprawdzić eksport, zanim się scali. Eksport daje Send gradient teal→blue (`bg-hub-bubble`), a CTA mint→lime (`bg-hub-cta`) — **to są dwa różne potraktowania i scalenie może być błędem** | otwarta — potrzebna przy 6.4 |
| Czwarty ton `TextField` | 6.2 | Dodać czwarty ton czy pogodzić się z trzema? | otwarta — potrzebna przy 6.2 |
| Gałąź zastępcza `Avatar` | 6.10 | Zabezpieczenie czy martwy kod? | otwarta — potrzebna przy 6.10 |
| Nowe miejsce `ThemeToggle` | 6.9 | Rail, nagłówek trasy, czy gdzie indziej? | otwarta — potrzebna przy 6.9 |

**Wiersz o `send` jest najważniejszy w tej tabeli.** Plan zakłada scalenie,
ale eksport pokazuje dwa różne gradienty dla dwóch różnych ról — a `globals.css`
przy `@utility bg-hub-cta` **sam to zapisuje**: „design daje CTA wypełnienie
mint-lime, podczas gdy Send zachowuje bąbelkowy teal-blue, a pojedyncze
`--theme-primary` nie wyrazi obu". Domyślną odpowiedzią jest więc
**nie scalać**, a §Krok 6 zamienić w decyzję zapisaną w 14.2.

### 14.3 Znalezione, ale NIE naprawione (Krok 6)

*(do wypełnienia w trakcie)*

### 14.4 Metoda weryfikacji — dowód braku zmiany

Refaktoryzacja bez zmiany wizualnej ma jedną naturalną metodę: **ta sama sonda
przed i po, wynik identyczny**.

Dla każdej jednostki 6.x, **przed** zmianą, zbieramy `getComputedStyle` dla
wszystkich miejsc użycia dotkniętego komponentu i zapisujemy wynik w 14.5.
Po zmianie powtarzamy. **Każda różnica jest błędem tej jednostki**, dopóki nie
zostanie uzasadniona wpisem — odwrotnie niż w Kroku 4, gdzie różnica była celem.

Wyjątki, znane z góry: 6.1 i 6.4 mogą **odblokować** wartości zablokowane
w Kroku 4 (padding, typografia). Te zmiany są zamierzone, ale **muszą pójść
osobnymi commitami** niż samo scalenie — inaczej dowód „nic się nie zmieniło"
przestaje cokolwiek dowodzić.

### 14.5 Dziennik jednostek Kroku 6

| Jednostka | Miejsca użycia | Sonda przed | Sonda po | Różnice i ich uzasadnienie |
|---|---|---|---|---|

### 14.6 Definicja ukończenia Kroku 6

- [ ] **S.1** `TextField` ma o jeden ton mniej albo wpis w 14.2, dlaczego nie
- [ ] **S.2** `Button` ma o jeden wariant mniej albo wpis w 14.2, dlaczego nie —
      i wpis ten cytuje eksport, nie intuicję
- [ ] **S.3** Żaden komponent nie nazywa dwóch palet naraz
- [ ] **S.4** Sondy przed/po zgodne wszędzie poza zmianami odblokowanymi,
      a te w osobnych commitach
- [ ] **S.5** 6.9 rozliczona jak jednostka strukturalna: osobny commit,
      test działaniem, `'use client'` bez przyrostu albo z uzasadnieniem
- [ ] **S.6** Liczba wariantów w aplikacji **spadła** — jeśli po Kroku 6 jest
      ich tyle samo, krok nie został wykonany, tylko udokumentowany
- [ ] **S.7** Sekcja 14 wypełniona

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
