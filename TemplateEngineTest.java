import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import st.EntryMap;
import st.TemplateEngine;

public class TemplateEngineTest {

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
    public void Test1() {
        map.store("name", "Adam", false);
        map.store("surname", "Dykes", false);
        String result = engine.evaluate("Hello ${name} ${surname}", map,"delete-unmatched");
        assertEquals("Hello Adam Dykes", result);
    }

    @Test
    public void Test2() {
        map.store("name", "Adam", false);
        map.store("surname", "Dykes", false);
        map.store("age", "29", false);
        String result = engine.evaluate("Hello ${name}, is your age ${age ${symbol}}", map,"delete-unmatched");
        assertEquals("Hello Adam, is your age 29", result);
    }

    
    @Test
    /*
     * Spec1 = null
     */
    public void storeArg1Null() {
      boolean thrown = false;

      try {
    	map.store(null, "Adam", false);
      } catch (Exception e) {
        thrown = true;
      }
      assertTrue(thrown);
    }
    
    @Test
    /*
     * Spec1 = empty
     */
    public void storeArg1Empty() {
      boolean thrown = false;

      try {
    	map.store("" , "Adam", false);
      } catch (Exception e) {
        thrown = true;
      }
      assertTrue(thrown);
    }
    
    @Test
    /*
     * Spec2 = null
     */
    public void storeArg2Null() {
      boolean thrown = false;

      try {
    	map.store("name" , null, false);
      } catch (Exception e) {
        thrown = true;
      }
      assertTrue(thrown);
    }
    
    @Test
    /*
     * ***********unknown result**************
     * Spec2 = Empty
     */
    public void storeArg2Empty() {
      boolean thrown = false;
    
      try {
    	map.store("name" , "", true);

      } catch (Exception e) {
        thrown = true;
      }
      assertFalse(thrown);
      
    }
    
    @Test
    /*
     * Spec3 = true; All cases will not find a match except last
     * case, testing arg2 as empty
     */
    public void storeArg3True() {
      boolean thrown = false;

      try {
    	map.store("name" , "John", true);
        String result = engine.evaluate("Hello ${nAme}", map,"keep-unmatched");
        assertEquals("Hello ${nAme}", result);
        map.store("aGe" , "20", true);
        String result1 = engine.evaluate("${age} today", map,"keep-unmatched");
        assertEquals("${age} today", result1);
    	map.store("nam" , "Edin", true);
        String result2 = engine.evaluate("${nam} yeah", map,"keep-unmatched");
        assertEquals("${nam} yeah", result2);
    	map.store("blank" , "", true);
        String result3 = engine.evaluate("${blank} space", map,"keep-unmatched");
        assertEquals(" space", result3);
    	
      } catch (Exception e) {
        thrown = true;
      }
      assertFalse(thrown);
    }
    
    @Test
    /*
     * Spec3 = false; All cases will find a match
     */
    public void storeArg3False() {
      boolean thrown = false;

      try {
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
      } catch (Exception e) {
        thrown = true;
      }
      assertFalse(thrown);
    }
    
    @Test
    /*
     * Spec3 = null;  All cases will find a match
     */
    public void storeArg3Null() {
      boolean thrown = false;

      try {
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
      } catch (Exception e) {
        thrown = true;
      }
      assertFalse(thrown);
    }
    
    @Test
    /*
     * Spec 4: Entry order is tested based on changes 
     * in argument 2 and ordering of argument 3
     */
    public void storeRepeats() {
      boolean thrown = false;

      try {
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
      } catch (Exception e) {
        thrown = true;
      }
      assertFalse(thrown);
    }
    
	
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
	
	/**
		spec1 - The template string can be NULL or empty. 
				If template string NULL or empty, then the unchanged 
				template string is returned.
	*/
	@Test
	public void testTemplateStringCanBeNull() {
		String result = engine.evaluate(null, map, "delete-unmatched");
		assertNull(result);
	}
	
