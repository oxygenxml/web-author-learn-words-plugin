package com.oxygenxml.learnword;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import ro.sync.ecss.extensions.api.webapp.plugin.PluginConfigExtension;

public class ConfigurationPage extends PluginConfigExtension {

  static final String FILE_SELECTED_NAME = "file-selected.name";
  static final String FILE_PATH_NAME = "file-path.name";
  
  static final String URL_SELECTED_NAME = "url-selected.name";
  static final String URL_NAME = "url.name";
  
  static final String SUGGESTIONS_NUMBER_NAME = "suggestions-no.name";

  private static final String DEFAULT_URL = "";
  
  static final String READ_ONLY_MODE = "lw.read-only-mode";
  
  

  /**
   * @see ro.sync.ecss.extensions.api.webapp.plugin.PluginConfigExtension#getOptionsForm()
   */
  @Override
  public String getOptionsForm() {  
    HashMap<String, Object> context = new HashMap<>();
    
    String suggestionsNumberValue = getOption(SUGGESTIONS_NUMBER_NAME, "1");
    context.put("SUGGESTIONS_NUMBER_NAME", SUGGESTIONS_NUMBER_NAME);
    context.put("suggestionsNumberValue", suggestionsNumberValue);
    
    String filePathValue = getOption(FILE_PATH_NAME, LearnWordPlugin.getDefaultTermsFilePath());
    String fileSelectedChecked = getOption(FILE_SELECTED_NAME, "on");
    context.put("FILE_SELECTED_NAME", FILE_SELECTED_NAME);
    context.put("fileSelectedChecked", fileSelectedChecked.equals("on"));
    context.put("FILE_PATH_NAME", FILE_PATH_NAME);
    context.put("filePathValue", filePathValue);
    

    String urlValue = getOption(URL_NAME, DEFAULT_URL);
    String urlSelectedChecked = getOption(URL_SELECTED_NAME, "off");
    context.put("URL_SELECTED_NAME", URL_SELECTED_NAME);
    context.put("urlSelectedChecked", urlSelectedChecked.equals("on"));
    context.put("URL_NAME", URL_NAME);
    context.put("urlValue", urlValue);
    
    boolean readOnlyValue = getOption(READ_ONLY_MODE, "off").equals("on");
    context.put("READ_ONLY_MODE", READ_ONLY_MODE);
    context.put("readOnlyValue", readOnlyValue);
    
    return getConfigurationForm(context);
  }

  @Override
  public String getPath() {
    return "spellcheck-learn-words-config";
  }
  
  /**
   * @param contextMap A map of values needed to fill the configuration form template.
   * @return The configuration form.
   */
  private String getConfigurationForm(Map<String, Object> contextMap) {
    String templatesDir = LearnWordPlugin.getBaseDir().getAbsolutePath();
    if (!templatesDir.endsWith("/")) {
      templatesDir += "/";
    }
    
    TemplateEngine htmlTplEngine = new TemplateEngine();
    FileTemplateResolver htmlTplResplver = new FileTemplateResolver();
    htmlTplResplver.setTemplateMode(TemplateMode.HTML);
    htmlTplResplver.setCacheable(true);
    htmlTplResplver.setPrefix(templatesDir);
    htmlTplResplver.setSuffix(".html");
    htmlTplEngine.setTemplateResolver(htmlTplResplver);
    
    Context context = new Context();
    context.setVariables(contextMap);
    
    return htmlTplEngine.process("configuration-form", context);
  }

  @Override
  public String getOptionsJson() {
    // Shows a console error if null.
    return "{\"lw.read-only-mode\": \"" + getOption(READ_ONLY_MODE, "on") + "\"}";
  }
}
