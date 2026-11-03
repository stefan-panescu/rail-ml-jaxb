# Architecture and API

## What this library does

The build uses JAXB/XJC to generate Java classes from a separately obtained RailML schema
distribution. At runtime, `RailMLParser` binds an XML document to the generated root type
for the version explicitly chosen by the caller.

```text
RailML XSDs (local build inputs)
        |
        +-- XJC execution + version-specific XJB bindings
        |       |
        |       +-- ro.apanescu.railml.generated.v32.*
        |       +-- ro.apanescu.railml.generated.v34.*
        |
        +-- compiled into the library artifact (generated classes only)

XML + caller-selected RailMLVersion
        |
        +-- RailMLParser
                |
                +-- ParsedRailML(version, generated root object)
```

The generated classes are part of the library JAR. The schema files and sample XML are
excluded from packaged resources. Consumers need the library's runtime dependencies but
not the source schema distributions.

## Public API

| Type | Responsibility |
|---|---|
| `RailMLParser` | Parses from `Path` or caller-owned `InputStream` |
| `RailMLVersion` | Identifies a supported schema distribution and its namespace |
| `ParsedRailML` | Pairs the selected version with its version-specific JAXB root object |

Example:

```java
ParsedRailML parsed = new RailMLParser().parse(path, RailMLVersion.V3_2_SR4);
var root = parsed.valueAs(ro.apanescu.railml.generated.v32.RailML.class);
```

Use the corresponding `ro.apanescu.railml.generated.v34.RailML` class for
`RailMLVersion.V3_4_RC`. The `valueAs` method rejects a root class that does not match the
selected version.

## Parser contract

- **Version selection:** The caller supplies the version. The parser does not inspect the
  XML namespace to choose a model and does not compare the namespace to the supplied
  version. The caller must pass the version that matches the document. If it does not,
  parsing still uses the selected model and may produce an incomplete or misleading
  object.
- **Validation:** Parsing is JAXB binding, not XSD validation. A successful parse does not
  establish that a document conforms to the RailML specification.
- **Root element:** The parser expects the document element's local name to be `railML`.
- **XML external resources:** DTD processing and external-entity support are disabled by
  the StAX input factory.
- **Resource ownership:** `parse(Path, ...)` opens and closes its file stream.
  `parse(InputStream, ...)` leaves the caller's stream open; callers should close it.
- **Errors:** File access errors are reported as `IOException`; JAXB binding failures as
  `JAXBException`; malformed XML or an unexpected root as `XMLStreamException`.
- **Threading:** JAXB contexts are shared per version; each parse creates its own
  unmarshaller and XML reader.

The API is documented with Javadoc. Locally generated HTML is written to
`target/reports/apidocs`; `mvn verify` also attaches a Javadoc JAR.

## Generated package and binding conventions

The XJC executions and binding files live in:

- `pom.xml`
- `src/main/xjb/railml-3-2.xjb`
- `src/main/xjb/railml-3-4.xjb`

Version 3.2 and 3.4 have independent XJC executions and generated output directories.
This isolation avoids type collisions in shared imported schemas. JAXB package names are
versioned so applications can reference models for multiple schema versions in one JVM.
Binding customizations also resolve known schema name conflicts.

Do not edit files under `target/generated-sources/jaxb` by hand. Change the XSD input
distribution, the applicable binding file, or the XJC configuration and regenerate.

## Adding a supported version

1. Confirm the schema distribution may be used for code generation and that any intended
   distribution of derived classes is allowed. Do not commit the schema files.
2. Add a dedicated, ignored schema directory and document its required layout in
   `docs/SCHEMAS.md`.
3. Add a binding file that assigns a unique package namespace, then add a separate XJC
   execution and output directory in `pom.xml`.
4. Add an enum entry in `RailMLVersion` pointing to the generated root class and namespace.
5. Add parser tests for the root type and representative content. Keep fixtures minimal and
   ensure they may be included in source control; the production JAR excludes
   `sample.xml`.
6. Update the version matrix, schema archive setup, CI/release workflows as needed, and
   the README and roadmap.
7. With all required schema inputs locally available, run `mvn clean verify` and inspect
   both JARs to ensure they contain no schema files.

Do not combine independent RailML schema versions into one XJC invocation without
establishing that their imported schemas can coexist without collisions.
