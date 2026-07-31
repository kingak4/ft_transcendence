# Plan — migracja pozostałej części aplikacji do designu 42Hub

**Dokument uzupełniający do:** `PLAN-static-chat-page.md` (w ramach którego dostarczono stronę czatu i tokeny `hub-*`)
**Data:** 2026-07-30

---

## Jak czytać ten dokument

Część 1 wyjaśnia prostym językiem, co właściwie robisz i dlaczego. Część 2 zawiera
listę kroków — ogólny plan. Część 3 opisuje **Krok 0** w pełnych szczegółach, ponieważ
to właśnie w Kroku 0 poznajesz system; każdy kolejny krok zakłada, że został wykonany
prawidłowo. Część 4 zawiera rejestr ryzyk.

Nie oczekuje się od Ciebie znajomości frontendu. Każde potrzebne pojęcie jest wyjaśniane
w momencie, w którym staje się potrzebne, a rolę podręcznika pełni ten codebase, nie tutorial.

**Nie zaczynaj od Kroku 1.** Krok 0 tworzy inwentaryzację, która dopiero pokazuje, czego
Krok 1 w ogóle dotyczy. Pominięcie go oznacza zgadywanie.

---

## Część 1 — Co właściwie robisz

### Wersja w jednym akapicie

W aplikacji funkcjonują obecnie **dwa systemy kolorów obok siebie**. Stary
(`brand-*` oraz zbudowane na nim nazwy semantyczne) odpowiada za wygląd każdej strony
poza czatem. Nowy (`hub-*`) odpowiada za stronę czatu i sidebar. Twoim celem jest
pozostawienie **jednego** systemu: nowy wygląd ma obowiązywać wszędzie, a stare nazwy
mają zostać usunięte. Pułapka polega na tym, że brzmi to jak „zmiana stylów w 19 plikach”,
ale nią nie jest — wykonane poprawnie zadanie polega głównie na **zmianie tego, na co
wskazuje kilka zmiennych**, poprzedzonej porządkami, które to umożliwią.

### Model mentalny: skrzynka bezpiecznikowa

Wyobraź sobie system kolorów jako skrzynkę bezpiecznikową w domu.

- **Token designu** to opisany obwód: `--color-surface` oznacza „tło strony”,
  a `--color-on-surface` oznacza „tekst znajdujący się na tle strony”.
- **Komponent** (przycisk, karta, sidebar) powinien być podłączony do *etykiety*, nigdy
  bezpośrednio do konkretnej żarówki. W kodzie oznacza to użycie `bg-surface`, a nie
  `#f0f0f0`.
- Zmiana oświetlenia w całym domu staje się wtedy **jedną zmianą w skrzynce
  bezpiecznikowej** — wskazujesz `--theme-surface` inną wartość, a każdy poprawnie
  podłączony komponent automatycznie za nią podąża.

Problem polega na tym, że część lamp w tym domu jest podłączona bezpośrednio do sieci,
z pominięciem skrzynki. Są to komponenty, w których kolor wpisano bezpośrednio. Po
przełączeniu bezpiecznika nie zmienią się — i nie dowiesz się, które z nich tak działają,
dopóki nie zajrzysz do każdego pomieszczenia. **Właśnie dlatego istnieje Krok 2** i dlatego
musi zostać wykonany przed jakąkolwiek zmianą wizualną.

### Dlaczego wcześniejsze prace pozostawiły dwie palety zamiast jednej

Była to świadoma decyzja, a jej uzasadnienie warto zrozumieć, ponieważ narzuca ono
ograniczenia na Twój plan. Z `PLAN-static-chat-page.md`, Decyzja 1:

> Odwrócenie `--theme-elevated-surface` z `#333333` na biel zmieniłoby ciemne panele
> na jasne we wszystkich 19 plikach korzystających z tych tokenów. Każdy komponent,
> w którym jasny tekst został wpisany na sztywno zamiast użycia
> `text-on-elevated-surface`, stałby się białym tekstem na białym tle.

W terminologii skrzynki bezpiecznikowej: poprzedni developer wiedział, że część lamp jest
podłączona bezpośrednio do sieci, dlatego zdecydował się zainstalować *drugą* skrzynkę dla
nowego pomieszczenia, zamiast ryzykować wyłączenie całego domu. Twoim zadaniem jest
dokończyć okablowanie, a następnie połączyć obie skrzynki w jedną. Druga skrzynka od
początku miała być tymczasowa — wskazują na to komentarze w kodzie oznaczone
`TODO(design-migration)`.

### Jak wygląda stan „gotowe”

1. `--color-brand-*` nie istnieje już w `app/globals.css`.
2. `hub-*` nie istnieje już **jako osobna nazwa** — jego wartości stały się tokenami
   semantycznymi (`--color-surface`, `--color-elevated-surface`, …).
3. Żaden plik `.tsx` nie zawiera literału koloru (`#0d2b47`, `rgb(...)`, `bg-[#fff]`).
4. Wszystkie komentarze `TODO(design-migration)` zostały usunięte z codebase’u, ponieważ
   opisywane przez nie zadania zostały wykonane.
5. Przełącznik motywu działa na każdej stronie, w tym na stronie czatu.

---

## Część 2 — Ogólny plan

Dziewięć kroków. Ich kolejność wynika z **ryzyka i zależności**, a nie z tego, jak bardzo
są interesujące. Przeczytaj całą listę przed rozpoczęciem, a następnie wykonuj kroki po
kolei.

### Dwie zasady, które zapewniają bezpieczeństwo migracji

**Zasada A — każdy krok jest albo refaktoryzacją, albo redesignem, nigdy jednym i drugim.**
Krok *refaktoryzacyjny* zmienia kod, ale po jego wykonaniu aplikacja wygląda
**identycznie co do piksela**. Krok *redesignu* zmienia wygląd **jednego wskazanego
obszaru** i niczego więcej. Jeśli połączysz oba rodzaje zmian i coś będzie wyglądać źle,
nie będziesz wiedzieć, czy refaktoryzacja coś zepsuła, czy nowy design po prostu tak
wygląda. Traktuj je jako oddzielne, możliwe do niezależnego zreviewowania jednostki.

**Zasada B — nie wolno Ci naprawiać tego, co znajdziesz.**
Krok 2 ujawni błędy: zły kontrast, zepsute layouty, martwy kod. **Zapisz je, ale ich nie
naprawiaj.** Migracji, która jednocześnie naprawia dwanaście niezwiązanych błędów, nie da
się sensownie zreviewować, a gdy coś się zepsuje, nie da się ustalić źródła problemu za
pomocą `git bisect`. Na poprawki istnieje osobny krok — Krok 7.

---

### Krok 0 — Zorientuj się w systemie i zbuduj inwentaryzację
*Refaktoryzacja: brak. Nie piszesz ani jednej linii kodu aplikacji.*

Uruchom aplikację, zobacz oba designy na własne oczy, poznaj pięć kluczowych pojęć i
przygotuj pisemną inwentaryzację każdej trasy, każdego komponentu i każdego miejsca,
w którym kolor został wpisany na sztywno. **Szczegółowe instrukcje znajdują się w
Części 3.**

**Gotowe, gdy:** istnieje `MIGRATION-INVENTORY.md`, istnieją bazowe screenshoty, a Ty
potrafisz odpowiedzieć na pięć pytań kontrolnych z §3.7 bez sprawdzania informacji.

---

### Krok 1 — Uporządkuj obszary według ryzyka
*Refaktoryzacja: brak. Tylko planowanie.*

Na podstawie inwentaryzacji przypisz każdą trasę do jednej z trzech kategorii:

