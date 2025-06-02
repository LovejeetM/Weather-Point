package com.weatherpoint;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.weatherpoint.service.UserService;

public class LoginPanel extends JPanel {

    private static final String LOGIN_PANEL = "Login";
    private static final String REGISTER_PANEL = "Register";

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton showRegisterButton;

    private JTextField regUsernameField;
    private JPasswordField regPasswordField;
    private JPasswordField regConfirmPasswordField;
    private JTextField regEmailField;
    private JButton registerSubmitButton;
    private JButton backToLoginButton;

    private UserService userService;
    private ImageIcon backgroundIcon;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel loginContentPanel;
    private JPanel registerContentPanel;

    public LoginPanel() {
        userService = new UserService();
        loadBackground();
        setLayout(new GridBagLayout());
        setOpaque(false);
        initializeComponents();
        setupLayout();
        setupListeners();
    }

    private void loadBackground() {
        URL imageURL = getClass().getResource("/rainm.gif");
        if (imageURL != null) {
            backgroundIcon = new ImageIcon(imageURL);
        } else {
            System.err.println("Error: Background image 'rainm.gif' not found in resources folder.");
            backgroundIcon = null;
            setBackground(new Color(230, 240, 255));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundIcon != null) {
            g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void initializeComponents() {
        Font labelFont = new Font("Franklin Gothic", Font.BOLD, 18);
        Font fieldFont = new Font("Franklin Gothic", Font.PLAIN, 18);
        Dimension fieldSize = new Dimension(350, 45);
        Dimension buttonSize = new Dimension(160, 50);
        Font buttonFont = new Font("Franklin Gothic", Font.PLAIN, 20);

        usernameField = new JTextField(25);
        usernameField.setFont(fieldFont);
        usernameField.setPreferredSize(fieldSize);

        passwordField = new JPasswordField(25);
        passwordField.setFont(fieldFont);
        passwordField.setPreferredSize(fieldSize);

        loginButton = new JButton("Login");
        loginButton.setFont(buttonFont);
        loginButton.setPreferredSize(buttonSize);

        showRegisterButton = new JButton("Register");
        showRegisterButton.setFont(buttonFont);
        showRegisterButton.setPreferredSize(buttonSize);

        regUsernameField = new JTextField(25);
        regUsernameField.setFont(fieldFont);
        regUsernameField.setPreferredSize(fieldSize);

        regPasswordField = new JPasswordField(25);
        regPasswordField.setFont(fieldFont);
        regPasswordField.setPreferredSize(fieldSize);

        regConfirmPasswordField = new JPasswordField(25);
        regConfirmPasswordField.setFont(fieldFont);
        regConfirmPasswordField.setPreferredSize(fieldSize);

        regEmailField = new JTextField(25);
        regEmailField.setFont(fieldFont);
        regEmailField.setPreferredSize(fieldSize);

        registerSubmitButton = new JButton("Submit Registration");
        registerSubmitButton.setFont(buttonFont);
        registerSubmitButton.setPreferredSize(new Dimension(220, 50));


        backToLoginButton = new JButton("Back to Login");
        backToLoginButton.setFont(buttonFont);
        backToLoginButton.setPreferredSize(new Dimension(180, 50));


        KeyListener enterListener = new KeyListener() {
            @Override public void keyTyped(KeyEvent e) {}
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                   if(loginContentPanel.isVisible()) {
                        handleLogin();
                   }
                }
            }
            @Override public void keyReleased(KeyEvent e) {}
        };

        usernameField.addKeyListener(enterListener);
        passwordField.addKeyListener(enterListener);

        regUsernameField.addKeyListener(enterListener);
        regPasswordField.addKeyListener(enterListener);
        regConfirmPasswordField.addKeyListener(enterListener);
        regEmailField.addKeyListener(enterListener);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("WeatherPoint");
        titleLabel.setFont(new Font("Franklin Gothic", Font.BOLD, 60));
        titleLabel.setForeground(new Color(230, 245, 255)); // color -----------------------
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Check Weather Anywhere at Anytime");
        subtitleLabel.setFont(new Font("Franklin Gothic", Font.PLAIN, 28));
        subtitleLabel.setForeground(new Color(190, 220, 240)); // Lighter color =-----------------------
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 15, 35, 15);
        panel.add(subtitleLabel, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Franklin Gothic", Font.PLAIN, 24));
        userLabel.setForeground(Color.WHITE);
        panel.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Franklin Gothic", Font.PLAIN, 24));
        passLabel.setForeground(Color.WHITE);
        panel.add(passLabel, gbc);


        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(showRegisterButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(40, 10, 10, 10);
        panel.add(buttonPanel, gbc);

        return panel;
    }

     private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Franklin Gothic", Font.BOLD, 48));
        titleLabel.setForeground(new Color(230, 245, 255));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 15, 35, 15);
        panel.add(titleLabel, gbc);

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 1;

