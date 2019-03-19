package com.oxygenxml.learnword;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class LearnWordTest {
  
  private final String en = "en";
  private final String fr = "fr";
  private final String ja = "ja";

  @Test
  public void testCustomMultiLanguageList() {
    String english = "en";
    String french = "fr";
    
    TermsDictionary apiDict = new TermsDictionary();
    apiDict.addForbiddenWord(english, "www");
    apiDict.addForbiddenWord(english, "zzz");
    apiDict.addLearnedWord(english, "wuwuwu");
    
    assertTrue(apiDict.isLearned(english, "wuwuwu"));
    assertFalse(apiDict.isLearned(english, "zzz"));
    // Language exists, word does not.
    assertFalse(apiDict.isLearned(english, "wzwzwz"));
    // Language was not added, word added to different language.
    assertFalse(apiDict.isLearned(french, "wuwuwu"));
    
    
    assertTrue(apiDict.isForbidden(english, "www"));
    assertFalse(apiDict.isForbidden(english, "wuwuwu"));
    // Language exists, word does not.
    assertFalse(apiDict.isForbidden(english, "wzwzwz"));
    // Language was not added, word added to different language.
    assertFalse(apiDict.isForbidden(french, "wuwuwu"));
    
    // Forbidden word cannot be learned.
    apiDict.addForbiddenWord(french, "wzwzwz");
    apiDict.addLearnedWord(french, "wzwzwz");
    assertTrue(apiDict.isForbidden(french, "wzwzwz"));
    assertFalse(apiDict.isLearned(french, "wzwzwz"));
    
    // Adding forbidden word will remove learned word.
    apiDict.addForbiddenWord(english, "wuwuwu");
    assertTrue(apiDict.isForbidden(english, "wuwuwu"));
    assertFalse(apiDict.isLearned(english, "wuwuwu"));
  }
  
  /**
   * Check getting variable numbers of suggestions and sorting.
   */
  @Test
  public void testGetSuggestions() {
    TermsDictionary apiDict = new TermsDictionary();
    apiDict.addLearnedWord(en, "aaaaaa");
    apiDict.addLearnedWord(en, "aaaaa");
    apiDict.addLearnedWord(en, "aaaa");
    apiDict.addLearnedWord(en, "bbbbbb");
    apiDict.addLearnedWord(en, "bbbbb");
    apiDict.addLearnedWord(en, "bbbb");
    apiDict.addLearnedWord(en, "cccccc");
    
    apiDict.addLearnedWord(fr, "aaaaaa");
    
    
    String[] s = apiDict.getSuggestions(en, "aaaaaaa");
    assertEquals(1, s.length);
    assertEquals("aaaaaa", s[0]);
    
    apiDict.setNumberOfSuggestions(2);
    s = apiDict.getSuggestions(en, "aaaaaaa");
    assertEquals(2, s.length);
    assertEquals("aaaaaa", s[0]);
    assertEquals("aaaaa", s[1]);
    
    // WA-2842: There is now a relevance treshold for suggestions. 
    apiDict.setNumberOfSuggestions(4);
    s = apiDict.getSuggestions(en, "aaaaaaa");
    assertEquals(2, s.length);
    assertEquals("aaaaaa", s[0]);
    assertEquals("aaaaa", s[1]);
    
    // Number of suggestions is greater than number of learned words.
    s = apiDict.getSuggestions(fr, "aaaaaaa");
    assertEquals(1, s.length);
    assertEquals("aaaaaa", s[0]);
    
    s = apiDict.getSuggestions(ja, "aaaaaaa");
    assertEquals(0, s.length);
  }
  
  /**
   * Check that suggestions are more relevant.
   */
  @Test
  public void testGetRelevantSuggestions() {
    TermsDictionary apiDict = new TermsDictionary();
    
    apiDict.setNumberOfSuggestions(4);
    apiDict.addLearnedWord(en, "aaaaaa");
    
    String[] s = apiDict.getSuggestions(en, "aaabbb");
    assertEquals(0, s.length);
    
    // If word sizes vary too much, not relevant.
    s = apiDict.getSuggestions(en, "aa");
    assertEquals(0, s.length);
    s = apiDict.getSuggestions(en, "longaaaaaa");
    assertEquals(0, s.length);
    
    apiDict.addLearnedWord(en, "aaa");
    s = apiDict.getSuggestions(en, "aa");
    assertEquals(1, s.length);
    s = apiDict.getSuggestions(en, "aaaa");
    assertEquals(1, s.length);
  }
  
  /**
   * WA-2827: Check that loading from file method works as expected.
   */
  @Test
  public void testFileOperations() {
    TermsDictionary apiDict = new TermsDictionary();
    try {
      String filePath = new File("test-files/from_url.xml").getAbsolutePath();
      apiDict.addWordsFromFile(filePath);
    } catch (IOException | ParserConfigurationException | SAXException e) {
      e.printStackTrace();
    }
    assertTrue(apiDict.isLearned("en", "wordb"));
    assertTrue(apiDict.isLearned("fr", "worta"));
    assertTrue(apiDict.isForbidden("en", "should"));
    assertTrue(apiDict.isForbidden("de", "ignoriert"));
  }
}
