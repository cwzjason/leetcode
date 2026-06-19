public class JumpGame55 {
    public boolean canJump(int[] nums) {

        //nums[0] = 2
        //意思：从下标0可以跳 1步 或 2步，但不能跳3步

        // 最远能到达的编号
        int maxReach = 0;

        for (int i = 0; i < nums.length; i++) {
            // 连这个i都到不了，更别说后面的了
            if (i > maxReach) {
                return false;
            }
            // get the current max index
            maxReach = Math.max(maxReach, i + nums[i]);


            // It's able to reach the last position of the array
            if (maxReach >= nums.length - 1) {
                return true;
            }

        }
        return false;
    }
}
