import FindingPhilosophy.Philosophy;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PhilosophyTest {
    Philosophy phil;

    @Before
    public void setUp(){
        phil = new Philosophy();
    }

    @After
    public void tearDown(){
        phil = null;
    }

    @Test
    public void loadPage_NoInputTest(){
        Document result = phil.loadPage(null);
        assertEquals(null, result);
    }

    @Test
    public void loadPage__EmptyInputTest(){
        Document result = phil.loadPage("");
        assertEquals(null, result);
    }

    @Test
    public void loadPage_CycleTest(){
        phil.loadPage("http://www.google.com/");
        Document result = phil.loadPage("http://www.google.com/");
        assertEquals(null, result);
    }

    @Test
    public void loadPage_Test(){
        Document result = phil.loadPage("http://www.google.com/");
        assertEquals("Google", result.title());
    }

    @Test
    public void findNextLink_Test(){
        // TODO: create a document in the test framework so the page being edited will not effect the test
        Document doc = phil.loadPage("https://en.wikipedia.org/wiki/Philosophy");
        String result = phil.findNextLink(doc);
        assertEquals("/wiki/Education", result);
    }

    @Test
    public void findNextLink_NonWikiPageTest(){
        Document doc = phil.loadPage("http://www.google.com/");
        String result = phil.findNextLink(doc);
        assertEquals(null, result);
    }
}
