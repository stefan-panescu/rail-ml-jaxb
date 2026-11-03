package org.railml.parser;

public enum RailMLVersion {
    V3_2_SR4("3.2-SR4", "https://www.railml.org/schemas/3.2",
            org.railml.generated.v32.RailML.class),
    V3_4_RC("3.4-RC", "https://www.railml.org/schemas/3.4",
            org.railml.generated.v34.RailML.class);

    private final String label;
    private final String namespace;
    private final Class<?> documentType;

    RailMLVersion(String label, String namespace, Class<?> documentType) {
        this.label = label;
        this.namespace = namespace;
        this.documentType = documentType;
    }

    public String getLabel() {
        return label;
    }

    public String getNamespace() {
        return namespace;
    }

    Class<?> getDocumentType() {
        return documentType;
    }

}
