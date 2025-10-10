public class runningSumOf1dArray1480 {
  /*
   * Example 1:
   *
   * Input: nums = [1,2,3,4] Output: [1,3,6,10] Explanation: Running sum is obtained as follows: [1,
   * 1+2, 1+2+3, 1+2+3+4].
   *
   * Example 2:
   *
   * Input: nums = [1,1,1,1,1] Output: [1,2,3,4,5] Explanation: Running sum is obtained as follows:
   * [1, 1+1, 1+1+1, 1+1+1+1, 1+1+1+1+1].
   *
   * Example 3:
   *
   * Input: nums = [3,1,2,10,1] Output: [3,4,6,16,17]
   */
  public int[] runningSum(int[] nums) {
    int[] array = new int[nums.length];
    // store the first number of nums[0]
    array[0] = nums[0];
    for (int i = 1; i < nums.length; i++) {
      // it's an accumulation's progress
      // 3=1+2 the next number is 3 and the current number is 1, the previous number is 2(sum of
      // previous numbers) array[i-1]
      array[i] = nums[i] + array[i - 1];
    }
    return array;
  }
}
