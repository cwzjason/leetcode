public class RemoveNthNodeFromEndofList19 {


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
    public ListNode removeNthFromEnd(ListNode head, int n) {
      int count = 0;
      ListNode current = head;
      while (current != null) {
        count++;
        current = current.next;
      }

      if (count == 1) {
        return null;
      }
      // remove the first node
      if (count == n) {
        return head.next;
      }
      current = head;
      // head index=1
      for (int i = 1; i < count - n; i++) {
        current = current.next;
      }
      current.next = current.next.next;
      // return the head of the LinkedList
      return head;
    }
  }
}
