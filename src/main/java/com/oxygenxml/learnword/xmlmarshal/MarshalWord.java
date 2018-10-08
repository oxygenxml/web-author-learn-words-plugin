package com.oxygenxml.learnword.xmlmarshal;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="w")
public class MarshalWord {
  private String text;

  public MarshalWord() {
  }
  
  public MarshalWord(String word) {
    text = word;
  }
  
  /**
   * @return the text
   */
  @XmlValue
  public String getText() {
    return text;
  }

  /**
   * @param text the text to set
   */
  public void setText(String text) {
    this.text = text;
  }
}
