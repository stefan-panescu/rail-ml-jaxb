package ro.apanescu.railml.parser;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import javax.xml.stream.XMLStreamException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RailMLParserTest {
    private final RailMLParser parser = new RailMLParser();

    @Test
    void rejectsMalformedXml() {
        assertThrows(
                XMLStreamException.class,
                () -> parser.parse(xml("<railML"), RailMLVersion.V3_2_SR4));
    }

    @Test
    void rejectsUnexpectedRootElement() {
        assertThrows(
                XMLStreamException.class,
                () -> parser.parse(xml("<notRailML/>"), RailMLVersion.V3_2_SR4));
    }

    @Test
    void rejectsNullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> parser.parse((InputStream) null, RailMLVersion.V3_2_SR4));
        assertThrows(
                NullPointerException.class,
                () -> parser.parse(xml("<railML/>"), null));
        assertThrows(
                NullPointerException.class,
                () -> parser.parse((Path) null, RailMLVersion.V3_2_SR4));
        assertThrows(
                NullPointerException.class,
                () -> parser.parse(Path.of("unused.xml"), null));
    }

    @Test
    void parsesRailML32SampleWithExplicitVersion() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/sample.xml")) {
            ParsedRailML parsed = parser.parse(input, RailMLVersion.V3_2_SR4);

            assertEquals(RailMLVersion.V3_2_SR4, parsed.version());
            var railML = assertInstanceOf(ro.apanescu.railml.generated.v32.RailML.class, parsed.value());
            assertEquals("co_01", railML.getCommon().getId());
            assertEquals(4, railML.getCommon().getOrganizationalUnits()
                    .getOrganizationalUnit().size());

            assertThrows(
                    IllegalArgumentException.class,
                    () -> parsed.valueAs(ro.apanescu.railml.generated.v34.RailML.class));
        }
    }

    @Test
    void parsesRailML34WithExplicitVersion() throws Exception {
        String xml = """
                <railML xmlns="https://www.railml.org/schemas/3.4" version="3.4"/>
                """;

        ParsedRailML parsed = parser.parse(
                new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)),
                RailMLVersion.V3_4_RC);

        assertEquals(RailMLVersion.V3_4_RC, parsed.version());
        assertInstanceOf(ro.apanescu.railml.generated.v34.RailML.class, parsed.value());
    }

    @Test
    void doesNotResolveExternalEntities() {
        String document = """
                <!DOCTYPE railML [
                  <!ENTITY external SYSTEM "file:///should-not-be-read">
                ]>
                <railML xmlns="https://www.railml.org/schemas/3.2" version="3.2">
                  &external;
                </railML>
                """;

        JAXBException exception = assertThrows(
                JAXBException.class,
                () -> parser.parse(xml(document), RailMLVersion.V3_2_SR4));
        assertTrue(exception.getLinkedException().getMessage().contains("not declared"));
    }

    @Test
    void leavesCallerProvidedInputStreamOpen() throws Exception {
        TrackingInputStream input = new TrackingInputStream(
                "<railML xmlns=\"https://www.railml.org/schemas/3.2\" version=\"3.2\"/>"
                        .getBytes(StandardCharsets.UTF_8));

        parser.parse(input, RailMLVersion.V3_2_SR4);

        assertFalse(input.closed);
    }

    private static InputStream xml(String document) {
        return new ByteArrayInputStream(document.getBytes(StandardCharsets.UTF_8));
    }

    private static final class TrackingInputStream extends ByteArrayInputStream {
        private boolean closed;

        private TrackingInputStream(byte[] bytes) {
            super(bytes);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }
}
