package com.oxygenxml.learnword.xmlmarshal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MarshalLanguage {
  private String code;
  
  private List<MarshalWord> wordList;
  
  public MarshalLanguage () {
  }
  
  public MarshalLanguage (String langCode, List<String> words) {
    code = langCode;
    wordList = new ArrayList<>();
    words.forEach(word -> wordList.add(new MarshalWord(word)));
  }

  /**
   * @return the wordList
   */
  @XmlElement(name="w")
  public List<MarshalWord> getWordList() {
    return wordList;
  }

  /**
   * @param wordList the wordList to set
   */
  public void setWordList(List<MarshalWord> wordList) {
    this.wordList = wordList;
  }

  /**
   * @return the code
   */
  public String getCode() {
    return code;
  }

  /**
   * @param code the code to set
   */
  @XmlAttribute
  public void setCode(String code) {
    this.code = code;
  }
}
