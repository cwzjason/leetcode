import java.util.*;

class DFS {
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    int t = input.nextInt();


    for (int i = 0; i < t; i++) {
      // n instances
      int n = input.nextInt();
      // move to the next line
      input.nextLine();


      List<String> names = new ArrayList<>();
      Map<String, Integer> map = new HashMap<>();
      List<String[]> lines = new ArrayList<>();

      for (int j = 0; j < n; j++) {
        String s = input.nextLine();
        String[] elements = s.split(" ");

        names.add(elements[0]);
        map.put(elements[0], j);
        lines.add(elements);


      }

      List<List<Integer>> adjacentList = new ArrayList<>();
      for (int k = 0; k < n; k++) {
        adjacentList.add(new ArrayList<>());
      }

      // change the neighbors' names to index
      for (int k = 0; k < n; k++) {
        String[] elements = lines.get(k);
        for (int m = 1; m < elements.length; m++) {
          int neighbor = map.get(elements[m]);
          adjacentList.get(k).add(neighbor);
        }
      }
      // default false
      boolean[] visited = new boolean[n];
      List<String> result = new ArrayList<>();

      for (int node = 0; node < n; node++) {
        if (!visited[node]) {
          dfs(node, adjacentList, visited, names, result);
        }
      }

      // print result
      for (int m = 0; m < result.size(); m++) {
        System.out.print(result.get(m));
        if (m != result.size() - 1) {
          System.out.print(" ");
        }
      }
      System.out.println();
    }
  }



  /**
   * @param start stores the index corresponding the node
   * @param adjacent stores each node's neighbor index
   * @param visited stores whether the node has been visited
   * @param names stores the nodes' names, according to input order
   * @param result stores DFS access order
   */
  public static void dfs(int start, List<List<Integer>> adjacent, boolean[] visited,
      List<String> names, List<String> result) {
    Stack<Integer> stack = new Stack<>();
    stack.push(start);

    while (!stack.isEmpty()) {
      int u = stack.pop();
      if (!visited[u]) {
        visited[u] = true;
        result.add(names.get(u));
        // last in first out, so we need to reverse stack pushing
        List<Integer> neighbors = adjacent.get(u);
        for (int i = neighbors.size() - 1; i >= 0; i--) {
          int v = neighbors.get(i);
          if (!visited[v]) {
            stack.push(v);
          }
        }
      }
    }
  }
}
