public class plusOne66 {
  /*
   * Example 1:
   * 
   * Input: digits = [1,2,3] Output: [1,2,4] Explanation: The array represents the integer 123.
   * Incrementing by one gives 123 + 1 = 124. Thus, the result should be [1,2,4]. Example 2:
   *
   * Example 2:
   *
   * Input: digits = [4,3,2,1] Output: [4,3,2,2] Explanation: The array represents the integer 4321.
   * Incrementing by one gives 4321 + 1 = 4322. Thus, the result should be [4,3,2,2]. Example 3:
   *
   * Example 3:
   * 
   * Input: digits = [9] Output: [1,0] Explanation: The array represents the integer 9. Incrementing
   * by one gives 9 + 1 = 10. Thus, the result should be [1,0].
   * 
   */
  public int[] plusOne(int[] digits) {
    // the simplest situation is +1 at the end of the element
    for (int i = digits.length - 1; i >= 0; i--) {
      // all numbers are < 9
      if (digits[i] < 9) {
        digits[i] += 1;
        return digits;
      }
      // if some numbers =9, it will set to 0
      digits[i] = 0;
    }
    // expand the array for the first number which is 9
    int array[] = new int[digits.length + 1];
    array[0] = 1;
    return array;
  }
}
