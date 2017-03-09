import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import st.EntryMap;
import st.TemplateEngine;

public class Task22 {

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
    /*
     * Spec3 = true; All cases will not find a match except last
     * case, testing arg2 as empty
     */
    public void storeArg3True() {
    	map.store("name" , "John", true);
        String result = engine.evaluate("Hello ${nAme}", map,"keep-unmatched");
        assertEquals("Hello ${nAme}", result);
        map.store("aGe" , "20", true);
        String result1 = engine.evaluate("${age} today", map,"keep-unmatched");
        assertEquals("${age} today", result1);
    	map.store("nam" , "Edin", true);
        String result2 = engine.evaluate("${nam} yeah", map,"keep-unmatched");
        assertEquals("Edin yeah", result2);
    	map.store("blank" , "", true);
        String result3 = engine.evaluate("${blank} space", map,"keep-unmatched");
        assertEquals(" space", result3);
    }

    @Test
    /*
     * Spec3 = false; All cases will find a match
     */
    public void storeArg3False() {

    	map.store("name" , "John", false);
        String result = engine.evaluate("Hello ${nAme}", map,"keep-unmatched");
        assertEquals("Hello John", result);
        map.store("aGe" , "20", false);
        String result1 = engine.evaluate("${age} today", map,"keep-unmatched");
        assertEquals("20 today", result1);
    	map.store("nam" , "Edin", false);
        String result2 = engine.evaluate("${nam} yeah", map,"keep-unmatched");
        assertEquals("Edin yeah", result2);
    	map.store("blank" , "", false);
        String result3 = engine.evaluate("${blank} space", map,"keep-unmatched");
        assertEquals(" space", result3);

    }

    @Test
    /*
     * Spec3 = null;  All cases will find a match
     */
    public void storeArg3Null() {

      	map.store("name" , "John", null);
        String result = engine.evaluate("Hello ${nAme}", map,"keep-unmatched");
        assertEquals("Hello John", result);
        map.store("aGe" , "20", null);
        String result1 = engine.evaluate("${age} today", map,"keep-unmatched");
        assertEquals("20 today", result1);
    	map.store("nam" , "Edin", null);
        String result2 = engine.evaluate("${nam} yeah", map,"keep-unmatched");
        assertEquals("Edin yeah", result2);
    	map.store("blank" , "", null);
        String result3 = engine.evaluate("${blank} space", map,"keep-unmatched");
        assertEquals(" space", result3);

    }

    /*
     * Spec 4: Entry order is tested based on changes
     * Spec 5: Only the first entry matters
     * in argument 2 and ordering of argument 3
     */
	@Test
    public void storeRepeatsTrueFalse() {
    	map.store("name" , "first", true);		//Sensitive first choice
        String result = engine.evaluate("${nAme} space", map,"keep-unmatched");
        assertEquals("${nAme} space", result);
    	map.store("name" , "", false);			//Insensitive first choice
    	map.store("name" , "error", false);		//Never occurs
        String result1 = engine.evaluate("${nAme} space", map,"keep-unmatched");
        assertEquals(" space", result1);
        map.store("name" , "error", true);		//Never occurs
        String result2 = engine.evaluate("${name} space", map,"keep-unmatched");
        assertEquals("first space", result2);
    }
    @Test
    public void storeRepeatsTrueNull() {
    	map.store("age" , "20", true);			//Sensitive first choice
        String result3 = engine.evaluate("${aGe} years", map,"keep-unmatched");
        assertEquals("${aGe} years", result3);
    	map.store("age" , "", null);			//Insensitive first choice
    	map.store("age" , "error", null);		//Never occurs
        String result4 = engine.evaluate("${aGe} years", map,"keep-unmatched");
        assertEquals(" years", result4);
        map.store("age" , "error", true);		//Never occurs
        String result5 = engine.evaluate("${aGe} years", map,"keep-unmatched");
        assertEquals(" years", result5);
        String result6 = engine.evaluate("${age} years", map,"keep-unmatched");
        assertEquals("20 years", result6);
    }


