import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String[] stringArray = {"1", "2", "3", "4", "5", "5"};
//        System.out.println("Hello World!");
        List<String> staticList = Arrays.asList(stringArray);
        List<String> list = new ArrayList<>(staticList);
        list.clear();
        list.add("1");
        System.out.println("");
    }
}
