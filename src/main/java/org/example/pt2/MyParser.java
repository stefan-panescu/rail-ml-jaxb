package org.example.pt2;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.railml.schemas._3.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;
import javax.xml.transform.stream.StreamSource;

public class MyParser {
    public void execute() throws JAXBException, IOException {
        try (InputStream input = getClass().getResourceAsStream("/sample.xml")) {
            if (input == null) {
                throw new IOException("Could not find sample.xml on the classpath");
            }

            JAXBContext context = JAXBContext.newInstance(RailML.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            RailML railML = unmarshaller.unmarshal(new StreamSource(input), RailML.class).getValue();

            if (railML.getCommon() == null) {
                throw new IllegalStateException("The sample has no common element");
            }

            OrganizationalUnits organizationalUnits =
                    railML.getCommon().getOrganizationalUnits();
            if (organizationalUnits == null) {
                throw new IllegalStateException("The common element has no organizationalUnits");
            }

            doSomethingElse(railML);
        }
    }

    private void doSomethingElse(RailML railML) {
        Interlocking ixl = railML.getInterlocking();

        ixl.getSignalBoxes().getSignalBox().forEach(signalBox -> {
//            signalBox.
        });
    }

    private static String formatName(Name name) {
        return name.getLanguage() + ":" + name.getName();
    }
}
