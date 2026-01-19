public class OddEvenLinkedList328 {

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
    public ListNode oddEvenList(ListNode head) {
      if (head == null) {
        return null;
      }
      // first node's index should be old and head
      ListNode odd = head;
      ListNode even = odd.next;
      // store the even's head
      ListNode evenHead = even;
      // even and odd move to next together
      while (even != null && even.next != null) {
        // odd connect to odd
        odd.next = even.next;
        // even connect to even
        even.next = even.next.next;

        // move to next
        odd = odd.next;
        even = even.next;
      }
      // connect odd tail and even head
      odd.next = evenHead;
      return head;
    }
  }
}
