# Guestbook (Kotlin)

This is a Kotlin conversion of the original Go guestbook application. It provides the same functionality as a vintage 90s guestbook website with:

* Working guestbook functionality  
* A hit counter
* Vintage "Microsoft Internet Explorer" badge
* Absolutely no persistence whatsoever

## Requirements

- Java 11 or higher
- Maven 3.6+

## Build and run the tests

```bash
mvn clean test
```

## Run the application

First compile, then run:
```bash
mvn compile
mvn exec:java
```

Or combine both steps:
```bash
mvn compile exec:java
```

## Create executable jar

```bash
mvn clean package
java -jar target/guestbook-kotlin-1.0.0-jar-with-dependencies.jar
```

The application will start on http://localhost:8080

## Technology Stack

- **Kotlin** - Programming language
- **Ktor** - Web framework
- **kotlinx.html** - HTML DSL for templating
- **Maven** - Build tool

## Key Differences from Go Version

- Uses Ktor web framework instead of net/http
- HTML templating with kotlinx.html DSL instead of Go templates
- Thread-safe in-memory storage using AtomicLong and synchronized collections
- Follows POST-redirect-GET pattern to prevent duplicate form submissions