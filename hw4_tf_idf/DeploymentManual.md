## Deployment Manual - hw4_tf_idf

Модуль `hw4_tf_idf` рассчитывает `tf`, `idf` и `tf-idf` по документам, скачанным в `hw1_crawler`.

Входные данные:
- `hw1_crawler/output/pages/*.html`

Выходные данные:
- `hw4_tf_idf/output/terms/<doc>.txt`
- `hw4_tf_idf/output/lemmas/<doc>.txt`

Каждый файл соответствует одному HTML-документу из `hw1_crawler/output/pages`.

Формат строки:

```text
<термин или лемма> <idf> <tf-idf>
```

Запуск на Windows:

```bash
.\gradlew.bat :hw4_tf_idf:run
```

Запуск на Linux/macOS:

```bash
./gradlew :hw4_tf_idf:run
```
