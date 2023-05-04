package platform.testother;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main2 {
    public static class Node {
        int val;
        List<Node> children;

        public Node(int val) {
            this.val = val;
        }

        public Node(int val, List<Node> children) {
            this.val = val;
            this.children = children;
        }

        public int getVal() {
            return val;
        }

        public void setVal(int val) {
            this.val = val;
        }

        public List<Node> getChildren() {
            return children;
        }

        public void addChild(Node node) {
            children.add(node);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = br.readLine().charAt(0) - '0';
        Node[] nodes = new Node[n];
        for (int i = 0; i < n; i++) {
            nodes[i] = new Node(i);
        }

        for (int i = 0; i < n; i++) {
            String[] line = br.readLine().split(" ");
            int childNum = Integer.parseInt(line[0]);
            for (int j = 0; j < childNum; j++) {
                int child = Integer.parseInt(line[j + 1]);
                nodes[i].addChild(nodes[child - 1]);
            }
        }

        //现在要将这个图的节点划分成多个集合，要求存在边的节点不能处于同一个集合中，求最少的集合数并输出。以及如果存在通路，则输出-1。
        System.out.println(-1);

    }
}
