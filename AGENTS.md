# Repository Guidelines

- Never commit unrelated files together – stage files selectively and make one focused commit per logical change with a clear, specific message

After **any** change to an existing rule or addition of a new rule, update `version` in `build.gradle.kts` (line 9).

```
<major>.<minor>.<patch>[-alpha-NN]
```

- **During development** (between releases): bump the minor digit by 1 and append `-alpha-01`. Each subsequent change increments only the alpha counter: `-alpha-02`, `-alpha-03`, etc.
- **On release**: drop the alpha suffix. The released version becomes the new baseline (e.g. `1.3.0`).

While the version has an `-alpha-NN` suffix (i.e. between releases):

- **Do not** add new rules to `detekt-rules/src/main/resources/config/config.yml` — this file is the public default config shipped to users; new rules are added there only at release time.
- **Do not** describe new rules in `readme.md` — documentation is updated only on release.

When cutting a release:
1. Add all new rules to `config.yml`.
2. Document all new rules in `readme.md`.
3. Drop the `-alpha-NN` suffix from the version (e.g. `1.3.0-alpha-05` → `1.3.0`).

| Event | Version |
|---|---|
| Current release | `1.2.0` |
| First rule change/addition after release | `1.3.0-alpha-01` |
| Second change | `1.3.0-alpha-02` |
| Third change | `1.3.0-alpha-03` |
| Release | `1.3.0` |
| First change after that release | `1.4.0-alpha-01` |
