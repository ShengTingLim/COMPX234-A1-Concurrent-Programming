class printList {
    Node head; // head of list

    /* Node */
    static class Node {
        printDoc document;
        Node next;

        // Constructor
        Node(printDoc doc) {
            document = doc;
            next = null;
        }
    }

    // Insert a print request in the queue
    public static printList queueInsert(printList list, printDoc doc) {
        Node new_node = new Node(doc);
        // if the queue is empty, start a queue
        if (list.head == null) {
            list.head = new_node;
            System.out.println("Inserted a request in the queue from " + new_node.document.getSender());
            System.out.println("Number of requests in the queue 1");
        } else {
            // Maintain a queue of 5 print requests.
            // If more than 5 requests are sent add the latest one in the queue but remove
            // the head of the list

            // traverse the list
            Node last = list.head;
            int count = 1;
            while (last.next != null) {
                last = last.next;
                count++;
            }

            // Insert the new_node at last node
            last.next = new_node;
            count++;
            System.out.println("Inserted a request in the queue from " + new_node.document.getSender());
            // if there are more than 5 nodes in the queue move the head one node down
            if (count > 5) {
                list.head = list.head.next;
                System.out.println("!!!!!!Attention: Overwrite!!!!!!");
                count--;
            }
            System.out.println("Number of requests in the queue " + count);

        }
        return list;
    }

    // Method to print the head of the list
    public static void queuePrint(printList list, int id) {
        // Only print if there is a node in the list
        if (list.head != null) {
            Node currNode = list.head;
            System.out.println(":::::");
            System.out.println("Printer " + id + "Printing the request from Machine ID: "
                    + currNode.document.getSender() + " " + currNode.document.getStr() + " ");
            System.out.println(":::::");
            System.out.flush();
            // Once printed remove the node from the queue
            list.head = list.head.next;
        }
    }

    // Print the contents of the entire list ---for debugging ---
    // Doesn't remove any nodes from the list
    public static void queuePrintAll(printList list) {
        Node currNode = list.head;

        System.out.print("LinkedList: ");

        // Traverse through the LinkedList
        while (currNode != null) {
            // Print the data at current node
            System.out.print(currNode.document.getStr() + " ");

            // Go to next node
            currNode = currNode.next;
        }
    }
}