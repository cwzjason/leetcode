import java.util.Arrays;

public class TheTwoSneakyNumbersofDigitville3289 {
  public int[] getSneakyNumbers(int[] nums) {
    Arrays.sort(nums);
    int newArray[] = new int[2];
    int j = 0;
    for (int i = 0; i < nums.length - 1; i++) {
      if (nums[i] == nums[i + 1]) {
        newArray[j++] = nums[i + 1];
      }
    }
    return newArray;
  }
}
