public class CountingBits {
  public int[] countBits(int n) {
    int[] ans = new int[n + 1];
    for (int i = 0; i < n; i++) {
      // ans[i] = Integer.bitCount(i);
      ans[i] = ans[i >> 1] + (i & 1);
      // i>>1 moving one position to the right 101->10 means remove the rightest bit 1
      // i&1 judge if the rightest bit is 1 if it's 1 add else 0
    }
    return ans;
  }
}
