import java.util.Stack;

public class RemoveKDigits402 {
  public String removeKdigits(String num, int k) {

    Stack<Character> stack = new Stack<>();
    for (char c : num.toCharArray()) {
      while (!stack.isEmpty() && k > 0 && stack.peek() > c) {
        // delete the bigger one
        stack.pop();
        k--;
      }
      stack.push(c);
    }
    while (k > 0) {
      stack.pop();
      k--;
    }
    StringBuilder sb = new StringBuilder();
    for (Character c : stack) {
      sb.append(c);
    }
    // delete the extra 0 in front of the number
    while (sb.length() > 1 && sb.charAt(0) == '0') {
      sb.deleteCharAt(0);
    }
    return sb.length() == 0 ? "0" : sb.toString();
  }

}