	@Test
	public void testTemplateStringCanBeEmpty() {
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
	}
	
	/*
		spec3 - Matching mode cannot be NULL and must be one of the 
			possible values ("keep-unmatched" and "delete-unmatched"). 
			If matching mode NULL or other value, it defaults to 
			"delete-unmatched".
    */
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
		map.store("fname", "Rose", false);
		map.store("lname", "Tyler", false);
		
		String result = engine.evaluate("${fname} ${lname} is ${age} years old", map, "foo");
		assertEquals("Rose Tyler is  years old", result);
	}
	
	@Test
	public void testMatchingModeNull() {
		map.store("fname", "Rose", false);
		map.store("lname", "Tyler", false);
		
		String result = engine.evaluate("${fname} ${lname} is ${age} years old", map, null);
		assertEquals("Rose Tyler is  years old", result);
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
	public void testMalformedTemplateMissingTemplatePart() {
		map.store("fname", "Rose", false);
		map.store("lname", "Tyler", false);
		
		String result = engine.evaluate("Hey there {fname} $lname}", map, "delete-unmatched");
		assertEquals("Hey there {fname} $lname}", result);
	}
	
	@Test
	public void testMalformedTemplateOpenWOClose() {
		map.store("fname", "Rose", false);
		map.store("lname", "Tyler", false);
		
		String result = engine.evaluate("Hey there ${fname ${lname}", map, "delete-unmatched");
		assertEquals("Hey there ${fname Tyler", result);
		
		map2.store("fname", "Rose", false);
		map2.store("lname", "Tyler", false);
		
		result = engine.evaluate("Hey there ${fname} ${lname", map2, "delete-unmatched");
		assertEquals("Hey there Rose ${lname", result);
	}
	
	@Test
	public void testMalformedTemplateCloseWOOpen() {
		map.store("fname", "Rose", false);
		map.store("lname", "Tyler", false);
		
		String result = engine.evaluate("Hey there fname} ${lname}", map, "delete-unmatched");
		assertEquals("Hey there fname} Tyler", result);
		
		map2.store("fname", "Rose", false);
		map2.store("lname", "Tyler", false);
		
		result = engine.evaluate("Hey there ${fname} lname}", map2, "delete-unmatched");
		assertEquals("Hey there Rose lname}", result);
	}
	
	@Test
	public void testMalformedTemplateBackards() {
		map.store("fname", "Rose", false);
		map.store("lname", "Tyler", false);
		
		String result = engine.evaluate("Hey there $}fname} ${lname{", map, "delete-unmatched");
		assertEquals("Hey there $}fname} ${lname{", result);
		
		map2.store("fname", "Rose", false);
		map2.store("lname", "Tyler", false);
		
		result = engine.evaluate("Hey there {$fname} }lname${", map2, "delete-unmatched");
		assertEquals("Hey there {$fname} }lname${", result);
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
	}
	
	@Test
	public void testWhitespaceLocationEnds() {
		map.store("Doctor Who", "TARDIS", false);
		map.store("Star Trek", "Enterprise", false);
		map.store("Star Wars", "Millennium Falcon", false);
		
		String result = engine.evaluate("The best ships in the universe: the ${   Doctor Who}, the ${Star Trek   }, and the ${    Star Wars    }", map, "delete-unmatched");
		assertEquals("The best ships in the universe: the TARDIS, the Enterprise, and the Millennium Falcon", result);
	}
	
	@Test
	public void testWhitespaceType() {
		map.store("Doctor Who", "TARDIS", false);
		map.store("Star Trek", "Enterprise", false);
		map.store("Star Wars", "Millennium Falcon", false);
		
		String result = engine.evaluate("The best ships in the universe: the ${Doctor\tWho}, the ${Star\nTrek}, and the ${Star\r\nWars}", map, "delete-unmatched");
		assertEquals("The best ships in the universe: the TARDIS, the Enterprise, and the Millennium Falcon", result);
	}
	
	@Test
	public void testWhitespaceNoneInOriginalWord() {
		map.store("firstname", "Rose", false);
		map.store("last name", "Tyler", false);
		
		String result = engine.evaluate("Hey there ${first name} ${lastname}", map, "delete-unmatched");
		assertEquals("Hey there Rose Tyler", result);
	}
	
	/*
		spec6 - In a template string every "${" and "}" occurrence acts as 
			a boundary of at MOST one template.
            ---> Processing from left-to-right, each "}" occurrence that is 
				not already a boundary to a template is matched to its 
				closest preceding "${" occurrence which also is not already 
				a boundary to a template.
            ---> In the template string "I heard that }: ${name} said: ${we 
				should try or best for winning the ${competition} cup.}" the 
				templates are:
					1 - ${name}
					2 - ${competition}
					3 - ${we should try or best for winning the ${competition} cup.}
    */
	@Test
	public void testNestingMultipleCloses() {
		map.store("fname", "Rose", false);
		map.store("lname", "Tyler", false);
		
		String result = engine.evaluate("Hey there ${fname}} ${lname}}}}}}}", map, "delete-unmatched");
		assertEquals("Hey there Rose} Tyler}}}}}}", result);
		
		map2.store("fname", "Rose", false);
		map2.store("lname", "Tyler", false);
		
		result = engine.evaluate("Hey there ${fname}} ${lname}}}}}}}", map2, "keep-unmatched");
		assertEquals("Hey there Rose} Tyler}}}}}}", result);
	}
	
	@Test
	public void testNestingMultipleOpenings() {
		map.store("fname", "Rose", false);
		map.store("lname", "Tyler", false);
		
		String result = engine.evaluate("Hey there ${${fname} ${${${lname} ${", map, "delete-unmatched");
		assertEquals("Hey there ${Rose ${${Tyler ${", result);
		
		map2.store("fname", "Rose", false);
		map2.store("lname", "Tyler", false);
		
		result = engine.evaluate("Hey there ${${fname} ${${${lname} ${", map2, "keep-unmatched");
		assertEquals("Hey there ${Rose ${${Tyler ${", result);
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
		map.store("Doctor Who", "the Doctor", false);
		
		String result = engine.evaluate("His name is ${${rank}${question}}", map, "delete-unmatched");
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
	
	// NOTE; I don't really know how to test this one, and I suspect this
	//		 is wrong, so please help suggest a better way #TODO
	@Test
	public void testTemplateOrderPureLength() {
		map.store("template", "Give me ${subtemplate}!", false);
		map.store("subtemplate", "5", false);
		
		String result = engine.evaluate("${template}", map, "delete-unmatched");
		assertEquals("Give me 5!", result);
		
		map2.store("really long template name", "Give me ${subtemplate}!", false);
		map2.store("subtemplate", "5", false);
		
		result = engine.evaluate("${really long template name}", map2, "delete-unmatched");
		assertEquals("Give me ${subtemplate}!", result);
	}
	
	@Test
	public void testTemplateOrderWithEqualLengths() {
		
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
		assertEquals("I have a blue house with a blue window. blue are the streets and now the trees are too.  I have a girlfriend, and she is so blue", result);
	}
	
	@Test
	public void testEntryUnused() {
		map.store("lots", "asdfghjkl", false);
		map.store("of", "qwertyuiop", false);
		map.store("things", "zxcvbnm", false);
		map.store("book", "Hitchhiker's Guide to the Galaxy", false);
		
		String result = engine.evaluate("The ${book} is my favourite book", map, "delete-unmatched");
		assertEquals("The Hitchhiker's Guide to the Galaxy is my favourite book", result);
	}
	
	/**
		Extra bits
	*/
	@Test
	public void testAcceptableTemplateCharacters() {
		// TODO
		// a-zA-Z0-9 + special characters (+ emoji 😊)
		// Note especially the characters ${} appearing in template name
	}
	

}
