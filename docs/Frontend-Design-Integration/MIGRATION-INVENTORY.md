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

### 12.2 Rozstrzygnięcie czterech pytań wejściowych (§8.1)

Decyzje podjęte **2026-08-03** przez osobę prowadzącą migrację (Zyta);
decyzja ① odwrócona **2026-08-06**. To jest tabela referencyjna dla
Kroków 5–8: każda z tych decyzji tłumaczy stan aplikacji, który bez niej
wygląda na przypadkowy.

| # | Pytanie (źródło) | Decyzja | Data (i zmiany) | Konsekwencja dla zakresu | Co unieważnia |
|---|---|---|---|---|---|
| ① | Breakpointy (9.5 pkt 6) | **Desktop i mobile.** Aplikacja ma wyglądać dobrze i być funkcjonalna na wąskim ekranie | 2026-08-03 → **zmieniona 2026-08-06**; poprzednio: "Tylko desktop, węższe ekrany poza zakresem migracji" | **Dwie szerokości oceny** (robocze: 390px / 1440px) — pierwsze wiersze 12.0. **Jeden breakpoint** (`md:`, 768px); drugi tylko przez wpis w 12.4. **Zapis mobile-first**: klasa bez prefiksu = wąski ekran, prefiks = szeroki. Druga kolumna wartości w 12.0 **tylko z uzasadnieniem** — domyślnie jedna wartość na obie szerokości. Dokłada jednostkę strukturalną **1b** (nawigacja przy 390px) → 12.7 oraz punkty 8–9 definicji ukończenia (§8.9) | Zastępuje "założenie robocze, nierozstrzygnięte" z 9.5 pkt 6 — **oraz własną poprzednią wersję**: zapis "tylko desktop" nie obowiązuje od 2026-08-06 |
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
| **1b** — nawigacja przy 390px | ① | jeśli menu ma stan otwarty/zamknięty | 12.7, z powodem |
| **2a** — trasa znajomych | ③ | jeśli strona nowej trasy musi być komponentem klienckim | 12.7, z powodem |

Każdy inny przyrost względem sekcji 6 jest błędem, nie wyjątkiem.
Zapis "najwyżej +2, oba nazwane" jest tu po to, żeby Krok 5 nie musiał
zgadywać, które wpisy są zaplanowane, a które przypadkowe.

### 12.5 Znalezione, ale NIE naprawione (Zasada B) — pozycje założone z góry

| Co | Gdzie | Do którego kroku należy |
|---|---|---|
| Rozbieżność `--theme-danger` (`#e5484d`) vs gradient przycisku "logout" w eksporcie | ogólnoaplikacyjne | Krok 4/6 (odziedziczone z 11.5) |
| `elevated-border` — kontrast 1.04–1.13:1 | `app/globals.css`, wszystkie panele | Krok 7 (odziedziczone z 11.5) |
| `success`/`danger` jako tekst — kontrast 2.38–3.91:1 | `app/(app)/stomp/page.tsx` | Krok 7, **z zastrzeżeniem powyżej** (decyzja ④) |
| `/stomp` do usunięcia przed oceną końcową | `app/(app)/stomp/` | Poza planem migracji — sprzątanie (decyzja ④) |

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
