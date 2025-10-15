public class DeleteNodeinaLinkedList237 {
  public class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
      val = x;
    }
  }

  class Solution {
    public void deleteNode(ListNode node) {
      // requirement: delete one node and delete node's value
      // change the node's value instead of the node itself
      // no head

      // node.val need to be deleted, so use the next node's value to cover this value
      node.val = node.next.val;
      // and decrease one node to satisfy the requirement
      node.next = node.next.next;
    }
  }
}
