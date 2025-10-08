package vending;

import java.util.Set;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class VendingMachine extends JFrame {
    private bloomFilter bloom;
    private RBTree rbTree;

    // Denominations allowed
    private static final Set<Integer> VALID_DENOM = Set.of(10, 20, 50, 100);

    private DefaultListModel<Product> productsModel = new DefaultListModel<>();
    private JList<Product> productsList = new JList<>(productsModel);
    private JTextField cashField = new JTextField("0", 10);
    private JLabel messageLabel = new JLabel(" ");
    private JButton buyButton = new JButton("Buy Selected");
    private JButton refreshButton = new JButton("Refresh List");
    private JButton addCashButton = new JButton("Add Cash");
    
    private int currentBalance = 0;

    public VendingMachine() {
        // Logic init
        bloom = new bloomFilter(256, 3);
        rbTree = new RBTree();

        // hardcoded sample products
        addProduct(new Product("chips", 30, 5));
        addProduct(new Product("chocolates", 50, 3));
        addProduct(new Product("snacks", 20, 10));
        addProduct(new Product("soda", 40, 6));
        addProduct(new Product("cookies", 25, 2));

        // Swing UI setup
        setTitle("Vending Machine");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        JPanel top = new JPanel();
        JPanel bottom = new JPanel();
        JPanel center = new JPanel(new BorderLayout());

        // Product list
        productsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane prodScroll = new JScrollPane(productsList);
        center.add(new JLabel("Products:"), BorderLayout.NORTH);
        center.add(prodScroll, BorderLayout.CENTER);

        // Add product and cash controls
        top.add(new JLabel("Insert cash: "));
        top.add(cashField);
        top.add(addCashButton);
        top.add(refreshButton);

        bottom.add(buyButton);
        bottom.add(messageLabel);

        main.add(top, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);

        add(main);

        refreshProductsList();
        updateCashDisplay();

        // Button actions
        buyButton.addActionListener(e -> handlePurchase());
        refreshButton.addActionListener(e -> {
            refreshProductsList();
            messageLabel.setText("Insert denomination (10/20/50/100). Select and buy.");
        });
        addCashButton.addActionListener(e -> addCash());
    }

    private void updateCashDisplay() {
        cashField.setText(String.valueOf(currentBalance));
    }

    private void addCash() {
        try {
            int cashToAdd = Integer.parseInt(cashField.getText().trim());
            
            // Denomination validation
            int rem = cashToAdd;
            int c100 = rem / 100; rem %= 100;
            int c50  = rem / 50;  rem %= 50;
            int c20  = rem / 20;  rem %= 20;
            int c10  = rem / 10;  rem %= 10;
            if (rem != 0) {
                messageLabel.setText("Use ONLY 10,20,50,100 notes/coins.");
                return;
            }
            
            currentBalance += cashToAdd;
            updateCashDisplay();
            messageLabel.setText("Added: " + cashToAdd + ". Total balance: " + currentBalance);
            
            // Check if balance is sufficient for any product
            checkBalanceSufficiency();
            
        } catch (Exception ex) {
            messageLabel.setText("Enter valid integer cash.");
        }
    }

    private void checkBalanceSufficiency() {
        int cheapest = getCheapestAvailableProductPrice();
        if (currentBalance < cheapest) {
            messageLabel.setText("Balance too low. Cheapest product costs: " + cheapest);
        }
    }

    private int getCheapestAvailableProductPrice() {
        return rbTree.allProducts().stream()
                .filter(p -> p.stock > 0)
                .mapToInt(p -> p.price)
                .min().orElse(Integer.MAX_VALUE);
    }

    private void refreshProductsList() {
        productsModel.clear();
        for (Product p : rbTree.allProducts()) {
            productsModel.addElement(p);
        }
    }

    private void addProduct(Product p) {
        rbTree.insert(p.name, p);
        for (int i = 0; i < p.stock; i++) bloom.add(p.name);
    }

    private void handlePurchase() {
        Product chosen = productsList.getSelectedValue();
        if (chosen == null) {
            messageLabel.setText("Please select a product.");
            return;
        }

        if (currentBalance <= 0) {
            messageLabel.setText("Please add cash first.");
            return;
        }

        if (chosen.price > currentBalance) {
            messageLabel.setText("Not enough balance for " + Product.capitalize(chosen.name) + 
                               ". Need: " + chosen.price + ", Have: " + currentBalance);
            return;
        }

        if (!bloom.mightContain(chosen.name)) {
            messageLabel.setText(Product.capitalize(chosen.name) + " out of stock.");
            return;
        }

        Product actual = rbTree.search(chosen.name);
        if (actual == null || actual.stock <= 0) {
            messageLabel.setText(Product.capitalize(chosen.name) + " is OUT OF STOCK.");
            return;
        }

        // Vend product
        actual.decrementStock();
        bloom.remove(actual.name);
        currentBalance -= actual.price;
        updateCashDisplay();

        messageLabel.setText("Bought: " + Product.capitalize(actual.name) +
                ". Balance left: " + currentBalance);

        refreshProductsList();

        // Check if balance can buy any more products
        int cheapest = getCheapestAvailableProductPrice();
        if (currentBalance < cheapest) {
            returnChange();
        } else {
            // Continue shopping automatically
            messageLabel.setText("Purchase successful! Balance: " + currentBalance + 
                               ". Select another product or add more cash.");
        }
    }

    private void returnChange() {
        if (currentBalance > 0) {
            JOptionPane.showMessageDialog(this,
                    "Returning change: " + currentBalance + 
                    "\nBalance is insufficient for any available product.",
                    "Change Returned", JOptionPane.INFORMATION_MESSAGE);
            currentBalance = 0;
            updateCashDisplay();
            messageLabel.setText("Change returned. Please add more cash to continue.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VendingMachine().setVisible(true));
    }
}
