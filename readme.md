# detekt-rules

[![Maven Central](https://img.shields.io/maven-central/v/io.github.michaelbel/detekt-rules.svg)](https://central.sonatype.com/artifact/io.github.michaelbel/detekt-rules)

Набор кастомных правил для `detekt`.

## Список правил

| Rule | Description |
| --- | --- |
| `ComposableFileOptIn` | Проверяет, что экспериментальные аннотации у `@Composable` объявляются только на уровне файла через `@file:OptIn(...)`. |
| `ModifierPaddingArgumentOrder` | Проверяет порядок именованных аргументов в Compose padding API. |
| `NoSpaceBeforeInheritanceColon` | Проверяет, что в объявлениях наследования и делегации перед `:` не ставится пробел. |
| `PaddingValuesSymmetry` | Проверяет симметричные значения в Compose padding API и предлагает сократить запись до `all`, `horizontal` и `vertical`. |
| `TextAlignInTextStyle` | Проверяет, что в Compose `Text` выравнивание задается внутри `style`, а не отдельным аргументом `textAlign`. |

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
