package ro.apanescu.railml.example;

import ro.apanescu.railml.generated.v32.OrganizationalUnit;
import ro.apanescu.railml.generated.v32.RailML;
import ro.apanescu.railml.parser.ParsedRailML;
import ro.apanescu.railml.parser.RailMLParser;
import ro.apanescu.railml.parser.RailMLVersion;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import jakarta.xml.bind.JAXBException;

public class MyParser {
    public void execute() throws IOException, JAXBException, XMLStreamException {
        try (InputStream input = getClass().getResourceAsStream("/sample.xml")) {
            if (input == null) {
                throw new IOException("Could not find sample.xml on the classpath");
            }

            ParsedRailML parsed = new RailMLParser().parse(input, RailMLVersion.V3_2_SR4);
            System.out.printf("Parsed RailML %s (%s)%n",
                    parsed.version().getLabel(), parsed.value().getClass().getName());

            RailML railML = parsed.valueAs(RailML.class);
            if (railML.getCommon() == null
                    || railML.getCommon().getOrganizationalUnits() == null) {
                throw new IllegalStateException(
                        "The sample has no common organizationalUnits element");
            }

            System.out.printf("common id=%s%n", railML.getCommon().getId());
            for (OrganizationalUnit unit
                    : railML.getCommon().getOrganizationalUnits().getOrganizationalUnit()) {
                System.out.printf("- id=%s, code=%s%n", unit.getId(), unit.getCode());
            }
        }
    }
}
