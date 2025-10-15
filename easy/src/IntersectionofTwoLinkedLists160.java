public class IntersectionofTwoLinkedLists160 {
  public class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
      val = x;
      next = null;
    }
  }

  public class Solution {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
      // Although two lists lengths maybe different, the sum of A and B lists are equal
      // So, when pointer A finishes the traversal, then jump to listB.
      // Likewise, when pointer B finishes the traversal, then jump to listA.

      // We need two pointers to traversal and two headers to store the first position of lists
      ListNode pointerA = headA;
      ListNode pointerB = headB;
      // target: pointerA==pointerB
      while (pointerA != pointerB) {
        if (pointerA == null) {
          pointerA.next = pointerB;
        } else {
          pointerA = pointerA.next;
        }
        if (pointerB == null) {
          pointerB.next = pointerA;
        } else {
          pointerB = pointerB.next;
        }

      }
      // whatever pointerA or pointerB
      return pointerA;
    }
  }
}
