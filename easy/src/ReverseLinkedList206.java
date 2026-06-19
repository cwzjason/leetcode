public class ReverseLinkedList206 {
    public class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

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
//prev(1) <- cur(2)    next(3) -> ...  // 2和3之间断了！


//prev=current.next相当于箭头还是从左往右 我需要从右到左
            while (current != null) {
                // store the next node
                next = current.next;
                // point the next to prev, change the direction of the list
                current.next = prev;
                // move the prev and current to the next node
                prev = current;
                current = next;

            }
            //循环结束时 prev 指向最后一个被处理的节点，也就是原链表的尾节点、反转后的头节点
            return prev;
        }
    }
}
