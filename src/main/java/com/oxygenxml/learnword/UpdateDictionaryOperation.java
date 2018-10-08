package com.oxygenxml.learnword;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.AuthorOperationWithResult;
import ro.sync.ecss.extensions.api.webapp.WebappRestSafe;
import ro.sync.ecss.extensions.api.webapp.WebappSpellchecker;
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
    
    String mode = (String) args.getArgumentValue("mode");
    String lang = (String) args.getArgumentValue("lang");
    String word = (String) args.getArgumentValue("word");
    
    String result = null;
    
    if (word != null && word.length() != 0) {
      WebappSpellchecker spellchecker = model.getSpellchecker();
      TermsDictionary td = (TermsDictionary) spellchecker.getTermsDictionary();
      
      if (mode.equals("forbidden")) {
        td.addForbiddenWord(lang, word);
        result = "ok";
      } else {
        if (td.isForbidden(lang, word)) {
          result = "Cannot learn forbidden word.";
        } else {
          td.addLearnedWord(lang, word);
          result = "ok";
        }
      }
      
      if (result.equals("ok")) {
        try {
          synchronizeFile(spellchecker.getTermsDictionary());
        } catch (IOException e) {
          logger.error("Error while synchronizing learn word dictionary");
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
    
    if (opts.getOption(ConfigurationPage.FILE_SELECTED_NAME, null).equals("on")) {
      String filePath = opts.getOption(ConfigurationPage.FILE_PATH_NAME, null);
      File termsDictFile = new File(filePath);
      FileUtils.writeStringToFile(termsDictFile, dictionary.toString(), "utf8");
    } else if (opts.getOption(ConfigurationPage.URL_SELECTED_NAME, null).equals("on")) {
      // TODO: do post request to update dictionary file from URL.
    }
  }
}
