import java.util.ArrayList;
import java.util.List;

public class BinaryTreePostorderTraversal145 {


  public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {}

    TreeNode(int val) {
      this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
      this.val = val;
      this.left = left;
      this.right = right;
    }
  }

  class Solution {
    public List<Integer> postorderTraversal(TreeNode root) {
      List<Integer> res = new ArrayList<>();
      postorder(root, res);
      return res;
    }

    private void postorder(TreeNode root, List<Integer> res) {
      if (root == null) {
        return;
      }
      //left right mid
      postorder(root.left, res);
      postorder(root.right, res);
      res.add(root.val);
    }
  }
}
