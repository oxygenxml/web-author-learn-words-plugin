package com.oxygenxml.learnword;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.oxygenxml.learnword.xmlmarshal.MarshalDictionary;

import ro.sync.exml.workspace.api.spell.Dictionary;

@XmlRootElement(name = "WordList")
public class TermsDictionary implements Dictionary {
  
  private static final Logger logger = LogManager.getLogger(TermsDictionary.class.getName());

  
  private static final String LEARNED_WORDS_TYPE = "Learned";
  private static final String FORBIDDEN_WORDS_TYPE = "Forbidden";
  /**
   * Regex for splitting strings on "-" or "_".
   */
  private static final Pattern LANG_SEPARATOR_REGEX = Pattern.compile("-|_");
  
  /**
   * Number of suggestions to display in the contextmenu.
   */
  private int numberOfSuggestions = 1;
  
  /**
   * Expected length for the short language code.
   */
  private static final int SHORT_LANG_LENGTH = 2;
  
  private HashMap<String, Set<String>> learnedWords = new HashMap<>();
  
  private HashMap<String, Set<String>> forbiddenWords = new HashMap<>();
  
  public TermsDictionary () {}
  
  public TermsDictionary (String fileContents) throws ParserConfigurationException, SAXException, IOException {
    addWordsFromString(fileContents);
  } 
  
  public TermsDictionary (Map<String, Set<String>> lWords, Map<String, Set<String>> fWords) {
    learnedWords = (HashMap<String, Set<String>>) lWords;
    forbiddenWords = (HashMap<String, Set<String>>) fWords;
  }
  
  /**
   * Add a new learned word for a certain language. 
   * If the word is forbidden, it should not be learned.
   * 
   * @param lang The language to learn word for.
   * @param word The word to learn.
   */
  public void addLearnedWord(String lang, String word) {
    // If word is forbidden it should not get learned.
    if (!wordExistsInSet(lang, word, forbiddenWords)) {
      HashSet<String> wordsForLang = (HashSet<String>) learnedWords.get(lang); 
      if (wordsForLang == null) {
        wordsForLang = new HashSet<>();
      }
      wordsForLang.add(word);
      learnedWords.put(lang, wordsForLang);
    }
  }

  /**
   * Add a new forbidden word for a certain language.
   * The word should be removed from the learned words so it does not show up in suggestions. 
   * 
   * @param lang The language to forbid word for.
   * @param word The word to forbid.
   */
  public void addForbiddenWord(String lang, String word) {
    removeLearnedWord(lang, word);
    HashSet<String> wordsForLang = (HashSet<String>) forbiddenWords.get(lang); 
    if (wordsForLang == null) {
      wordsForLang = new HashSet<>();
    }
    wordsForLang.add(word);
    forbiddenWords.put(lang, wordsForLang);
  }

  /**
   * Remove a learned word from a certain language.
   * 
   * @param lang The language to remove learned word from.
   * @param word The word to remove from learned words.
   */
  private void removeLearnedWord(String lang, String word) {
    if (isLearned(lang, word)) {
      Set<String> learnedForLang = learnedWords.get(lang);
      if (learnedForLang != null) {
        learnedForLang.remove(word);
      }
    }
  }

  /**
   * Check if a word from a certain language exists in a certain collection.
   * If the language code is specific (en_US), also check the general language (en).
   * @param word The word to search for.
   * @param langCode The language to find the word in.
   * @param wordsMap The collection of words to check in.
   * @return Whether the word was found.
   */
  private boolean wordExistsInSet(String langCode, String word, HashMap<String,Set<String>> wordsMap) {
    return wordsMap.containsKey(langCode) && wordsMap.get(langCode).contains(word);
  }
  
  /**
   * Get the two letter language code.
   * If the language code is the two letter language code (ISO 639-1), return it as it is.
   * If the language code is specific (language_region or language-region), return the two letter part before "_" or "-".
   * 
   * For en_US it returns en.
   * For en it returns en.
   * 
   * @param langCode The language code to check.
   * @return The two letter language code.
   */
  private String getShortLangCode(String langCode) {
    String shortLangCode = langCode;
    if (langCode.length() > SHORT_LANG_LENGTH) {
      String[] langPieces = LANG_SEPARATOR_REGEX.split(langCode);
      if (langPieces.length > 1) {
        shortLangCode = langPieces[0];
      }
    } 
    return shortLangCode;
  }
  
  /**
   * Check if a word is learned for a certain language.
   * If the specific language (en_US) does not contain this learned word, 
   * the general language (en) will also be checked.
   * 
   * Priority order for en_US:
   * Forbidden (en_US) > Learned (en_US) > Forbidden (en) > Learned (en)
   * 
   * @param langCode The language to find the word in.
   * @param word The word to search for.
   * @return Whether the word is learned.
   */
  public boolean isLearned(String langCode, String word) {
    boolean learned = false;
    // Forbidden is stronger than learned
    if (!isForbidden(langCode, word)) {
      // Check exact language code
      learned = wordExistsInSet(langCode, word, learnedWords);
      if (!learned) {
        // If language is specific, also check generic language code
        String shortLang = getShortLangCode(langCode);
        if (!shortLang.equals(langCode)) {
          learned = isLearned(shortLang, word);
        }
      }
    }
    return learned;
  }
  
