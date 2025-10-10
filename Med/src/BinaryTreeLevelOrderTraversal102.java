import java.util.ArrayList;
import java.util.List;

public class BinaryTreeLevelOrderTraversal102 {
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
    public List<List<Integer>> levelOrder(TreeNode root) {
      // create list of list first
      List<List<Integer>> res = new ArrayList<>();

      if (root == null) {
        return res;
      }
      // use current to record current level's nodes
      List<TreeNode> current = new ArrayList<>();
      current.add(root);

      while (!current.isEmpty()) {
        // store the next level's nodes
        List<TreeNode> next = new ArrayList<>();
        // store current level's nodes' values
        List<Integer> values = new ArrayList<>();

        for (int i = 0; i < current.size(); i++) {
          // get current specific node
          TreeNode node = current.get(i);
          values.add(node.val);
          if (node.left != null) {
            next.add(node.left);
          }
          if (node.right != null) {
            next.add(node.right);
          }
        }
        // put all the current nodes into the arraylist
        res.add(values);
        // update the current
        current = next;


      }
      return res;
    }
  }
}
