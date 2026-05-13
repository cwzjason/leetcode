import java.util.*;

public class FordFulkerson {
  // nodes
  static int n;
  // adjacency list
  static ArrayList<Integer>[] graph;
  // residual graph
  static int[][] capacity;

  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    int instances = input.nextInt();
    for (int i = 0; i < instances; i++) {
      n = input.nextInt();
      int edges = input.nextInt();

      graph = new ArrayList[n + 1];
      for (int j = 0; j <= n; j++) {
        graph[j] = new ArrayList<>();
      }
      capacity = new int[n + 1][n + 1];
      for (int j = 0; j < edges; j++) {
        int start = input.nextInt();
        int end = input.nextInt();
        int c = input.nextInt();

        graph[start].add(end);
        graph[end].add(start);
        capacity[start][end] += c;

      }
      System.out.println(maxFlow(1, n));
    }
  }

  static int maxFlow(int s, int t) {
    int flow = 0;
    // record the argument path
    int[] parent = new int[n + 1];
    // when the argument path exists
    while (bfs(s, t, parent)) {
      int pathFlow = Integer.MAX_VALUE;
      int v = t;
      while (v != s) {
        // previous node
        int u = parent[v];
        // bottleneck
        pathFlow = Math.min(capacity[u][v], pathFlow);
        v = u;
      }
      v = t;
      while (v != s) {
        int u = parent[v];
        capacity[u][v] -= pathFlow;
        capacity[v][u] += pathFlow;
        v = u;
      }
      flow += pathFlow;
    }

    return flow;

  }

  public static boolean bfs(int s, int t, int[] parent) {
    Arrays.fill(parent, -1);
    Queue<Integer> queue = new LinkedList<>();
    queue.add(s);
    parent[s] = -2;

    while (!queue.isEmpty()) {
      int u = queue.poll();
      for (int v : graph[u]) {
        if (parent[v] == -1 && capacity[u][v] > 0) {
          parent[v] = u;
          if (v == t) {
            return true;
          }
          queue.add(v);
        }
      }
    }
    return false;
  }
}
