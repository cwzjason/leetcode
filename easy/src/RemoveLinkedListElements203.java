public class RemoveLinkedListElements203 {
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
    public ListNode removeElements(ListNode head, int val) {

      if (head == null) {
        return head;
      }


      ListNode dummy = new ListNode();
      dummy.next = head;
      // use temp to replace head and head can't remove itself
      // temp's index is 0 and head's index is 1
      // so the head can be removed
      ListNode temp = dummy;

      while (temp.next != null) {

        if (temp.next.val == val) {
          temp.next = temp.next.next;
        } else {
          temp = temp.next;
        }
      }


      return dummy.next;
    }
  }
}
