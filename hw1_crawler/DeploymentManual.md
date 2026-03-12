## Deployment Manual — hw1_crawler

Этот документ описывает, как локально запустить задание **hw1_crawler** и получить результат работы краулера.

---

### 1. Требования

- **Операционная система**: Windows, Linux или macOS.
- **Java**: JDK **17** (Gradle настроен на использование Java 17 через toolchain).
- **Git**: для клонирования репозитория.
- **Интернет‑подключение**: краулер скачивает страницы с веб‑сайтов.

Gradle Wrapper (`gradlew` / `gradlew.bat`) уже лежит в репозитории, отдельно устанавливать Gradle не требуется.

---

### 2. Клонирование репозитория

1. Откройте терминал/PowerShell.
2. Выполните:

```bash
git clone https://github.com/EgorCharntsev/information-search.git

cd InformationSearch
```

(Если проект уже скачан, просто перейдите в корень проекта, где лежат файлы `settings.gradle.kts` и папка `hw1_crawler`.)

---

### 3. Структура модуля hw1_crawler

Основные файлы и директории:

- `hw1_crawler/src/main/java/ru/kpfu/itis/charntsev/crawler/Main.java` — точка входа в программу.
- `hw1_crawler/src/main/resources/seeds.txt` — файл со стартовыми URL для обхода.
- `hw1_crawler/output` — директория для результатов работы краулера:
  - `hw1_crawler/output/pages` — сохранённые HTML‑страницы.
  - `hw1_crawler/output/index.txt` — список сохранённых файлов: `имя_файла\tURL`.

---

### 4. Сборка проекта

На Windows (PowerShell / cmd):

```bash
.\gradlew.bat :hw1_crawler:build
```

На Linux/macOS:

```bash
./gradlew :hw1_crawler:build
```

При первом запуске Gradle скачает зависимости, затем соберёт модуль `hw1_crawler`.

---

### 5. Настройка параметров запуска (опционально)

Конфигурация краулера задаётся через:

- **переменные окружения** (например, `CRAWL_LIMIT`, `CRAWL_LANG`, `CRAWL_OUT_DIR`, `CRAWL_SEEDS`, `CRAWL_OVERWRITE` и др.);
- **аргументы командной строки**.

По умолчанию используются значения из `CrawlConfig.builderWithDefaults()`:

- лимит страниц: `100`;
- язык: `ru`;
- минимальный объём текста: `200` символов;
- выходная директория: `hw1_crawler/output`.

Этот шаг можно пропустить.

---

### 6. Запуск краулера

#### С помощью Gradle

На Windows:

```bash
.\gradlew.bat :hw1_crawler:run
```

На Linux/macOS:

```bash
./gradlew :hw1_crawler:run
```

Gradle сам найдёт класс `Main` в модуле `hw1_crawler` и запустит метод `public static void main`.

---

### 7. Где смотреть результат

После успешного запуска в консоли появятся строки вида:

- `Готово. Скачано страниц: <N>`
- `Вывод: <ПУТЬ_К_ДИРЕКТОРИИ>`
- `index.txt: <ПОЛНЫЙ_ПУТЬ_К_ФАЙЛУ>`

По умолчанию:

- страницы лежат в `hw1_crawler/output/pages`;
- список скачанных страниц в `hw1_crawler/output/index.txt`, где каждая строка имеет формат:

```text
00001.html    https://пример.сайт/страница
```

---
