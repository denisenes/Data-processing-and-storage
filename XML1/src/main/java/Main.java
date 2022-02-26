import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) {
        try (InputStream stream = new FileInputStream("/home/charlie/Downloads/task_X1/people.xml")) {
            ArrayList<PersonInfo> data = new PeopleParser().parse(stream);
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }
    }

}
