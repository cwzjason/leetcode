public class GasStation134 {
  public int canCompleteCircuit(int[] gas, int[] cost) {
    // start index
    int start = 0;
    // total tank
    int total = 0;
    // current tank
    int tank = 0;
    for (int i = 0; i < gas.length; i++) {
      int differ = gas[i] - cost[i];
      tank += differ;
      total += differ;
      // gas can't afford the cost
      if (tank < 0) {
        // change the new start point
        start = i + 1;
        // reset the tank
        tank = 0;
      }
    }
    return total < 0 ? -1 : start;
  }
}
