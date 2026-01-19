public class MinimumCosttoMoveChiptoTheSamePosition1217 {
  public int minCostToMoveChips(int[] position) {
    // odd position move to odd opsition cost 0
    // even position move to even opsition cost 0
    // our goal is move minimal odd to even or even to odd
    int odd = 0;
    int even = 0;
    for (int i = 0; i < position.length; i++) {
      if (position[i] % 2 == 0) {
        even++;
      } else {
        odd++;
      }
    }
    return Math.min(even, odd);
  }

}
