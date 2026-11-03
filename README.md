# RailML JAXB

Version-specific JAXB models and a small parser for RailML XML documents. The caller
selects the schema version and receives its generated JAXB root object. RailML 3.2-SR4 is
the only currently configured version; 3.4-RC support was removed pending an official
release and clarification of its licensing and use conditions.

## Use the parser

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

The parser trusts the selected version. It does not detect or compare the XML namespace and
does not validate the document against an XSD. `parse(InputStream, ...)` leaves the stream open;
the caller owns and closes it. See [the API and architecture guide](docs/ARCHITECTURE.md).

## Build and use locally

Requires Java 17+, Maven, and the licensed schema inputs described in
[Schema setup and licensing](docs/SCHEMAS.md).

```shell
mvn clean verify
mvn install
```

After installing the artifact locally, use this dependency:

```xml
<dependency>
  <groupId>ro.apanescu</groupId>
  <artifactId>rail-ml-jaxb</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

The project is not currently published to a public Maven repository. Schema distributions
are build-time inputs only; consumers of the built JAR do not need them. Generated JAXB
classes are included in the JAR. Schema files are excluded, and the sample XML is kept
under test resources rather than production resources.

The command-line example accepts the XML path as its single program argument. From an IDE,
run `ro.apanescu.railml.example.App` with a runtime classpath and set that argument to the
path of a RailML document. To launch it through Maven:

```shell
mvn -q "-Dexec.mainClass=ro.apanescu.railml.example.App" "-Dexec.args=<path-to-xml>" org.codehaus.mojo:exec-maven-plugin:3.5.0:java
```

## Project documentation

| Guide | Contents |
|---|---|
| [Architecture and API](docs/ARCHITECTURE.md) | Parser behavior, generated model namespaces, API contract, and adding versions |
| [Development workflow](docs/DEVELOPMENT.md) | Local setup, build/test/Javadoc commands, change and review flow |
| [Schema setup and licensing](docs/SCHEMAS.md) | Local schema locations, private archive layout, exclusions, and licensing cautions |
| [CI/CD and releases](docs/CI-CD.md) | GitHub Actions configuration, repository settings, and guarded release process |
| [Roadmap and plans](docs/ROADMAP.md) | Current status, priorities, prerequisites, and proposed completion criteria |
| [AI contributor instructions](AGENTS.md) | Repository-specific guidance for AI coding assistants |

## License and schema terms

No license has been selected for the original project code. Do not assume that this
repository grants permission to use or redistribute it.

RailML schema files have separate license and usage conditions and are not distributed by
this repository. Generated JAXB classes are derived from those schemas; confirm that
generating and distributing them is permitted by the applicable RailML terms before
publishing any library artifacts. RailML 3.4-RC is not supported by this project while it
remains a development release and its conditions are unresolved.
