import java.util.Scanner;

public class Knapsack {
  public static int knapsack(int n, int capacity, int[] weights, int[] values) {
    int[] dp = new int[capacity + 1];
    for (int i = 0; i < n; i++) {
      int weight = weights[i];
      int value = values[i];
      // each item use once by using the inverse order
      for (int j = capacity; j >= weight; j--) {
        dp[j] = Math.max(dp[j], dp[j - weight] + value);
      }
    }
    return dp[capacity];
  }

  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    int instances = input.nextInt();
    for (int i = 0; i < instances; i++) {
      // number of items
      int n = input.nextInt();
      int capacity = input.nextInt();

      int[] weights = new int[n];
      int[] values = new int[n];
      for (int j = 0; j < n; j++) {
        weights[j] = input.nextInt();
        values[j] = input.nextInt();
      }
      int result = knapsack(n, capacity, weights, values);
      System.out.println(result);
    }
  }
}
