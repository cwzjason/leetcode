import java.util.Arrays;

public class CheckifArrayisGood2784 {
    public boolean isGood(int[] nums) {
        Arrays.sort(nums);
        //get the biggest number
        int n = nums[nums.length - 1];
        //length should be biggest num+1==length
        if (nums.length != n + 1) {
            return false;
        }
        //n-1 corresponds n-1 numbers all appear once
        for (int i = 0; i < n - 1; i++) {
            if (nums[i] != i + 1) {
                return false;
            }
        }
        if (nums[n - 1] != nums[n] && nums[n - 1] != n + 1) {
            return false;
        }
        return true;
    }
}
