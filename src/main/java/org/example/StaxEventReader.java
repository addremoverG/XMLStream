package org.example;



import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stax.StAXSource;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;

public class StaxEventReader {

//    public static final String OFFERS = "offers";
//    public static final String OFFER = "offer";
//    public static final String OPENING_TIME = "opening_times";

    private StaxWriteToFIle writeToFIle;
    private int countActiveT;
    private int countActiveF;
    private int doubleCounter;
    private String inputFile;
    private String checkHour;
    private String checkDay;

    public StaxEventReader(String checkHour, String checkDay, String inputFile, String outputFile) throws XMLStreamException, IOException {
        this.writeToFIle = new StaxWriteToFIle(Utils.PATH+outputFile);
        this.countActiveT = 0;
        this.countActiveF = 0;
        this.doubleCounter = 0;
        this.checkHour = checkHour;
        this.checkDay = checkDay;
        this.inputFile = Utils.PATH+inputFile;
    }


    public void readDataFromXML() throws FileNotFoundException, XMLStreamException, TransformerException, ParseException, java.text.ParseException, DOMException, org.json.simple.parser.ParseException {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileReader(this.inputFile));//this.inputFile
        xmlStreamReader.nextTag(); // Advance to statements element

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        this.writeToFIle.beginingDocument();

        while (xmlStreamReader.nextTag() == XMLStreamConstants.START_ELEMENT) {
            DOMResult result = new DOMResult();
            transformer.transform(new StAXSource(xmlStreamReader), result);
            Node domNode = result.getNode();

            NodeList childNode = domNode.getChildNodes();
            for (int i = 0; i < childNode.getLength(); i++) {
                this.writeToFIle.childNode(true);
                NodeList innerNodeLits = childNode.item(i).getChildNodes();
                for (int j = 0; j < innerNodeLits.getLength(); j++) {
                    if (innerNodeLits.item(j).getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }
                    this.writeToFIle.innerElement(innerNodeLits.item(j).getNodeName(), innerNodeLits.item(j).getTextContent());
                    if (innerNodeLits.item(j).getNodeName().equals("opening_times")) {
                        if(this.validateJson(innerNodeLits.item(j).getTextContent())){
                            this.countActiveT++;
                            this.writeToFIle.innerElement("is_active", "true");
                        } else {
                            this.countActiveF++;
                            this.writeToFIle.innerElement("is_active", "false");
                        }
                    }

                }
                this.writeToFIle.childNode(false);
                this.counterFunction();
            }

        }
        this.writeToFIle.endDocument();
        System.out.println("Active: " + this.countActiveT + " Paused: " + this.countActiveF
                + " Total: " + (this.countActiveT + this.countActiveF)
                + " Nodes with more than one element in array: " + this.doubleCounter);
    }


    private boolean validateJson(String jsonS) throws ParseException, java.text.ParseException, org.json.simple.parser.ParseException {

        Timestamp check = Utils.timestampCoverter(this.checkHour);

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(jsonS);

        //
        if (!json.containsKey(this.checkDay)) {//"1"
            return false;
        }
        //
        JSONArray jsonArray = (JSONArray) json.get(this.checkDay);//"1"

        if (json.isEmpty() || jsonArray.isEmpty()) {
            return false;
        }

        if (jsonArray.size() > 1) {
            this.doubleCounter++;
        }


        if (jsonArray.size() == 0) {
            return false;
        }

        JSONObject jsonObject = (JSONObject) jsonArray.get(0);

        String open = (String) jsonObject.get("opening");
        String close = (String) jsonObject.get("closing");

        if (open.isEmpty() || close.isEmpty() || !Utils.isValidTime(open) || !Utils.isValidTime(close)) {
            return false;
        }

        Timestamp openTime = Utils.timestampCoverter(open);
        Timestamp closeTime = Utils.timestampCoverter(close);

        if (closeTime.before(openTime) || closeTime.equals(openTime)) {
            closeTime = Utils.addOneDay(closeTime);
        }


        if (check.after(openTime) && check.before(closeTime)) {
            return true;
        }

        return false;

    }

    private void counterFunction(){
        if ((this.countActiveT + this.countActiveF) % 50000 == 0 && this.countActiveT + this.countActiveF > 0) {
            System.out.println("Records processed: "+(this.countActiveT + this.countActiveF));
        }
    }



}







/*


public void readDataFromXML() throws IOException, XMLStreamException, ParseException, java.text.ParseException {
        InputStream inputStream = new FileInputStream(new File("src/main/resources/feed_sample.xml"));
        XMLInputFactory factory = XMLInputFactory.newFactory();
        XMLEventReader eventReader = factory.createXMLEventReader(inputStream);

        this.writeToFIle.beginingDocument();

        while (eventReader.hasNext()) {

            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String elementName = startElement.getName().getLocalPart();


                switch (elementName) {
                    case StaxEventReader.OFFERS:
                        break;
                    case StaxEventReader.OFFER:
                        this.writeToFIle.childNode(true);
                        break;
                    case StaxEventReader.OPENING_TIME:
                        String value = eventReader.getElementText();
                        this.writeToFIle.innerElement(StaxEventReader.OPENING_TIME, value);
//                        boolean test = this.validateJson(json) ? true : false;
//                        System.out.println(test);
                        if (this.validateJson(value)) {
                            this.countActiveT++;
                            this.writeToFIle.innerElement("is_active", "true");
                        } else {
                            this.countActiveF++;
                            this.writeToFIle.innerElement("is_active", "false");
                        }
                        break;
                    default:
                        this.writeToFIle.innerElement(elementName,eventReader.getElementText());
                        break;
                }
            }

            if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (endElement.getName().getLocalPart() == "offer") {
                    this.writeToFIle.childNode(false);

//                    this.writeToFIle.endDocument();
//                    break;
                }
            }
        }
        this.writeToFIle.endDocument();
        System.out.println("Active: "+this.countActiveT+" Paused: "+this.countActiveF + " Total: "+(this.countActiveT+this.countActiveF));
    }

 */