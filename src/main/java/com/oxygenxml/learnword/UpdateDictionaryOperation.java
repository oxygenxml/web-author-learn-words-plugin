package com.oxygenxml.learnword;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.AuthorOperationWithResult;
import ro.sync.ecss.extensions.api.webapp.WebappRestSafe;
import ro.sync.ecss.extensions.api.webapp.WebappSpellchecker;
import ro.sync.ecss.extensions.api.webapp.access.WebappPluginWorkspace;
import ro.sync.exml.workspace.api.PluginResourceBundle;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;
import ro.sync.exml.workspace.api.spell.Dictionary;

@WebappRestSafe
public class UpdateDictionaryOperation extends AuthorOperationWithResult {

  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(UpdateDictionaryOperation.class.getName());
  
  @Override
  public String getDescription() {
    return null;
  }

  @Override
  public String doOperation(AuthorDocumentModel model, ArgumentsMap args)
      throws AuthorOperationException {

    String result = null;
    String lang = (String) args.getArgumentValue("lang");
    String word = (String) args.getArgumentValue("word");
    String mode = (String) args.getArgumentValue("mode");
    if (mode == null) {
      mode = "learned";
    }
    
    if (word != null && word.length() != 0) {
      WebappSpellchecker spellchecker = model.getSpellchecker();
      TermsDictionary td = (TermsDictionary) spellchecker.getTermsDictionary();
      
      if (mode.equals("forbidden")) {
        td.addForbiddenWord(lang, word);
        result = "ok";
      } else {
        if (td.isForbidden(lang, word)) {
          PluginResourceBundle rb = ((WebappPluginWorkspace)PluginWorkspaceProvider.getPluginWorkspace()).getResourceBundle();
          result = rb.getMessage(TranslationTags.CANNOT_LEARN_FORBIDDEN_WORD);
        } else {
          td.addLearnedWord(lang, word);
          result = "ok";
        }
      }
      
      if (result.equals("ok")) {
        WSOptionsStorage opts = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
        if (opts.getOption(ConfigurationPage.FILE_SELECTED_NAME, null).equals("on")) {
          try {
            synchronizeFile(spellchecker.getTermsDictionary());
          } catch (IOException e) {
            logger.error("Error while synchronizing learn word dictionary ", e);
          }
        } else if (opts.getOption(ConfigurationPage.URL_SELECTED_NAME, null).equals("on")) {
          String learnWordUrl = opts.getOption(ConfigurationPage.URL_NAME, null);
          if (learnWordUrl != null) {
            try {
              URL url = new URL(learnWordUrl);
              HttpURLConnection connection = (HttpURLConnection) url.openConnection();
              connection.setRequestMethod("POST");
              connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
              try (OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream())) {
                out.write("mode=" + mode + "&lang=" + lang + "&word=" + word);
              }
              connection.connect();
            } catch (IOException e) {
              logger.error("Error while sending learn word request ", e);
            }
          } else {
            logger.error("No URL specified for learn word action");
          }
        }
      }
    }
    
    return result;
  }
  
  /**
   * Update the dictionary.
   * 
   * @param dictionary The current dictionary value.
   * 
   * @throws IOException
   */
  private void synchronizeFile(Dictionary dictionary) throws IOException {
    WSOptionsStorage opts = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
    String filePath = opts.getOption(ConfigurationPage.FILE_PATH_NAME, null);
    File termsDictFile = new File(filePath);
    FileUtils.writeStringToFile(termsDictFile, dictionary.toString(), "utf8");
  }
}
