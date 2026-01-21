public class WiggleSubsequence376 {
  public int wiggleMaxLength(int[] nums) {
    // add all the + and - times together
    int add = 0;
    int minus = 0;
    if (nums.length < 2) {
      return nums.length;
    }

    for (int i = 1; i < nums.length; i++) {
      if (nums[i] > nums[i - 1]) {
        minus = add + 1;

      } else if (nums[i] < nums[i - 1]) {
        add = minus + 1;
      }
    }
    return Math.max(add, minus) + 1;
  }
}
