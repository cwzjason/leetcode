public class removeDuplicatesFromSortedArray26 {
    /*
     * Example 1:
     *
     * Input: nums = [1,1,2] Output: 2, nums = [1,2,_]
     *
     * Example 2:
     *
     * Input: nums = [0,0,1,1,1,2,2,3,3,4] Output: 5, nums = [0,1,2,3,4,_,_,_,_,_]
     */
    // int type method -> return new array.length
    public int removeDuplicates(int[] nums) {
        // index start from 0, i start from 1-> 0,1; 1,2 to compare
        int index = 0;
        for (int i = 1; i < nums.length; i++) {
            if (nums[index] != nums[i]) {
                index++;
                //为什么需要这一步
                //新值覆盖老值
                nums[index] = nums[i];
            }
        }
        // index+1=array.length
        return index + 1;
    }
}