- **Niskie ryzyko** — niewiele komponentów, brak współdzielonego layoutu, strona nie leży
  na ścieżce logowania. (Strony statyczne lub informacyjne.)
- **Średnie ryzyko** — korzysta ze współdzielonych komponentów (`Button`, `TextField`,
  `Avatar`), ale ma własny layout.
- **Wysokie ryzyko** — uwierzytelnianie, wszystko, z czym użytkownik styka się przed
  zalogowaniem, oraz wszystko, co renderuje treści generowane przez użytkowników.

**Dlaczego taka kolejność:** pierwszą migrację wizualną najlepiej wykonać na stronie, na
której pomyłka będzie jedynie niezręczna, a nie na stronie, na której może zablokować
użytkownikom dostęp do aplikacji. Warto również ostylować `Button` *zanim* przejdziesz do
strony, której jedyną kontrolką jest przycisk.

**Gotowe, gdy:** inwentaryzacja zawiera kolumnę `risk` i proponowaną kolejność.

---

### Krok 2 — Usuń kolory wpisane na sztywno (refaktoryzacja umożliwiająca migrację)
*Refaktoryzacja. Po tym kroku aplikacja musi wyglądać **identycznie**.*

Dla każdego literału koloru znalezionego w Kroku 0 zastąp go tokenem, który już ma tę
samą wartość. `#f0f0f0` staje się `bg-surface`. Jeśli żaden token nie ma takiej wartości,
**zatrzymaj się i zapytaj** — niedopasowany kolor jest pytaniem dotyczącym designu, a nie
kodu, a wymyślanie tokenu jest sposobem na rozrośnięcie palety do 40 niemal identycznych
odcieni szarości.

**Dlaczego tutaj:** to etap „podłącz każdą lampę do skrzynki bezpiecznikowej”. Dopóki nie
zostanie wykonany, przełączenie tokenów zmieni tylko część aplikacji i nie da się
przewidzieć, którą. Po jego zakończeniu warstwa tokenów rzeczywiście kontroluje wygląd —
a to jest jedyne założenie, na którym opierają się wszystkie późniejsze kroki.

**Jak sprawdzić, czy zadziałało:** porównaj aplikację ze screenshotami z Kroku 0. Każda
widoczna różnica jest błędem *tego* kroku, a nie zapowiedzią nowego designu.

**Znany przypadek, który należy uwzględnić:** `app/globals.css`, linia 152 —

```css
@utility bg-hub-shell {
  background: linear-gradient(165deg, #0b2b3a, #0d3339 55%, #0d2b3f);
}
```

Dwie sąsiednie utilities (`bg-hub-bubble`, `bg-hub-cta`) korzystają z
`var(--color-hub-*)`. Ta używa surowych wartości hex. Ujednolić ją, dodając tokeny dla
trzech odcieni powłoki.

---

### Krok 3 — Połącz obie skrzynki bezpiecznikowe
*Refaktoryzacja dla czatu (już korzysta z nowych wartości). Redesign dla całej reszty.*

To punkt zwrotny całej migracji, a jednocześnie zmiana **mniejsza, niż się wydaje**. Nie
zmieniasz w całym codebase’ie `bg-hub-panel` na `bg-elevated-surface`. Zmieniasz to, na
co wskazują tokeny semantyczne w `:root` w `app/globals.css`:

```css
:root {
  --theme-elevated-surface: #ffffff;      /* wcześniej var(--color-brand-reversed-main-color) */
  --theme-on-elevated-surface: #0d2b47;   /* wcześniej var(--color-brand-main-color) */
  /* … */
}
```

Wszystko, co zostało poprawnie podłączone, zmieni się jednocześnie. To korzyść wynikająca
z wykonania Kroku 2.

**Wprowadzaj zmiany po jednej parze tokenów**, przenosząc jednocześnie surface i
on-surface oraz sprawdzając efekt po każdej zmianie. Tło i kolor tekstu muszą zmieniać się
jako para, w przeciwnym razie powstaną tymczasowe, nieczytelne stany.

**Dlaczego pośrednia warstwa `--theme-*` ma tutaj znaczenie:** zauważ, że edytujesz
`:root`, a nie blok `@theme inline`. Nazwy w `@theme inline` są tym, co Tailwind zamienia
na nazwy klas podczas builda — muszą pozostać stabilne. Wartości w `:root` są rozwiązywane
przez przeglądarkę w runtime — to je zmieniasz. Dzięki temu rozdzieleniu ten krok wymaga
zmiany dziesięciu linii zamiast tysiąca.

**Gotowe, gdy:** aplikacja renderuje się w nowej palecie, a `--color-brand-*` jest
referencjonowane wyłącznie we własnych, obecnie martwych definicjach.

---

### Krok 4 — Przejdź przez trasy w kolejności ustalonej w Kroku 1
*Redesign. Jeden obszar na jednostkę pracy.*

Same tokeny nie kończą zadania. Między starym i nowym designem zmieniły się również
odstępy, promienie zaokrągleń, obramowania, cienie i typografia. Przechodź stronę po
stronie, porównując ją z eksportem designu i dostosowując utilities layoutu.

**Zasada dla tego kroku:** jeśli strona potrzebuje koloru, którego nie zapewnia żaden
token, jest to decyzja designerska — zapisz ją i idź dalej. Nie dodawaj tokenów ad hoc
w trakcie pracy nad pojedynczą stroną.

---

### Krok 5 — Przywróć nowej palecie obsługę motywów
*Redesign ograniczony do trybu `.mocha` / `.latte`.*

Strona czatu jest obecnie **wyspą o stałym jasnym motywie**: ignoruje przełącznik motywu.
Był to udokumentowany skrót. Linie 94–98 w `app/globals.css` dokładnie opisują, co należy
zrobić, a nawet wskazują wartości docelowe:

> `TODO(design-migration)`: strona czatu obecnie ignoruje ThemeToggle. Dodaj blok
> nadpisujący `--theme-hub-*` do poniższej reguły `.mocha, .latte` (panel →
> `ctp-surface0`, surface → `ctp-base`, border → `ctp-surface1`, muted/time →
> `ctp-subtext0/overlay1`). Ta warstwa pośrednia istnieje po to, aby wystarczyło dodać
> tutaj jeden blok i nie wykonywać żadnych zmian w JSX.

**Nie pozwól, aby ten krok pozostawał daleko za Krokiem 4.** W momencie, gdy pozostała
część aplikacji korzysta już z nowej palety *i* obsługuje motywy, nieobsługujący ich czat
przestaje wyglądać jak świadoma decyzja, a zaczyna wyglądać jak błąd.

---

### Krok 6 — Scal zduplikowane warianty komponentów
*Refaktoryzacja. Brak zmiany wizualnej, jeśli Kroki 3–5 wykonano poprawnie.*

Trzy komponenty otrzymały równoległy wariant wyłącznie dlatego, że istniała równoległa
paleta. Gdy pozostanie jedna paleta, warianty te będą opisywać ten sam wygląd dwa razy.
Każdy z nich zawiera komentarz wskazujący warunek, pod którym należy go usunąć:

| Plik | Co scalić | Treść komentarza |
|---|---|---|
| `app/components/TextField.tsx` :11–14 | tone `chat` scalić z tone `surface` | „Gdy `hub-*` stanie się *jedyną* paletą, `surface` i `chat` będą opisywać to samo — scal je i usuń ten tone, zamiast pozostawiać trzy nazwy dla dwóch wyglądów.” |
| `app/components/Button.tsx` :15–17 | variant `send` scalić z `primary` | „Pozostaw go osobno tylko wtedy, gdy design faktycznie traktuje Send inaczej niż pozostałe CTA.” |
| `app/globals.css` :164–171 | `bg-hub-cta` scalić z wariantem `primary` | „Gdy wariant `primary` komponentu Button przyjmie nowy design, powinien przejąć ten gradient.” |

