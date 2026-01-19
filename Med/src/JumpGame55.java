public class JumpGame55 {
  public boolean canJump(int[] nums) {

    // 不是累加所有的步之和,而是用单点能跳出这整个区间
    int maxReach = 0;

    for (int i = 0; i < nums.length; i++) {
      // index是累加的,1 1 1 1 从index0开始，每个index+1，直到碰到0，就会卡住
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
