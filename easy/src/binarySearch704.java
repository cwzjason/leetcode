public class binarySearch704 {
  /*
   * Use left and right two pointers to help find the mid. Compare the target with the mid: 1.
   * target=mid, find and return 2. target>mid, narrow the range from [mid+1,right] 3. target<mid,
   * narrow the range from [left,mid-1] When left >right, not find
   * 
   */
  public int search(int[] nums, int target) {
    int left = 0;
    int right = nums.length - 1;

    while (left <= right) {
      // why is not the int mid=(right+left)/2;
      // It has int overflow risk, overpass the maximum of the range of the int
      int mid = left + (right - left) / 2;

      if (nums[mid] == target) {
        return mid;
      }
      if (nums[mid] < target) {
        left = mid + 1;
      } else {
        right = mid - 1;
      }
    }
    return -1;


  }
}