	/*
		spec5 - The EntryMap objects stored again, only keeps the first entry
    */
    @Test
	public void testSpec5RepeatedEntryWithSpace() {
		map.store("na me", "Adam", true);
		map.store("name", "John", true);
		map.store("name","John",true);
		map.store("${na ${asdasadsadasd}me}", "", false);

		String result = engine.evaluate("Hello ${name} ${na me} ${na${asdasadsadasd}me}", map,"delete-unmatched");
		assertEquals("Hello Adam Adam Adam", result);
	}

    @Test
	public void testSpec5RepeatedEntryWithBackslashSpace() {
		map.store("fna\tme", "Adam", true);		// Tab
		map.store("lna\nme", "Mitchell", true);	// Newline
		map.store("fname", "John", true);
		map.store("fname","John",true);
		map.store("lname","Smith",true);
		map.store("${fna ${asdasadsadasd}me}", "", false);
		map.store("${lna ${asdasadsadasd}me}", "", false);

		String result = engine.evaluate("Hello ${fname} ${lname} ${fna me} ${lna me} ${fna${asdasadsadasd}me} ${lna ${asdasadsadasd}me}", map,"delete-unmatched");
		assertEquals("Hello Adam Mitchell Adam Mitchell Adam Mitchell", result);
	}

<<<<<<< HEAD
	/**
		spec1 - The template string can be NULL or empty.
				If template string NULL or empty, then the unchanged
				template string is returned.
	*/
	@Test
	public void testTemplateStringCanBeNull() {
		map.store("null", "error", false);
		String result = engine.evaluate(null, map, "delete-unmatched");
		assertNull(result);
	}

	@Test
	public void testTemplateStringCanBeEmpty() {
		map.store("null", "error", false);
		String result = engine.evaluate("", map, "delete-unmatched");
		assertEquals("", result);
	}

	/*
		spec2 - The EntryMap object can be NULL.
			If EntryMap object NULL, then the unchanged template string
			is returned.
    */
	@Test
	public void testMapObjectCanBeNull() {
		String result = engine.evaluate("Hello world!", null, "delete-unmatched");
		assertEquals("Hello world!", result);

		map.store("template", "error", false);

		result = engine.evaluate("${template}", null, "delete-unmatched");
		assertEquals("${template}", result);

	}

  @Test
  public void testMapObjectCanHaveNoContent() {
  		String result = engine.evaluate("Hello world!", map, "delete-unmatched");
  		assertEquals("Hello world!", result);
  }

	/*
		spec1 and 2 - EntryMap and Template string can be null
    */
	@Test
	public void testMapAndTemplateNull() {
		String result = engine.evaluate(null, null, "keep-unmatched");
		assertNull(result);

		result = engine.evaluate("", null, "keep-unmatched");
		assertEquals("", result);

	}


	/*
		spec3 - Matching mode cannot be NULL and must be one of the
			possible values ("keep-unmatched" and "delete-unmatched").
			If matching mode NULL or other value, it defaults to
			"delete-unmatched".
    */
=======
	/******************************************************************/
	/*
		Testing TemplateEngine.evaluate()
	*/

	/**
		Default example from spec
	*/
	@Test
	public void testDefaultExample() {
		map.store("name", "Adam", false);
		map.store("surname", "Dykes", false);

		String result = engine.evaluate("Hello ${name} ${surname}", map,"delete-unmatched");
		assertEquals("Hello Adam Dykes", result);
	}

>>>>>>> c2339a876839c7a2a9f26075ba6370ba497f84a5
	@Test
	public void testMatchingModeKeepUnmatched() {
		map.store("fname", "Rose", false);
		map.store("lname", "Tyler", false);

		String result = engine.evaluate("${fname} ${lname} is ${age} years old", map, "keep-unmatched");
		assertEquals("Rose Tyler is ${age} years old", result);
	}

	@Test
	public void testMatchingModeDeleteUnmatched() {
		map.store("fname", "Rose", false);
		map.store("lname", "Tyler", false);

		String result = engine.evaluate("${fname} ${lname} is ${age} years old", map, "delete-unmatched");
		assertEquals("Rose Tyler is  years old", result);
	}

	@Test
	public void testMatchingModeInvalid() {
		map.store("fname", "Rose", true);
		map.store("lname", "Tyler", true);

		String result = engine.evaluate("${fname} ${lname} is ${age} years old", map, "foo");
		assertEquals("Rose Tyler is  years old", result);
	}

