# Detekt Rules

[![Maven Central](https://img.shields.io/maven-central/v/io.github.michaelbel/detekt-rules.svg?style=for-the-badge)](https://central.sonatype.com/artifact/io.github.michaelbel/detekt-rules)

Набор кастомных правил для Detekt.

## Как подключить

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    detektPlugins("io.github.michaelbel:detekt-rules:1.3.0")
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
  LazyListItemsSharedPadding:
    active: true
  PaddingValuesZeroArguments:
    active: true
  SealedClassCanBeInterface:
    active: true
  UseLastIndexInsteadOfSizeMinusOne:
    active: true
  UseParentHorizontalPadding:
    active: true
  VectorIconBooleanNamedArguments:
    active: true
```

## Список правил

| Правило                                                                                                                                                                                           | Описание                                                                                                                                                                     | Добавлено в |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------|
| [`ComposableFileOptIn`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/ComposableFileOptIn.kt)                             | Проверяет, что экспериментальные аннотации у `@Composable` объявляются только на уровне файла через `@file:OptIn(...)`.                                                      | 1.0.0       |
| [`ModifierPaddingArgumentOrder`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/ModifierPaddingArgumentOrder.kt)           | Проверяет порядок именованных аргументов в Compose padding API.                                                                                                              | 1.0.0       |
| [`NoSpaceBeforeInheritanceColon`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/NoSpaceBeforeInheritanceColon.kt)         | Проверяет, что в объявлениях наследования и делегации перед `:` не ставится пробел.                                                                                          | 1.0.0       |
| [`PaddingValuesSymmetry`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/PaddingValuesSymmetry.kt)                         | Проверяет симметричные значения в Compose padding API и предлагает сократить запись до `all`, `horizontal` и `vertical`.                                                     | 1.0.0       |
| [`TextAlignInTextStyle`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/TextAlignInTextStyle.kt)                           | Проверяет, что в Compose `Text` выравнивание задается внутри `style`, а не отдельным аргументом `textAlign`.                                                                 | 1.0.0       |
| [`ConstrainAsOperatorOrder`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/ConstrainAsOperatorOrder.kt)                   | Проверяет порядок операторов внутри блока `constrainAs`: `width`, `height`, `start`, `top`, `end`, `bottom`.                                                                 | 1.1.0       |
| [`UseLastIndexInsteadOfSizeMinusOne`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/UseLastIndexInsteadOfSizeMinusOne.kt) | Проверяет, что для обращения к последнему индексу коллекции используется `lastIndex` вместо `size - 1` или `size.minus(1)`.                                                  | 1.1.0       |
| [`ConstraintLayoutRefsPostfix`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/ConstraintLayoutRefsPostfix.kt)             | Проверяет, что переменные, созданные через `createRef`/`createRefs`, имеют постфикс `Ref`.                                                                                   | 1.2.0       |
| [`MissingTransactionOnRelation`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/MissingTransactionOnRelation.kt)           | Проверяет, что методы в `@Dao`-интерфейсах, возвращающие Pojo-типы, аннотированы `@Transaction`.                                                                             | 1.2.0       |
| [`MultipleSerializableApiModels`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/MultipleSerializableApiModels.kt)         | Проверяет, что файл содержит не более одной API-модели, аннотированной `@Serializable` и `@SerialName`.                                                                      | 1.2.0       |
| [`SizeModifierWithConstrainAs`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/SizeModifierWithConstrainAs.kt)             | Проверяет, что модификаторы размера (`size`, `width`, `height` и др.) не используются вместе с `constrainAs` — размер должен задаваться внутри блока через Dimension API.    | 1.2.0       |
| [`SnackbarDismissOutsideLaunch`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/SnackbarDismissOutsideLaunch.kt)           | Проверяет, что `currentSnackbarData?.dismiss()` вызывается до запуска корутины `scope.launch`, а не внутри неё.                                                              | 1.2.0       |
| [`UseArrangementSpacedBy`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/UseArrangementSpacedBy.kt)                       | Проверяет, что одинаковые отступы между дочерними элементами `Row`/`Column` через `Spacer` заменяются на `Arrangement.spacedBy()`.                                           | 1.2.0       |
| [`UseParentHorizontalPadding`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/UseParentHorizontalPadding.kt)               | Проверяет, что одинаковые крайние отступы у первого и последнего дочерних элементов `Row`/`Column` переносятся в родительский контейнер как `horizontal`/`vertical` padding. | 1.2.0       |
| [`LazyListItemsSharedPadding`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/LazyListItemsSharedPadding.kt)               | Проверяет, что одинаковый `horizontal`-padding на всех элементах `LazyColumn` (или `vertical` у `LazyRow`) выносится в аргумент `contentPadding`.                            | 1.3.0       |
| [`PaddingValuesZeroArguments`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/PaddingValuesZeroArguments.kt)               | Проверяет, что `PaddingValues` с нулевыми аргументами заменяется на `PaddingValues()` без аргументов, так как все отступы по умолчанию равны `0.dp`.                         | 1.3.0       |
| [`SealedClassCanBeInterface`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/SealedClassCanBeInterface.kt)                 | Проверяет, что `sealed class` без параметров конструктора и только с `object`-наследниками заменяется на `sealed interface`.                                                  | 1.3.0       |
| [`VectorIconBooleanNamedArguments`](https://github.com/michaelbel/detekt-rules/blob/main/detekt-rules/src/main/kotlin/org/michaelbel/detektrules/rules/VectorIconBooleanNamedArguments.kt)     | Проверяет, что булевые аргументы в файлах с векторными иконками передаются через именованные параметры.                                                                       | 1.3.0       |
