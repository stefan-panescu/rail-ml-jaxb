package ro.apanescu.railml.parser;

/**
 * RailML schema distributions for which this library generates JAXB models.
 */
public enum RailMLVersion {
    /** RailML 3.2 Service Release 4. */
    V3_2_SR4("3.2-SR4", "https://www.railml.org/schemas/3.2",
            ro.apanescu.railml.generated.v32.RailML.class);

    private final String label;
    private final String namespace;
    private final Class<?> documentType;

    private RailMLVersion(String label, String namespace, Class<?> documentType) {
        this.label = label;
        this.namespace = namespace;
        this.documentType = documentType;
    }

    /**
     * Returns the display label for this schema distribution.
     *
     * @return version label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the XML namespace associated with this schema distribution.
     *
     * @return XML namespace URI
     */
    public String getNamespace() {
        return namespace;
    }

    Class<?> getDocumentType() {
        return documentType;
    }
}
