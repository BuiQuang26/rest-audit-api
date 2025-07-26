# rest-audit-api

[![JitPack](https://jitpack.io/v/BuiQuang26/rest-audit-api.svg)](https://jitpack.io/#BuiQuang26/rest-audit-api)

## Introduction

**rest-audit-api** is a library that automatically **logs (audits) all REST API** requests and responses in your **Spring Boot application**. It helps you easily monitor, trace, and analyze system activities for security, auditing, or user behavior analysis purposes.

## Table of Contents

- [Key Features](#key-features)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Usage](#usage)
- [Kafka Configuration (if using default sink)](#4-kafka-configuration-if-using-default-sink)
- [Extension: Custom Audit Sink](#extension-custom-audit-sink)
- [Troubleshooting](#troubleshooting)
- [Requirements](#requirements)
- [Contribution & Contact](#contribution--contact)
- [License](#license)

## Key Features

- Detailed logging: method, URL, headers, body, status code, processing time, timestamp, etc.
- Flexible auditing: annotate at both class and method levels.
- Easy configuration via properties.
- Default log sink to Kafka, easily extendable to file, database, message queue, etc.
- Simple integration with just one annotation.

## Installation

You can import this library using [JitPack](https://jitpack.io):

1. Add the JitPack repository to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

2. Add the dependency:

```xml
<dependency>
    <groupId>com.github.BuiQuang26</groupId>
    <artifactId>rest-audit-api</artifactId>
    <version>1.0.0</version> <!-- or latest version/tag -->
</dependency>
```

> **Tip:**  
> To get the latest version, visit [JitPack page for this repo](https://jitpack.io/#quangbs/rest-audit-api) and use the latest tag or commit hash.

## Quick Start

1. Add the dependency via JitPack (see [Installation](#installation)).
2. Enable audit in your main class:
    ```java
    @RestAuditEnable
    @SpringBootApplication
    public class MyApplication {}
    ```
3. Annotate your controller or method with `@RestAudit`.
4. Add minimal config to `application.properties`:
    ```properties
    rest-audit.service-id=demo-service
    rest-audit.sink.kafka.bootstrap-servers=localhost:9092
    rest-audit.sink.kafka.topic=rest-audit-log
    ```
5. Start your app, call any REST API, and check your Kafka topic for logs!

## Usage

### 1. Enable Rest Audit

Add the `@RestAuditEnable` annotation to your main configuration class (usually the one with `@SpringBootApplication`):

```java
import com.quangbs.restaudit.annotions.RestAuditEnable;

@RestAuditEnable
@SpringBootApplication
public class MyApplication {
    // ...
}
```

### 2. Annotate APIs to Audit

You can annotate at the controller or method level using `@RestAudit`:

```java
import com.quangbs.restaudit.annotions.RestAudit;

@RestController
@RequestMapping("/api")
@RestAudit(message = "Audit all APIs in this controller")
public class MyController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }

    @PostMapping("/data")
    @RestAudit(message = "Audit only this API")
    public String postData(@RequestBody String data) {
        return "Received";
    }
}
```

### 3. Configuration Properties

Add to your `application.properties` or `application.yml`:

```properties
# Service identifier (required)
rest-audit.service-id=your-service-id

# Limit the response body length to log (default 10240 bytes ~ 10KB)
rest-audit.response.max-length=10240
```

### 4. Kafka Configuration (if using default sink)

By default, the library sends logs to Kafka.  
You need to configure the following properties in your `application.properties` or `application.yml`:

```properties
# Kafka sink configuration for audit logs
rest-audit.sink.kafka.topic=rest-audit-log
rest-audit.sink.kafka.bootstrap-servers=localhost:9092
rest-audit.sink.kafka.partition-count=1
rest-audit.sink.kafka.replication-factor=1
rest-audit.sink.kafka.client-id=rest-audit-producer
rest-audit.sink.kafka.acks=1
rest-audit.sink.kafka.max-idle-ms=60000
rest-audit.sink.kafka.max-block-ms=60000
```

- `rest-audit.sink.kafka.topic`: Kafka topic to send audit logs to.
- `rest-audit.sink.kafka.bootstrap-servers`: Kafka bootstrap servers.
- `rest-audit.sink.kafka.partition-count`: Number of partitions for the topic.
- `rest-audit.sink.kafka.replication-factor`: Replication factor for the topic.
- `rest-audit.sink.kafka.client-id`: Kafka producer client ID.
- `rest-audit.sink.kafka.acks`: Number of acknowledgments the producer requires.
- `rest-audit.sink.kafka.max-idle-ms`: Maximum idle time for connections.
- `rest-audit.sink.kafka.max-block-ms`: Maximum block time for producer.

> The library will automatically create the topic if it does not exist (with the specified partition and replication settings).

## Extension: Custom Audit Sink

You can implement the `AuditSinkService` interface to log to other systems (file, database, message queue, etc.).

### Example: Log to File

```java
import com.quangbs.restaudit.models.RestAuditData;
import com.quangbs.restaudit.sinks.AuditSinkService;
import org.springframework.stereotype.Service;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class FileAuditSinkService implements AuditSinkService {
    @Override
    public void sendAuditData(RestAuditData auditData) {
        try (FileWriter fw = new FileWriter("audit.log", true)) {
            fw.write(auditData.toString() + System.lineSeparator());
        } catch (IOException e) {
            // Handle file write error
        }
    }
}
```

> **Note:**  
> - There should be only one `AuditSinkService` bean in the context. If you have multiple beans, use `@Primary` for the one you want to use.
> - The library will automatically use your custom sink if you register an `AuditSinkService` bean (thanks to `@ConditionalOnMissingBean`).

## Troubleshooting

- **No logs sent to Kafka:**  
  - Check your Kafka server is running and accessible.
  - Verify `rest-audit.sink.kafka.bootstrap-servers` and `topic` are correct.
  - Check application logs for errors.

- **Custom sink not working:**  
  - Make sure only one `AuditSinkService` bean is present.
  - Use `@Primary` if you have multiple beans.

## Requirements

- Spring Boot application (Java 17+)
- Kafka configured if using the default sink, or implement your own custom sink

## Contribution & Contact

For feedback, bug reports, or feature requests, please visit [github.com/quangbs/rest-audit-api](#) or contact the author directly.

---

If you need more examples for custom sinks (e.g., database, message queue), feel free to reach out for support!

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
