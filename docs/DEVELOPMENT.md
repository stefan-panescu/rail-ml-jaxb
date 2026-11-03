# Development workflow

## Prerequisites

- Java Development Kit 17 or newer.
- Network access to download the Maven distribution and project dependencies.
- The RailML 3.2-SR4 schema distribution, obtained under its applicable terms and
  placed at the path described in [Schema setup and licensing](SCHEMAS.md).
- Optional: `actionlint` for validating GitHub Actions workflow files.

Use `./mvnw` on Linux/macOS, or `.\mvnw.cmd` in Windows PowerShell or Command Prompt.
The wrapper pins Maven 3.10.0, so a separate Maven installation is not needed.

## First local build

After obtaining and placing the schema sets:

```shell
./mvnw clean verify
```

This cleans prior output, generates the JAXB sources, compiles code, runs tests, and
generates the API Javadocs. `verify` fails if Javadoc errors or warnings are detected.
Build outputs go under `target/` and are ignored by Git.

To install the artifact in the local Maven repository for use by another local project:

```shell
./mvnw install
```

To build a non-snapshot version locally (for example, to check release version
interpolation):

```shell
./mvnw -Drevision=0.1.0 clean verify
```

## Typical change flow

1. Start from an up-to-date `master` and create a focused feature or fix branch.
2. Read the relevant API, architecture, and schema documentation before changing the
   parser, generated model setup, or version support.
3. Implement the smallest complete change. Add or update tests and documentation when
   behavior, supported versions, setup, CI, or release procedure changes.
4. Run `./mvnw clean verify` with the local schema inputs. For workflow changes, run
   `actionlint` if available. Also run `git diff --check`.
5. Review the diff and status. Confirm no schema files, credentials, generated build
   output, or unrelated edits are included.
6. Commit with a concise message and push the branch. Open a pull request for review and
   merge only after the required checks pass.

Do not commit to `master` or publish releases as part of routine development unless
specifically requested and authorized.

## Useful commands

| Command | Purpose |
|---|---|
| `./mvnw validate` | Validate Maven project metadata; does not compile or run tests |
| `./mvnw clean test` | Generate/compile sources and run tests |
| `./mvnw clean verify` | Full local verification, including Javadocs |
| `./mvnw -Drevision=0.1.0 clean verify` | Verify a release-style version |
| `./mvnw install` | Install the built artifact in the local Maven repository |
| `git diff --check` | Detect whitespace errors in the working diff |
| `git check-ignore -v src/main/resources/railML-3-2-SR4/source/schema/railml3.xsd` | Confirm a local schema file is ignored |

On Windows, substitute `.\mvnw.cmd` for `./mvnw` in the Maven commands above.

The schema-dependent Maven commands require the licensed local schema sets. Without those
inputs, `./mvnw validate` can still check project metadata, but it does not demonstrate that
XJC generation, compilation, tests, or Javadocs succeed.

## Tests and fixtures

Parser tests are under `src/test/java/ro/apanescu/railml/parser`, with XML fixtures under
`src/test/resources`. They exercise explicit
version selection, typed generated roots, representative 3.2 sample data, and the
caller-owned `InputStream` contract. Keep tests deterministic and avoid adding complete
licensed schema distributions or large real-world RailML datasets as fixtures.

## Build outputs

The Maven build writes generated JAXB Java sources to `target/generated-sources/jaxb/`,
classes and JARs to `target/`, and Javadocs to `target/reports/apidocs/`. Do not edit or
commit generated output. The release creates a versioned library JAR and Javadoc JAR;
their contents must be checked for excluded schemas before distribution.