	@Test
	public void testMatchingModeEmpty() {
		map.store("fname", "Rose", true);
		map.store("lname", "Tyler", true);

		String result = engine.evaluate("${fname} ${lname} is ${age} years old", map, "");
		assertEquals("Rose Tyler is  years old", result);
	}


	@Test
	public void testMatchingModeNull() {
		map.store("Fname", "Rose", true);
		map.store("lname", "Tyler", false);

		String result = engine.evaluate("${fname} ${lname} is ${age} years old", map, null);
		assertEquals(" Tyler is  years old", result);
	}

	/*
		spec4 - Templates in a template string occur between "${" and "}".
			In a template, everything between its boundaries ("${" and "}")
			is treated as normal text when matched against an entry.
            ---> In the template string "Hello ${name}, could you please
				give me your ${item} ?" the two templates are:
					1 - ${name}
					2 - ${item}
            ---> The text of each template that will be matched against
				the EntryMap stored entries are:
					1 - "name"
					2 - "item"
					(i.e. the template boundaries are omitted)
    */
	@Test
	public void testMalformedTemplateGapAfterDollar() {
		map.store("fname", "Rose", false);
		map.store("lname", "Tyler", false);

		String result = engine.evaluate("Hey there $ {fname} ${lname}", map, "delete-unmatched");
		assertEquals("Hey there $ {fname} Tyler", result);
	}

	@Test
	public void testMalformedTemplateOpenWOClose() {
		map.store("fname", "Rose", false);
		map.store("lname", "Tyler", false);

		String result = engine.evaluate("Hey there ${fname ${lname } ${age} 2", map, "keep-unmatched");
		assertEquals("Hey there ${fname Tyler ${age} 2", result);

		map2.store("fname", "Rose", false);
		map2.store("lname", "Tyler", false);

		result = engine.evaluate("Hey there ${fname} ${lname", map2, "delete-unmatched");
		assertEquals("Hey there Rose ${lname", result);
	}


	/*
		spec5 - When a template is matched against an entry key, any
			non visible character does not affect the result.
            ---> The entry "middle name"/"Peter" will match all of the
				following templates:
					1 - ${middle name}
					2 - ${middlename}
					3 - ${middle       name}
    */
	@Test
	public void testWhitespaceLocationMiddle() {
		map.store("Doctor Who", "TARDIS", false);
		map.store("Star Trek", "Enterprise", false);
		map.store("Star Wars", "Millennium Falcon", false);

		String result = engine.evaluate("The best ships in the universe: the ${Doctor Who}, the ${StarTrek}, and the ${Star     Wars}", map, "delete-unmatched");
		assertEquals("The best ships in the universe: the TARDIS, the Enterprise, and the Millennium Falcon", result);

		map2.store("Doctor Who", "TARDIS", true);
		map2.store("Star Trek", "Enterprise", true);
		map2.store("Star Wars", "Millennium Falcon", true);

		result = engine.evaluate("The best ships in the universe: the ${Doctor Who}, the ${StarTrek}, and the ${Star     Wars}", map2, "delete-unmatched");
		assertEquals("The best ships in the universe: the TARDIS, the Enterprise, and the Millennium Falcon", result);
	}

	@Test
	public void testWhitespaceLocationEnds() {
		map.store("Doctor Who", "TARDIS", false);
		map.store("Star Trek", "Enterprise", false);
		map.store("Star Wars", "Millennium Falcon", false);

		String result = engine.evaluate("The best ships in the universe: the ${   Doctor Who}, the ${Star Trek   }, and the ${    Star Wars    }", map, "delete-unmatched");
		assertEquals("The best ships in the universe: the TARDIS, the Enterprise, and the Millennium Falcon", result);

		map2.store("Doctor Who", "TARDIS", true);
		map2.store("Star Trek", "Enterprise", true);
		map2.store("Star Wars", "Millennium Falcon", true);

		result = engine.evaluate("The best ships in the universe: the ${   Doctor Who}, the ${Star Trek   }, and the ${    Star Wars    }", map2, "delete-unmatched");
		assertEquals("The best ships in the universe: the TARDIS, the Enterprise, and the Millennium Falcon", result);
	}

