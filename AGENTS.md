# Instructions for AI coding assistants

These instructions apply to the entire repository. Read the relevant guides linked from
the root [README](README.md), especially [development](docs/DEVELOPMENT.md),
[architecture](docs/ARCHITECTURE.md), [schema handling](docs/SCHEMAS.md), and the
[roadmap](docs/ROADMAP.md), before making changes that affect those areas.

## Repository invariants

- Keep RailML schema distributions out of Git and out of all packaged artifacts. Schema
  paths under `src/main/resources/railML*/` are intentionally ignored. Never copy a local
  schema set into a tracked path, print its contents into documentation, or include it in
  a patch.
- Do not fetch, publish, or otherwise redistribute licensed schemas or generated schema
  content as part of a task unless the user explicitly authorizes an action and the
  applicable RailML terms permit it. Do not infer distribution rights from repository
  access.
- The caller explicitly selects `RailMLVersion`; the parser intentionally trusts that
  choice. Do not silently add version auto-detection or XSD validation or change stream
  ownership semantics.
- Generated Java sources belong under `target/generated-sources/`. Edit the XJB bindings
  and Maven configuration, not generated output.
- Keep each RailML version in its distinct `ro.apanescu.railml.generated.vXX` package
  namespace. The per-version XJC executions are isolated intentionally.
- Do not commit credentials, private archive URLs, local schema archives, build output,
  or changes the user did not request.

## Working agreement

- Inspect existing behavior and nearby tests before changing code; preserve unrelated
  working-tree changes.
- Keep changes focused and update the documentation when behavior, supported versions,
  setup, or release procedures change.
- Add or update tests for behavior changes. For changes that touch parser behavior, cover
  version selection, errors, and input-stream ownership where relevant.
- Run `mvn clean verify` when the required local schemas are available. Otherwise, state
  clearly that the schema-dependent build could not be run; do not imply that `mvn validate`
  proves compilation or tests pass.
- Check workflow YAML with `actionlint` when available and run `git diff --check`.
- Do not publish a release or enable distribution gates without explicit user approval.
- Report exactly which checks ran and any prerequisites or checks that remain outstanding.
