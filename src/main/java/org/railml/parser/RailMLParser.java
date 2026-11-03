package org.railml.parser;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class RailMLParser {
    private static final Map<RailMLVersion, JAXBContext> CONTEXTS = createContexts();

    public ParsedRailML parse(Path xmlFile, RailMLVersion version)
            throws IOException, JAXBException, XMLStreamException {
        Objects.requireNonNull(xmlFile, "xmlFile");
        Objects.requireNonNull(version, "version");
        try (InputStream input = Files.newInputStream(xmlFile)) {
            return parse(input, version);
        }
    }

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
                // Closing the XML reader must not close the caller-owned input stream.
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
