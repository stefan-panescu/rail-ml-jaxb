package org.example.cli;

import org.example.pt2.MyParser;

import java.io.IOException;
import jakarta.xml.bind.JAXBException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws JAXBException, IOException {
        new MyParser().execute();
    }
}
