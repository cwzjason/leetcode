import java.util.Arrays;

public class ArrayPartition561 {
  public int arrayPairSum(int[] nums) {
    // small value pairs with small value big value pairs with big value
    int total = 0;
    Arrays.sort(nums);
    for (int i = 0; i < nums.length - 1; i += 2) {
      // we already sort this array, so it's ok for us to only get the first element of pairs
      total += nums[i];
      // total += Math.min(nums[i], nums[i + 1]);
    }
    return total;
  }
}
