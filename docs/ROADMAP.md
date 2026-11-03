# Roadmap and plans

This roadmap records project direction, prerequisites, and completion criteria. It is not
a release promise or a fixed schedule. Status reflects the repository configuration, not
the legal or distribution readiness of a release.

## Current status

| Area | Status | Notes |
|---|---|---|
| Version-specific JAXB generation | Implemented | RailML 3.2-SR4 only; 3.4-RC support removed pending release and license clarification |
| Explicit-version parser API | Implemented | Caller selects a version; parser returns the corresponding generated root object |
| Parser tests and Javadocs | Implemented | Parser tests cover both input forms, explicit-version behavior, error cases, resource ownership, and disabled external entities |
| GitHub Actions workflows | Implemented, configuration-dependent | Maven metadata validation runs on Linux and Windows; full CI needs schema inputs obtained under RailML's registration and license conditions |
| Dependency update automation | Implemented | Dependabot checks Maven and GitHub Actions dependencies weekly; updates still require review and schema-enabled verification |
| Public source-code license | Blocked / undecided | No license has been selected; do not assume public reuse or redistribution is authorized |
| Generated-class distribution approval | Blocked / undecided | Confirm applicable RailML terms before distributing generated models |
| Public Maven publication | Not implemented | Current release workflow attaches JARs to GitHub Releases only |

## Planned work

### P0 — Establish a release decision

**Prerequisites:** Project owner review of code ownership and RailML terms.

**Plan:** Select and add a license for original project code; document permitted uses of
schema-derived output; determine which versions may be distributed and used productively.
Only after that decision should the release approval variable be enabled.

**Done when:** Licensing decisions are recorded in repository documentation, required
license/notice files are present, and release gates reflect the approved scope.

**Research note:** RailML's current pages identify RailML 3.2 as restricted CC BY-NC-ND
4.0, require registration before schema downloads, state that certification is required
for productive use, and require RailML.org attribution in software and descriptions.
Whether distributing XJC-generated bindings is permitted under these terms has not been
confirmed. See [Schema setup and licensing](SCHEMAS.md) and its official sources.

### P1 — Make full CI reproducible on GitHub

**Prerequisites:** A compliant, stable source for the complete RailML 3.2-SR4 schema
tree. RailML's site requires acceptance of its terms and registration before schema
download. The currently configured CI path uses a private archive; direct public URLs
exist, but should only be used by automation if the required registration and terms are
satisfied.

**Plan:** Configure the documented CI variable and secrets, run the full workflow on a
pull request or manual dispatch, and resolve failures in schema restoration, XJC,
compilation, tests, or Javadocs.

**Done when:** A GitHub Actions run reports XJC generation, compilation, tests, and
Javadocs passing using the complete, correct 3.2-SR4 schema set obtained under the
applicable terms, and no schema files are included in build artifacts or uploaded.

### P2 — Define stable consumer distribution

**Prerequisites:** P0 distribution decision.

**Plan:** Decide whether consumers should obtain releases from GitHub Releases or a Maven
repository. If Maven publication is desired, define repository credentials, protected
publishing permissions, signing/verification requirements if applicable, and a release
versioning policy before implementing deployment.

**Done when:** A consumer can follow one documented installation route, and release
artifacts and metadata match that route.

### P3 — Expand parser behavior coverage — complete

**Plan:** Add focused tests for malformed XML, unexpected root elements, null arguments,
incorrect typed access, and external-entity/DTD handling. Add representative minimal
fixtures for each version only where their licensing permits.

**Done when:** Public parser contracts and error behavior are covered without including
licensed schema bundles or unsuitable real-world data fixtures. Focused tests now cover
both file and stream parsing, trusted caller-selected versions, malformed XML, unexpected
roots, null arguments, incorrect typed access, and rejection of unresolved external
entities.

**Design constraint:** Explicit caller-selected version remains the default and current
contract. Automatic namespace-based version detection is not planned unless separately
requested; if ever added, it should be an explicit API with documented ambiguity/error
behavior, not a silent change to `parse`.

### P4 — Maintain version support deliberately

**Plan:** For each proposed RailML version, track its schema availability, maturity,
license/redistribution terms, compatibility decisions, parser test coverage, and CI
archive availability. Keep generated packages and XJC executions isolated per version.
Do not restore 3.4-RC support until an official release is available and its use and
distribution terms are clarified.

**Done when:** The version is explicitly listed in `RailMLVersion`, its API/package and
schema prerequisites are documented, tests pass, and licensing/use limitations are
visible to consumers.

### P5 — Improve release ergonomics — checksums and tag validation complete

**Plan:** Keep a concise release checklist. Consider Maven repository publishing only
after P2 is decided.

**Done when:** Each release has reproducible source/version metadata, verified JAR
contents, and a documented consumer-facing installation instruction. The release
workflow now attaches a SHA-256 manifest for both JARs and checks that schema and sample
files are excluded. It also rejects tags that do not match `vMAJOR.MINOR.PATCH` before
schema restoration or building, and verifies that the binary and Javadoc JARs contain
their expected model/API entries. The Maven Wrapper pins Maven 3.10.0 and verifies its
distribution checksum for consistent local and CI builds. Schema-enabled CI and the
release workflow share `scripts/verify-artifacts.sh` for JAR content checks.

## Plan maintenance

Update this roadmap when a prerequisite is met, a plan changes, or a feature is completed.
Use pull requests to record scope and acceptance criteria. Do not mark a release-ready
milestone complete based solely on a successful local build; required legal approvals and
GitHub configuration are separate prerequisites.
