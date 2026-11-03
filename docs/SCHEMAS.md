# Schema setup and licensing

## Required local files

Schema distributions are external, licensed build inputs. Obtain each distribution under
its applicable terms, then retain its directory structure and place it in these exact
locations:

```text
src/main/resources/railML-3-2-SR4/source/schema/railml3.xsd
src/main/resources/railML-3-2-SR4/source/schema/common3.xsd
...all included RailML 3.2-SR4 XSDs and subdirectories...

src/main/resources/railML-3-4-RC/source/schema/railml3.xsd
src/main/resources/railML-3-4-RC/source/schema/common3.xsd
...all included RailML 3.4-RC XSDs and subdirectories...
```

The build entry points are `railml3.xsd` in each version's schema folder. The schemas
include other files by relative paths, so do not flatten, rename, or partially copy the
directory tree. The directories are ignored by Git. Never force-add schema files.

Verify a local file is ignored before staging work:

```shell
git check-ignore -v src/main/resources/railML-3-2-SR4/source/schema/railml3.xsd
```

## CI schema archive

When full GitHub Actions builds are enabled, the private ZIP archive must have these
version directories at the ZIP root:

```text
railML-3-2-SR4/
  source/schema/railml3.xsd
  source/schema/...
railML-3-4-RC/
  source/schema/railml3.xsd
  source/schema/...
```

Preserve all relative paths used by XSD includes. Configure a repository secret
`RAILML_SCHEMA_ARCHIVE_URL` and a read-only bearer token in
`RAILML_SCHEMA_ARCHIVE_TOKEN`. The archive host must accept an HTTP
`Authorization: Bearer <token>` header. Keep the URL and token out of source, logs, and
documentation. CI downloads and extracts the archive into its ephemeral runner workspace;
the workflow does not upload the schemas as artifacts.

The CI job checks for each entry-point XSD before copying the two version folders into
their ignored build locations. A missing file, unavailable URL, invalid token, or archive
with the wrong layout should fail the schema-dependent build.

## Exclusions from distribution

Maven excludes `railML*/**` from normal resources and JAR contents. It also excludes
`sample.xml` from the binary JAR. Generated Java model classes are compiled and included;
they are derived from the schema inputs and may themselves be subject to RailML terms.
Both release JARs are checked in the release workflow for schema files before upload.

Do not treat the absence of `.xsd` files in a JAR as proof that distribution is permitted.
Review the applicable RailML terms for both the schema distributions and generated classes
before enabling releases.

## Licensing notes

The project does not select or grant a license for the original Java code. RailML
distributions also have their own licensing and usage requirements. The provided
3.2-SR4 and 3.4-RC source distributions may impose different conditions; 3.4-RC is a
development release and may not be suitable for productive use. Consult the current terms
from RailML and obtain any required registrations, approvals, or permissions.

Do not add schema bundles to GitHub Releases, Maven repositories, container images,
documentation examples, or other deliverables unless the applicable terms expressly allow
that distribution.
