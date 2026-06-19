public class MaximumSubarray53 {
  public int MaxSubArray(int[] nums) {
    int currentMax = nums[0];
    int globalMax = nums[0];

    for (int i = 1; i < nums.length; i++) {
      // current value compares to current+previous
      //正确方式会重新开始一个子数组，而错误方式会继续拖着负数。
      //不能用Math.max(currentMax, currentMax + nums[i]);
      currentMax = Math.max(nums[i], currentMax + nums[i]);
      // use previous global max compare to current max
      globalMax = Math.max(globalMax, currentMax);
    }

    return globalMax;
  }
}
