public class sortColors75 {
  /*
   * 假设要排序 [5, 3, 4, 1, 2]：
   * 
   * 初始时 [5] 视为已排序，其余 [3, 4, 1, 2] 为未排序
   * 
   * 取 3，插入到 [5] 前 → [3, 5] [4, 1, 2]
   * 
   * 取 4，插入到 [3, 5] 中间 → [3, 4, 5] [1, 2]
   * 
   * 取 1，插入到最前面 → [1, 3, 4, 5] [2]
   * 
   * 取 2，插入到 1 和 3 之间 → [1, 2, 3, 4, 5]
   */


  public void sortColors(int[] nums) {
    for (int i = 0; i < nums.length; i++) {
      for (int j = i + 1; j < nums.length; j++) {
        // the first number is sorted, so we don't care.
        // use nums[i+1](the second number) to compare with the first number
        // if the second number is smaller than the first one, swap
        if (nums[i] > nums[j]) {
          int temp = nums[i];
          nums[i] = nums[j];
          nums[j] = temp;
        }
      }
    }

  }
}
