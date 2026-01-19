public class ReverseString344 {
  public void reverseString(char[] s) {

    // head and tail exchange
    int left = 0;
    int right = s.length - 1;
    while (left < right) {
      char temp = s[left];
      s[left] = s[right];
      s[right] = temp;
      left++;
      right--;
    }
  }
}
