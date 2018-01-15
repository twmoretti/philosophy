import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Philosophy {
    private Set<String> vistedPages;
    private List<String> path;
    private int numberOfSteps;

    Philosophy(){
        vistedPages = new HashSet<>();
        path = new ArrayList<>();
        numberOfSteps = 0;
    }

    public Document loadPage(String nextPage){
        if(nextPage == null || nextPage.equals("") || vistedPages.contains(nextPage))
            return null; // TODO: Create an exception to show that there is a cycle? Or at least return an error message
        try {
            Document results = Jsoup.connect(nextPage).get();
            vistedPages.add(nextPage);
            path.add(nextPage);
            return results;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String findNextLink(Document currentPage){
        // Load the body of the article
        Element body = currentPage.body();
        // The start of the actual article will always start with a paragraph tag
        Elements paragraphs = body.select("p");
        if(paragraphs.isEmpty()){
            return null; // TODO: Decide if we should create exceptions or just have special messages
        }
        Elements wikiLinks = paragraphs.first().select("a[href^=/wiki/]");
        if(wikiLinks.isEmpty()){
            return null; // TODO: Decide if we should create exceptions or just have special messages
        }
        // Find the first link that is valid:
        Element link = firstValidLink(wikiLinks);
        if(link == null){
            return null; // TODO: Decide if we should create exceptions or just have special messages
        }
        return link.attr("href");
    }

    private Element firstValidLink(Elements links){
        for (Element link: links) {
            if(     link.attr("href").contains("Help:") ||
                    link.attr("href").contains("File:") ||
                    link.attr("href").contains("Wikipedia:"))
                continue;
            String surroundingHtml = link.parent().toString();
            String toStart = surroundingHtml.substring(0, surroundingHtml.indexOf(link.toString()));
            // Strip out any complete parentheses pairs
            while(toStart.contains("(") && toStart.contains(")")){
                toStart = toStart.substring(toStart.indexOf(')') + 1);
            }
            while(toStart.contains("<i>") && toStart.contains("</i>")){
                toStart = toStart.substring(toStart.indexOf("</i>") + 1);
            }
            if(!(toStart.contains("(") || toStart.contains("<i>")))
                return link;
        }
        return null;
    }

    public String walkThePath(String url){
        if(url.toUpperCase().contains("/wiki/Philosophy".toUpperCase()))
            return "You have landed on Philosophy!";

        if(!url.toUpperCase().contains("en.wikipedia.org".toUpperCase()))
            return "You must provide a page from the English Wikipedia!";

        Document page = loadPage(url);
        if(page == null)
            return "We have found a cycle! Cannot reach Philosophy!";

        String nextPage = findNextLink(page);
        if(nextPage == null)
            return "We have found a dead end! Cannot reach Philosophy!";

        String nextUrl = "https://en.wikipedia.org" + nextPage;
        numberOfSteps++;
        if(numberOfSteps > 100)
            return "We were unable to reach Philosophy after 100 steps!";

        return walkThePath(nextUrl);
    }

    public static void main(String[] args) {
        Philosophy phil = new Philosophy();
        String result = phil.walkThePath("https://en.wikipedia.org/wiki/Ankylosaurus");
        System.out.println(result);
        System.out.println("Number of steps taken: " + phil.numberOfSteps);
        System.out.println("Path taken:");
        for (String step: phil.path) {
            System.out.println("\t" + step);
        }
    }
}
