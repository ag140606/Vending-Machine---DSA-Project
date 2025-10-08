package vending;

import java.util.ArrayList;
import java.util.List;

// Red-Black Tree for products
class RBTree {
    private final boolean RED = true;
    private final boolean BLACK = false;
    class Node {
        String key;
        Product value;
        Node left, right, parent;
        boolean color = RED;
        Node(String key, Product value, Node parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }
    }
    private Node root = null;

    public void insert(String key, Product value) {
        key = key.toLowerCase();
        if (root == null) {
            root = new Node(key, value, null);
            root.color = BLACK;
            return;
        }
        Node cur = root;
        Node parent = null;
        int cmp = 0;
        while (cur != null) {
            parent = cur;
            cmp = key.compareTo(cur.key);
            if (cmp == 0) {
                cur.value = value;
                return;
            } else if (cmp < 0) cur = cur.left;
            else cur = cur.right;
        }
        Node node = new Node(key, value, parent);
        if (cmp < 0) parent.left = node;
        else parent.right = node;
        fixAfterInsertion(node);
    }

    public Product search(String key) {
        Node n = getNode(key.toLowerCase());
        return n == null ? null : n.value;
    }

    private Node getNode(String key) {
        key = key.toLowerCase();
        Node cur = root;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp == 0) return cur;
            if (cmp < 0) cur = cur.left;
            else cur = cur.right;
        }
        return null;
    }

    public List<Product> allProducts() {
        List<Product> out = new ArrayList<>();
        inorder(root, out);
        return out;
    }

    private void inorder(Node n, List<Product> out) {
        if (n == null) return;
        inorder(n.left, out);
        out.add(n.value);
        inorder(n.right, out);
    }

    // Standard RB tree helpers
    private void rotateLeft(Node p) {
        if (p == null) return;
        Node r = p.right;
        p.right = r.left;
        if (r.left != null) r.left.parent = p;
        r.parent = p.parent;
        if (p.parent == null) root = r;
        else if (p.parent.left == p) p.parent.left = r;
        else p.parent.right = r;
        r.left = p;
        p.parent = r;
    }
    private void rotateRight(Node p) {
        if (p == null) return;
        Node l = p.left;
        p.left = l.right;
        if (l.right != null) l.right.parent = p;
        l.parent = p.parent;
        if (p.parent == null) root = l;
        else if (p.parent.right == p) p.parent.right = l;
        else p.parent.left = l;
        l.right = p;
        p.parent = l;
    }
    private void fixAfterInsertion(Node x) {
        x.color = RED;
        while (x != null && x != root && x.parent.color == RED) {
            if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
                Node y = rightOf(parentOf(parentOf(x)));
                if (colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                } else {
                    if (x == rightOf(parentOf(x))) {
                        x = parentOf(x);
                        rotateLeft(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    rotateRight(parentOf(parentOf(x)));
                }
            } else {
                Node y = leftOf(parentOf(parentOf(x)));
                if (colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                } else {
                    if (x == leftOf(parentOf(x))) {
                        x = parentOf(x);
                        rotateRight(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    rotateLeft(parentOf(parentOf(x)));
                }
            }
        }
        root.color = BLACK;
    }
    private boolean colorOf(Node n) { return (n == null) ? BLACK : n.color; }
    private Node parentOf(Node n) { return (n == null) ? null : n.parent; }
    private Node leftOf(Node n) { return (n == null) ? null : n.left; }
    private Node rightOf(Node n) { return (n == null) ? null : n.right; }
    private void setColor(Node n, boolean c) { if (n != null) n.color = c; }
}