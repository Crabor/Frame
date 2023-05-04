package platform.testother;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
         String[] line = br.readLine().split(" ");
         int start = Integer.parseInt(line[0]);
         int end = Integer.parseInt(line[1]);
         List<Integer> list = new LinkedList<>();
         Set<Integer> set = new HashSet<>();
         for (int i = start; i <= end; i++) {
             list.add(i);
         }

         int n = Integer.parseInt(br.readLine());
         for (int i = 0; i < n; i++) {
             line = br.readLine().split(" ");
             int op = Integer.parseInt(line[0]);
             int x = Integer.parseInt(line[1]);
             if (op == 1) {
                 if (list.size() >= x) {
                     list.subList(0, x).clear();
                 }
             } else if (op == 2) {
                 int index = list.indexOf(x);
                 if (index != -1) {
                     list.remove(index);
                     set.add(x);
                 }
             } else {
                 if (set.contains(x)) {
                     set.remove(x);
                     list.add(x);
                 }
             }
         }
         System.out.println(list.get(0));

    }

    //实现一个List的实例QuickList，它的基础是一个双向链表，但是它的查找效率是O(1)，插入效率是O(1)，删除效率是O(1)。
    //要求实现以下接口：
    //void add(int index, T t);
    //void remove(int index);
    //T get(int index);
    //int size();
    //void clear();
    //void addAll(List<T> list);
    //void addAll(int index, List<T> list);
    //List<T> subList(int fromIndex, int toIndex);
    //boolean contains(T t);
    //boolean containsAll(List<T> list);
    //boolean isEmpty();
    //int indexOf(T t);
//    public class QuickList implements List {
//        public class Node {
//            Node prev;
//            Node next;
//            Object data;
//
//            public Node(Object data) {
//                this.data = data;
//            }
//        }
//
//        private Node head;
//        private Node tail;
//        private int size;
//        private Set<Node> set = new HashSet<>();
//
//        public QuickList() {
//            head = new Node(-1);
//            tail = new Node(-1);
//            head.next = tail;
//            tail.prev = head;
//        }
//
//        @Override
//        public int size() {
//            return size;
//        }
//
//        @Override
//        public boolean isEmpty() {
//            return size == 0;
//        }
//
//        @Override
//        public boolean contains(Object o) {
//            return false;
//        }
//    }
}
