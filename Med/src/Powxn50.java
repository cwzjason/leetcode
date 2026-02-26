public class Powxn50 {

  public double myPow(double x, int n) {
    // avoid n overflow
    long N = n;
    if (n < 0) {
      x = 1 / x;
      N = -N;
    }
    return solve(x, n);
  }

  public static double solve(double x, int n) {
    if (n == 0) {
      return 1;
    }
    // solve half first
    double half = solve(x, n / 2);
    if (n % 2 == 0) {
      return half * half;
    } else {
      return half * half * x;
    }
  }
}
