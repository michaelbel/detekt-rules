# detekt-rules

[![Maven Central](https://img.shields.io/maven-central/v/io.github.michaelbel/detekt-rules.svg)](https://central.sonatype.com/artifact/io.github.michaelbel/detekt-rules)

Набор кастомных правил для `detekt`.

## Что внутри

- `detekt-rules` - основной `ruleset`-jar с `RuleSetProvider`, правилами и тестами
- `sample` - локальный demo-модуль для быстрых экспериментов

## Список правил

| Rule | Description |
| --- | --- |
| `ComposableFileOptIn` | Проверяет, что экспериментальные аннотации у `@Composable` объявляются только на уровне файла через `@file:OptIn(...)`. |
| `ModifierPaddingArgumentOrder` | Проверяет порядок именованных аргументов в Compose padding API. |
| `NoSpaceBeforeInheritanceColon` | Проверяет, что в объявлениях наследования и делегации перед `:` не ставится пробел. |
| `PaddingValuesSymmetry` | Проверяет симметричные значения в Compose padding API и предлагает сократить запись до `all`, `horizontal` и `vertical`. |
| `TextAlignInTextStyle` | Проверяет, что в Compose `Text` выравнивание задается внутри `style`, а не отдельным аргументом `textAlign`. |

## Как подключить в рабочем проекте

Добавить `mavenLocal()` в репозитории:

```kotlin
repositories {
    mavenLocal()
    mavenCentral()
}
```

Подключить ruleset:

```kotlin
dependencies {
    detektPlugins("io.github.michaelbel:detekt-rules:1.0.0")
}
```

Если рабочий проект уже запускал `detekt` с прежней версией артефакта, полезно один раз выполнить:

```bash
./gradlew --refresh-dependencies detekt
```

## Публикация в Maven Central

Проект настроен на публикацию через `Sonatype Central Portal` и совместимый `OSSRH Staging API`.

Перед первым релизом нужно:

1. Верифицировать `namespace` для `groupId`.
   Сейчас в проекте используется `io.github.michaelbel`, значит в Sonatype должен быть подтвержден именно этот namespace.
2. Создать `Portal User Token` в `central.sonatype.com`.
3. Экспортировать приватный GPG-ключ в ASCII armor и положить секреты в `~/.gradle/gradle.properties` или в переменные окружения `ORG_GRADLE_PROJECT_*`.
4. Указать лицензию артефакта через `POM_LICENSE_NAME` и `POM_LICENSE_URL`.

Пример `~/.gradle/gradle.properties`:

```properties
POM_LICENSE_NAME=The Apache License, Version 2.0
POM_LICENSE_URL=https://www.apache.org/licenses/LICENSE-2.0.txt
POM_DEVELOPER_EMAIL=you@example.com

sonatypeUsername=central-token-username
sonatypePassword=central-token-password

signingKeyId=optional
signingKey=-----BEGIN PGP PRIVATE KEY BLOCK-----
...
-----END PGP PRIVATE KEY BLOCK-----
signingPassword=your-key-password
```

Команды:

```bash
# локальная проверка публикации
./gradlew publishToMavenLocal

# snapshot
./gradlew publishToSonatype

# release
./gradlew publishToSonatype closeAndReleaseStagingRepositories
```

Для релиза версия не должна заканчиваться на `-SNAPSHOT`.

## Настройка `detekt.yml`

```yaml
michaelbel:
  active: true
  ComposableFileOptIn:
    active: true
  ModifierPaddingArgumentOrder:
    active: true
  NoSpaceBeforeInheritanceColon:
    active: true
  PaddingValuesSymmetry:
    active: true
  TextAlignInTextStyle:
    active: true
```
