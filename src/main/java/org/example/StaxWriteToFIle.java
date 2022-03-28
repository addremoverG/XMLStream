package org.example;

import javanet.staxutils.IndentingXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StaxWriteToFIle {


    private IndentingXMLStreamWriter writer;

    public StaxWriteToFIle(String output) throws IOException, XMLStreamException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        FileWriter fileWriterw = new FileWriter(new File(output));//output
        XMLStreamWriter streamWriter = factory.createXMLStreamWriter(fileWriterw);
        this.writer= new IndentingXMLStreamWriter(streamWriter);
    }

    public void beginingDocument() throws XMLStreamException {
        this.writer.writeStartDocument("UTF-8" ,"1.0");
        this.writer.writeStartElement("offers");
    }

    public void endDocument() throws XMLStreamException {
        this.writer.writeEndElement();
        this.writer.writeEndDocument();
        this.writer.flush();
        this.writer.close();
    }

    public void childNode(boolean value) throws XMLStreamException {
        if(value){
            this.writer.writeStartElement("offer");
        }else{
            this.writer.writeEndElement();
        }
    }

    public void innerElement(String name,String value) throws XMLStreamException {
        this.writer.writeStartElement(name);
        writer.writeCData(value);
        this.writer.writeEndElement();
    }
}
