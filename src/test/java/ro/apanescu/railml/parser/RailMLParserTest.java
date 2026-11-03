package ro.apanescu.railml.parser;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class RailMLParserTest {
    private final RailMLParser parser = new RailMLParser();

    @Test
    void parsesRailML32SampleWithExplicitVersion() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/sample.xml")) {
            ParsedRailML parsed = parser.parse(input, RailMLVersion.V3_2_SR4);

            assertEquals(RailMLVersion.V3_2_SR4, parsed.version());
            var railML = assertInstanceOf(ro.apanescu.railml.generated.v32.RailML.class, parsed.value());
            assertEquals("co_01", railML.getCommon().getId());
            assertEquals(4, railML.getCommon().getOrganizationalUnits()
                    .getOrganizationalUnit().size());
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
    void leavesCallerProvidedInputStreamOpen() throws Exception {
        TrackingInputStream input = new TrackingInputStream(
                "<railML xmlns=\"https://www.railml.org/schemas/3.2\" version=\"3.2\"/>"
                        .getBytes(StandardCharsets.UTF_8));

        parser.parse(input, RailMLVersion.V3_2_SR4);

        assertFalse(input.closed);
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
