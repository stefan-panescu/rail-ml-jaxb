# RailML JAXB

JAXB models and a small parser for RailML XML documents. The caller selects the schema
version and gets the corresponding generated JAXB root object. Supported distributions are
RailML 3.2-SR4 and 3.4-RC.

## Use the parser

The parser accepts a file path or an input stream. `parse(InputStream, ...)` does not close
the stream; the caller remains responsible for closing it.

```java
import ro.apanescu.railml.parser.ParsedRailML;
import ro.apanescu.railml.parser.RailMLParser;
import ro.apanescu.railml.parser.RailMLVersion;

import java.nio.file.Path;

ParsedRailML parsed = new RailMLParser().parse(
    Path.of("network.xml"),
    RailMLVersion.V3_2_SR4
);

var railML = parsed.valueAs(ro.apanescu.railml.generated.v32.RailML.class);
var units = railML.getCommon().getOrganizationalUnits().getOrganizationalUnit();
units.forEach(unit -> System.out.println(unit.getId()));
```

For an input stream:

```java
import java.nio.file.Files;
import java.nio.file.Path;

try (var input = Files.newInputStream(Path.of("network.xml"))) {
    ParsedRailML parsed = new RailMLParser().parse(input, RailMLVersion.V3_4_RC);
    var railML = parsed.valueAs(ro.apanescu.railml.generated.v34.RailML.class);
}
```

Use the version matching the XML. The parser trusts the version argument: it does not
auto-detect or compare the document namespace, and it does not validate the document against
an XSD. `ParsedRailML.value()` contains the version-specific generated object;
`valueAs(RailML.class)` returns it with a typed API.

## Build from source

Java 17+ and Maven are required. The RailML schema distributions are **not included in this
repository or in the packaged JAR**. Obtain them under the applicable RailML license terms
and place their `source` folders at these exact locations:

```text
src/main/resources/railML-3-2-SR4/source/schema/railml3.xsd
src/main/resources/railML-3-2-SR4/source/schema/common3.xsd
...the other RailML 3.2-SR4 schema files and subfolders...

src/main/resources/railML-3-4-RC/source/schema/railml3.xsd
src/main/resources/railML-3-4-RC/source/schema/common3.xsd
...the other RailML 3.4-RC schema files and subfolders...
```

Keep the schema distribution's directory structure intact, since the XSDs include one
another using relative paths. These locations are ignored by Git. Do not commit the schema
distributions. The build uses them to generate Java sources; Maven excludes the schema
folders from packaged resources.

Build and run tests:

```shell
mvn clean verify
```

The generated JAXB classes are compiled into the library artifact; downloaded schemas and
the project sample XML are not. For a local consumer project, install it with:

```shell
mvn install
```

Then add this dependency:

```xml
<dependency>
  <groupId>ro.apanescu</groupId>
  <artifactId>rail-ml-jaxb</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

The schema distributions are build-time inputs only; consumers of the built JAR do not need
to download them.

## Add a supported RailML version

Place the licensed schema set in its own ignored directory under
`src/main/resources/railML-<version>/source/schema/`. Add a JAXB binding file with unique
Java package names, an XJC execution in `pom.xml`, and an entry in `RailMLVersion` pointing
to the generated root class. Add parser tests for the new version, then run `mvn clean verify`.

## GitHub

The repository is `https://github.com/stefan-panescu/rail-ml-jaxb`. Push the source to that
repository and create a GitHub Release from a version tag after a successful local
`mvn clean verify`. The build generates API Javadocs in `target/reports/apidocs` and attaches
a Javadoc JAR alongside the library JAR.

The GitHub Actions CI workflow always validates Maven metadata. To enable full compilation,
tests, and Javadocs on GitHub-hosted runners, configure these repository settings:

* Repository variable `RAILML_CI_ENABLED=true`.
* Repository secret `RAILML_SCHEMA_ARCHIVE_URL`, pointing to a privately hosted ZIP.
* Repository secret `RAILML_SCHEMA_ARCHIVE_TOKEN`, a read-only token for that archive.

The ZIP must contain both directories at its root:
`railML-3-2-SR4/source/schema/...` and `railML-3-4-RC/source/schema/...`, retaining all
relative paths. Host schema distributions only where their license permits; the workflow
uses them transiently and does not upload them as artifacts.

The release workflow attaches the library and Javadoc JARs to a published GitHub Release only when
repository variable `RAILML_DISTRIBUTION_APPROVED=true` is set. Before enabling it, confirm
that distribution of both the project code and schema-derived generated classes is allowed.
Configure a protected GitHub environment named `release` with required reviewers. Both JARs
are checked to ensure they do not contain schema files.

To push the current branch to a newly created GitHub repository:

```shell
git remote add origin git@github.com:stefan-panescu/rail-ml-jaxb.git
git push -u origin HEAD
```

## License and schema terms

No license has been selected for the original project code yet. Do not assume that this
repository grants permission to use or redistribute it.

The RailML schema files have separate license and usage conditions; obtain and use them
accordingly. This project does not redistribute those files. Generated JAXB classes are
derived from the schemas, so confirm that generating and distributing them in a library
artifact is permitted by the applicable RailML terms before publishing releases. The JAR
contains the generated Java classes even though it excludes the schema files.
