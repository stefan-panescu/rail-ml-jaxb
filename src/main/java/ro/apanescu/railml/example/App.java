package ro.apanescu.railml.example;

import ro.apanescu.railml.example.MyParser;

import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;

/**
 * Command-line example that parses the bundled sample RailML document.
 */
public class App {
    private App() {
    }

    /**
     * Runs the sample parser.
     *
     * @param args unused command-line arguments
     * @throws JAXBException if JAXB cannot bind the sample
     * @throws IOException if the sample resource cannot be read
     * @throws XMLStreamException if the sample is malformed
     */
    public static void main(String[] args)
            throws JAXBException, IOException, XMLStreamException {
        new MyParser().execute();
    }
}