Przypadek komponentu Button jest rzeczywistym pytaniem, a nie mechanicznym usunięciem —
przed scaleniem sprawdź eksport designu.

---

### Krok 7 — Odroczone prace przekrojowe
*Charakter mieszany. Każdy element jest niezależny; traktuj każdy jako osobną jednostkę.*

Poniższe zadania zostały jawnie odroczone w poprzednim planie (§8) lub zgłoszone w
`PR_REVIEW.md`. Mają charakter przekrojowy, dlatego najtaniej wykonać je po ustabilizowaniu
palety:

1. **Responsywny layout.** Strona czatu ma elementy interfejsu o stałej szerokości i nie
   ma breakpointów. Obecnie problem nie jest rozwiązany w całej aplikacji i przed
   rozpoczęciem prac wymaga decyzji dotyczącej obsługiwanych szerokości.
2. **`100vh` → `100dvh`.** W przeglądarkach mobilnych `100vh` uwzględnia pasek adresu,
   którego faktycznie nie ma w widocznym obszarze, przez co layouty o pełnej wysokości
   wychodzą poza ekran. Poprawka całej aplikacji polega na zmianie jednego znaku w klasie.
3. **Kontrast.** Trzy z pięciu kolorów avatarów nie spełniają WCAG AA dla białego tekstu
   (`#4ac9a0` przy 2.1:1, `#2f9bcf` przy 3.1:1, `#7c8a92` przy 3.6:1 — wymagane jest
   4.5:1). Popraw paletę, nie pojedyncze miejsca użycia.
4. **Fonty.** Wprowadzenie Manrope / Noto Sans Arabic.
5. **Przegląd dostępności.** Nieopisane landmarki `<nav>`; brak `<h1>` w całym drzewie
   `(app)`.
6. **Grupa tras o pełnej wysokości.** Grupa `(chat)` bez footera, jeśli layout o pełnej
   wysokości ma przestać zależeć od obliczeń `calc()` uwzględniających footer.
7. **i18n / RTL.** Strona czatu w większości korzysta z logicznych utilities (`ms-*`,
   `text-start`); pozostała część aplikacji tego nie robi. To duży, niezależny projekt —
   określ jego zakres osobno.

---

### Krok 8 — Usuń scaffolding
*Refaktoryzacja. Nic nie może zmienić się wizualnie.*

Usuń `--color-brand-*` oraz martwe tokeny (`--color-hub-ink-deep` i
`--color-hub-surface` / `--theme-hub-surface` są już dziś nieużywane). Usuń wszystkie
komentarze `TODO(design-migration)`, których zadania zostały wykonane. Usuń komentarze,
które wymieniają plik `PLAN-static-chat-page.md` z nazwy — nie ma gwarancji, że plik ten
istnieje w każdym klonie, więc wskazujący na niego komentarz jest martwym linkiem
(`globals.css` :93, `components/Sidebar.tsx` :20).

**Dlaczego na końcu:** elementy te są krytyczne dopóty, dopóki nie przestają być
potrzebne. Zbyt wczesne usunięcie tokenu zmienia czytelny komunikat „ta strona nadal
wymaga migracji” w błąd builda.

**Jak sprawdzić:** wyszukiwanie w codebase’ie fraz `brand-` i
`TODO(design-migration)` nie zwraca żadnych wyników, a aplikacja wygląda identycznie jak
po zakończeniu Kroku 7.

---

## Część 3 — Krok 0 w szczegółach

Budżet: **jeden do dwóch dni.** Rezultatem jest dokument, nie diff.

### 3.1 Potwierdź, że masz trzy dokumenty referencyjne

Przed rozpoczęciem sprawdź, czy w Twojej kopii repozytorium istnieją poniższe trzy pliki:

| Ścieżka | Czym jest |
|---|---|
| `PLAN-static-chat-page.md` | Wyjaśnia, dlaczego obecny stan wygląda właśnie tak. Przeczytaj w całości §2 (Decisions) i §8 (Deferred). |
| `PR_REVIEW.md` | Peer review wykonanych prac. Przeczytaj sekcje 🟠 i 🔵 — zawierają otwarte pytania, które przejmujesz. |
| `42Hub UIUX design/Forti2Hub.dc.html` | Eksport designu. Wizualne źródło prawdy. |

**Plików tych może brakować.** W momencie pisania dokumentu były nieśledzonymi plikami
lokalnymi, niecommitowanymi do brancha. Jeśli któregoś brakuje, poproś o niego przed
kontynuowaniem — pozostała część planu zakłada, że możesz je przeczytać.

### 3.2 Uruchom aplikację i obejrzyj ją

Z katalogu `/root/design/frontend`:

```bash
npm install
```

*„Przeczytaj listę zależności i pobierz każdą bibliotekę potrzebną projektowi do
`node_modules/`. Uruchom raz; uruchom ponownie za każdym razem, gdy ktoś zmieni
`package.json`.”*

```bash
npm run dev
```

*„Uruchom serwer developerski. Kompiluje aplikację, udostępnia ją pod lokalnym adresem
(zwykle `http://localhost:3000`) i automatycznie przebudowuje po każdym zapisaniu pliku.
Pozostaw go uruchomionego w osobnym terminalu.”*

Następnie w przeglądarce:

1. Otwórz `/chat`. **To jest design docelowy.** Ostatecznie cała aplikacja ma wyglądać
   właśnie tak.
2. Otwórz `/chat?friend=grzes`. Zwróć uwagę, że wybrana rozmowa się zmieniła — aplikacja
   przechowuje ten wybór w URL-u, a nie w pamięci.
3. Otwórz każdą inną dostępną trasę. **To jest design źródłowy.** Zapisz, które strony
   wyglądają wyraźnie starzej.
4. Znajdź przełącznik motywu i przełącz go. Zauważ, że zmienia pozostałą część aplikacji,
   ale **nie** stronę czatu. Ta asymetria jest przedmiotem Kroku 5.

Zrób screenshot każdej strony w obu motywach i zapisz je w folderze **poza repozytorium**
(np. `~/migration-baseline/`). Będą dowodem, że Krok 2 niczego nie zmienił. Nazwij je
zgodnie z trasami: `chat.png`, `chat-mocha.png` itd.

### 3.3 Pięć pojęć

Przeczytaj je raz teraz i ponownie przed rozpoczęciem Kroku 2. Każde wskazuje rzeczywisty
plik, dzięki czemu możesz zobaczyć dane rozwiązanie zamiast wierzyć opisowi na słowo.

**① Token designu to nazwany kolor.**
W `app/globals.css`, linia 100:

```css
--theme-hub-panel: #ffffff;
```

To *CSS custom property* — zmienna. Komponenty odnoszą się do nazwy. Gdy zmienia się
wartość przypisana do nazwy, zmienia się wszystko, co z niej korzysta. Na tym polega cała
idea.

**② Istnieją dwie warstwy nazw i pełnią różne funkcje.**
Porównaj linie 66–76 z liniami 99–109:

```css
@theme inline {
  --color-hub-panel: var(--theme-hub-panel);   /* stabilna NAZWA  */
}
:root {
  --theme-hub-panel: #ffffff;                  /* aktualna WARTOŚĆ */
}
```

Tailwind odczytuje nazwy `--color-*` **w czasie builda** i generuje na ich podstawie klasy
CSS: istnienie `--color-hub-panel` pozwala zapisać `className="bg-hub-panel"`. Nazwy te
muszą być stabilne, ponieważ zmiana jednej z nich oznacza edycję każdego pliku
korzystającego z danej klasy.

