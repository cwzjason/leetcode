import java.util.HashSet;
import java.util.Set;

public class LongestSubstringWithoutRepeatingCharacters3 {
  public int lengthOfLongestSubstring(String s) {
    Set<Character> set = new HashSet();
    int left = 0;
    int max = 0;

    // slide window method
    for (int right = 0; right < s.length(); right++) {
      char c = s.charAt(right);
      // we need continuously to remove the repeat element until it disappear
      // To avoid "abba" such situation
      while (set.contains(c)) {
        set.remove(s.charAt(left));
        left++;
      }
      set.add(c);
      max = Math.max(max, right - left + 1);
    }
    return max;
  }
}
