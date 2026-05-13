import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Matching {
  static int[][] capacity;

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    int instances = sc.nextInt();

    for (int inst = 0; inst < instances; inst++) {
      int m = sc.nextInt();
      int n = sc.nextInt();
      int q = sc.nextInt();

      // source + sink + m + n
      int totalNodes = m + n + 2;
      // start
      int source = 0;
      // end
      int sink = totalNodes - 1;
      capacity = new int[totalNodes][totalNodes];

      // source -> A [0,1]...->[0,m]
      for (int i = 1; i <= m; i++)
        capacity[source][i] = 1;
      // B -> sink [m,m+n]
      for (int j = 1; j <= n; j++)
        capacity[m + j][sink] = 1;
      // A -> B
      for (int i = 0; i < q; i++) {
        int a = sc.nextInt();
        int b = sc.nextInt();
        capacity[a][m + b] = 1;
      }

      int maxMatching = maxFlow(source, sink);
      boolean perfect = (maxMatching == m && maxMatching == n);
      System.out.println(maxMatching + " " + (perfect ? "Y" : "N"));
    }

  }

  // 找增广路径（保证路径上都有剩余容量）
  // 计算路径瓶颈流量
  // 沿路径更新正向边和反向边的容量
  // 累加流量
  // 重复，直到没有增广路径
  static int maxFlow(int s, int t) {
    // 最大流
    int flow = 0;

    // 记录增广路径上每个节点的 前驱节点。通过它可以回溯增广路径，找出流量的瓶颈边，并更新容量
    // s → A → B → t
    // parent[A] = s
    // parent[B] = A
    // parent[t] = B
    int[] parent = new int[capacity.length];
    // 用于在当前残余网络中找到一条从 s 到 t 的增广路径。
    while (bfs(s, t, parent)) {
      // 记录瓶颈流量
      int pathFlow = Integer.MAX_VALUE;
      // 从sink 回溯到 source
      int v = t;
      // 为了找到增广路径中最短的瓶颈
      while (v != s) {
        int u = parent[v];
        pathFlow = Math.min(pathFlow, capacity[u][v]);
        v = u;
      }

      // 恢复原来的t
      v = t;
      // 更新residual graph
      // 将计算好的可以再加入的flow加入
      // 正向边减少flow 反向边增加flow
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

  // BFS 检验是否有增广路径
  // 可以看到，队列在过程中 不断增加节点、不断删除节点，最后队列空了就停止
  // queue是清空了但是 parent[]一直在记录前个节点的值
  static boolean bfs(int s, int t, int[] parent) {
    // 把 parent 数组全部初始化为 -1，表示 该节点还没被访问过
    Arrays.fill(parent, -1);
    Queue<Integer> q = new LinkedList<>();
    // s 作为起点
    q.add(s);
    // 特殊标记源点，表示它没有前驱节点。
    parent[s] = -2;
    while (!q.isEmpty()) {
      // 取出节点 u
      int u = q.poll();// u=s
      for (int v = 0; v < capacity.length; v++) {
        // 没访问过+仍然有余量
        if (parent[v] == -1 && capacity[u][v] > 0) {
          // parent[v]=s round1
          // parent[v1]=v round2
          parent[v] = u;
          if (v == t)
            return true;
          q.add(v);
        }
      }
    }
    return false;
  }

}