Wartości `--theme-*` są rozwiązywane **przez przeglądarkę w runtime**, dzięki czemu mogą
się zmieniać bez ponownego builda — właśnie dlatego jedna klasa na `<html>` może
przemalować całą aplikację.

Słowo kluczowe `inline` umożliwia takie działanie: mówi Tailwindowi, aby wygenerował
`background-color: var(--color-hub-panel)`, zamiast wbudować `#ffffff` bezpośrednio w
stylesheet. Bez `inline` wartość zostałaby zamrożona podczas builda, a przełącznik motywu
nie miałby czego przełączać. **To najważniejsza rzecz, którą trzeba zrozumieć przed
Krokiem 3.**

**③ Stylujesz elementy, umieszczając na nich nazwy klas.**
Nie istnieją osobne pliki CSS dla komponentów. `app/(app)/layout.tsx`, linia 15:

```tsx
<div className="bg-surface text-on-surface flex min-h-screen">
```

Cztery instrukcje: ustaw tło na podstawie tokenu `surface`, tekst na podstawie
`on-surface`, ułóż elementy potomne w wierszu i zapewnij wysokość co najmniej równą
wysokości okna.

**④ Dwie klasy ustawiające tę samą właściwość remisują — a o wyniku nie decyduje
kolejność, w jakiej je zapisano.**
Decyduje o nim ich kolejność w *wygenerowanym stylesheet*, nad którą nie masz kontroli.
Codebase dokumentuje to już w dwóch miejscach; `TextField.tsx`, linie 50–52:

> `className` jest dołączane wyłącznie dla potrzeb *layoutu* (marginesy, położenie w
> gridzie). Przekazywanie tutaj utilities koloru lub paddingu konkuruje z powyższymi
> klasami przy tej samej specyficzności, a wynik zależy od kolejności w stylesheet, nie od
> kolejności zapisu w tym miejscu.

**Praktyczna zasada: aby zmienić wygląd komponentu, edytuj komponent.** Nie próbuj
nadpisywać go z zewnątrz, przekazując klasy kolorów. Może wyglądać, jakby działało w
jednym miejscu, a w innym po cichu zawiedzie — i taki błąd jest bardzo trudny do
zdiagnozowania.

**⑤ Niektóre pliki uruchamiają się w przeglądarce; większość nie.**
Plik zaczynający się od `'use client'` wysyła JavaScript do przeglądarki. Pliki bez tej
dyrektywy renderują się raz na serwerze i wysyłają wyłącznie HTML. Pliki serwerowe są
tańsze i mogą bezpośrednio odczytywać takie dane jak cookies (zobacz
`app/(app)/layout.tsx`, linia 11).

**Zasada dla tej migracji: nigdy nie dodawaj `'use client'` do pliku, który jeszcze go
nie ma.** Jeśli restylizacja wydaje się tego wymagać, próbujesz rozwiązać problem CSS za
pomocą JavaScriptu. Zatrzymaj się i zapytaj.

### 3.4 Zbuduj inwentaryzację

Uruchom każde polecenie z katalogu `/root/design/frontend`. Wklej wynik do
`MIGRATION-INVENTORY.md`, tworząc osobny nagłówek dla każdego polecenia.

**Każda trasa w aplikacji:**
```bash
find app -name 'page.tsx' | sort
```
*„Przeszukaj folder `app` w poszukiwaniu każdego pliku o nazwie `page.tsx` i wyświetl je
alfabetycznie. W tym frameworku jeden `page.tsx` = jeden URL, dlatego jest to lista tras.”*

**Każdy współdzielony komponent:**
```bash
find app/components -type f | sort
```
*„Wyświetl każdy plik (`-type f` wyklucza foldery) w folderze komponentów.”*

**Pliki nadal korzystające ze starej palety:**
```bash
grep -rn --include='*.tsx' 'brand-' app
```
*„Przeszukaj rekurencyjnie (`-r`) katalog `app`, wyłącznie pliki `.tsx`, w poszukiwaniu
tekstu `brand-`, i pokaż każde dopasowanie wraz z numerem linii (`-n`).”* Są to
bezpośredni użytkownicy starej palety — najbardziej oczywiste cele migracji.

**Pliki korzystające z tokenów semantycznych:**
```bash
grep -rln --include='*.tsx' -E '(bg|text|border)-(surface|on-surface|elevated-surface|on-elevated-surface|elevated-border|primary|on-primary|success|danger)' app
```
*„To samo wyszukiwanie, ale `-E` włącza rozszerzone wzorce, dzięki czemu `(a|b)` oznacza
„a lub b”, a `-l` wyświetla tylko nazwy plików zamiast pasujących linii.”* Są to poprawnie
podłączone komponenty — te, które zmienią się automatycznie w Kroku 3. **Policz je.**
Stosunek długości tej listy do następnej pokaże, jak dużo pracy wymaga Krok 2.

**Kolory wpisane na sztywno — najważniejsze wyszukiwanie:**
```bash
grep -rn --include='*.tsx' -E '#[0-9a-fA-F]{3,8}|rgb\(|hsl\(' app
```
*„Znajdź wszystko, co wygląda jak surowy kolor: `#`, po którym następuje od 3 do 8 cyfr
hex, albo wywołanie `rgb(` / `hsl(`.”* **Każda zwrócona linia jest pracą w Kroku 2.**
Spodziewaj się false positives (`#` w URL-u lub komentarzu) — sprawdź każdy wynik ręcznie.

```bash
grep -rn --include='*.tsx' -E '\[(#|rgb|hsl)' app
```
*„Znajdź klasy Tailwinda z „arbitrary value”, takie jak `bg-[#0d2b47]` — kolor
przemycony w nazwie klasy. `\[` escape’uje nawias, aby został dopasowany dosłownie.”*
To również praca do wykonania w Kroku 2.

**Pliki uruchamiane po stronie przeglądarki:**
```bash
grep -rln "'use client'" app
```
*„Wyświetl pliki zawierające znacznik `'use client'`.”* Zanotuj je; zgodnie z pojęciem ⑤
lista nie może się powiększyć.

**Wskazówki pozostawione przez poprzedniego developera:**
```bash
grep -rnF 'TODO(design-migration)' app
```
*„Znajdź dokładnie ten tekst (`-F` = traktuj wzorzec dosłownie, aby nawiasy nie zostały
zinterpretowane jako znaki specjalne).”* Każdy wynik jest wcześniej podjętą i zapisaną
decyzją. Przeczytaj je wszystkie. Stanowią szkielet Kroków 5, 6 i 8.

**Sprawdź również sam stylesheet**, pomijany przez powyższe filtry
`--include='*.tsx'`:
```bash
grep -n -E '#[0-9a-fA-F]{3,8}' app/globals.css
```
*„Pokaż każdy surowy kolor hex w stylesheet wraz z numerem linii.”* Surowe wartości hex
są oczekiwane w `:root` — to właśnie tam *powinny* znajdować się wartości. Surowy hex w
jakimkolwiek innym miejscu (np. w linii 152) jest pracą do wykonania w Kroku 2.

### 3.5 Przeczytaj stronę czatu jako kompletny przykład

Strona czatu jest jedyną częścią aplikacji znajdującą się już w stanie docelowym i została
zbudowana w sposób ułatwiający jej analizę. Poproś o poniższe pliki i przeczytaj je w tej
kolejności:

1. `app/(app)/chat/page.tsx` — jak strona składa komponenty i pobiera dane.
2. `app/(app)/chat/fixtures.ts` — dane placeholderowe. Zwróć uwagę, że to właśnie ten
   moduł ma zostać zastąpiony prawdziwymi danymi przez innego developera.
3. `app/(app)/chat/Conversation.tsx`, `MessageBubble.tsx`, `FriendRail.tsx`,
   `FriendRow.tsx`, `Composer.tsx` — elementy prezentacyjne.
