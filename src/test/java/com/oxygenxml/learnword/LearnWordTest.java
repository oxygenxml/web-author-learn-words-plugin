package com.oxygenxml.learnword;

import static org.junit.Assert.*;

import org.junit.Test;

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
    
    apiDict.setNumberOfSuggestions(4);
    s = apiDict.getSuggestions(en, "aaaaaaa");
    assertEquals(4, s.length);
    assertEquals("aaaaaa", s[0]);
    assertEquals("aaaaa", s[1]);
    assertEquals("aaaa", s[2]);
    assertEquals("bbbb", s[3]);
    
    // Number of suggestions is greater than number of learned words.
    s = apiDict.getSuggestions(fr, "aaaaaaa");
    assertEquals(1, s.length);
    assertEquals("aaaaaa", s[0]);
    
    s = apiDict.getSuggestions(ja, "aaaaaaa");
    assertEquals(0, s.length);
  }
}
