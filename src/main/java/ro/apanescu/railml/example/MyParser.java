package ro.apanescu.railml.example;

import ro.apanescu.railml.generated.v32.OrganizationalUnit;
import ro.apanescu.railml.generated.v32.RailML;
import ro.apanescu.railml.parser.ParsedRailML;
import ro.apanescu.railml.parser.RailMLParser;
import ro.apanescu.railml.parser.RailMLVersion;

import java.io.IOException;
import java.nio.file.Path;
import javax.xml.stream.XMLStreamException;
import jakarta.xml.bind.JAXBException;

/**
 * Demonstrates parsing a RailML 3.2 document and reading common organizational units.
 */
public class MyParser {
    /**
     * Creates the sample parser.
     */
    public MyParser() {
    }

    /**
     * Parses an XML file and prints the identifiers and codes of its organizational units.
     *
     * @param xmlFile path to the RailML XML document
     * @throws IOException if the XML file cannot be opened or read
     * @throws JAXBException if JAXB cannot bind the document
     * @throws XMLStreamException if the XML is malformed
     */
    public void execute(Path xmlFile) throws IOException, JAXBException, XMLStreamException {
        ParsedRailML parsed = new RailMLParser().parse(xmlFile, RailMLVersion.V3_2_SR4);
        System.out.printf("Parsed RailML %s (%s)%n",
                parsed.version().getLabel(), parsed.value().getClass().getName());

        RailML railML = parsed.valueAs(RailML.class);
        if (railML.getCommon() == null
                || railML.getCommon().getOrganizationalUnits() == null) {
            throw new IllegalStateException(
                    "The document has no common organizationalUnits element");
        }

        System.out.printf("common id=%s%n", railML.getCommon().getId());
        for (OrganizationalUnit unit
                : railML.getCommon().getOrganizationalUnits().getOrganizationalUnit()) {
            System.out.printf("- id=%s, code=%s%n", unit.getId(), unit.getCode());
        }
    }
}
