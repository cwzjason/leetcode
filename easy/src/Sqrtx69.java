public class Sqrtx69 {
  public int mySqrt(int x) {
    // r^2=x -> r?
    // return (int) Math.sqrt(x);
    // 4/2=sqrt(4)-> when x=4,r=2, so 2 is a critical value
    // x return 1 or 0
    if (x < 2) {
      return x;
    }
    // binary search
    int left = 1;
    int right = x / 2;
    int temp = 0;
    while (left <= right) {
      int mid = left + (right - left) / 2;
      // use long and can't use the double because mid maybe over the double's maximum range
      long value = (long) mid * mid;
      if (x == value) {
        return mid;
      } else if (x > value) {
        // use temp to store the mid
        temp = mid;
        left = mid + 1;
      } else {
        right = mid - 1;
      }
    }
    return temp;
  }
}
