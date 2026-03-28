## Deployment Manual - hw5_search_engine

Модуль `hw5_search_engine` представляет собой веб-приложение с поиском по документам на основе векторной модели и TF-IDF.

### Что делает модуль

- загружает векторы документов из `hw4_tf_idf/output/lemmas`
- принимает пользовательский запрос
- лемматизирует термины запроса
- вычисляет косинусное сходство между запросом и документами
- ранжирует документы по score
- показывает top 10 результатов в web-интерфейсе

### Требования

- JDK 17
- Apache Tomcat 10+
- должны быть готовы результаты предыдущих заданий:
  - `hw1_crawler/output/index.txt`
  - `hw4_tf_idf/output/lemmas/*.txt`

### Сборка артефакта

Модуль собирается как WAR:

```bash
.\gradlew.bat :hw5_search_engine:war
```

Результат:

```text
hw5_search_engine/build/libs/hw5_search_engine.war
```

### Настройка Tomcat

Сервлет читает пути к данным из system properties.

Рекомендуемые VM options для Tomcat:

```text
-Dsearch.projectRoot=D:\Projects\JavaProjects\InformationSearch
```

При необходимости можно указать пути явно:

```text
-Dsearch.tfidf.dir=D:\Projects\JavaProjects\InformationSearch\hw4_tf_idf\output\lemmas
-Dsearch.index.path=D:\Projects\JavaProjects\InformationSearch\hw1_crawler\output\index.txt
-Dsearch.topLimit=10
```

### Настройка в IntelliJ IDEA + Tomcat

1. Выполни `Reload Gradle Project`.
2. Открой `File -> Project Structure -> Artifacts`.
3. Создай артефакт `Web Application: Exploded -> From Modules`.
4. Выбери модуль `hw5_search_engine`.
5. Открой `Run -> Edit Configurations`.
6. Создай конфигурацию `Tomcat Server -> Local`.
7. Укажи установленный Apache Tomcat.
8. На вкладке `Deployment` добавь артефакт `hw5_search_engine:war exploded`.
9. Укажи `Application context`, например `/hw5_search_engine`.
10. На вкладке `Server` в `VM options` добавь `-Dsearch.projectRoot=...`.

### Открытие в браузере

Если context path равен `/hw5_search_engine`, открой:

```text
http://localhost:8080/hw5_search_engine/search
```

Если context path равен `/`, открой:

```text
http://localhost:8080/search
```
