package ro.apanescu.railml.parser;

import java.util.Objects;

/**
 * A parsed RailML root object paired with the schema version used to bind it.
 *
 * @param version RailML schema version selected by the caller
 * @param value version-specific generated JAXB root object
 */
public record ParsedRailML(RailMLVersion version, Object value) {
    /**
     * Creates a parsed-document result and checks that its value matches the version.
     *
     * @param version RailML schema version
     * @param value generated JAXB root object
     * @throws NullPointerException if either argument is {@code null}
     * @throws IllegalArgumentException if the value is not the root type for the version
     */
    public ParsedRailML {
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(value, "value");
        if (!version.getDocumentType().isInstance(value)) {
            throw new IllegalArgumentException("The value does not match the RailML version");
        }
    }

    /**
     * Returns the generated root object as the type associated with this result's version.
     *
     * @param documentType generated root class for the selected version
     * @param <T> generated JAXB root type
     * @return the parsed root object
     * @throws NullPointerException if {@code documentType} is {@code null}
     * @throws IllegalArgumentException if {@code documentType} does not match the version
     */
    public <T> T valueAs(Class<T> documentType) {
        Objects.requireNonNull(documentType, "documentType");
        if (!version.getDocumentType().equals(documentType)) {
            throw new IllegalArgumentException(
                    "This document uses RailML " + version.getLabel());
        }
        return documentType.cast(value);
    }
}
