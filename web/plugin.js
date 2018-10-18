(function () {
  goog.events.listenOnce(workspace, sync.api.Workspace.EventType.EDITOR_LOADED, function(e) {

    // If read-only dictionary, do nothing.
    var readOnlyMode = sync.options.PluginsOptions.getClientOption('lw.read-only-mode');
    if (!readOnlyMode || readOnlyMode !== 'off') {
      return;
    }

    var spellchecker = e.editor.getSpellChecker();
    if (!spellchecker) {
      console.warn('Could not get spellchecker.');
      return;
    }

    /**
     * Action to be added in context menu for learning words.
     * @constructor
     */
    var LearnWordAction = function (wordToLearn, language) {
      this.wordToLearn_ = wordToLearn;
      this.language_ = language;
    };
    LearnWordAction.prototype = new sync.actions.AbstractAction();
    LearnWordAction.prototype.actionPerformed = function (callback) {
      e.editor.getActionsManager().invokeOperation(
        'com.oxygenxml.learnword.UpdateDictionaryOperation', {
          word: this.wordToLearn_,
          lang: this.language_
        },
        function (err, resultString) {
          if (resultString === 'ok') {
            spellchecker.performFullSpellCheck();
          } else {
            workspace.getNotificationManager().showWarning(resultString, true);
            console.warn(resultString);
          }
          callback && callback();
      })
    };
    LearnWordAction.prototype.getDisplayName = function () {
      return tr(msgs.LEARN_WORD_X_, {'0': this.wordToLearn_});
    };

    spellchecker.setLearnWordAction(LearnWordAction);
  });
})();