4. `app/components/PresenceAvatar.tsx` — mały, przejrzysty przykład tworzenia nowego
   komponentu przez *opakowanie* istniejącego zamiast jego kopiowania.

Przeczytaj komentarze. Wyjaśniają decyzje i rozumowanie, a nie składnię, i kilka z nich
jest jedynym pisemnym zapisem danej decyzji.

### 3.6 Czego nie wolno robić w Kroku 0

- **Nie edytuj żadnego pliku.** Jeśli zauważysz problem, dodaj go do sekcji „Found in
  Step 0” w inwentaryzacji. Zasada B.
- **Nie dodawaj tokenu.** Nie wiesz jeszcze, czy potrzebny kolor nie istnieje już pod inną
  nazwą.
- **Nie zaczynaj od strony, którą uważasz za najbrzydszą.** Niemal zawsze jest ona
  najbardziej ryzykowna. Kolejność ustala Krok 1 i robi to na podstawie ryzyka.

### 3.7 Definicja ukończenia Kroku 0

Krok jest ukończony, gdy istnieją wszystkie cztery elementy:

1. `MIGRATION-INVENTORY.md`, zawierający wynik każdego polecenia z §3.4 oraz listę
   „Found in Step 0” z problemami, które zostały zauważone, ale nie naprawione.
2. Bazowe screenshoty każdej trasy w obu motywach, zapisane poza repozytorium.
3. Jednoliniowa notatka dla każdego wyniku `TODO(design-migration)`, wskazująca krok,
   który go rozwiąże.
4. Pisemne odpowiedzi z pamięci na pięć poniższych pytań:

   - Dlaczego `--color-hub-panel` wskazuje na `--theme-hub-panel`, zamiast po prostu mieć
     wartość `#ffffff`?
   - Co przestałoby działać, gdyby słowo kluczowe `inline` zostało usunięte z bloku
     `@theme`?
   - Przekazujesz `className="bg-red-500"` do `<TextField />`, ale tło się nie zmienia.
     Dlaczego — i jaka jest poprawna naprawa?
   - Co jest bardziej ryzykowne i dlaczego: zmiana `--theme-elevated-surface` czy zmiana
     `--theme-danger`?
   - Dlaczego Krok 2 musi zostać wykonany przed Krokiem 3?

Jeśli nie potrafisz odpowiedzieć na wszystkie pięć pytań, przeczytaj ponownie §3.3,
zamiast przechodzić dalej. Każdy kolejny krok pogłębia skutki niezrozumienia tych zasad.

---

## Część 4 — Rejestr ryzyk

| # | Ryzyko | Dlaczego powstaje | Sposób ograniczenia |
|---|---|---|---|
| R1 | Biały tekst na białym tle po Kroku 3 | W komponencie jasny tekst został wpisany na sztywno zamiast użycia `text-on-elevated-surface`; zmiana tła na białe pozostawia niewidoczny tekst | Właśnie temu zapobiega Krok 2. Jeśli problem wystąpi, Krok 2 był niekompletny — wróć do niego, zamiast łatać objaw |
| R2 | Lint nie przechodzi dla niestandardowych klas `bg-hub-*` | `eslint-plugin-tailwindcss@3` powstał przed dyrektywą `@utility` w Tailwind 4 i może oznaczać je jako nieznane nazwy klas | **Niezweryfikowane w momencie pisania.** Uruchom `npm run lint` w Kroku 0 i zapisz wynik; jeśli zakończy się błędem, jest to poprawka konfiguracji, którą należy wdrożyć przed Krokiem 2, a nie powód, aby unikać własnych utilities |
| R3 | Regresje kontrastu trafiają na produkcję niezauważone | Nowa paleta jest jaśniejsza; tekst, który spełniał wymagania na ciemnych panelach, może ich nie spełniać na białych | Sprawdzaj kontrast przy zmianie każdej pary tokenów, nie dopiero na końcu. Trzy znane problemy są wymienione w Kroku 7 |
| R4 | Strona czatu zaczyna odstawać od reszty | Krok 4 sprawia, że aplikacja korzysta z nowej palety i obsługuje motywy, podczas gdy czat pozostaje w stałym jasnym motywie | Nie pozwól, aby Krok 5 pozostawał więcej niż jeden krok za Krokiem 4 |
| R5 | Rozrost zakresu pochłania migrację | Krok 2 ujawnia rzeczywiste błędy i ich naprawianie wydaje się odpowiedzialne | Zasada B. Zapisuj, nie naprawiaj. Krok 7 istnieje właśnie w tym celu |
| R6 | Komentarze wskazują pliki, które nie istnieją | Dwa komentarze odnoszą się do `PLAN-static-chat-page.md`, który nie był śledzony | Potwierdź jego istnienie w §3.1; rozwiąż problem w Kroku 8 |

---

## Otwarte pytania do osoby odpowiedzialnej za migrację

1. **Jaki jest docelowy zestaw obsługiwanych szerokości ekranu?** Bez tej informacji nie
   da się określić zakresu elementu 1 w Kroku 7, a wpływa ona również na Krok 4 — ponowne
   stylowanie strony tylko dlatego, że breakpointy pojawiły się zbyt późno, jest czystym
   marnotrawstwem.
2. **Czy eksport designu obejmuje każdą trasę, czy tylko część?** Jeśli dana trasa nie ma
   designu, Krok 4 nie ma dla niej źródła prawdy i ktoś musi zdecydować, jak ma wyglądać.
3. **Czy `.mocha` / `.latte` powinny w ogóle przetrwać migrację**, czy nowy design ma
   zastąpić obsługę motywów, zamiast samemu jej podlegać? W zależności od odpowiedzi Krok
   5 jest rzeczywistym zadaniem albo usunięciem istniejącego rozwiązania.

---

## Część 5 — Krok 1 w szczegółach

*Dopisane 2026-07-31, po zakończeniu Kroku 0. Wejściem dla tej części jest artefakt Kroku 0:
`MIGRATION-INVENTORY.md`. Bez niego ta część nie ma na czym pracować.*

Budżet: **pół dnia (2–4 godziny).** Rezultatem jest tabela i kolejność, nie diff. Nie
edytujesz **ani jednego** pliku aplikacji. Jedyny plik, który w tym kroku zmieniasz, to
`MIGRATION-INVENTORY.md`.

### 5.0 Dlaczego ten krok nie jest biurokracją

Krok 1 wygląda na wypełnianie tabelki i łatwo go potraktować jako formalność do
odhaczenia. Jego rzeczywista funkcja jest inna: **Krok 4 to ciąg niezależnych jednostek
pracy, a kolejność tych jednostek decyduje o tym, gdzie popełnisz pierwsze błędy.** Błędy
popełnisz na pewno — pytanie brzmi tylko, czy zobaczysz je na stronie regulaminu, czy na
ekranie logowania.

Analogia: nauki jazdy nie zaczyna się na autostradzie. Nie dlatego, że autostrada jest
trudniejsza technicznie, tylko dlatego, że koszt pomyłki jest tam nieporównywalnie wyższy.
Krok 1 to wskazanie parkingu.

Jest jeszcze drugi, mniej oczywisty powód. Migracja wizualna ma naturalny **kierunek
zależności**: strona składa się z komponentów, a nie odwrotnie. Jeśli ostylujesz stronę
logowania przed komponentem `Button`, to po dojściu do `Button` wrócisz do strony logowania
po raz drugi. Krok 1 ustala kolejność zgodną z tym kierunkiem, żeby każde miejsce dotknąć
dokładnie raz.

### 5.1 Ustalenia wejściowe — trzy rzeczy już rozstrzygnięte

