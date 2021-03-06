import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import st.EntryMap;
import st.TemplateEngine;

public class Task32 {

    private EntryMap map;
    private EntryMap map2;

    private TemplateEngine engine;

    @Before
    public void setUp() throws Exception {
        map = new EntryMap();
        map2 = new EntryMap();
        engine = new TemplateEngine();
    }

	@Test
	public void testOptDeleteEqualKeep() {
		map.store("name2", "Maddie", false);
		map.store("nothing Maddie", "Delete", false);
		map.store("nothing ${nothing} Maddie", "Keep", false);
		String result = engine.evaluate("Winner should be Keep: ${nothing ${nothing} ${name2}}", map, "optimization");
		String result2 = engine.evaluate("Winner should be Keep: ${nothing ${nothing} ${name2}}", map, "keep-unmatched");
		assertEquals(result2, result);
	}

	@Test
	public void testOptKeepBetter() {
		map.store("name2", "Maddie", false);
		map.store("name Maddie ${nothing}", "Gina", false);
		String result = engine.evaluate("Hello ${name ${name2} ${nothing}}", map, "optimization");
		String result2 = engine.evaluate("Hello ${name ${name2} ${nothing}}", map, "keep-unmatched");
		assertEquals(result2, result);
	}

    @Test
    public void testOptDeleteBetter() {
        map.store("Mr Worf", "Delete won", false);
        map.store("Klingons", "Worf", false);
        map.store("Aliens", "Klingons", false); 
        map.store("Aliens ${ space }", "Vulcans", false);

        String result = engine.evaluate("${Mr ${${Aliens ${ space }}}}", map, "optimization");
        String result2 = engine.evaluate("${Mr ${${Aliens ${ space }}}}", map, "delete-unmatched");
        assertEquals(result2, result);

    }

}
