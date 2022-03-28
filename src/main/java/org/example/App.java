package org.example;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

//https://stackoverflow.com/questions/9379868/reading-a-big-xml-file-using-stax-and-dom
//https://stackoverflow.com/questions/11553697/reading-cdata-xml-in-java
public class App {
    public static void main(String[] args) throws IOException, XMLStreamException, TransformerException, ParseException, java.text.ParseException, DOMException, org.json.simple.parser.ParseException {

        String checkTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        String checkDay = String.valueOf(DayOfWeek.from(LocalDate.now()).getValue());
        String inputFile = "feed_sample.xml";
        String outputFile = "feed_out.xml";


        if(args.length > 1) {
            if (args.length > 2 && Utils.isValidTime(args[0])) {
                checkTime = args[0];
            } else {
                System.out.println("first parameter have to be in hour format --:--");
                System.exit(1);
            }


            if (args.length > 3 && ((Integer.parseInt(args[1]) < 1 || Integer.parseInt(args[1]) > 7))) {
                System.out.println("second parameter have to be in day range 1-7");
                System.exit(1);
            } else if(args.length > 3) {
                checkDay = args[1];
            }


            if (args.length > 4) {
                inputFile = args[2];
            }

            if (args.length > 5) {
                inputFile = args[4];
            }
        }


        StaxEventReader eventReader = new StaxEventReader(checkTime, checkDay, inputFile, outputFile);
        eventReader.readDataFromXML();
    }
}
