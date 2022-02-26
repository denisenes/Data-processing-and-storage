import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.xml.sax.SAXException;
import ru.nsu.fit.cs.peopleschema.*;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {
        HashMap<String, PersonInfo> data = new HashMap<>();
        try (InputStream stream = new FileInputStream("src/main/resources/people.xml")) {
            data = new PeopleParser().parse(stream);
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }

        People people = new People();
        HashMap<String, PersonType> table = new HashMap<>();
        for (var p : data.values()) {
            PersonType pp = new PersonType();

            // set main info
            pp.setId(p.id);
            pp.setName(p.getFullName());
            pp.setGender(GenderType.fromValue(p.gender));

            table.put(p.id, pp);
        }

        for (var p : table.values()) {
            var person = data.get(p.getId());

            // set spouse
            if (person.spouseId != null) {
                IdType id = new IdType(); id.setId(table.get(person.spouseId));
                p.setSpouse(id);
            }
            if (person.wifeId != null) {
                IdType id = new IdType(); id.setId(table.get(person.wifeId));
                p.setSpouse(id);
            }
            if (person.husbandId != null) {
                IdType id = new IdType(); id.setId(table.get(person.husbandId));
                p.setSpouse(id);
            }

            // set children
            ChildrenType childrenType = new ChildrenType();
            childrenType.setCount(BigInteger.
                    valueOf(Objects.requireNonNullElse(person.childrenCount, -1)));
            for (var i : person.childrenId) {
                IdType id = new IdType(); id.setId(table.get(i));
                childrenType.getChildId().add(id);
            }
            p.setChildren(childrenType);


            // set siblings
            SiblingsType siblingsType = new SiblingsType();
            siblingsType.setCount(BigInteger.
                    valueOf(Objects.requireNonNullElse(person.siblingsCount, -1)));
            for (var i : person.siblingsId) {
                IdType id = new IdType(); id.setId(table.get(i));
                siblingsType.getSiblingId().add(id);
            }
            p.setSiblings(siblingsType);

            // set parents
            ParentsType parentsType = new ParentsType();
            for (var i : person.parentsId) {
                IdType id = new IdType(); id.setId(table.get(i));
                parentsType.getParentId().add(id);
            }
            p.setParents(parentsType);
        }

        people.getPerson().addAll(table.values());

        try {
            JAXBContext jc = null;
            System.out.println(People.class);
            ClassLoader classLoader = People.class.getClassLoader();
            jc = JAXBContext.newInstance("ru.nsu.fit.cs.peopleschema", classLoader);
            Marshaller writer = jc.createMarshaller();

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            File schemaFile = new File("src/main/resources/schema.xml");
            writer.setSchema(schemaFactory.newSchema(schemaFile));
            writer.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            writer.marshal(people, new File("src/main/resources/output.xml"));
        } catch (JAXBException | SAXException e) {
            e.printStackTrace();
        }
    }

}
