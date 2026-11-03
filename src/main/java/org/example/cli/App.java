package org.example.cli;

import org.example.pt2.MyParser;

import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args)
            throws JAXBException, IOException, XMLStreamException {
        new MyParser().execute();
    }
}
