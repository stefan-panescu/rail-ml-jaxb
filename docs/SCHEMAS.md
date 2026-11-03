# Schema setup and licensing

## Required local files

The only currently supported schema distribution is RailML 3.2-SR4. Obtain it under its
applicable terms, then retain its directory structure and place it in this location:

```text
src/main/resources/railML-3-2-SR4/source/schema/railml3.xsd
src/main/resources/railML-3-2-SR4/source/schema/common3.xsd
...all included RailML 3.2-SR4 XSDs and subdirectories...
```

The build entry point is `railml3.xsd`. Its included schemas use relative paths, so do not
flatten, rename, or partially copy the directory tree. The directory is ignored by Git.
Never force-add schema files.

RailML 3.4-RC support is intentionally removed: the distribution describes itself as a
development release that must not be used productively, and the project's rights to
generate and redistribute bindings have not been clarified. Reconsider adding it only
after an official release is available and RailML.org confirms the applicable terms.

Verify a local file is ignored before staging work:

```shell
git check-ignore -v src/main/resources/railML-3-2-SR4/source/schema/railml3.xsd
```

## CI schema archive

The current workflow uses a private ZIP as its transport mechanism; this is not a claim
that the schemas themselves must be confidential. If official schema files can be
retrieved directly by the GitHub runner, a future workflow may fetch the complete required
tree from RailML instead, provided it respects registration, license, and include-path
requirements. In either case, keep schema files out of Git and release artifacts.

When full GitHub Actions builds are enabled, the private ZIP archive must have this
version directory at the ZIP root:

```text
railML-3-2-SR4/
  source/schema/railml3.xsd
  source/schema/...
```

Preserve all relative paths used by XSD includes. Configure a repository secret
`RAILML_SCHEMA_ARCHIVE_URL` and a read-only bearer token in
`RAILML_SCHEMA_ARCHIVE_TOKEN`. The archive host must accept an HTTP
`Authorization: Bearer <token>` header. Keep the URL and token out of source, logs, and
documentation. CI downloads and extracts the archive into its ephemeral runner workspace;
the workflow does not upload the schemas as artifacts.

The CI job checks for the entry-point XSD before copying the version folder into its
ignored build location. A missing file, unavailable URL, invalid token, or archive with
the wrong layout should fail the schema-dependent build.

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
distributions also have their own licensing and usage requirements. The 3.2-SR4
distribution has additional terms, including registration/certification requirements.
Consult the current terms from RailML and obtain any required registrations, approvals,
or permissions.

RailML.org's schema page says users must accept the license and register before
downloading schemas, and that certification is a prerequisite for productive use. The
RailML.org site also asks that it be referenced in software, manuals, and brochures that
describe RailML capabilities. This repository does not claim that public URL accessibility
waives those requirements. The legal status of distributing XJC-generated JAXB classes
remains unconfirmed; keep the release gate disabled until that point is clarified.

Official references:

- [RailML 3.2 schema download page](https://www.railml.org/en/schemas)
- [RailML license](https://www.railml.org/en/licence)
- [RailML version timeline](https://www.railml.org/en/version-timeline)
- [RailML certification](https://www.railml.org/en/certification)

Do not add schema bundles to GitHub Releases, Maven repositories, container images,
documentation examples, or other deliverables unless the applicable terms expressly allow
that distribution.
