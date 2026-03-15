package com.teenwallet.ui.components;

import com.teenwallet.service.WalletService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VirtualCardPanel extends JPanel {
    private JLabel balanceLabel;
    private JLabel cardNumberLabel;
    private JLabel expiryLabel;
    private JLabel cvvLabel;
    private boolean cardFlipped = false;
    private String username;

    public VirtualCardPanel(String username) {
        this.username = username;
        setPreferredSize(new Dimension(350, 200));
        setBackground(new Color(25, 25, 112)); // Midnight blue
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        setLayout(new GridBagLayout());

        initComponents();
        addTapToPayAnimation();
        updateBalance();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Card chip icon (simulated)
        JLabel chipLabel = new JLabel("💳");
        chipLabel.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        chipLabel.setForeground(new Color(255, 215, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        add(chipLabel, gbc);

        // Card type
        JLabel typeLabel = new JLabel("TEEN WALLET");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        typeLabel.setForeground(new Color(255, 215, 0));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(typeLabel, gbc);

        // Balance label
        JLabel balanceTitleLabel = new JLabel("Balance");
        balanceTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        balanceTitleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(balanceTitleLabel, gbc);

        // Balance amount
        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        balanceLabel.setForeground(Color.WHITE);
        gbc.gridy = 2;
        add(balanceLabel, gbc);

        // Card number
        cardNumberLabel = new JLabel("4242 4242 4242 4242");
        cardNumberLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        cardNumberLabel.setForeground(Color.WHITE);
        gbc.gridy = 3;
        add(cardNumberLabel, gbc);

        // Expiry and CVV panel
        JPanel expiryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        expiryPanel.setOpaque(false);

        expiryLabel = new JLabel("12/25");
        expiryLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        expiryLabel.setForeground(Color.WHITE);
        expiryPanel.add(expiryLabel);

        cvvLabel = new JLabel("***");
        cvvLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        cvvLabel.setForeground(Color.WHITE);
        expiryPanel.add(new JLabel("CVV:"));
        expiryPanel.add(cvvLabel);

        gbc.gridy = 4;
        add(expiryPanel, gbc);
    }

    private void addTapToPayAnimation() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                simulateTapToPay();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private void simulateTapToPay() {
        if (!cardFlipped) {
            cvvLabel.setText("123");
            cardFlipped = true;

            Timer timer = new Timer(2000, e -> {
                cvvLabel.setText("***");
                cardFlipped = false;
            });
            timer.setRepeats(false);
            timer.start();

            JOptionPane.showMessageDialog(this,
                    "💫 Tap to Pay Simulation 💫\nPayment terminal connected!\n(Simulated transaction)",
                    "Tap to Pay",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void updateBalance() {
        double balance = WalletService.getBalanceForUser(username);
        balanceLabel.setText(String.format("₹%.2f", balance));
    }
}