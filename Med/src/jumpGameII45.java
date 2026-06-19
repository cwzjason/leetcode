public class jumpGameII45 {
    public int jump(int[] nums) {
        //跳0步
        if (nums.length == 1) return 0;
        int maxReach = 0;
        int steps = 0;
        // 当前这一步能到达的最远位置
        int end = 0;
        for (int i = 0; i < nums.length; i++) {
            maxReach = Math.max(maxReach, i + nums[i]);
            if (i == end) {
                steps++;
                end = maxReach;
                if (end >= nums.length - 1) {
                    break;
                }
            }
        }
        return steps;
    }
}
