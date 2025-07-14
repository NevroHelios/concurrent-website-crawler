package crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Helper {

    public static void siteConfig(
        String siteName,
        String siteUrl,
        List<String> urlsFound
    ) {
        System.out.println("Site Name: " + siteName);
        System.out.println("Site URL: " + siteUrl);
        System.out.println("URLs Found:");
        for (String url : urlsFound) {
            System.out.println("- " + url);
        }
    }

    /** Extract URLs and their metadata from the page
     * @param doc The Jsoup Document object representing the page
     * @param verbose If true, print the URLs and titles to the console
     * @param data the json struture holding everything {url, {title, times_appeared, depth}}
     * @return A map where the key is the URL and the value is a map containing the title and times appeared
     * @implNote {url, {title, times_appeared, depth}}
     */
    public static Map<String, HashMap<String, Object>> getUrlsInPage(Document doc, Boolean verbose, Map<String, HashMap<String, Object>> data) {
        if (verbose) System.out.println(doc.title());

        Elements urlLiElements = doc.getElementsByAttribute("href");
        for (Element urlElement : urlLiElements) {
            String title = urlElement.attr("title").split(".ogg")[0]; // remove .ogg from title
            if (title.isEmpty()) {
                continue; 
            }
            String href = urlElement.absUrl("href");
            if (href.isEmpty() || !href.contains(".ogg")) { 
                continue; // skip if no href
            }
            href = href.split(".ogg")[0] + ".ogg"; // helps with downloading the audio files
            Number timesAppeared = (Number) data.getOrDefault(href, new HashMap<>()).getOrDefault("times_appeared", 0);

            if (verbose) System.out.printf("%s -> \t%s\n", title, href);

            HashMap<String, Object> info = new HashMap<>();
            info.put("title", title);
            info.put("times_appeared", timesAppeared.intValue() + 1);
            data.put(href, info);

            saveAudioFiles(href, "data/audio_files", title);
        }
        if (verbose) System.out.println("\n");
        return data;
    }

    public static void saveAudioFiles(String url, String audioDir, String title) {
        Path dirPath = Paths.get(audioDir);
        try {
            Files.createDirectories(dirPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ProcessBuilder builder = new ProcessBuilder(
            "curl", url, "--output", dirPath + "/" + title + ".mp3"
        );

        try {
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Error with: " + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to download: " + url);
        }
    }

    public static void writeToJson(String fileName, Map<String, HashMap<String, Object>> data, Boolean append) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, append));
            String json = mapper.writeValueAsString(data);
            writer.write(json);
            writer.newLine();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
