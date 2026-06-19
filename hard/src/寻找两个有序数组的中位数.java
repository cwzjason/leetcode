public class 寻找两个有序数组的中位数 {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        //确保 nums1 是较短的数组（为了二分效率）
        //在较短的数组上二分，时间复杂度更低：O(log(min(m, n)))
        if (nums1.length > nums2.length) {
            int[] temp = nums1;
            nums1 = nums2;
            nums2 = temp;
        }
        int m = nums1.length;
        int n = nums2.length;
        //左边应该有多少个元素
        //+1 是为了处理奇数长度，让左边比右边多1个
        int totalLeft = (m + n + 1) / 2;
        //在 nums1 上二分查找分割线位置 i
        int left = 0;
        int right = m;

        while (left < right) {

            //i = 数组1左边有 i 个元素 分割线在第一个数组左边的元素个数
            //j = 数组2左边有 j 个元素 分割线在第二个数组左边的元素个数
            //取 left 和 right 之间的右中位数（向上取整）
            //i+j=totalLeft
            int i = left + (right - left + 1) / 2;
            // 对应的 j
            int j = totalLeft - i;
            //nums1[i - 1]：nums1 左边的最大值（分割线左边最后一个元素）

            //nums2[j]：nums2 右边的最小值（分割线右边第一个元素）
            if (nums1[i - 1] > nums2[j]) {
                //当 i 太大时，把搜索范围缩小到 [left, i-1]。
                right = i - 1;
            } else {
                left = i;
            }

        }
// 4. 找到分割线，计算中位数
        int i = left;
        int j = totalLeft - i;
//如果 i == 0：nums1 左边没有元素，用 Integer.MIN_VALUE 表示负无穷
//
//否则：取 nums1[i - 1]（分割线左边的最后一个元素）
        int nums1LeftMax = (i == 0) ? Integer.MIN_VALUE : nums1[i - 1];
        int nums1RightMin = (i == m) ? Integer.MAX_VALUE : nums1[i];
        int nums2LeftMax = (j == 0) ? Integer.MIN_VALUE : nums2[j - 1];
        int nums2RightMin = (j == n) ? Integer.MAX_VALUE : nums2[j];

        if ((m + n) % 2 == 0) {
            int leftMax = Math.max(nums1LeftMax, nums2LeftMax);
            int rightMin = Math.min(nums1RightMin, nums2RightMin);
            return (leftMax + rightMin) / 2.0;
        } else {
            return Math.max(nums1LeftMax, nums2LeftMax);
        }
    }
}
