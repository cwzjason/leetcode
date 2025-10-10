public class moveZeroes283 {
  /*
   * Example 1:
   * 
   * Input: nums = [0,1,0,3,12] Output: [1,3,12,0,0]
   *
   * Example 2:
   * 
   * Input: nums = [0] Output: [0]
   */
  public static void moveZeroes(int[] nums) {
    // use count to record the non-zero value
    int count = 0;
    for (int i = 0; i < nums.length; i++) {
      // use the new array to store non-zero value
      if (nums[i] != 0) {
        nums[count] = nums[i];
        count++;
      }
    }
    // the range: [count,nums.length] store 0
    for (int j = count; j < nums.length; j++) {
      nums[count++] = 0;
    }
  }


}
