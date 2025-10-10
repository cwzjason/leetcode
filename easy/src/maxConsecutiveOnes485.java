public class maxConsecutiveOnes485 {
  /*
   * Example 1:
   * 
   * Input: nums = [1,1,0,1,1,1] Output: 3
   *
   * Example 2:
   * 
   * Input: nums = [1,0,1,1,0,1] Output: 2
   */
  public int findMaxConsecutiveOnes(int[] nums) {
    // need 2 parameters to record
    // max to record max 1
    // count to record every time's 1
    int max = 0;
    int count = 0;
    for (int i = 0; i < nums.length; i++) {
      if (nums[i] == 1) {
        count++;
        // get max consecutive 1
        max = Math.max(max, count);
      } else {
        // after meet 0, reset the count value and continue entering the loop
        count = 0;
      }
    }
    return max;
  }
}
