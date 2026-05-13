public class FindMinimuminRotatedSortedArray153 {
    public int findMin(int[] nums) {
        //binary search
        /*
        如果 nums[mid] > nums[right]
说明 mid 在左段，最小值在 右半部分（mid右边），因为左段都比右段大。
操作：left = mid + 1
如果 nums[mid] <= nums[right]
说明 mid 在右段 或就是最小值。
最小值可能在 mid 左边或 mid 本身。
操作：right = mid
         */
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return nums[left];

    }
}
