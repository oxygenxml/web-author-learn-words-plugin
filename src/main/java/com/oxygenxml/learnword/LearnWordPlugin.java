package com.oxygenxml.learnword;

import java.io.File;

import ro.sync.exml.plugin.PluginDescriptor;

/**
 * Dummy class required in order to be able to include an operation.
 */
public class LearnWordPlugin extends ro.sync.exml.plugin.Plugin {
  
  private static String defaultTermsFile = "dictionary.xml";
  
  /**
   * The base directory of the plugin.
   */
  private static File baseDir;
  
  /**
   * @return The base directory of the plugin.
   */
  public static File getBaseDir() {
    return baseDir;
  }
  
  /**
   * Constructor.
   * 
   * @param descriptor The plugin descriptor.
   */
  public LearnWordPlugin(PluginDescriptor descriptor) {
    super(descriptor);
    baseDir = descriptor.getBaseDir();
  }

  /**
   * @return the defaultTermsFile
   */
  public static String getDefaultTermsFile() {
    return defaultTermsFile;
  }
  
  public static String getDefaultTermsFilePath () {
    String base = LearnWordPlugin.getBaseDir().getAbsolutePath();
    if (!base.endsWith("/")) {
      base += "/";
    }
    return base + LearnWordPlugin.getDefaultTermsFile();
  }

  /**
   * @param filename the defaultTermsFile to set
   */
  public static void setDefaultTermsFile(String filename) {
    LearnWordPlugin.defaultTermsFile = filename;
  }

}
