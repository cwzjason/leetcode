public class powerOfThree326 {
  /*
   * Given an integer n, return true if it is a power of three. Otherwise, return false.
   *
   *
   */

  public boolean isPowerOfThree(int n) {
    // times of 3,include 3^0
    if (n == 1) {
      return true;
    }
    // n%3=0 like 27%3=0
    if (n <= 0 || n % 3 != 0) {
      return false;
    }
    // invoke this method recursively
    return isPowerOfThree(n / 3);
  }

}
