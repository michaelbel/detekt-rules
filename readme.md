# Detekt Rules

[![Maven Central](https://img.shields.io/maven-central/v/io.github.michaelbel/detekt-rules.svg?style=for-the-badge)](https://central.sonatype.com/artifact/io.github.michaelbel/detekt-rules)

Набор кастомных правил для Detekt.

## Как подключить

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    detektPlugins("io.github.michaelbel:detekt-rules:1.2.0")
}
```

```yaml
michaelbel:
  active: true
  ComposableFileOptIn:
    active: true
  ConstrainAsOperatorOrder:
    active: true
  ConstraintLayoutRefsPostfix:
    active: true
  MissingTransactionOnRelation:
    active: true
  ModifierPaddingArgumentOrder:
    active: true
  MultipleSerializableApiModels:
    active: true
  NoSpaceBeforeInheritanceColon:
    active: true
  PaddingValuesSymmetry:
    active: true
  SizeModifierWithConstrainAs:
    active: true
  SnackbarDismissOutsideLaunch:
    active: true
  TextAlignInTextStyle:
    active: true
  UseArrangementSpacedBy:
    active: true
  UseLastIndexInsteadOfSizeMinusOne:
    active: true
  UseParentHorizontalPadding:
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
| `ConstraintLayoutRefsPostfix` | Проверяет, что переменные, созданные через `createRef`/`createRefs`, имеют постфикс `Ref`. | 1.2.0 |
| `MissingTransactionOnRelation` | Проверяет, что методы в `@Dao`-интерфейсах, возвращающие Pojo-типы, аннотированы `@Transaction`. | 1.2.0 |
| `MultipleSerializableApiModels` | Проверяет, что файл содержит не более одной API-модели, аннотированной `@Serializable` и `@SerialName`. | 1.2.0 |
| `SizeModifierWithConstrainAs` | Проверяет, что модификаторы размера (`size`, `width`, `height` и др.) не используются вместе с `constrainAs` — размер должен задаваться внутри блока через Dimension API. | 1.2.0 |
| `SnackbarDismissOutsideLaunch` | Проверяет, что `currentSnackbarData?.dismiss()` вызывается до запуска корутины `scope.launch`, а не внутри неё. | 1.2.0 |
| `UseArrangementSpacedBy` | Проверяет, что одинаковые отступы между дочерними элементами `Row`/`Column` через `Spacer` заменяются на `Arrangement.spacedBy()`. | 1.2.0 |
| `UseParentHorizontalPadding` | Проверяет, что одинаковые крайние отступы у первого и последнего дочерних элементов `Row`/`Column` переносятся в родительский контейнер как `horizontal`/`vertical` padding. | 1.2.0 |
