import java.util.ArrayList;
import java.util.List;

public class BinaryTreeInorderTraversal94 {

  // Definition for a binary tree node.
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


  public List<Integer> inorderTraversal(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    inorder(root, res);
    return res;
  }

  private void inorder(TreeNode root, List<Integer> res) {
    if (root == null) {
      return;
    }
    // inorder sequence: left mid right
    inorder(root.left, res);
    res.add(root.val);
    inorder(root.right, res);

  }


}
