## Deployment Manual — hw2_tokenization

Этот документ описывает, как локально запустить задание **hw2_tokenization** и получить файлы с токенами и леммами на 
основе HTML‑страниц из `hw1_crawler`.

---

### 1. Требования

- **Операционная система**: Windows, Linux или macOS.
- **Java**: JDK **17** (Gradle настроен на использование Java 17).
- **Git**: для клонирования репозитория.
- **Интернет‑подключение**: нужно только на этапе работы `hw1_crawler` (скачивание страниц).

Gradle Wrapper (`gradlew` / `gradlew.bat`) уже лежит в репозитории, отдельно устанавливать Gradle не требуется.

---

### 2. Клонирование репозитория

1. Откройте терминал / PowerShell.
2. Выполните:

```bash
git clone https://github.com/EgorCharntsev/information-search.git

cd InformationSearch
```

(Если проект уже скачан, просто перейдите в корень проекта, где лежат файлы `settings.gradle.kts` и папки `hw1_crawler`, `hw2_tokenization`.)

---

### 3. Предварительный шаг: подготовка HTML‑страниц (hw1_crawler)

Модуль `hw2_tokenization` работает **поверх результата hw1_crawler**. Перед запуском токенизации убедитесь, что:

- выполнен краулер `hw1_crawler`;
- в директории `hw1_crawler/output/pages` — лежат HTML‑страницы (`*.html`).

Если страниц нет, сначала выполните инструкции из `hw1_crawler/DeploymentManual.md` и запустите:

```bash
.\gradlew.bat :hw1_crawler:run    # Windows
# или
./gradlew :hw1_crawler:run        # Linux/macOS
```

После этого в `hw1_crawler/output/pages` должно быть не менее 100 HTML‑файлов.

---

### 4. Структура модуля hw2_tokenization

Основные файлы и директории:

- `hw2_tokenization/src/main/java/ru/kpfu/itis/charntsev/tokenization/Main.java` — точка входа в программу токенизации.
- `hw2_tokenization/src/main/java/ru/kpfu/itis/charntsev/tokenization/core/*` — основной конвейер токенизации и модель результата.
- `hw2_tokenization/src/main/java/ru/kpfu/itis/charntsev/tokenization/nlp/*` — нормализация токенов и лемматизация (морфологический анализ).
- `hw2_tokenization/src/main/java/ru/kpfu/itis/charntsev/tokenization/html/*` — извлечение текста из HTML.
- `hw2_tokenization/src/main/java/ru/kpfu/itis/charntsev/tokenization/io/*` — запись результатов в файлы.
- `hw2_tokenization/output` — директория для результатов работы токенизатора:
  - `hw2_tokenization/output/tokens.txt` — список уникальных токенов;
  - `hw2_tokenization/output/lemmas.txt` — леммы и их токены.

---

### 5. Сборка проекта

На Windows (PowerShell / cmd):

```bash
.\gradlew.bat :hw2_tokenization:build
```

На Linux/macOS:

```bash
./gradlew :hw2_tokenization:build
```

При первом запуске Gradle скачает зависимости (включая библиотеку AOT для русской морфологии), затем соберёт модуль `hw2_tokenization`.

---

### 6. Запуск токенизации и лемматизации

#### Через Gradle

На Windows:

```bash
.\gradlew.bat :hw2_tokenization:run
```

На Linux/macOS:

```bash
./gradlew :hw2_tokenization:run
```

Gradle запустит `ru.kpfu.itis.charntsev.tokenization.Main`.  
Код ожидает, что HTML‑страницы лежат в директории `hw1_crawler/output/pages` (относительно корня репозитория), и записывает результаты в `hw2_tokenization/output`.

---

### 7. Где смотреть результат

После успешного выполнения задачи в модуле `hw2_tokenization` появится директория `output` со следующими файлами:

- `hw2_tokenization/output/tokens.txt`  
  Каждая строка содержит один токен:

  ```text
  информатика
  информация
  лесами
  ...
  ```

- `hw2_tokenization/output/lemmas.txt`  
  Каждая строка имеет формат:

  ```text
  <лемма><пробел><токен1><пробел><токен2> ... <пробел><токенN>
  ```

  Пример:

  ```text
  лес лес леса лесу лесами лесов
  информатика информатика информатики информатике
  ```

---
