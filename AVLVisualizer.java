import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;



class TreeNode {
    int data;
    int height;
    TreeNode left, right;

    public TreeNode(int data) {
        this.data = data;
        this.height = 1;
        this.left = null;
        this.right = null;
    }
}



class AVLTree {

    private int getHeight(TreeNode node) {
        return (node == null) ? 0 : node.height;
    }

    private int getBalance(TreeNode node) {
        if (node == null) return 0;
        return getHeight(node.left) - getHeight(node.right);
    }

    private void updateHeight(TreeNode node) {
        if (node != null) {
            node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        }
    }

    private TreeNode rotateRight(TreeNode y) {
        TreeNode x = y.left;
        TreeNode T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    private TreeNode rotateLeft(TreeNode x) {
        TreeNode y = x.right;
        TreeNode T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    public TreeNode insert(TreeNode node, int key) {

        if (node == null)
            return new TreeNode(key);

        if (key < node.data)
            node.left = insert(node.left, key);
        else if (key > node.data)
            node.right = insert(node.right, key);
        else
            return node; 

        updateHeight(node);

        int balance = getBalance(node);

        if (balance > 1 && key < node.left.data)
            return rotateRight(node);

      
        if (balance < -1 && key > node.right.data)
            return rotateLeft(node);

     
        if (balance > 1 && key > node.left.data) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

 
        if (balance < -1 && key < node.right.data) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }
}



class TreePanel extends JPanel {

    private TreeNode root;

    private final int NODE_RADIUS = 25;
    private final int VERTICAL_GAP = 80;

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        setBackground(new Color(10, 15, 35));

        if (root != null) {
            drawTree(g, root, getWidth() / 2, 60, getWidth() / 4);
        }
    }

    private void drawTree(Graphics g, TreeNode node, int x, int y, int horizontalGap) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));


        if (node.left != null) {
            int childX = x - horizontalGap;
            int childY = y + VERTICAL_GAP;

            g2.setColor(Color.WHITE);
            g2.drawLine(x, y, childX, childY);

            drawTree(g2, node.left, childX, childY, horizontalGap / 2);
        }


        if (node.right != null) {
            int childX = x + horizontalGap;
            int childY = y + VERTICAL_GAP;

            g2.setColor(Color.WHITE);
            g2.drawLine(x, y, childX, childY);

            drawTree(g2, node.right, childX, childY, horizontalGap / 2);
        }


        g2.setColor(new Color(100, 150, 255));
        g2.fillOval(x - NODE_RADIUS, y - NODE_RADIUS,
                NODE_RADIUS * 2, NODE_RADIUS * 2);

        g2.setColor(Color.WHITE);
        g2.drawOval(x - NODE_RADIUS, y - NODE_RADIUS,
                NODE_RADIUS * 2, NODE_RADIUS * 2);

     
        String text = String.valueOf(node.data);
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        g2.drawString(text,
                x - textWidth / 2,
                y + textHeight / 4);
    }
}


public class AVLVisualizer extends JFrame {

    private JTextField nodeCountField;
    private JTextField valuesField;
    private JButton buildButton;
    private JButton resetButton;

    private TreePanel treePanel;
    private AVLTree avl;
    private TreeNode root;

    public AVLVisualizer() {

        avl = new AVLTree();
        root = null;

        setTitle("AVL Tree Visualizer");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeTopPanel();
        initializeTreePanel();
    }

    private void initializeTopPanel() {

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(20, 25, 45));
        topPanel.setPreferredSize(new Dimension(1000, 70));

        JLabel title = new JLabel("AVL Tree Visualizer");
        title.setForeground(new Color(120, 180, 255));
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel countLabel = new JLabel("Nodes:");
        countLabel.setForeground(Color.WHITE);

        nodeCountField = new JTextField(5);

        JLabel valuesLabel = new JLabel("Values:");
        valuesLabel.setForeground(Color.WHITE);

        valuesField = new JTextField(20);

        buildButton = new JButton("Build");
        resetButton = new JButton("Reset");

        buildButton.addActionListener(e -> buildTree());
        resetButton.addActionListener(e -> resetTree());

        topPanel.add(title);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(countLabel);
        topPanel.add(nodeCountField);
        topPanel.add(valuesLabel);
        topPanel.add(valuesField);
        topPanel.add(buildButton);
        topPanel.add(resetButton);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initializeTreePanel() {
        treePanel = new TreePanel();
        add(treePanel, BorderLayout.CENTER);
    }

    private void buildTree() {

        try {
            int n = Integer.parseInt(nodeCountField.getText().trim());
            String[] values = valuesField.getText().trim().split("\\s+");

            if (values.length != n) {
                JOptionPane.showMessageDialog(this,
                        "Number of values must match node count!");
                return;
            }

            root = null;

            for (int i = 0; i < n; i++) {
                int val = Integer.parseInt(values[i]);
                root = avl.insert(root, val);
            }

            treePanel.setRoot(root);
            treePanel.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid input! Enter integers only.");
        }
    }

    private void resetTree() {
        root = null;
        nodeCountField.setText("");
        valuesField.setText("");
        treePanel.setRoot(null);
        treePanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AVLVisualizer().setVisible(true);
        });
    }
}