        addRegisterField(panel, "Username:", regUsernameField, gbc, 1);
        addRegisterField(panel, "Password:", regPasswordField, gbc, 2);
        addRegisterField(panel, "Confirm Password:", regConfirmPasswordField, gbc, 3);
        addRegisterField(panel, "Email:", regEmailField, gbc, 4);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(registerSubmitButton);
        buttonPanel.add(backToLoginButton);


        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 10, 10, 10);
        panel.add(buttonPanel, gbc);

        return panel;
    }


    private void setupLayout() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        loginContentPanel = createLoginPanel();
        registerContentPanel = createRegisterPanel();

        cardPanel.add(loginContentPanel, LOGIN_PANEL);
        cardPanel.add(registerContentPanel, REGISTER_PANEL);

        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.weightx = 1.0;
        gbcMain.weighty = 1.0;
        gbcMain.anchor = GridBagConstraints.CENTER;//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        add(cardPanel, gbcMain);

        cardLayout.show(cardPanel, LOGIN_PANEL);
    }


    private void setupListeners() {
        loginButton.addActionListener(e -> handleLogin());
        showRegisterButton.addActionListener(e -> cardLayout.show(cardPanel, REGISTER_PANEL));
        registerSubmitButton.addActionListener(e -> handleRegisterSubmit());
        backToLoginButton.addActionListener(e -> {
            clearRegistrationFields();
            cardLayout.show(cardPanel, LOGIN_PANEL);
             });
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            showError("Username cannot be empty");
            usernameField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            showError("Password cannot be empty");
            passwordField.requestFocus();
            return;
        }

        try {
            if (userService.authenticate(username, password)) {
                showMainDashboard();
            } else {
                showError("Invalid username or password");
                passwordField.setText("");
                passwordField.requestFocus();
            }
        } catch (Exception e) {
            showError("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleRegisterSubmit() {
         try {
            String username = regUsernameField.getText().trim();
            String password = new String(regPasswordField.getPassword());
            String confirm = new String(regConfirmPasswordField.getPassword());
            String email = regEmailField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || confirm.isEmpty()) {
                throw new IllegalArgumentException("All fields are required");
            }
            if (!password.equals(confirm)) {
                 regConfirmPasswordField.setText("");
                 regPasswordField.setText("");
                 regPasswordField.requestFocus();
                throw new IllegalArgumentException("Passwords do not match");
            }
            if (!isValidEmail(email)) {
                 regEmailField.requestFocus();
                throw new IllegalArgumentException("Invalid email format");
            }

            userService.registerUser(username, password, email);

            showSuccess("Registration successful! Please login.");
            clearRegistrationFields();
            cardLayout.show(cardPanel, LOGIN_PANEL);

            usernameField.setText(username);
            passwordField.setText("");
            passwordField.requestFocus();

        } catch (IllegalArgumentException ex) {
                showError("Registration error: " + ex.getMessage());
        } catch (Exception ex) {
            showError("Registration error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void clearRegistrationFields() {
        regUsernameField.setText("");
        regPasswordField.setText("");
        regConfirmPasswordField.setText("");
        regEmailField.setText("");
    }


    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void addRegisterField(Container container, String labelText,
                                JComponent field, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Franklin Gothic", Font.PLAIN, 22));
        label.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        container.add(label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(field, gbc);
    }

    private void showMainDashboard() {
        Container parent = SwingUtilities.getAncestorOfClass(JFrame.class, this);
        if (parent instanceof JFrame) {
            JFrame frame = (JFrame) parent;
            frame.getContentPane().removeAll();
            
            frame.getContentPane().add(new DashboardPanel());
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
        } else {
             showError("Internal error: Could not find main application window.");
        }
    }

     private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}