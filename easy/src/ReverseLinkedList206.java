public class ReverseLinkedList206 {
  public class ListNode {
    int val;
    ListNode next;

    ListNode() {}

    ListNode(int val) {
      this.val = val;
    }

    ListNode(int val, ListNode next) {
      this.val = val;
      this.next = next;
    }
  }


  class Solution {
    public ListNode reverseList(ListNode head) {
      ListNode current = head;
      ListNode prev = null;
      ListNode next = null;


      while (current != null) {
        // store the next node
        next = current.next;
        // point the next to prev, change the direction of the list
        current.next = prev;
        // move the prev and current to the next node
        prev = current;
        current = next;

      }
      return prev;
    }
  }
}
