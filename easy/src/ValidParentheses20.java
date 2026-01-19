import java.util.Stack;

public class ValidParentheses20 {
  public boolean isValid(String s) {
    // check if is the one to one [](){}
    if (s.length() % 2 != 0) {
      return false;
    }
    // stack: last in first out
    Stack<Character> stack = new Stack<Character>();
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == '(') {
        stack.push('(');
      } else if (s.charAt(i) == '[') {
        stack.push('[');
      } else if (s.charAt(i) == '{') {
        stack.push('{');
      } else {
        // already push all of the {[( and check if has another })]
        if (stack.isEmpty()) {
          return false;
        }
        char top = stack.peek();
        if (s.charAt(i) == ')' && top == '(' || s.charAt(i) == '}' && top == '{'
            || s.charAt(i) == ']' && top == '[') {
          // after pop, it should be empty if it is correct
          stack.pop();
        } else {
          return false;
        }
      }
    }
    return stack.isEmpty();
  }
}