	@Test
	public void testWhitespaceType() {
		map.store("Doctor Who", "TARDIS", false);
		map.store("Star Trek", "Enterprise", false);
		map.store("Star Wars", "Millennium Falcon", false);

		String result = engine.evaluate("The best ships in the universe: the ${Doctor\tWho}, the ${Star\nTrek}, and the ${Star\r\nWars}", map, "delete-unmatched");
		assertEquals("The best ships in the universe: the TARDIS, the Enterprise, and the Millennium Falcon", result);

		map2.store("Doctor Who", "TARDIS", true);
		map2.store("Star Trek", "Enterprise", true);
		map2.store("Star Wars", "Millennium Falcon", true);

		result = engine.evaluate("The best ships in the universe: the ${Doctor\tWho}, the ${Star\nTrek}, and the ${Star\r\nWars}", map2, "delete-unmatched");
		assertEquals("The best ships in the universe: the TARDIS, the Enterprise, and the Millennium Falcon", result);
	}

	@Test
	public void testNestingMultipleOpenings() {
		map.store("fname", "Rose", false);
		map.store("lname", "Tyler", false);

		String result = engine.evaluate("Hey there ${${fname} ${${${lname} ${}", map, "delete-unmatched");
		assertEquals("Hey there ${Rose ${${Tyler ", result);

		map2.store("fname", "Rose", false);
		map2.store("lname", "Tyler", false);

		result = engine.evaluate("Hey there ${${fname} ${${${lname} ${}", map2, "keep-unmatched");
		assertEquals("Hey there ${Rose ${${Tyler ${}", result);
	}

	@Test
	public void testNestingTemplatesDept() {
		map.store("system", "metric", false);
		map.store("metric", "cm", false);
		map.store("length cm", "30cm", false);
		map.store("length in", "12in", false);

		String result = engine.evaluate("Here is a ${length ${${system}}} ruler", map, "delete-unmatched");
		assertEquals("Here is a 30cm ruler", result);
	}

	@Test
	public void testNestingMultipleTemplates() {
		map.store("rank", "Doctor", false);
		map.store("question", "Who", false);
		map.store("Who Doctor", "the Doctor", false);

		String result = engine.evaluate("His name is ${${question}${rank}}", map, "delete-unmatched");
		assertEquals("His name is the Doctor", result);
	}

	/*
		spec7 - In a template string the different templates are ordered
			according to their length. The shorter templates precede.
            ---> In the case of same-length templates, the one that occurs
				first when traversing the template string from left-to-right
				precedes.
            ---> In the template string
				"abc}${de}${fgijk${lm}nopqr}${s}uvw${xyz" the sorted
				templates are:
					1 - ${s}
					2 - ${de}
					3 - ${lm}
					4 - ${fgijk${lm}nopqr}
    */

	// NOTE; I don't really know how to test this one, so please help
	// 			figure out this one TODO
	@Test
	public void testTemplateOrderNesting1() {
		map.store("inside", "outside", false);

		String result = engine.evaluate("${${inside}}", map, "keep-unmatched");
		assertEquals("${outside}", result);
	}

	@Test
	public void testTemplateNestingChanges() {
		map.store("one", "1", false);
		map.store("two", "", false);
		map.store("long", "last", false);

		String result = engine.evaluate("${long ${one}} and ${long ${two}} and ${two} and ${one}", map, "keep-unmatched");
		assertEquals("${long 1} and last and  and 1", result);
	}

	@Test
	public void testTemplateOrdering() {
		map.store("one", "1", false);
		map.store("long", "last", false);

		String result = engine.evaluate("${long ${one}} and ${one}", map, "keep-unmatched");
		assertEquals("${long 1} and 1", result);

		result = engine.evaluate("${long ${one}} and ${long}", map, "keep-unmatched");
		assertEquals("${long 1} and last", result);
	}

