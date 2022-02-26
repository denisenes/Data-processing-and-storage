import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        try {
            DocumentBuilderFactory documentBuilderFactory =
                DocumentBuilderFactory.newInstance();
            SchemaFactory schemaFactory =
                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            File schemaFile = new File("src/main/resources/schema.xml");
            documentBuilderFactory.setSchema(schemaFactory.newSchema(schemaFile));
            documentBuilderFactory.setNamespaceAware(true);

            Document doc = documentBuilderFactory.
                    newDocumentBuilder().parse("src/main/resources/people.xml");

            StreamSource styleSheet =
                    new StreamSource(new File("src/main/resources/xslt.xml"));
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer(styleSheet);
            transformer.transform(new DOMSource(doc),
                    new StreamResult((new FileOutputStream("src/main/resources/output.html"))));


        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }


    }

}
