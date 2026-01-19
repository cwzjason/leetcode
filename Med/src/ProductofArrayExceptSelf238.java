public class ProductofArrayExceptSelf238 {

  // It works but it exceeds the time limit, the time complexity has to be O(n)
  // int total[] = new int[nums.length];
  // for (int i = 0; i < nums.length; i++) {
  // int temp = 1;
  // for (int k = 0; k < nums.length; k++) {
  // // continue is skip current element
  // // break is jumping out of the loop
  // if (k == i) {
  // continue;
  // }
  // temp *= nums[k];
  // }
  // total[i] = temp;
  // temp = 1;
  // }
  // return total;
  //
  // }
  public int[] productExceptSelf(int[] nums) {
    int n = nums.length;
    int total[] = new int[n];

    // the leftest side is empty
    total[0] = 1;

    for (int i = 1; i < n; i++) {
      // it equals num[0]* ...* num[i-1]
      total[i] = total[i - 1] * nums[i - 1];
    }
    // the rightest side is empty
    int right = 1;

    for (int i = n - 1; i >= 0; i--) {
      // last left side -> not include the rightest element
      // only right=1 itself, multiple all of the previous elements
      total[i] *= right;
      // last second number... right x last second element
      right *= nums[i];
    }
    return total;
  }
}
