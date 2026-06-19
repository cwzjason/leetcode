public class BestTimetoBuyandSellStock121 {
    public int maxProfit(int[] prices) {
        int min = Integer.MAX_VALUE;
        int max = 0;
        // keep the smallest and calculate the max and update max
        for (int i = 0; i < prices.length; i++) {
            //找到更低的买点，更新 min
            if (prices[i] < min) {
                min = prices[i];
                // 计算当前利润，更新最大利润
            } else if (prices[i] - min > max) {
                max = prices[i] - min;
            }

        }
        return max;
    }
}
