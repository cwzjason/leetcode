public class removeElement27 {
  /*
   * use nums[count] to replace old array's value
   */

  public int removeElement(int[] nums, int val) {
    // use new index for the array
    int count = 0;
    for (int i = 0; i < nums.length; i++) {

      if (nums[i] != val) {
        // assign the old array's value to the new array without val value
        nums[count] = nums[i];
        // go to the next value
        count++;
      }
    }
    // return the length of the new array
    return count;
  }
}
