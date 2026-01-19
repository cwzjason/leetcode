public class LongestCommonPrefix14 {
  public String longestCommonPrefix(String[] strs) {
    if (strs == null || strs.length == 0) {
      return "";
    }
    String prefix = strs[0];
    for (int i = 1; i < strs.length; i++) {

      // if the prefix's index does not appear at 0, it proves they don't have the common prefix and
      // ==-1
      while (strs[i].indexOf(prefix) != 0) {
        // use prefix as standard if no match-> narrow the prefix's range
        prefix = prefix.substring(0, prefix.length() - 1);
        // if narrow the range until empty, it won't have the common index
        if (prefix.isEmpty()) {
          return "";
        }
      }
    }
    return prefix;
  }
}
