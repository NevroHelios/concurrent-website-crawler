package crawler;

import java.util.HashMap;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.Map;
import java.util.Queue;

public class Main {

    public static void main(String[] args) throws Exception {
        final String FILENAME = "data/raw_data.json";
        final String startURL = "https://genshin-impact.fandom.com/wiki/Barbara/Voice-Overs";

        HashMap<Number, Queue<String>> urls_to_be_searched = new HashMap<>();
        Map<String, HashMap<String, Object>> data = new HashMap<>(); // url -> {title, times_appeared}
        Number curr_depth = 0;
        LinkedList<String> startList = new LinkedList<>();
        startList.add(startURL);
        urls_to_be_searched.put(curr_depth, startList);

        try {
            String curr_url = urls_to_be_searched.get(0).poll();
            Document doc = Jsoup.connect(curr_url).get();
            Map<String, HashMap<String, Object>> temp = Helper.getUrlsInPage(doc, true, data);
            data.putAll(temp);
            urls_to_be_searched.get(0).addAll(temp.keySet());

        } catch (Exception e) {
            ;
        }
        
        Helper.writeToJson(FILENAME, data, false);
    }

}
