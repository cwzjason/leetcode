import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Randomization {

  public static class Clause {
    int x, y, z;

    Clause(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
  }

  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);

    int n = input.nextInt();
    // clauses
    int m = input.nextInt();
    // store all clauses
    Clause[] clauses = new Clause[m];

    for (int i = 0; i < m; i++) {
      int x = input.nextInt();
      int y = input.nextInt();
      int z = input.nextInt();
      clauses[i] = new Clause(x, y, z);
    }

    Random rand = new Random();
    // current best assignment
    int[] bestAssign = new int[n + 1];
    // current best clauses
    int bestScore = -1;

    // try multiple random assignments
    for (int t = 0; t < 20; t++) {

      int[] assign = new int[n + 1];

      // random ±1 assignment
      for (int i = 1; i <= n; i++) {
        assign[i] = rand.nextBoolean() ? 1 : -1;
      }

      int satisfied = countSatisfied(assign, clauses);

      // replace the best
      if (satisfied > bestScore) {
        bestScore = satisfied;
        bestAssign = Arrays.copyOf(assign, n + 1);
      }
    }

    // output
    StringBuilder sb = new StringBuilder();
    for (int i = 1; i <= n; i++) {
      sb.append(bestAssign[i]).append(" ");
    }
    System.out.println(sb.toString().trim());
  }

  // check clauses are true or false
  public static int countSatisfied(int[] assign, Clause[] clauses) {
    int count = 0;

    for (Clause c : clauses) {
      if (eval(c.x, assign) == 1 || eval(c.y, assign) == 1 || eval(c.z, assign) == 1) {
        count++;
      }
    }

    return count;
  }

  // check the literal is true or false
  public static int eval(int lit, int[] assign) {
    int var = Math.abs(lit);
    int val = assign[var];

    if (lit > 0)
      return val; // xi
    else
      return -val; // ¬xi
  }
}