  /**
   * Check if a word is forbidden for a certain language.
   * If the specific language (en_US) does not contain this forbidden word, 
   * the general language (en) will also be checked.
   * 
   * Priority order for en_US:
   * Forbidden (en_US) > Learned (en_US) > Forbidden (en) > Learned (en)
   * 
   * @param langCode The language to find the word in.
   * @param word The word to search for.
   * @return Whether the word is forbidden.
   */
  public boolean isForbidden(String langCode, String word) {
    boolean forbidden = wordExistsInSet(langCode, word, forbiddenWords);
    // It might be forbidden in the generic language.
    if (!forbidden) {
      // Learned in specific language is stronger than forbidden in generic.
      boolean isLearned = wordExistsInSet(langCode, word, learnedWords);
      if (!isLearned) {
        // If language is specific, also check generic language code
        String shortLang = getShortLangCode(langCode);
        if (!shortLang.equals(langCode)) {
          forbidden = wordExistsInSet(shortLang, word, forbiddenWords);
        }
      }
    }
    return forbidden;
  }
  
  /**
   * Get suggestions from the learned words for a word in a certain language.
   * @param lang The language to check.
   * @param word The word to get suggestions for.
   * @return The list of suggestions from learned words.
   */
  public String[] getSuggestions(String lang, String word) {
    String[] result = {};
    if (lang != null && word != null && 
      learnedWords != null && learnedWords.get(lang) != null) {
        result = SuggestionsHandler.getSuggestions(word, learnedWords.get(lang), numberOfSuggestions);
    }
    return result;
  }
  
  
  /**
   * Load the api dictionary from an xml file from an URL.
   * @param url The url to check for the dictionary xml file.
   * 
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws SAXException
   */
  public void addWordsFromUrl(URL url) 
      throws IOException, ParserConfigurationException, SAXException {
    InputStream openStream = url.openStream();
    String string = IOUtils.toString(openStream);
    addWordsFromString(string);
    openStream.close();
  }
  
  /**
   * Parse the dictionary string and load the words.
   * @param xmlString The dictionary in string form.
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   */
  public void addWordsFromString (String xmlString) 
      throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
    
    loadWordsOfType(doc, LEARNED_WORDS_TYPE);
    loadWordsOfType(doc, FORBIDDEN_WORDS_TYPE);
  }

  /**
   * Load the words from the element containing words of a certain type.
   * @param element The element which contains words of a certain type.
   * @param type The type of words contained in the element.
   */
  private void loadWordsOfType(Document doc, String type) {
    Node categoryElement = doc.getElementsByTagName(type).item(0);
    if (categoryElement != null) {
      NodeList wordElementsFromUrl = categoryElement.getChildNodes();
      for (int i = 0; i < wordElementsFromUrl.getLength(); i++) {
        loadWordsOfTypeFromLanguage(type, wordElementsFromUrl.item(i));
      }
    }
  }

  /**
   * Load the words from the language element according to type.
   * @param type The type of words contained in the element.
   * @param languageElement The element which contains words for a certain language.
   */
  private void loadWordsOfTypeFromLanguage(String type, Node languageElement) {
    String languageCode = languageElement.getNodeName(); 
    if (languageElement.getNodeType() == Node.ELEMENT_NODE) {
      Element langEl = (Element) languageElement;
      if (langEl.getAttribute("code") != null) {
        languageCode = langEl.getAttribute("code");
      }
    }
    
    for (int i = 0; i < languageElement.getChildNodes().getLength(); i++) {
      Node currentWord = languageElement.getChildNodes().item(i);
      if (currentWord.getNodeType() == Node.ELEMENT_NODE) {
        if (type.equals(LEARNED_WORDS_TYPE)) {
          addLearnedWord(languageCode, currentWord.getTextContent());
        } else if (type.equals(FORBIDDEN_WORDS_TYPE)) {
          addForbiddenWord(languageCode, currentWord.getTextContent());
        }
      }
    }
  }
  
  /**
   * Get the string representation of the dictionary, 
   * used to write to file after updating the word lists.
   * @return The string representation of the dictionary
   */
  public String toString() {
    return new MarshalDictionary(learnedWords, forbiddenWords).toString();
  }

  /**
   * @return the numberOfSuggestions
   */
  public int getNumberOfSuggestions() {
    return numberOfSuggestions;
  }

  /**
   * Set the number of suggestions to display in the context menu.
   * @param numberOfSuggestions The number of suggestions to display.
   */
  public void setNumberOfSuggestions(int numberOfSuggestions) {
    this.numberOfSuggestions = numberOfSuggestions;
  }

  /**
   * Load words from the specified file.
   * @param filePath The file path to load words from.
   * 
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws SAXException
   */
  public void addWordsFromFile(String filePath) throws IOException, ParserConfigurationException, SAXException {
    File targetFile = new File(filePath);
    String fileContent = "";
    try {
      fileContent = FileUtils.readFileToString(targetFile, StandardCharsets.UTF_8.toString());
    } catch (FileNotFoundException e) {
      // The file does not exist on disk, so no words to be added.
      logger.debug("Learned words dictionary file not found", e);
    }
    if (!fileContent.equals("")) {
      addWordsFromString(fileContent);
    }
  }
}
