public class SwapNodesinPairs24 {


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
    public ListNode swapPairs(ListNode head) {
      // base case
      if (head == null || head.next == null) {
        return head;
      }

      ListNode dummy = new ListNode();
      dummy.next = head;
      ListNode temp = dummy;

      while (head != null && head.next != null) {
        ListNode first = head;
        ListNode second = head.next;

        // swap
        first.next = second.next;
        temp.next = second;
        second.next = first;

        temp = first;
        head = first.next;

      }

      return dummy.next;
    }
  }
}
