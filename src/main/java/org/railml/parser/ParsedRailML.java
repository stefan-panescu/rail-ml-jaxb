package org.railml.parser;

import java.util.Objects;

public record ParsedRailML(RailMLVersion version, Object value) {
    public ParsedRailML {
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(value, "value");
        if (!version.getDocumentType().isInstance(value)) {
            throw new IllegalArgumentException("The value does not match the RailML version");
        }
    }

    public <T> T valueAs(Class<T> documentType) {
        Objects.requireNonNull(documentType, "documentType");
        if (!version.getDocumentType().equals(documentType)) {
            throw new IllegalArgumentException(
                    "This document uses RailML " + version.getLabel());
        }
        return documentType.cast(value);
    }
}
