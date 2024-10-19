# TelegramBot mit OpenAI (ChatGPT) Integration

Dies ist ein Telegram-Bot, der mit der OpenAI (ChatGPT) API integriert ist. Der Bot kann auf Nachrichten antworten, indem er die OpenAI API aufruft und die Antworten an die Benutzer weiterleitet.

## Voraussetzungen

Bevor du beginnst, stelle sicher, dass du die folgenden Voraussetzungen erfüllst:

- **Java**: Installiere [Java 17+](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) (oder höher).
- **Maven**: Installiere [Maven](https://maven.apache.org/install.html), um das Projekt zu verwalten und zu bauen.
- **Telegram-Bot**: Du benötigst einen Telegram-Bot und dessen Access-Token.
- **OpenAI API-Schlüssel**: Du benötigst einen Access-Token von OpenAI (ChatGPT).

## Schritt-für-Schritt Anleitung

### 1. Telegram-Bot erstellen

Um einen Telegram-Bot zu erstellen, folge der offiziellen Anleitung von Telegram:
- Öffne den [BotFather](https://core.telegram.org/bots#botfather) in Telegram.
- Verwende den Befehl `/newbot`, um einen neuen Bot zu erstellen.
- Folge den Anweisungen, um einen **Benutzernamen** für deinen Bot festzulegen.
- Nach der Erstellung erhältst du den **Access-Token** für deinen Bot. Bewahre diesen sicher auf.

Für weitere Details, siehe die offizielle Dokumentation von Telegram: [Telegram Bots: An introduction for developers](https://core.telegram.org/bots).

### 2. OpenAI API-Schlüssel (ChatGPT)

Um die OpenAI API verwenden zu können, musst du einen API-Schlüssel generieren:

- Gehe zu deinem OpenAI-Dashboard: [OpenAI API Keys](https://platform.openai.com/account/api-keys)
- Erstelle einen neuen API-Schlüssel und bewahre ihn sicher auf.

### 3. Konfiguration (config.properties)

Erstelle eine `config.properties`-Datei im Hauptverzeichnis des Projekts und füge die folgenden Konfigurationen hinzu:

```properties
# Telegram-Bot Konfiguration
BOT_NAME=YourTelegramBotUsername
TELEGRAM_ACCESS_KEY=your-telegram-access-token

# OpenAI API-Schlüssel
OPENAI_API_KEY=your-openai-access-token

# Whitelist der Benutzer-IDs, die Zugriff auf den Bot haben
WHITELIST_USER_IDS=<userId>,<userId>
```

## 4. Projekt bauen und ausführen

### 4.1. Repository klonen

Klonen das Repository lokal:

```bash
git clone https://github.com/besimshala03/TelegramBot.git
cd TelegramBot
```

### 4.2. Abhängigkeiten installieren
```bash
mvn clean install
```
### 4.3.  Bot starten
```bash
mvn exec:java -Dexec.mainClass="org.example.Main"
```

## 5. Den Bot verwenden

Nachdem der Bot erfolgreich gestartet ist, kannst du ihn in Telegram finden und mit ihm interagieren. Nur die Benutzer, deren IDs in der Whitelist (WHITELIST_USER_IDS) enthalten sind, können den Bot verwenden.




