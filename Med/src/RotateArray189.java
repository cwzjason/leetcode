public class RotateArray189 {
  public void rotate(int[] nums, int k) {
    int length = nums.length;
    if (length == 0) {
      return;
    }
    // To avoid k is greater than length
    // rotate to right positions: (i+k)%length (key point)
    k = k % length;
    int rotateArray[] = new int[length];
    // 1234567
    // k=3
    // r[3]=num[0]=1
    // r[4]=num[1]=2
    // r[5]=num[2]=3
    // r[6]=num[3]=4
    // r[0]=num[4]=5
    // r[1]=num[5]=6
    // r[2]=num[6]=7
    // store in odd array and update it
    // 5671234

    for (int i = 0; i < length; i++) {
      rotateArray[(i + k) % length] = nums[i];
    }
    for (int i = 0; i < length; i++) {
      nums[i] = rotateArray[i];
    }

  }
}
