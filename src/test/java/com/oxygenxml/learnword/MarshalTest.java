package com.oxygenxml.learnword;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.oxygenxml.learnword.TermsDictionary;

public class MarshalTest {
  
  @Test
  public void testEmptyDictionary() {
    TermsDictionary td = new TermsDictionary();
    String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
        "<Dictionary/>\n";
    assertEquals(expected, td.toString());
  }
  
  @Test
  public void testDictionaryOneLearned() {
    TermsDictionary td = new TermsDictionary();
    td.addLearnedWord("de", "hue");
    
    String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
        "<Dictionary>\n" + 
        "    <Learned>\n" + 
        "        <Language code=\"de\">\n" + 
        "            <w>hue</w>\n" + 
        "        </Language>\n" + 
        "    </Learned>\n" + 
        "</Dictionary>\n";
    assertEquals(expected, td.toString());
  }
  
  @Test
  public void testDictionaryOneForbidden() {
    TermsDictionary td = new TermsDictionary();
    td.addForbiddenWord("ja", "hue");
    
    String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
        "<Dictionary>\n" + 
        "    <Forbidden>\n" + 
        "        <Language code=\"ja\">\n" + 
        "            <w>hue</w>\n" + 
        "        </Language>\n" + 
        "    </Forbidden>\n" + 
        "</Dictionary>\n";
    assertEquals(expected, td.toString());
  }
  
  @Test
  public void testDictionaryOneOfEachForbiddenFirst() {
    TermsDictionary td = new TermsDictionary();
    td.addForbiddenWord("fr", "hue");
    td.addLearnedWord("fr", "hue");
    
    String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
        "<Dictionary>\n" + 
        "    <Forbidden>\n" + 
        "        <Language code=\"fr\">\n" + 
        "            <w>hue</w>\n" + 
        "        </Language>\n" + 
        "    </Forbidden>\n" + 
        "</Dictionary>\n";
    assertEquals(expected, td.toString());
  }
  
  @Test
  public void testDictionaryOneOfEachLearnedFirst() {
    TermsDictionary td = new TermsDictionary();
    td.addForbiddenWord("fr", "hue");
    td.addLearnedWord("fr", "hue");
    
    String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
        "<Dictionary>\n" + 
        "    <Forbidden>\n" + 
        "        <Language code=\"fr\">\n" + 
        "            <w>hue</w>\n" + 
        "        </Language>\n" + 
        "    </Forbidden>\n" + 
        "</Dictionary>\n";
    assertEquals(expected, td.toString());
  }
  
  @Test
  public void testDictionaryMoreOfEach() {
    TermsDictionary td = new TermsDictionary();
    td.addLearnedWord("en", "huea");
    td.addLearnedWord("de", "hueb");
    td.addLearnedWord("de", "huebb");
    
    td.addForbiddenWord("fr", "huec");
    td.addForbiddenWord("ja", "hued");
    td.addForbiddenWord("ja", "huedd");
    
    String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
        "<Dictionary>\n" + 
        "    <Forbidden>\n" + 
        "        <Language code=\"ja\">\n" +
        "            <w>huedd</w>\n" +
        "            <w>hued</w>\n" + 
        "        </Language>\n" + 
        "        <Language code=\"fr\">\n" + 
        "            <w>huec</w>\n" + 
        "        </Language>\n" +
        "    </Forbidden>\n" + 
        "    <Learned>\n" +
        "        <Language code=\"de\">\n" + 
        "            <w>huebb</w>\n" +
        "            <w>hueb</w>\n" +
        "        </Language>\n" + 
        "        <Language code=\"en\">\n" + 
        "            <w>huea</w>\n" + 
        "        </Language>\n" +
        "    </Learned>\n" + 
        "</Dictionary>\n";
    assertEquals(expected, td.toString());
  }
}
