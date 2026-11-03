package ro.apanescu.railml.parser;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Parses RailML XML into the generated JAXB model for a caller-selected schema version.
 *
 * <p>The parser does not perform XSD validation. It disables DTD processing and external
 * entity resolution while reading XML.</p>
 */
public final class RailMLParser {
    private static final Map<RailMLVersion, JAXBContext> CONTEXTS = createContexts();

    /**
     * Creates a parser backed by the JAXB contexts for all supported RailML versions.
     */
    public RailMLParser() {
    }

    /**
     * Parses a RailML document from a file.
     *
     * @param xmlFile XML file to read
     * @param version schema version used to select the JAXB model
     * @return the parsed document and selected version
     * @throws IOException if the file cannot be opened or read
     * @throws JAXBException if JAXB cannot bind the document
     * @throws XMLStreamException if the XML is malformed or its root is not {@code railML}
     * @throws NullPointerException if {@code xmlFile} or {@code version} is {@code null}
     */
    public ParsedRailML parse(Path xmlFile, RailMLVersion version)
            throws IOException, JAXBException, XMLStreamException {
        Objects.requireNonNull(xmlFile, "xmlFile");
        Objects.requireNonNull(version, "version");
        try (InputStream input = Files.newInputStream(xmlFile)) {
            return parse(input, version);
        }
    }

    /**
     * Parses a RailML document from an input stream.
     *
     * <p>The caller retains ownership of {@code input}; this method does not close it. The
     * supplied version is trusted and is not checked against the XML namespace.</p>
     *
     * @param input XML input stream
     * @param version schema version used to select the JAXB model
     * @return the parsed document and selected version
     * @throws JAXBException if JAXB cannot bind the document
     * @throws XMLStreamException if the XML is malformed or its root is not {@code railML}
     * @throws NullPointerException if {@code input} or {@code version} is {@code null}
     */
    public ParsedRailML parse(InputStream input, RailMLVersion version)
            throws JAXBException, XMLStreamException {
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(version, "version");

        XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

        XMLStreamReader reader = factory.createXMLStreamReader(new FilterInputStream(input) {
            @Override
            public void close() {
                // The caller owns the original input stream.
            }
        });
        try {
            while (reader.hasNext() && reader.next() != XMLStreamConstants.START_ELEMENT) {
            }
            if (reader.getEventType() != XMLStreamConstants.START_ELEMENT
                    || !"railML".equals(reader.getLocalName())) {
                throw new XMLStreamException("Expected a railML document element", reader.getLocation());
            }

            Unmarshaller unmarshaller = CONTEXTS.get(version).createUnmarshaller();
            Object value = unmarshaller.unmarshal(reader, version.getDocumentType()).getValue();
            return new ParsedRailML(version, value);
        } finally {
            reader.close();
        }
    }

    private static Map<RailMLVersion, JAXBContext> createContexts() {
        Map<RailMLVersion, JAXBContext> contexts = new EnumMap<>(RailMLVersion.class);
        for (RailMLVersion version : RailMLVersion.values()) {
            try {
                contexts.put(version, JAXBContext.newInstance(version.getDocumentType()));
            } catch (JAXBException exception) {
                throw new ExceptionInInitializerError(exception);
            }
        }
        return Map.copyOf(contexts);
    }
}
