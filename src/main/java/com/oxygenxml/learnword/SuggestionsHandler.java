package com.oxygenxml.learnword;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SuggestionsHandler {

  private SuggestionsHandler() {
    // Hide implicit constructor.
  }
  
  /**
   * 
   * @param word 
   * @param learnedWords
   * @param numberOfSuggestions
   * @return
   */
  public static String[] getSuggestions(String word, Set<String> learnedWords, int numberOfSuggestions) {
    TreeMap<Integer, String> matches = new TreeMap<>();
    String[] allLearnedWords = learnedWords.toArray(new String[learnedWords.size()]);
    for (int i = 0; i < allLearnedWords.length; i++) {
      String learnedWord = allLearnedWords[i];
      matches.put(calculate(learnedWord, word), learnedWord);
    }
    return getSuggestionsFromCostMap(matches, numberOfSuggestions);
  }

  private static String[] getSuggestionsFromCostMap(Map<Integer, String> matches, int numberOfSuggestions) {
    List<String> s = new ArrayList<>(); 
    if (!matches.isEmpty()) {
      int l = 0;
      Set<Integer> keys = matches.keySet();
      for (Iterator<Integer> i = keys.iterator(); i.hasNext() && l < numberOfSuggestions;) {
        Integer key = i.next();
        s.add(matches.get(key));
        l++;
      }
    }
    return s.toArray(new String[s.size()]);
  }

  private static int min(int... numbers) {
    return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
  }

  private static int costOfSubstitution(char a, char b) {
    return a == b ? 0 : 1;
  }

  private static int calculate(String x, String y) {
    int[][] dp = new int[x.length() + 1][y.length() + 1];

    for (int i = 0; i <= x.length(); i++) {
      for (int j = 0; j <= y.length(); j++) {
        if (i == 0)
          dp[i][j] = j;

        else if (j == 0)
          dp[i][j] = i;

        else {
          dp[i][j] = min(dp[i - 1][j - 1]
              + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
              dp[i - 1][j] + 1, dp[i][j - 1] + 1);
        }
      }
    }

    return dp[x.length()][y.length()];
  }
}
