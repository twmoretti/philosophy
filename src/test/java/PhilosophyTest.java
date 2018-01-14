import org.junit.*;

import static org.junit.Assert.assertEquals;

public class PhilosophyTest {
    @Test
    public void returnOneTest(){
        assertEquals(Philosophy.returnOne(), 1);
    }
}