Definicja ukończenia Kroku 0 (§3.7) wymieniała elementy, które od tego czasu zostały
rozstrzygnięte inaczej, niż zakładał pierwotny plan. Nie są to luki do domknięcia; są to
decyzje, które zmieniają sposób pracy w Krokach 2–4, dlatego zapisujemy je tutaj.

**① Weryfikacja przez równoległe branche, nie przez screenshoty.**
Krok 2 wymaga dowodu, że refaktoryzacja **niczego nie zmieniła wizualnie**. Zamiast zdjęć
bazowych porównanie odbywa się na żywo: stary branch i branch migracyjny uruchomione
jednocześnie, dwa okna obok siebie, ta sama trasa w obu.

Ta metoda jest lepsza w jednym istotnym wymiarze i gorsza w innym — warto znać oba.
Screenshot jest artefaktem *zamrożonym*: pokazuje stan z dnia, w którym powstał, i po
tygodniu przestaje odpowiadać czemukolwiek, jeśli w międzyczasie ktoś domerguje zmianę do
punktu odniesienia. Uruchomiony branch jest zawsze aktualny i pozwala porównać rzeczy,
których zdjęcie nie łapie — hover, focus, zachowanie przy zmianie szerokości okna,
przełączenie motywu. W zamian wymaga, żeby branch odniesienia **pozostawał uruchamialny**
przez cały czas trwania migracji. To warunek, o którym łatwo zapomnieć, bo łamie się go
przypadkiem, a nie świadomie.

Praktycznie: drugi serwer deweloperski musi wystartować na innym porcie, bo domyślny 3000
jest już zajęty przez pierwszy.

```bash
npm run dev -- -p 3001
```

*„Uruchom skrypt `dev`, ale przekaż mu dodatkowy argument. Podwójny myślnik `--` to
granica: wszystko po nim npm przestaje traktować jako własne opcje i przekazuje dalej, do
uruchamianego programu. `-p 3001` mówi serwerowi Next.js, żeby nasłuchiwał na porcie 3001
zamiast domyślnego 3000. Dzięki temu dwie wersje aplikacji działają obok siebie pod
`localhost:3000` i `localhost:3001`.”*

**② Ryzyko R2 (lint) jest zamknięte.** Lint działa jako check CI przy każdym PR i przy
każdym pushu do `main`. Nie ma więc potrzeby uruchamiania go ręcznie „na wszelki wypadek”
przed Krokiem 2 ani zapisywania wyniku w inwentaryzacji — jeśli `@utility` i klasy
`bg-hub-*` miałyby wywracać linter, byłoby to widoczne już przy PR, który je wprowadził.
Pozycję R2 w rejestrze ryzyk można traktować jako nieaktualną.

**③ Oba miejsca z surowym hexem idą do poprawki.** Sekcja 8 inwentaryzacji zostawiała
`globals.css:144–145` jako „do sprawdzenia ręcznego”. Sprawdzenie zostało wykonane: to ten
sam typ problemu co linia 152. Zakres Kroku 2 jest zatem **zamknięty i liczy trzy pozycje**:

| # | Miejsce | Co jest nie tak |
|---|---|---|
| 1 | `app/globals.css` :152 (`bg-hub-shell`) | gradient na surowych `#0b2b3a`, `#0d3339`, `#0d2b3f` zamiast `var(--color-hub-*)` |
| 2 | `app/globals.css` :144–145 | gradient na surowych hexach poza `:root` |
| 3 | `app/components/Sidebar.tsx` :27 | hardkodowane `white/70` jako placeholder |

To jest cały Krok 2 — trzy miejsca, jeden commit. Warto to zapamiętać przy planowaniu
kolejności w §5.5: Krok 2 nie jest fazą, tylko drobiazgiem, a jego jedyna trudność polega
na tym, że pozycja 1 i 2 wymagają **dodania tokenów** dla odcieni powłoki, a nie tylko
podmiany nazw.

### 5.2 Zamień trzy kategorie ryzyka w pytania rozstrzygalne

Krok 1 w Części 2 podaje trzy kategorie opisowo („niewiele komponentów”, „wszystko, z czym
użytkownik styka się przed zalogowaniem”). To za mało, żeby dwie osoby doszły do tej samej
odpowiedzi — a tabela ryzyka, którą da się wypełnić dowolnie, zawsze zostaje wypełniona tak,
żeby pasowała do kolejności, którą ktoś już wcześniej sobie założył.

Dlatego rozbijamy to na **sześć pytań zamkniętych**. Dla każdej trasy odpowiadasz TAK/NIE.

| # | Pytanie | Jeśli TAK |
|---|---|---|
| P1 | Czy zepsucie tej strony **uniemożliwia** użytkownikowi korzystanie z aplikacji? (logowanie, rejestracja, jedyne wejście do aplikacji) | **Wysokie** |
| P2 | Czy strona renderuje **treści pochodzące od użytkownika** (nazwa wyświetlana, avatar, lista znajomych, wiadomości)? | **Wysokie** |
| P3 | Czy strona zawiera komponenty `'use client'` wykonujące **akcje zmieniające dane** (dodaj znajomego, zmień avatar)? | co najmniej **Średnie** |
| P4 | Czy strona używa **5 lub więcej** komponentów współdzielonych? | co najmniej **Średnie** |
| P5 | Czy strona dzieli layout z innymi trasami (jest w grupie z własnym `layout.tsx`)? | co najmniej **Średnie** |
| P6 | Czy dla tej trasy **istnieje** ekran w eksporcie designu? | jeśli **NIE** → trasa jest **zablokowana**, nie planuj jej w Kroku 4 |

**Zasada agregacji: wygrywa najwyższa kategoria, nie średnia.** Jedno TAK w P1 czyni stronę
wysokiego ryzyka niezależnie od tego, jak niewinnie wypada w pozostałych pięciu pytaniach.
To nie jest punktacja — ryzyko się nie uśrednia. Strona z jedną krytyczną właściwością i
pięcioma nieszkodliwymi nie jest „średnio ryzykowna”; jest ryzykowna.

**Dlaczego akurat te sześć pytań.** P1 i P2 mierzą **koszt błędu** — co się stanie, jeśli
strona będzie wyglądać źle. P3, P4 i P5 mierzą **prawdopodobieństwo błędu** — ile ruchomych
części ma strona i jak daleko sięga pomyłka. To dwie niezależne osie, mnożone przez siebie.
P6 nie mierzy ryzyka w ogóle; jest bramką wykonalności i siedzi w tej samej tabeli tylko
dlatego, że tabelę i tak wypełniasz trasa po trasie, a lepiej odkryć brak designu teraz niż
w połowie Kroku 4.

**Uwaga o P5.** Wszystkie trasy w grupie `(app)` dzielą `app/(app)/layout.tsx`, a ten
layout renderuje `Sidebar` — czyli komponent, w którym inwentaryzacja (sekcja 7) znalazła
hardkodowane `white/70`. To jedyny znany „bezpiecznik pominięty” w warstwie widocznej na
**każdej** stronie aplikacji. Sam w sobie nie podnosi ryzyka pojedynczej trasy, ale
przesądza o kolejności: `Sidebar` musi zostać domknięty w Kroku 2, zanim ocenisz wygląd
którejkolwiek strony z grupy `(app)`, bo inaczej za każdym razem będziesz patrzeć na jeden
i ten sam błąd i zastanawiać się, czy to wina strony.

### 5.3 Zbuduj mapę zależności: trasa → komponenty

Do pytania P4 potrzebujesz wiedzieć, ile i jakich komponentów używa każda trasa. Do
ustalenia kolejności (§5.5) potrzebujesz odwrotności: ile tras zależy od danego komponentu.
To ta sama informacja czytana z dwóch stron i warto mieć obie.

