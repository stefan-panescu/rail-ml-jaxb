# CI/CD and releases

## Workflows

The repository defines two GitHub Actions workflows:

| Workflow | Trigger | Behavior |
|---|---|---|
| `CI` (`.github/workflows/ci.yml`) | Push to `master`, pull request, or manual dispatch | Always runs Maven metadata validation; runs schema-dependent `clean verify` only when explicitly enabled |
| `Release` (`.github/workflows/release.yml`) | A GitHub Release is published | Attaches the binary and Javadoc JARs plus a SHA-256 manifest only when distribution approval is enabled and the protected environment allows the job |

The CI workflow runs with Java 17 and Maven caching. A green run with full checks disabled
means only `mvn validate` passed; it does not mean sources compiled, tests passed, or
Javadocs were generated. The job summary makes this skipped state explicit.

## Enabling full CI

In the repository's **Settings → Secrets and variables → Actions**, configure:

| Name | Type | Value |
|---|---|---|
| `RAILML_CI_ENABLED` | Repository variable | Exact text `true` |
| `RAILML_SCHEMA_ARCHIVE_URL` | Repository secret | URL of the private ZIP containing both schema distributions |
| `RAILML_SCHEMA_ARCHIVE_TOKEN` | Repository secret | Read-only bearer token accepted by the archive host |

The archive layout and local schema terms are detailed in
[Schema setup and licensing](SCHEMAS.md). If the variable is not exactly `true`, full
generation, compilation, tests, and Javadoc checks are skipped. When enabled, missing
secrets or a malformed archive fail the job rather than silently falling back to partial
validation.

## Release controls

Before publishing a release:

1. Confirm and document the project's code license.
2. Confirm that use and distribution of generated classes are allowed by the relevant
   RailML terms. Check each schema version independently; 3.4-RC is a development release.
3. Ensure full CI is enabled and has passed with the licensed schemas.
4. Set repository variable `RAILML_DISTRIBUTION_APPROVED=true` only after the distribution
   decision has been made.
5. Configure a protected GitHub environment named `release` with required reviewers and
   any applicable deployment protection rules.
6. Create a version tag and publish the corresponding GitHub Release.

The release workflow runs only for a published release and only if the approval variable
is exactly `true`. It waits for the `release` environment's protection rules. It restores
the schema archive, runs `mvn clean verify` with the leading `v` removed from the GitHub
release tag (for example, tag `v0.1.0` builds Maven version `0.1.0`), checks that the
binary and Javadoc JARs exist and do not contain schema files, then attaches both JARs to
the GitHub Release along with `SHA256SUMS`. The checksum manifest is generated from the
built JARs on the runner; it is an integrity aid for downloads, not a signature or proof
of publisher identity.

`RAILML_DISTRIBUTION_APPROVED` is a repository-controlled gate, not evidence of legal
approval. Do not set it merely to make the workflow run. The workflow does not publish to
Maven Central or another Maven repository.

After downloading the JARs and `SHA256SUMS`, verify the checksums from a shell in the
directory containing all three files:

```shell
sha256sum --check SHA256SUMS
```

On Windows PowerShell, compare the manifest's expected digest against the output of
`Get-FileHash .\rail-ml-jaxb-<version>.jar -Algorithm SHA256` and
`Get-FileHash .\rail-ml-jaxb-<version>-javadoc.jar -Algorithm SHA256`.

## Troubleshooting

| Symptom | Likely cause |
|---|---|
| CI reports full checks skipped | `RAILML_CI_ENABLED` is unset or not exactly `true` |
| Schema restore fails | URL/token incorrect, archive host rejects the bearer token, ZIP layout is wrong, or one version's `railml3.xsd` is missing |
| Local XJC cannot find an included XSD | Schema folders were flattened, renamed, incomplete, or placed at the wrong path |
| Release job is skipped | `RAILML_DISTRIBUTION_APPROVED` is not exactly `true` |
| Release job waits for approval | The `release` environment requires reviewer approval |
| Release build cannot find its artifact | Tag/version is inconsistent with the release workflow's version interpolation or the build failed |

Inspect the workflow run summary and failed step logs. Never paste secret values into an
issue, PR, workflow log, or support request.
