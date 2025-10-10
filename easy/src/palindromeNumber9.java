public class palindromeNumber9 {
  public boolean isPalindrome(int x) {
    // x=0 is allowed, x must greater than 0
    if (x < 0) {
      return false;
    }
    // use orignial variable to store x
    int original = x;
    // use reversed variable to store reversed number
    int reversed = 0;
    // if x!=0, it can continous/10
    while (x != 0) {
      int digit = x % 10;
      reversed = reversed * 10 + digit;
      x /= 10;
    }
    return reversed == original;
  }
}
