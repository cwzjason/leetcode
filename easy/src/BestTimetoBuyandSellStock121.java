public class BestTimetoBuyandSellStock121 {
  public int maxProfit(int[] prices) {
    int min = Integer.MAX_VALUE;
    int max = 0;
    // keep the smallest and calculate the max and update max
    for (int i = 0; i < prices.length; i++) {
      if (prices[i] < min) {
        min = prices[i];
      } else if (prices[i] - min > max) {
        max = prices[i] - min;
      }

    }
    return max;
  }
}
