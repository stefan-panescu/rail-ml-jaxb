package ro.apanescu.railml.example;

import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Path;
import javax.xml.stream.XMLStreamException;

/**
 * Command-line example that parses a RailML XML file.
 */
public class App {
    private App() {
    }

    /**
     * Parses the XML file named on the command line.
     *
     * @param args exactly one argument: the XML file path
     * @throws IllegalArgumentException if no XML file path is supplied
     * @throws JAXBException if JAXB cannot bind the document
     * @throws IOException if the XML file cannot be read
     * @throws XMLStreamException if the XML is malformed
     */
    public static void main(String[] args)
            throws JAXBException, IOException, XMLStreamException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: App <railml-xml-file>");
        }
        new MyParser().execute(Path.of(args[0]));
    }
}
