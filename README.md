Web Author Learn Words Plugin
=============================

Plugin for adding learned/forbidden words functionality to Web Author spellchecking.

How it works
------------

The list of words is stored in a file with the following structure:

```
<?xml version="1.0" encoding="UTF-8"?>
<Dictionary>
    <Learned>
        <Language code="en">
            <w>wordexample</w>
            <w>worda</w>
            <w>longword</w>
            <w>learned word</w>
        </Language>
        <Language code="fr">
            <w>worta</w>
            <w>wortb</w>
        </Language>
    </Learned>
    <Forbidden>
        <Language code="en">
            <w>some</w>
            <w>forbidden</w>
            <w>words</w>
        </Language>
        <Language code="de">
            <w>verboten</w>
        </Language>
    </Forbidden>
</Dictionary>
```

The file is loaded either from a the server's local file system, or from an external HTTP server (GET request). 

When a user chooses to learn a word the following action happens:
1. If the plugin is configured to use a local file, the new word is added to that file.
2. If the plugin is configured to load the file from an HTTP server, a POST request is sent to the URL of the file
with the following form parameters:

| Parameter name | Parameter value                 |
| -------------- |-------------------------------- |
| `mode`         | One of `learned` or `forbidden` |
| `lang`         | The language for which the word is added, e.g. `en`     |
| `word`         | The word to be added            |

Installation
------------

To install this plugin, download one of the releases ([link](https://github.com/oxygenxml/web-author-learn-words-plugin/releases)) and [upload it in your Web Author deployment](https://www.oxygenxml.com/doc/ug-webauthor/topics/webapp-configure-plugins.html).

Configuration through Administration page.
-------------
The plugin can be configured from the [Web Author Administration Page](https://www.oxygenxml.com/doc/ug-waCustom/topics/webapp-admin-page.html).
You can set:
- the number of suggestions to be shown from the learned words list. They will always be shown at the top of the list of suggestions.
- the read-only mode, enabled by default. If turned off, a "Learn word" action is added to the contextual menu and any user can add words to the learned words list.
- the dictionary. It may be a local file, or an HTTP server able to serve the list of learned words(GET) and add new learn words(POST).



Configuration through options.xml file
-------------
The plugin can also be configured by [customizing options](https://www.oxygenxml.com/doc/ug-waCustom/topics/customizing-options.html) in Web Author's options.xml:
- the number of suggestions to be shown from the learned words list
```
<entry>
    <String>PLUGIN_CUSTOM_OPTIONS.suggestions-no.name</String>
    <String>2</String>
</entry>
```
- the read-only mode
```
<entry>
    <String>PLUGIN_CUSTOM_OPTIONS.lw.read-only-mode</String>
    <String>off</String>
</entry>
```
- use a local dictionary file
```
<!--Use local file-->
<entry>
    <String>PLUGIN_CUSTOM_OPTIONS.file-selected.name</String>
    <String>on</String>
</entry>
<entry>
    <String>PLUGIN_CUSTOM_OPTIONS.file-path.name</String>
    <String>PATH_TO_FILE</String>
</entry>
```
- OR use an HTTP server
```
<!--Use URL to connect to an HTTP server-->  
<entry>
    <String>PLUGIN_CUSTOM_OPTIONS.url-selected.name</String>
    <String>on</String>
</entry>
<entry>
    <String>PLUGIN_CUSTOM_OPTIONS.url.name</String>
    <String>http://EXAMPLEURL</String>
</entry>
```

Copyright and License
---------------------
Copyright 2021 Syncro Soft SRL.

This project is licensed under [Apache License 2.0](https://github.com/oxygenxml/web-author-learn-words-plugin/blob/master/LICENSE)
