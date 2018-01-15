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

    Philosophy(){
        vistedPages = new HashSet<>();
        path = new ArrayList<>();
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
        // TODO need to add validation that this link is valid (i.e. that it isn't italic or in parenthesises)
        if(paragraphs.isEmpty()){
            return null; // TODO: Decide if we should create exceptions or just have special messages
        }
        Elements wikiLinks = paragraphs.first().select("a[href*=/wiki/]");
        if(wikiLinks.isEmpty()){
            return null; // TODO: Decide if we should create exceptions or just have special messages
        }
        return wikiLinks.first().attr("href");
    }
}