Polecenia uruchom z katalogu **`/root/design/frontend`**:

```bash
grep -rn --include='*.tsx' -F 'components/' app | sort
```

*„Przeszukaj rekurencyjnie katalog `app`, tylko pliki `.tsx`, szukając dosłownego tekstu
`components/`, i posortuj wynik. `-F` znaczy »traktuj wzorzec dosłownie«, więc `/` nie jest
interpretowane jako nic specjalnego. Ponieważ każdy import komponentu zawiera w ścieżce
`components/` — niezależnie od tego, czy zapisano go jako `@/app/components/Button`, czy
jako `../../components/Button` — to wyszukiwanie daje listę »który plik importuje co«.”*

To jest mapa w kierunku **trasa → komponent**. Teraz kierunek odwrotny:

```bash
grep -rno --include='*.tsx' -E '<(AccentLink|Avatar|BareLayout|BrandLink|Button|Card|ContactBlock|Footer|Hero|LegalSection|PresenceAvatar|SessionCard|Sidebar|Tag|TextField|ThemeToggle|UserList|UserSearch)\b' app | sort
```

*„To samo przeszukiwanie, ale szukamy **użycia** komponentu w JSX, czyli znaku `<` z nazwą
komponentu. `-E` włącza rozszerzone wzorce, dzięki czemu `(A|B|C)` znaczy »A albo B albo
C«. `\b` to »granica słowa« — pilnuje, żeby `<Card` nie dopasowało się do `<CardHeader`.
`-o` wypisuje samo dopasowanie zamiast całej linii, przez co wynik jest krótki i łatwo w
nim policzyć wystąpienia.”*

Z tego drugiego wyniku policz, **ile różnych plików** używa każdego komponentu. Ta liczba
(nazywa się to *fan-in* — ilu ma odbiorców) jest jedynym rzetelnym miernikiem tego, które
komponenty należy ostylować najpierw. Komponent używany w sześciu miejscach ostylowany
poprawnie załatwia sześć miejsc naraz; ostylowany błędnie psuje sześć miejsc naraz.

Jedna trasa nie da się w ten sposób zmierzyć rzetelnie: `/(app)/[userId]` trzyma część
elementów interfejsu w plikach obok siebie (`FriendsPanel.tsx`, `AddFriendButton.tsx`,
`EditAvatarButton.tsx`, …), a nie w `app/components`. Nie są to komponenty współdzielone,
więc nie liczą się do P4, ale **są** pracą w Kroku 4. Policz je osobno jako „komponenty
lokalne trasy”, inaczej niedoszacujesz jej rozmiaru.

### 5.4 Proponowana klasyfikacja — do zweryfikowania, nie do przepisania

Poniżej punkt wyjścia oparty na tym, co już wynika z `MIGRATION-INVENTORY.md`. Kolumny
oznaczone `?` wymagają rozstrzygnięcia po wykonaniu §5.3 — nie da się ich odpowiedzialnie
wypełnić bez mapy zależności.

| Trasa | P1 blokada | P2 treści użytkownika | P3 akcje | P4 komponenty | P5 wspólny layout | Ryzyko |
|---|---|---|---|---|---|---|
| `(app)/stomp` | NIE | NIE | TAK (klient) | ? | TAK | **Niskie** |
| `(app)/privacy-policy` | NIE | NIE | NIE | ? | TAK | **Niskie** |
| `(app)/terms-of-service` | NIE | NIE | NIE | ? | TAK | **Niskie** |
| `(marketing)/` | TAK | NIE | NIE | ? | ? | **Wysokie** |
| `(app)/[userId]` | NIE | TAK | TAK | ? (+5 lokalnych) | TAK | **Wysokie** |
| `(auth)/login` | TAK | NIE | TAK | ? | ? | **Wysokie** |
| `(auth)/register` | TAK | NIE | TAK | ? | ? | **Wysokie** |

Uzasadnienia wymagające komentarza:

- **`(app)/stomp` jest najniższego ryzyka mimo `'use client'` i akcji.** To strona
  diagnostyczna, nie produktowa. Jeśli po migracji będzie wyglądać dziwnie, zobaczy to
  developer, a nie użytkownik. To czyni ją idealnym miejscem na pierwszą pomyłkę — z tego
  samego powodu, dla którego jest bezwartościowa jako dowód, że migracja się udała.
- **Strony prawne są niskiego ryzyka, ale nie zerowego.** Renderują długi tekst ciągły,
  czyli jedyne miejsce w aplikacji, gdzie realnie zobaczysz problem z typografią i
  kontrastem tekstu podstawowego. To dobre pierwsze *prawdziwe* strony.
- **`(marketing)/` to jedyny przypadek naprawdę sporny.** Wg litery kryterium z Części 2
  („wszystko, z czym użytkownik styka się przed zalogowaniem”) jest wysokiego ryzyka. Wg
  P1 również — to wejście do aplikacji. Ale jej ryzyko ma inną naturę niż ryzyko strony
  logowania: zepsuta strona marketingowa **wygląda** źle, zepsuta strona logowania
  **blokuje**. Dodatkowo to jedyne miejsce (`Hero.tsx`, `Tag.tsx`) używające jeszcze starej
  palety `brand-*`, więc i tak musi zostać dotknięte. **Rekomendacja:** zostaw ją w kategorii
  wysokiej, ale ustaw jako **pierwszą** wśród wysokich — ma najwyższy koszt wizerunkowy i
  najniższy koszt funkcjonalny z całej trójki.
- **`(app)/[userId]` jest wysokiego ryzyka mimo braku P1.** Renderuje treści od
  użytkownika (P2) i ma najwięcej ruchomych części ze wszystkich tras (pięć lokalnych
  komponentów klienckich z akcjami mutującymi). To najdroższa pojedyncza jednostka pracy w
  całym Kroku 4 — zaplanuj ją jako osobny, samodzielny kawałek, nie jako „jeszcze jedną
  stronę”.

**Zweryfikuj tę tabelę, nie ufaj jej.** Powstała z inwentaryzacji, a nie z lektury tras.
Jeśli twoja odpowiedź na któreś pytanie różni się od powyższej — twoja wygrywa; dopisz
jednozdaniowe uzasadnienie, bo za trzy tygodnie nikt nie odtworzy, dlaczego kolejność
wygląda tak, a nie inaczej.

### 5.5 Kolejność prac — cztery etapy

Kategoria ryzyka mówi, **jak ostrożnie** pracować. Kolejność wynika z **zależności**, a te
biegną od komponentów do stron. Stąd kolejność „od liści”: najpierw rzeczy, które nic nie
zawierają, potem rzeczy, które je zawierają.

**Etap A — komponenty-liście o najwyższym fan-in.** `Button`, `TextField`, `Card`,
`Avatar`, `Tag`, `AccentLink`. Nie mają w sobie innych komponentów współdzielonych, a używa
ich niemal wszystko. Ostyluj je pierwsze, bo inaczej każda strona da ci ten sam werdykt
(„przyciski wyglądają staro”) siedem razy.

**Etap B — trasy niskiego ryzyka.** `stomp` → `privacy-policy` → `terms-of-service`. To
pierwsza okazja, żeby zobaczyć Etap A w rzeczywistym użyciu. Znalezione tu problemy są
problemami Etapu A, nie tych stron — wracaj do komponentu.

**Etap C — chrome, czyli layout i nawigacja.** `Sidebar`, `Footer`, `BareLayout`,
`ThemeToggle`. Widoczne na wszystkich stronach naraz, więc pomyłka jest natychmiast
widoczna wszędzie — ale właśnie dlatego jest natychmiast zauważalna, a nie utajona. Idą po
Etapie B, bo do tego momentu masz już zaufanie do warstwy komponentów i wiesz, że to, co
widzisz, jest winą chrome’u.

