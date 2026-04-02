# Detekt Rules

[![Maven Central](https://img.shields.io/maven-central/v/io.github.michaelbel/detekt-rules.svg?style=for-the-badge)](https://central.sonatype.com/artifact/io.github.michaelbel/detekt-rules)

Набор кастомных правил для Detekt.

## Как подключить

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    detektPlugins("io.github.michaelbel:detekt-rules:1.1.0")
}
```

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
  ConstrainAsOperatorOrder:
    active: true
  UseLastIndexInsteadOfSizeMinusOne:
    active: true
```

## Список правил

| Правило | Описание | Добавлено в |
| --- | --- | --- |
| `ComposableFileOptIn` | Проверяет, что экспериментальные аннотации у `@Composable` объявляются только на уровне файла через `@file:OptIn(...)`. | 1.0.0 |
| `ModifierPaddingArgumentOrder` | Проверяет порядок именованных аргументов в Compose padding API. | 1.0.0 |
| `NoSpaceBeforeInheritanceColon` | Проверяет, что в объявлениях наследования и делегации перед `:` не ставится пробел. | 1.0.0 |
| `PaddingValuesSymmetry` | Проверяет симметричные значения в Compose padding API и предлагает сократить запись до `all`, `horizontal` и `vertical`. | 1.0.0 |
| `TextAlignInTextStyle` | Проверяет, что в Compose `Text` выравнивание задается внутри `style`, а не отдельным аргументом `textAlign`. | 1.0.0 |
| `ConstrainAsOperatorOrder` | Проверяет порядок операторов внутри блока `constrainAs`: `width`, `height`, `start`, `top`, `end`, `bottom`. | 1.1.0 |
| `UseLastIndexInsteadOfSizeMinusOne` | Проверяет, что для обращения к последнему индексу коллекции используется `lastIndex` вместо `size - 1` или `size.minus(1)`. | 1.1.0 |
