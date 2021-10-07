import java.util.Collections;
import java.util.LinkedList;

public class StrList {

    private final LinkedList<String> list;

    StrList() {
        list = new LinkedList<>();
    }

    public synchronized void add(String str) {
        list.add(0, str);
    }

    public String get(int n) {
        return list.get(n);
    }

    public synchronized void printList() {
        for (int i = list.size()-1; i >= 0; i--) {
            System.out.println(get(i));
        }
    }

    public void swap(int n1, int n2) {
        Collections.swap(list, n1, n2);
    }

    public synchronized  void sort() {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (get(i).compareTo(get(j)) > 0) {
                    swap(i, j);
                }
            }
        }
    }

}
