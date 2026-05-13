public class LowestCommonAncestorofaBinaryTree236 {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        //if current node is empty node or p or q return current node
        //root is the LowestCommonAncestor
        if (root == null || root == p || root == q) {
            return root;
        }
        //traverse left and right
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);
        //if left and right all have node then return the root
        
        //root is the LowestCommonAncestor
        if (left != null && right != null) {
            return root;
        }
        //if left doesn't null return left else return right

        //right is null and left is not null
        if (left != null) {
            return left;
        }
        return right;
    }


    //Definition for a binary tree node.
    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

}
