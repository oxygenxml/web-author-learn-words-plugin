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

The file is loaded either from a the server's local file system, or from an external HTTP server. When a user chooses to
learn a word the following action happens:
1. If the plugin is configured to use a local file, the new word is added to that file.
2. If the plugin is configured to use load the file from an HTTP server, a POST request is sent to the URL of the file
with the following form parameters:

| Parameter name | Parameter value                 |
| -------------- |-------------------------------- |
| `mode`         | One of `learned` or `forbidden` |
| `lang`         | The language for which the word is added, e.g. `en`     |
| `word`         | The word to be added            |

Installation
------------

To install this plugin, download one of the releases ([link](https://github.com/oxygenxml/web-author-learn-words-plugin/releases)) and [upload it in your Web Author deployment](https://www.oxygenxml.com/doc/ug-webauthor/topics/webapp-configure-plugins.html).

Configuration
-------------

The location of the dictionary file and other options can be configured from the Web Author Administration Page.

Copyright and License
---------------------
Copyright 2018 Syncro Soft SRL.

This project is licensed under [Apache License 2.0](https://github.com/oxygenxml/web-author-learn-words-plugin/blob/master/LICENSE)
