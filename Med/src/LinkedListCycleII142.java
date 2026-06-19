public class LinkedListCycleII142 {
    public ListNode detectCycle(ListNode head) {
        if (head == null || head.next == null) {
            return null;
        }
        ListNode fast = head;
        ListNode slow = head;
        boolean hasCycle = false;
        //1. 判断是否存在环
        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            //meet represents exist cycle, but it doesn't represent it is the start of the cycle
            if (fast == slow) {
                hasCycle = true;
                break;
            }

        }
        if (!hasCycle) {
            return null;
        }
        /*
L1：链表头到环起点的距离
C：环的长度
L2：慢指针第一次相遇点到环起点的距离（顺着环走）
n：快指针比慢指针多走了环的圈数（整数）

慢指针走 L1 + L2
快指针走两倍 = 2 × (L1 + L2)
另一种表示 = L1 + L2 + n*C
新指针从head走到环的起始点 L1
slow 从meet 节点走到环的起始点 n*C-L2=L1  所以二者会同时到达环的起点
因为多走那几圈（n-1圈）是在环里绕，最终还是会回到入环点，不影响最终位置。
         */

        ListNode ptr = head;
        while (ptr != slow) {
            ptr = ptr.next;
            slow = slow.next;
        }

        return ptr;
    }

    class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
            next = null;
        }
    }
    /**
     * Definition for singly-linked list.

     */
}
