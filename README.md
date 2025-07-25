# rest-audit-api

## Introduction

**rest-audit-api** is a library that automatically logs (audits) all REST API requests and responses in your Spring Boot application. It helps you easily monitor, trace, and analyze system activities for security, auditing, or user behavior analysis purposes.

## Key Features

- Detailed logging: method, URL, headers, body, status code, processing time, timestamp, etc.
- Flexible auditing: annotate at both class and method levels.
- Easy configuration via properties.
- Default log sink to Kafka, easily extendable to file, database, message queue, etc.
- Simple integration with just one annotation.

## Installation

Add to your `pom.xml`:

```xml
<dependency>
    <groupId>com.quangbs</groupId>
    <artifactId>rest-audit-api</artifactId>
    <version>1.0.0</version>
</dependency>
```

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

By default, the library sends logs to Kafka. You need to configure Kafka parameters (topic, bootstrap servers, etc.) according to your application.

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

## Requirements

- Spring Boot application (Java 17+)
- Kafka configured if using the default sink, or implement your own custom sink

## Contribution & Contact

For feedback, bug reports, or feature requests, please visit [github.com/quangbs/rest-audit-api](#) or contact the author directly.

---

If you need more examples for custom sinks (e.g., database, message queue), feel free to reach out for support!
