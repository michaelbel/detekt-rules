# detekt-rules

Набор кастомных правил для `detekt`.

## Что внутри

- `detekt-rules` - основной `ruleset`-jar с `RuleSetProvider`, правилами и тестами
- `sample` - локальный demo-модуль для быстрых экспериментов

## Текущие правила

### `ModifierPaddingArgumentOrder`
Проверяет порядок именованных аргументов в Compose padding API:

### `PaddingValuesSymmetry`
Проверяет симметричные значения в Compose padding API и предлагает сократить запись до `horizontal` и `vertical`.

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
    detektPlugins("org.michaelbel:detekt-rules:0.1.1-SNAPSHOT")
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
  ModifierPaddingArgumentOrder:
    active: true
  PaddingValuesSymmetry:
    active: true
```