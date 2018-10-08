package com.oxygenxml.learnword.xmlmarshal;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Dictionary")
public class MarshalDictionary {
  private List<MarshalLanguage> learnedWords;
  
  private List<MarshalLanguage> forbiddenWords;
  
  
  public MarshalDictionary() {
    // Empty constructor for jaxb
  }

  public MarshalDictionary(
      Map<String, Set<String>> learnedWords, 
      Map<String, Set<String>> forbiddenWords) {
    
    if (learnedWords != null) {
      learnedWords.forEach((language, wordsSet) -> {
        MarshalLanguage marshalLang = new MarshalLanguage(language, new ArrayList<>(wordsSet));
        this.addLearnedWords(marshalLang);
      });
    }
    if (forbiddenWords != null) {
      forbiddenWords.forEach((language, wordsSet) -> {
        MarshalLanguage marshalLang = new MarshalLanguage(language, new ArrayList<>(wordsSet));
        this.addForbiddenWords(marshalLang);
      });
    }
  }

  /**
   * @return the learnedWords
   */
  @XmlElementWrapper(name="Learned")
  @XmlElement(name="Language")
  public List<MarshalLanguage> getLearnedWords() {
    return learnedWords;
  }

  /**
   * @param learnedWords the learnedWords to set
   */
  public void setLearnedWords(List<MarshalLanguage> learnedWords) {
    this.learnedWords = learnedWords;
  }

  /**
   * @return the ignoredWords
   */
  @XmlElementWrapper(name="Forbidden")
  @XmlElement(name="Language")
  public List<MarshalLanguage> getForbiddenWords() {
    return forbiddenWords;
  }

  /**
   * @param ignoredWords the ignoredWords to set
   */
  public void setForbiddenWords(List<MarshalLanguage> ignoredWords) {
    this.forbiddenWords = ignoredWords;
  }
  
  public void addLearnedWords(MarshalLanguage lw) {
    if (learnedWords == null) {
      learnedWords = new ArrayList<>();
    }
    learnedWords.add(lw);
  }
  
  public void addForbiddenWords(MarshalLanguage lw) {
    if (forbiddenWords == null) {
      forbiddenWords = new ArrayList<>();
    }
    forbiddenWords.add(lw);
  }
  
  public String toString() {
    StringWriter sw = new StringWriter();
    try {
      JAXBContext context = JAXBContext.newInstance(MarshalDictionary.class);
      Marshaller m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      m.marshal(this, sw);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return sw.toString();
  }
}