	@Test
	public void testOrderingOfTemplates() {
		map.store("na me", "Adam", true);
		map.store("na         ${asdasadsadasd}me", "error", false);
		map.store("asdasadsadasd", "", false);

		String result = engine.evaluate("Hello ${na me} ${na         ${asdasadsadasd}me}", map,"keep-unmatched");
		assertEquals("Hello Adam Adam", result);
	}
	/*
		spec8 - The engine processes one template at a time and attempts to
			match it against the keys of the EntryMap entries until there
			is a match or the entry list is exhausted.
            ---> The engine processes both templates and entries according
				to their order.
            ---> If there is a match:
                    1 - The template (including its boundaries) in the
						template string is replaced by the value of the
						matched entry.
                    2 - The same replace happens to all other templates
						which include the replaced template.
                    3 - The template engine moves on to the next template
						and repeats.
            ---> If the entry list is exhausted and no match found for the
				current template:
                    1 - The template engine just moves on to the next
						template if matching the mode is "keep-unmatched".
                    2 - The engine deletes the unmatched template from the
						template string and all other templates which
						include it.
	*/
	@Test
	public void testOrderAddToMapDiffersFromOrderInTemplateString() {
		map.store("one", "1", false);
		map.store("two", "2", false);
		map.store("three", "3", false);

		String result = engine.evaluate("${three} ${two} ${one}", map, "delete-unmatched");
		assertEquals("3 2 1", result);
	}

<<<<<<< HEAD
	//TODO: Discuss expected output of these two situations
	@Test
	public void testTemplateCalledMultipleTimes() {
		map.store("thing", "test", false);
		map.store("action", "test", false);

		String result = engine.evaluate("Yo dawg! I heard you liked ${thing}s, so I put a ${thing} in your ${thing} so you can ${action} while you ${action}", map, "delete-unmatched");
		assertEquals("Yo dawg! I heard you liked tests, so I put a test in your test so you can test while you test", result);
	}

	@Test
	public void testMultipleEntriesTheSame() {
		map.store("colour", "blue", false);
		map.store("thing", "house", false);
		map.store("thing", "window", false);
		map.store("thing", "streets", false);
		map.store("thing", "trees", false);
		map.store("thing", "girlfriend", false);

		String result = engine.evaluate("I have a ${colour} ${thing} with a ${colour} ${thing}. ${colour} are the ${thing} and now the ${thing} are too.  I have a ${thing}, and she is so ${colour}", map, "delete-unmatched");
		assertEquals("I have a blue house with a blue house. blue are the house and now the house are too.  I have a house, and she is so blue", result);
	}

=======
	@Test
	public void testEntryUnused() {
		map.store("lots", "asdfghjkl", false);
		map.store("of", "qwertyuiop", false);
		map.store("things", "zxcvbnm", false);
		map.store("book", "Hitchhiker's Guide to the Galaxy", false);

		String result = engine.evaluate("The ${book} is my favourite book", map, "delete-unmatched");
		assertEquals("The Hitchhiker's Guide to the Galaxy is my favourite book", result);
	}
>>>>>>> c2339a876839c7a2a9f26075ba6370ba497f84a5

	/**
		Extra bits:
			No specification of what characters are allowed, so assuming
			that all characters other than {} are allowed in template
			entry keys ({} are assumed to be excluded because of evaluate
			spec6) and all characters are allowed as template entry values
	*/
	@Test
	public void testAcceptableTemplateCharacters() {
		// TODO
		// a-zA-Z0-9 + special characters (+ emoji 😊)
		// Note especially the characters ${} appearing in template name

		map.store("abc", "def", false);
		map.store("123", "456", false);
		map.store("!@#$%^&*()", "!@#$%^&*()", false);
		map.store("_+-=`~[]\\|", "_+-=`~[]{}\\|", false);
		map.store(":;\"'<>,./?", ":;\"'<>,./?", false);

		String result = engine.evaluate("${abc} ${123} ${!@#$%^&*()} ${_+-=`~[]\\|} ${:;\"'<>,./?}", map, "delete-unmatched");
		assertEquals("def 456 !@#$%^&*() _+-=`~[]{}\\| :;\"'<>,./?", result);

	}

