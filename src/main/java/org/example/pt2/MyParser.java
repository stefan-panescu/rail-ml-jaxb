package org.example.pt2;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.StringReader;

public class MyParser {
    public void execute() {
        String xml = """
                <train>
                    <name>ICE 123</name>
                </train>
                """;

        try {
            JAXBContext context = JAXBContext.newInstance(Train.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Train train = (Train) unmarshaller.unmarshal(new StringReader(xml));

            System.out.println("Parsed train: " + train.getName());
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to parse XML", e);
        }
    }

    @XmlRootElement(name = "train")
    public static class Train {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
