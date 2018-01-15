package FindingPhilosophy;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;


@Controller
public class Philosophy {
    private Set<String> visitedPages;
    private List<String> path;
    private String finalPath;
    private String startingPage;
    private int numberOfSteps;

    @Autowired
    private PathRepository repository;

    public Philosophy(){
        visitedPages = new HashSet<>();
        path = new ArrayList<>();
        numberOfSteps = 0;
    }

    public Document loadPage(String nextPage){
        if(nextPage == null || nextPage.equals("") || visitedPages.contains(nextPage))
            return null; // TODO: Create an exception to show that there is a cycle? Or at least return an error message
        try {
            Document results = Jsoup.connect(nextPage).get();
            visitedPages.add(nextPage);
            path.add(nextPage);
            return results;
        } catch (HttpStatusException e){
            // TODO This is gross, figure out a better way
            // Fake an empty document to get the correct error
            Document results = Jsoup.parse("<html><head><title>blank</title></head><body>blank</body></html>");
            return results;
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String findNextLink(Document currentPage){
        // Load the body of the article
        Element body = currentPage.body();
        // Because the first link might not be in the fist paragraph, we need to check all the links
        Elements wikiLinks = body.select("p > a[href^=/wiki/]");
        if(wikiLinks.isEmpty()){ // Found an article with no links
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
            // Count open and closed tags to make sure they're balanced
            if(specialBalanced(toStart))
                return link;
        }
        return null;
    }

    private boolean specialBalanced(String htmlToTest){
        int openParens = getCount(htmlToTest, "(");
        int closedParens = getCount(htmlToTest, ")");
        if(openParens != closedParens)
            return false;

        int openI = getCount(htmlToTest, "<i"); // Sometimes the tags contain more information
        int closedI = getCount(htmlToTest, "</i>");
        return openI == closedI;
    }

    private int getCount(String htmlToTest, String tagToFind){
        int index = htmlToTest.indexOf(tagToFind);
        int count = 0;
        while(index != -1){
            count++;
            htmlToTest = htmlToTest.substring(index + 1);
            index = htmlToTest.indexOf(tagToFind);
        }
        return count;
    }

    private String walkThePath(String url) {
        if (url.toUpperCase().contains("/wiki/Philosophy".toUpperCase())) {
            path.add(url);
            numberOfSteps++;
            finalPath = finalizePath();
            return "You have landed on Philosophy!";
        }

        Document page = loadPage(url);
        if (page == null) {
            path.add("Cycle here: " + url);
            finalPath = finalizePath();
            return "We have found a cycle! Cannot reach Philosophy!";
        }

        String nextPage = findNextLink(page);
        if (nextPage == null){
            path.add("Dead end here: " + url);
            finalPath = finalizePath();
            return "We have found a dead end! Cannot reach Philosophy!";
        }

        String nextUrl = "https://en.wikipedia.org" + nextPage;
        numberOfSteps++;
        if(numberOfSteps > 100)
            return "We were unable to reach Philosophy after 100 steps!";

        return walkThePath(nextUrl);
    }

    private String finalizePath(){
        StringBuilder sb = new StringBuilder();
        for (String step: path) {
            sb.append("<li>");
            sb.append(step);
            sb.append("</li>");
        }
        return sb.toString();
    }

    public String addNewPath(Philosophy phil){
        Path p = new Path();
        p.setArticle(phil.startingPage);
        p.setPath(phil.finalPath);
        p.setNumHops(phil.numberOfSteps);
        p.setLastRun(LocalDateTime.now().format(BASIC_ISO_DATE));
        repository.save(p);
        return "Saved";
    }

    @RequestMapping(value="/findingPhilosophy", method=RequestMethod.POST)
    public String findingPhilosophy(@ModelAttribute(value="url") UrlHolder urlHolder, Model model){
        String url = urlHolder.getUrl();
        if(!url.toUpperCase().contains("en.wikipedia.org".toUpperCase())) {
            model.addAttribute("results", "You must provide a page from the English Wikipedia!");
            return "findingPhilosophy";
        }

        Philosophy phil = new Philosophy();
        phil.startingPage = url;

        String tmpResults = phil.walkThePath(url);

        if(tmpResults.equals("We were unable to reach Philosophy after 100 steps!")){
            model.addAttribute("results", tmpResults);
            return "findingPhilosophy";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(tmpResults);
        sb.append("<br />");
        sb.append("Steps taken: ");
        sb.append(phil.numberOfSteps);
        sb.append("<br />");
        sb.append("Path taken:");
        sb.append("<br />");
        sb.append("<ul>");
        sb.append(phil.finalPath);
        sb.append("</ul>");

        addNewPath(phil);
        model.addAttribute("results", sb.toString());
        return "findingPhilosophy";
    }

    @RequestMapping(value="/beginSearch",method=RequestMethod.GET)
    public String showForm(Model model) {
        UrlHolder urlHolder = new UrlHolder();
        urlHolder.setUrl("url");

        model.addAttribute("urlHolder", urlHolder);
        return "beginSearch";
    }
}

