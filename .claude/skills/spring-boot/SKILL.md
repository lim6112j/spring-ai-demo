---
name: spring-boot
description: Build, debug, and maintain Spring Boot applications. Use this skill whenever the user mentions Spring Boot, Spring MVC/WebFlux, REST controllers, application.yml/properties, Maven/Gradle Java services, startup failures, bean wiring errors, dependency conflicts, or asks to add/modify endpoints, services, configuration, tests, or runtime behavior in a Spring Boot project.
---

# Spring Boot Skill

Use this skill to make reliable changes in Spring Boot projects with clear validation.

## What this skill handles
- Adding or changing REST endpoints (`@RestController`, request/response DTOs)
- Service/repository wiring and bean lifecycle issues
- Configuration updates (`application.yml`, profiles, env variables)
- Dependency and version alignment (Maven/Gradle)
- Startup/runtime failures (autoconfiguration, missing beans, binding errors)
- Test updates for controller/service integration behavior

## Operating principles
1. Prefer minimal, targeted changes that solve the reported issue.
2. Keep behavior backward-compatible unless the user asked for breaking changes.
3. Validate with project-native build/test commands before finishing.
4. Explain concrete root cause and fix, not generic Spring theory.

## Standard workflow
1. Identify build tool and project shape.
   - Maven: `pom.xml`
   - Gradle: `build.gradle(.kts)`
2. Reproduce the issue first (compile/test/run command).
3. Locate the failing class/config and related wiring.
4. Apply focused code/config change.
5. Re-run compile/tests; if app issue, run app and verify endpoint/behavior.
6. Report: root cause, changed files, validation command results.

## Build and run commands

### Maven projects
- Compile: `./mvnw -DskipTests compile`
- Test: `./mvnw test`
- Run app: `./mvnw spring-boot:run`

### Gradle projects
- Compile: `./gradlew classes`
- Test: `./gradlew test`
- Run app: `./gradlew bootRun`

Use the wrapper (`mvnw` / `gradlew`) when available for reproducibility.

## Common failure patterns and fixes

### 1) Bean not found / autowiring failure
- Check component scanning boundaries (`@SpringBootApplication` package root)
- Ensure implementation is annotated (`@Service`, `@Repository`, `@Component`)
- Verify conditional annotations/profile guards are satisfied

### 2) Configuration binding errors
- Verify property paths and types in `application.yml`/`application.properties`
- Align `@ConfigurationProperties` prefix and field names
- Confirm environment variable overrides use Spring naming conventions

### 3) Dependency/API mismatch
- Align starters/BOM versions (especially Spring Boot + ecosystem libs)
- Prefer BOM-managed versions over hardcoded transitive versions
- Re-import project and re-run clean compile after version updates

### 4) Endpoint mapping or serialization issues
- Verify request mapping paths/methods and DTO field names
- Check Jackson/Kotlin module usage for Kotlin data classes
- Add/adjust integration tests for request/response contract

## Testing expectations
- For controller changes: add/update `@SpringBootTest` or web-layer tests.
- For service logic changes: add/update unit tests with clear assertions.
- When bugfixing: include at least one regression test when practical.

## Response format to user
When completing work, provide:
1. Root cause
2. What changed (high-level, file-focused)
3. Validation commands run and outcomes
4. Any follow-up action needed from user (if applicable)

## Guardrails
- Do not introduce broad refactors for localized issues.
- Do not change dependency versions unless needed for the fix.
- Do not claim success without running at least compile/build validation.