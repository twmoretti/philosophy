import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void loadPageNoInputTest(){
        String result = phil.loadPage(null);
        assertEquals(null, result);
    }

    @Test
    public void loadPageEmptyInputTest(){
        String result = phil.loadPage("");
        assertEquals(null, result);
    }

    @Test
    public void loadPageCycleTest(){
        phil.loadPage("http://www.google.com/");
        String result = phil.loadPage("http://www.google.com/");
        assertEquals(null, result);
    }

    @Test
    public void loadPageTest(){
        String result = phil.loadPage("http://www.google.com/");
        assertTrue(result.contains("Google"));
    }
}