	@Test
	public void testIntroducingNewTemplates() {
		// TODO
		// a-zA-Z0-9 + special characters (+ emoji 😊)
		// Note especially the characters ${} appearing in template name

		map.store("abc", "${123}", false);
		map.store("123", "error", false);

		String result = engine.evaluate("${abc}", map, "delete-unmatched");
		assertEquals("${123}", result);

	}
	//todo PLEASE CHECK THIS CASE
	@Test
	public void testNonAlphaCaseSensitive() {
		map.store("$", "$", true);
        String result = engine.evaluate("${${${${$}}}}", map, "keep-unmatched");
        //not sure what the answer is.....
        //1. $
        //2. ${$}
        //3. ${${$}}
        //4. ${${${$}}}
        assertEquals("$", result);
        //1. $
        //2. ${$}
        result = engine.evaluate("${${$}} ${$}", map, "keep-unmatched");
        assertEquals("$ $", result);
	}

	@Test
	public void deleteUnmatchedFromOtherTemplates() {
		String result = engine.evaluate("${$}hello${${$}}heh${$}", map, "delete-unmatched");
		assertEquals("helloheh", result);
	}

	@Test
	public void testNonAlphaCaseSensitive2() {
		map.store("/.,123$%678(-+~`?", "pass", true);
		String result = engine.evaluate("${/.,123$%678(-+~`?}", map, "keep-unmatched");
		assertEquals("pass", result);
	}

	@Test
	public void testTemplateOrderNesting2() {
		map.store("${}", "outside", false);

		String result = engine.evaluate("${${}}", map, "keep-unmatched");
		assertEquals("outside", result);
	}

	@Test
	public void testTemplateDeleteToEmpty() {
		map.store("${}", "pass", false);

		String result = engine.evaluate("${${}}", map, "delete-unmatched");
		assertEquals("", result);
	}

  /**
    Tests of other methods for Assignment 2.1
  */
  @Test
  public void testEntryEqualsMethod() {
    map.store("hello", "world", true);
    map.store("hello", "world", false);
    map.store("hello", "World", true);
    map.store("  hello  ", "World", true);
    map.store("  hello  ", "World", null);

    ArrayList entries = map.getEntries();

    assertEquals(false, entries.get(0).equals(entries.get(1)));
    assertEquals(false, entries.get(0).equals(entries.get(2)));
    assertEquals(false, entries.get(2).equals(entries.get(3)));
    assertEquals(false, entries.get(3).equals(entries.get(4)));
    assertEquals(false, entries.get(0).equals(null));
    assertEquals(false, entries.get(0).equals("blah"));
  }

  @Test
  public void testEntryHashCode() {
      map.store("hello", "world", true);
      map.store("hello", "world", false);
      map.store("hallo", "world", null);
      map.store("hallo", "world", true);
      map.store("hello", "earth", true);

      ArrayList entries = map.getEntries();

      assertThat(entries.get(0).hashCode(), is(entries.get(0).hashCode()));
      assertThat(entries.get(0).hashCode(), not(entries.get(1).hashCode()));
      assertThat(entries.get(3).hashCode(), not(entries.get(2).hashCode()));
      assertThat(entries.get(0).hashCode(), not(entries.get(3).hashCode()));
      assertThat(entries.get(0).hashCode(), not(entries.get(4).hashCode()));
  }

  @Test
  public void testTemplateSort() {
    map.store("1", "1", false);
    map.store("2", "2", false);
    map.store("3", "3", false);
    map.store("12", "4", false);
    map.store("23", "5", false);
    map.store("34", "6", false);

    String result = engine.evaluate("${3} ${${1}${2}} ${2} ${3${${1}${2}}} ${23}", map, "keep-unmatched");
		assertEquals("3 4 2 6 5", result);
  }

  @Test
  public void testDoReplace() {
    map.store("bla", "blabla", false);

    String result = engine.evaluate("${bla}", map, "keep-unmatched");
		assertEquals("blabla", result);

    map2.store("bla", "blablah", false);

    result = engine.evaluate("${bla}", map2, "keep-unmatched");
		assertEquals("blablah", result);
  }

  @Test
	public void testSpacing() {
		map.store("${ }", "", false);

		String result = engine.evaluate("${${}}", map, "keep-unmatched");
		assertEquals("", result);
	}

  @Test
	public void testSpacingAgain() {
		map.store("${ }", "${}", false);

		String result = engine.evaluate("${${}}", map, "keep-unmatched");
		assertEquals("${}", result);
	}
}