**Etap D — trasy wysokiego ryzyka**, w kolejności: `(marketing)/` → `(auth)/login` →
`(auth)/register` → `(app)/[userId]`. Uzasadnienie kolejności wewnątrz etapu: marketing ma
najniższy koszt funkcjonalny; `login` i `register` dzielą wygląd, więc drugą z nich zrobisz
niemal za darmo po pierwszej; `[userId]` na końcu, bo jest największa i najbardziej korzysta
z tego, że wszystko inne jest już ustabilizowane.

**Kompromis, który tu przyjmujesz:** kolejność „od liści” oznacza, że przez Etap A i część
Etapu B aplikacja jest wizualnie **niespójna** — nowe przyciski na starych stronach. To
wygląda gorzej niż stan wyjściowy i jest to normalne. Alternatywa (strona po stronie,
z dociąganiem komponentów w miarę potrzeb) daje ładniejsze stany pośrednie kosztem
wielokrotnego wracania do tych samych plików i sprzeczek o to, który wariant komponentu jest
ten właściwy. Wybieramy brzydsze stany pośrednie i mniej pracy.

### 5.6 Format artefaktu

Dopisz do `MIGRATION-INVENTORY.md` nową sekcję. Szkielet:

```markdown
## 9. Krok 1 — ryzyko i kolejność

### 9.1 Klasyfikacja tras

| Trasa | P1 | P2 | P3 | P4 (liczba) | P5 | P6 design | Ryzyko | Etap | Uzasadnienie |
|---|---|---|---|---|---|---|---|---|---|

### 9.2 Fan-in komponentów współdzielonych

| Komponent | Liczba miejsc użycia | Etap |
|---|---|---|

### 9.3 Proponowana kolejność wykonania Kroku 4

1. Etap A: …
2. Etap B: …
3. Etap C: …
4. Etap D: …

### 9.4 Trasy zablokowane (brak ekranu w eksporcie designu)

### 9.5 Odchylenia od propozycji z §5.4 planu i ich uzasadnienie
```

Sekcja 9.5 jest ważniejsza, niż wygląda. Każde miejsce, w którym twoja ocena różni się od
propozycji w planie, jest informacją dla osoby, która będzie to reviewować — bez niej
review sprowadza się do „czemu w innej kolejności?”.

### 5.7 Otwarte pytania — teraz jest moment, żeby je zadać

Trzy pytania z końca Części 4 przestają być teoretyczne dokładnie w tym kroku, bo od
odpowiedzi zależy zakres i kolejność:

| Pytanie | Co blokuje | Co zrobić, jeśli brak odpowiedzi |
|---|---|---|
| Docelowe szerokości ekranu | Krok 4 dla **każdej** trasy — stylowanie strony przed ustaleniem breakpointów oznacza jej ponowne stylowanie później | Zapisz założenie („tylko desktop, ≥1280px”) w 9.5 i pracuj na nim jawnie. Nieudokumentowane założenie to nie to samo co udokumentowane |
| Pokrycie tras eksportem designu | Kolumnę P6 i tym samym listę 9.4 | To jedyne z trzech pytań, które **musisz** zamknąć w Kroku 1 — bez niego nie wiesz, czy Krok 4 ma 7 pozycji, czy 4 |
| Los `.mocha` / `.latte` | Krok 5 w całości: jest zadaniem do wykonania albo usunięciem istniejącego rozwiązania — to dwa różne kawałki pracy o różnym rozmiarze | Do czasu odpowiedzi porównuj branche w obu motywach (§5.1). Przy porównaniu na żywo przełączenie motywu kosztuje jedno kliknięcie, więc zakładanie, że motywy zostają, nic nie kosztuje — a odwrotne założenie jest nieodwracalne |

Pytanie o pokrycie designem zamykasz, otwierając `42Hub UIUX design/Forti2Hub.dc.html` w
przeglądarce i przechodząc przez ekrany z listą 8 tras w ręku. To zajmuje kwadrans i jest
jedyną częścią Kroku 1, której nie da się zastąpić żadnym poleceniem.

### 5.8 Czego nie wolno robić w Kroku 1

- **Nie edytuj żadnego pliku aplikacji.** Ani `.tsx`, ani `globals.css`. Zmieniasz wyłącznie
  `MIGRATION-INVENTORY.md`. Krok 1 to nadal planowanie — pierwsza linia kodu należy do
  Kroku 2.
- **Nie „poprawiaj przy okazji” `white/70` w `Sidebar.tsx`.** Wiesz już, gdzie to jest, i
  poprawka zajęłaby minutę. To jest praca Kroku 2 i tam ma trafić — razem z resztą, w jednym
  możliwym do zreviewowania kawałku. Zasada B.
- **Nie ustalaj kolejności pod to, co wydaje się najciekawsze.** Najciekawsza strona jest
  zwykle najbardziej złożona, a złożona strona jako pierwsza oznacza, że uczysz się nowego
  systemu i debugujesz trudny layout jednocześnie.
- **Nie łącz etapów, „bo to małe zmiany”.** Etapy są jednostkami review, nie jednostkami
  wysiłku. Dwa małe, ale niezwiązane etapy w jednym kawałku to kawałek, którego nie da się
  częściowo cofnąć.

### 5.9 Definicja ukończenia Kroku 1

Krok jest ukończony, gdy istnieją wszystkie cztery elementy:

1. `MIGRATION-INVENTORY.md` zawiera sekcję 9 z wypełnioną kolumną `Ryzyko` dla wszystkich
   7 tras — bez pustych pól i bez `?`.
2. Istnieje policzony fan-in dla komponentów współdzielonych, a nie oszacowany.
3. Istnieje ponumerowana kolejność Kroku 4 z przypisaniem każdej trasy i każdego komponentu
   do etapu A–D.
4. Pytanie o pokrycie eksportem designu jest zamknięte, a trasy bez designu są wypisane
   w 9.4 jako zablokowane.

Do tego jeden warunek, który nie kończy się wraz z Krokiem 1, tylko obowiązuje aż do końca
Kroku 4: **branch odniesienia musi pozostać uruchamialny.** Metoda weryfikacji z §5.1 opiera
się na tym, że w dowolnym momencie da się postawić obok siebie stary i nowy wygląd. Nie
wymaga to niczego poza niełamaniem tego branche’a — ale skoro nie ma już screenshotów,
w momencie, w którym przestanie się uruchamiać, punkt odniesienia znika bezpowrotnie.

**Pytania kontrolne** — odpowiedz na piśmie, z pamięci:

- Dlaczego `Button` jest stylowany przed stroną logowania, a nie odwrotnie?
- Trasa `X` ma jedno TAK w P1 i pięć NIE w pozostałych pytaniach. Jakie ma ryzyko i dlaczego
  nie jest to średnia?
- Dlaczego `(app)/stomp` jest dobrym miejscem na pierwszą migrację, a jednocześnie złym
  dowodem na to, że migracja działa?
- Który jeden komponent, ostylowany błędnie, popsuje wygląd wszystkich siedmiu tras naraz —
  i w którym etapie go dotykasz?
- Co konkretnie trzeba by powtórzyć, gdyby odpowiedź na pytanie o breakpointy przyszła
  dopiero po zakończeniu Kroku 4?

Jeśli nie potrafisz odpowiedzieć na pytanie drugie i czwarte, wróć do §5.2 i §5.3 — to na
nich opiera się cała kolejność, a błąd w kolejności jest tym rodzajem błędu, który ujawnia
się dopiero wtedy, gdy jest już drogi do naprawienia.
