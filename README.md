# GmailCSVExporter
Export [self](https://github.com/yaronshemesh/GmailCSVExporter/blob/master/src/main/java/GmailMessageToCsv.java#L58) gmail messages to csv
>Currently only Subject, Form, To, Date and a Message snippet are supported

### Setup:
1. Java 8
2. [Gradle 2.3](https://gradle.org/install/)
3. Gmail account (API enabled)
4. Create [credentials.json](https://console.developers.google.com/apis/credentials) and place it under [resources](https://github.com/yaronshemesh/GmailCSVExporter/blob/master/src/main/resources/credentials.json)

### Usage:
1. Change the gmail query in build.gradle [(first arg)](https://github.com/yaronshemesh/GmailCSVExporter/blob/master/build.gradle#L20)
2. Change csv file name [(second arg)](https://github.com/yaronshemesh/GmailCSVExporter/blob/master/build.gradle#L20)
3. Run gradle task (The first time you run, it will prompt you to authorize access)
```
gradle -q run
```
4. Delete tokens folder to change user or scope
