package com.oxygenxml.learnword;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import com.oxygenxml.learnword.xmlmarshal.MarshalDictionary;
import com.oxygenxml.learnword.xmlmarshal.MarshalLanguage;
import com.oxygenxml.learnword.xmlmarshal.MarshalWord;

public class TermsDictionaryTest {
  
  private String xmlTestFile2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
      + "<Dictionary>\n" + 
      "    <Forbidden>\n" + 
      "        <Language code=\"en\">\n" + 
      "            <w>forbidden2</w>\n" + 
      "            <w>forbidden1</w>\n" + 
      "        </Language>\n" + 
      "        <Language code=\"de\">\n" + 
      "            <w>verboten1</w>\n" + 
      "            <w>verboten2</w>\n" + 
      "        </Language>\n" + 
      "    </Forbidden>\n" + 
      "    <Learned>\n" + 
      "        <Language code=\"en\">\n" + 
      "            <w>wub1</w>\n" + 
      "            <w>wub2</w>\n" + 
      "        </Language>\n" + 
      "        <Language code=\"de\">\n" + 
      "            <w>der wub</w>\n" + 
      "            <w>das wub</w>\n" + 
      "        </Language>\n" + 
      "    </Learned>\n" + 
      "</Dictionary>\n";
  
  @Test
  public void wordMarshalTest() throws JAXBException {
    MarshalWord w = new MarshalWord();
    w.setText("huehue");
    JAXBContext context = JAXBContext.newInstance(MarshalWord.class);
    Marshaller m = context.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    
    StringWriter sw = new StringWriter();
    m.marshal(w, sw);
    assertTrue(sw.toString().contains("<w>huehue</w>"));
  }
  
  @Test
  public void languageMarshalTest() throws JAXBException {
    MarshalLanguage l = new MarshalLanguage("en", Arrays.asList("wub1", "wub2"));
    
    JAXBContext context = JAXBContext.newInstance(MarshalLanguage.class);
    Marshaller m = context.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    
    StringWriter sw = new StringWriter();
    m.marshal(l, sw);
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
        "<marshalLanguage code=\"en\">\n" + 
        "    <w>wub1</w>\n" + 
        "    <w>wub2</w>\n" + 
        "</marshalLanguage>\n", sw.toString());
  }
  
  @Test
  public void dictionaryMarshalTest() throws JAXBException {
    MarshalLanguage l1 = new MarshalLanguage("en", Arrays.asList("wub1", "wub2"));
    MarshalLanguage l2 = new MarshalLanguage("de", Arrays.asList("der wub", "das wub"));
    
    MarshalLanguage l3 = new MarshalLanguage("en", Arrays.asList("forbidden2", "forbidden1"));
    MarshalLanguage l4 = new MarshalLanguage("de", Arrays.asList("verboten1", "verboten2"));
    
    MarshalDictionary dict = new MarshalDictionary();
    dict.addLearnedWords(l1);
    dict.addLearnedWords(l2);
    dict.addForbiddenWords(l3);
    dict.addForbiddenWords(l4);
    
    JAXBContext context = JAXBContext.newInstance(MarshalDictionary.class);
    Marshaller m = context.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    
    StringWriter sw = new StringWriter();
    m.marshal(dict, sw);
    assertEquals(sw.toString(), xmlTestFile2);
  }
  
  @Test
  public void dictionaryUnmarshalTest() throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(MarshalDictionary.class);

    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    StringReader sr = new StringReader(xmlTestFile2);
    MarshalDictionary dict = (MarshalDictionary) jaxbUnmarshaller.unmarshal(sr);
    List<MarshalLanguage> lw = dict.getLearnedWords();
    List <MarshalLanguage> fw = dict.getForbiddenWords();
    
    assertEquals(2, lw.size());
    assertEquals(2, fw.size());
  }
  
}
