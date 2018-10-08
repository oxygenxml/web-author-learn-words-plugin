# web-author-learn-words-plugin
Plugin for adding learned/forbidden words functionality to Web Author spellchecking.

The list of words can be read from a local file or an URL.

Expected file structure:

    <?xml version="1.0" encoding="UTF-8"?>
    <Dictionary>
        <Learned>
            <Language code="en">
                <w>worda</w>
                <w>wordb</w>
                <w>wordc</w>
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
