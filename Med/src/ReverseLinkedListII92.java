public class ReverseLinkedListII92 {

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
    public ListNode reverseBetween(ListNode head, int left, int right) {
      ListNode dummy = new ListNode(0);
      dummy.next = head;
      // figure out the beforeLeft position
      ListNode beforeLeft = dummy;
      ListNode prev = null;

      for (int i = 1; i < left; i++) {
        // store the beforeLeft position
        beforeLeft = beforeLeft.next;
      }
      // store the start position
      ListNode start = beforeLeft.next;
      ListNode current = start;
      // swap like ReverseLinkedList1
      for (int i = left; i <= right; i++) {
        ListNode temp = current.next;
        current.next = prev;
        prev = current;
        current = temp;
      }
      // connect the reverse part
      beforeLeft.next = prev;
      start.next = current;

      return dummy.next;
    }
  }
}
