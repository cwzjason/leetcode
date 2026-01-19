import java.util.Arrays;

public class AssignCookies455 {
  public int findContentChildren(int[] g, int[] s) {
    // sort ascending first
    Arrays.sort(g);
    Arrays.sort(s);
    int children = 0;
    int cookie = 0;
    // set bound
    int childrenSize = g.length;
    int cookieSize = s.length;

    while (children < childrenSize && cookie < cookieSize) {
      if (g[children] <= s[cookie]) {
        children++;
      }
      // until one cookie can reach the children's needs or can't
      cookie++;
    }
    return children;
  }
}
