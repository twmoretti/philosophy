import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Philosophy {
    private HashSet<String> vistedPages;
    private List<String> path;

    public Philosophy(){
        vistedPages = new HashSet<>();
        path = new ArrayList<>();
    }

    public String loadPage(String input){
        if(input == null || input.equals("") || vistedPages.contains(input))
            return null; // TODO: Create an exception to show that there is a cycle? Or at least return an error message
        StringBuilder result = new StringBuilder();
        try {
            // TODO: turn into actual api call and do validation
            URL url = new URL(input);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            path.add(input);
            vistedPages.add(input);
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
