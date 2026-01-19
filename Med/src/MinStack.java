import java.util.Stack;

// 55
public class MinStack {

  Stack<Integer> stack;
  Stack<Integer> minStack;

  public MinStack() {
    stack = new Stack<>();
    minStack = new Stack<>();
  }

  public void push(int val) {
    stack.push(val);
    if (!minStack.isEmpty()) {
      int top = minStack.peek();
      if (val <= top) {
        minStack.push(val);
      }
    } else {

      minStack.push(val);
    }
  }

  public void pop() {
    // stack pop first
    int top = stack.pop();
    if (top == minStack.peek()) {
      minStack.pop();
    }
  }

  public int top() {
    // the whole stack's top--last push element
    return stack.peek();
  }

  public int getMin() {
    return minStack.peek();
  }


  /**
   * Your MinStack object will be instantiated and called as such: MinStack obj = new MinStack();
   * obj.push(val); obj.pop(); int param_3 = obj.top(); int param_4 = obj.getMin();
   */
}
