package com.oxygenxml.learnword;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.oxygenxml.learnword.xmlmarshal.MarshalDictionary;

import ro.sync.exml.workspace.api.spell.Dictionary;

@XmlRootElement(name = "WordList")
public class TermsDictionary implements Dictionary {
  
  private static final Logger logger = Logger.getLogger(TermsDictionary.class.getName());

  
  private static final String LEARNED_WORDS_TYPE = "Learned";
  private static final String FORBIDDEN_WORDS_TYPE = "Forbidden";
  
  /**
   * Number of suggestions to display in the contextmenu.
   */
  private int numberOfSuggestions = 1;
  
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
    if (!isForbidden(lang, word)) {
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
      learnedForLang.remove(word);
    }
  }
  
  public boolean isLearned(String language, String word) {
    return learnedWords.containsKey(language) && learnedWords.get(language).contains(word);
  }
  
  public boolean isForbidden(String lang, String word) {
    return forbiddenWords.containsKey(lang) && forbiddenWords.get(lang).contains(word);
  }
  
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
  
  public void addWordsFromString (String xmlString) 
      throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.parse(new ByteArrayInputStream(xmlString.getBytes()));
    
    Node learnedWordsElement = doc.getElementsByTagName(LEARNED_WORDS_TYPE).item(0);
    if (learnedWordsElement != null) {
      loadWordsOfType(learnedWordsElement, LEARNED_WORDS_TYPE);
    }
    
    Node forbiddenWordsElement = doc.getElementsByTagName(FORBIDDEN_WORDS_TYPE).item(0);
    if (forbiddenWordsElement != null) {
      loadWordsOfType(forbiddenWordsElement, FORBIDDEN_WORDS_TYPE);
    }
  }

  /**
   * Load the words from the container element according to type.
   * @param elementFromUrl The element which contains words of a certain type.
   * @param type The type of words contained in the element.
   */
  private void loadWordsOfType(Node elementFromUrl, String type) {
    NodeList wordElementsFromUrl = elementFromUrl.getChildNodes();
    for (int i = 0; i < wordElementsFromUrl.getLength(); i++) {
      Node language = wordElementsFromUrl.item(i);
      String languageCode = language.getNodeName(); 
      if (language.getNodeType() == 1) {
        Element langEl = (Element) language;
        if (langEl.getAttribute("code") != null) {
          languageCode = langEl.getAttribute("code");
        }
      }
      
      for (int j = 0; j < language.getChildNodes().getLength(); j++) {
        Node currentWord = language.getChildNodes().item(j);
        if (currentWord.getNodeType() == 1) {
          if (type.equals(LEARNED_WORDS_TYPE)) {
            addLearnedWord(languageCode, currentWord.getTextContent());
          } else if (type.equals(FORBIDDEN_WORDS_TYPE)) {
            addForbiddenWord(languageCode, currentWord.getTextContent());
          }
        }
      }
    }
  }
  
  /**
   * Get the string representation of the dictionary, 
   * used to write to file after updating the word lists.
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
      fileContent = FileUtils.readFileToString(targetFile, "UTF-8");
    } catch (FileNotFoundException e) {
      // The file does not exist on disk, so no words to be added.
      logger.debug("Learned words dictionary file not found", e);
    }
    if (fileContent != "") {
      addWordsFromString(fileContent);
    }
  }
}
