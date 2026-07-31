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
