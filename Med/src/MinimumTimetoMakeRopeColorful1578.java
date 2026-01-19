public class MinimumTimetoMakeRopeColorful1578 {
  public int minCost(String colors, int[] neededTime) {
    int total = 0;


    for (int i = 0; i < colors.length() - 1; i++) {
      if (colors.charAt(i) == colors.charAt(i + 1)) {

        // if more than 2 continuous same Strings-> it will have problems
        // it always use current and next value to compare, and it will ignore previous value
        /*
         * colors = "aaaa" neededTime = [1, 100, 2, 3]
         *
         * 1,100 ->delete 1
         *
         * 100,2 ->delete 2
         *
         * 2,3 -> delete 2
         *
         * total:5
         * 
         * In fact: 1+2+3 and leave 100
         */
        // if (neededTime[i] > neededTime[i + 1]) {
        // total += neededTime[i + 1];
        // } else {
        // total += neededTime[i];
        // }
        total += Math.min(neededTime[i], neededTime[i + 1]);
        // store current max value for the next comparison
        neededTime[i + 1] = Math.max(neededTime[i + 1], neededTime[i]);
      }
    }
    return total;
  }
}
