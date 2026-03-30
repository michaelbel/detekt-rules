# detekt-rules

Набор кастомных правил для `detekt`.

## Что внутри

- `detekt-rules` - основной `ruleset`-jar с `RuleSetProvider`, правилами и тестами
- `sample` - локальный demo-модуль для быстрых экспериментов

## Список правил

- `ComposableFileOptIn` - проверяет, что экспериментальные аннотации у `@Composable` объявляются только на уровне файла через `@file:OptIn(...)`.
- `ModifierPaddingArgumentOrder` - проверяет порядок именованных аргументов в Compose padding API.
- `NoSpaceBeforeInheritanceColon` - проверяет, что в объявлениях наследования и делегации перед `:` не ставится пробел.
- `PaddingValuesSymmetry` - проверяет симметричные значения в Compose padding API и предлагает сократить запись до `horizontal` и `vertical`.
- `TextAlignInTextStyle` - проверяет, что в Compose `Text` выравнивание задается внутри `style`, а не отдельным аргументом `textAlign`.

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
    detektPlugins("org.michaelbel:detekt-rules:0.1.2-SNAPSHOT")
}
```

Если рабочий проект уже запускал `detekt` с прежней версией артефакта, полезно один раз выполнить:

```bash
./gradlew --refresh-dependencies detekt
```

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
