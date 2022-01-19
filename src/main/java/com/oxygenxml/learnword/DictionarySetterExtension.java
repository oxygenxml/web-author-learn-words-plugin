package com.oxygenxml.learnword;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import ro.sync.basic.util.NumberFormatException;
import ro.sync.basic.util.NumberParserUtil;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.WebappSpellchecker;
import ro.sync.ecss.extensions.api.webapp.access.EditingSessionOpenVetoException;
import ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener;
import ro.sync.ecss.extensions.api.webapp.access.WebappPluginWorkspace;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class DictionarySetterExtension implements WorkspaceAccessPluginExtension {
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = LogManager.getLogger(DictionarySetterExtension.class.getName());

  private TermsDictionary apiDict;
  
  private WebappSpellchecker spellchecker;
  
  public void applicationStarted(StandalonePluginWorkspace pluginWorkspaceAccess) {
    WebappPluginWorkspace ws = (WebappPluginWorkspace) pluginWorkspaceAccess;
    ws.addEditingSessionLifecycleListener(new WebappEditingSessionLifecycleListener() {
      @Override
      public void editingSessionAboutToBeStarted(String docId, String licenseeId, URL systemId,
          Map<String, Object> options) throws EditingSessionOpenVetoException {
        // Before editing session starts, get the api dictionary.
        try {
          setDefaultOptions();
          apiDict = new TermsDictionary();
          WSOptionsStorage opts = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
          String fileSelected = opts.getOption(ConfigurationPage.FILE_SELECTED_NAME, null);
          String urlSelected = opts.getOption(ConfigurationPage.URL_SELECTED_NAME, null);
          
          if (fileSelected != null && fileSelected.equals("on")) {
            apiDict.addWordsFromFile(opts.getOption(ConfigurationPage.FILE_PATH_NAME, null));
          } else if (urlSelected != null && urlSelected.equals("on")) {
            apiDict.addWordsFromUrl(new URL(opts.getOption(ConfigurationPage.URL_NAME, null)));
          } else {
            logger.error("No source set for the learn word dictionary.");
          }
          
          // Get the number of suggestions to show from the dictionary.
          int suggestionsToShow = 1;
          String suggestionsNumberFromOptions = opts.getOption(ConfigurationPage.SUGGESTIONS_NUMBER_NAME, null);
          if (suggestionsNumberFromOptions != null) {
            suggestionsToShow = NumberParserUtil.parseInt(suggestionsNumberFromOptions);
          }
          apiDict.setNumberOfSuggestions(suggestionsToShow);
          
        } catch (ParserConfigurationException | SAXException | IOException e) {
          logger.error("Error while getting learn word dictionary", e);
        } catch (NumberFormatException e) {
          logger.error("Error while setting number of suggestions", e);
        }
      }
      
      @Override
      public void editingSessionStarted(String sessionId, AuthorDocumentModel documentModel) {
        // Add the api dictionary when the spellchecker is available.
        spellchecker = documentModel.getSpellchecker();
        if (spellchecker != null && apiDict != null) {
          spellchecker.setTermsDictionary(apiDict);
        }
      }
    });
  }

  /**
   * Set default options if options were never changed.
   */
  private void setDefaultOptions() {
    WSOptionsStorage opts = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
    String fileSelected = opts.getOption(ConfigurationPage.FILE_SELECTED_NAME, null);
    String urlSelected = opts.getOption(ConfigurationPage.URL_SELECTED_NAME, null);
    
    if (fileSelected == null && urlSelected == null) {
      opts.setOption(ConfigurationPage.FILE_SELECTED_NAME, "on");
      opts.setOption(ConfigurationPage.FILE_PATH_NAME, LearnWordPlugin.getDefaultTermsFilePath());
    }
  }

  public boolean applicationClosing() {
    return false;
  }
}
