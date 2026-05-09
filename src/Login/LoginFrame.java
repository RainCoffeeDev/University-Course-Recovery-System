/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Login;

/**
 *
 * @author 2ndUF
 */
import MainMenu.MainMenuFrame;
import UserManagement.User;
import UserManagement.UserManager;
import javax.swing.*;
import java.awt.*;
import java.security.SecureRandom;

public class LoginFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private UserManager userManager;
    private LoginLogger logger;

    public LoginFrame(UserManager userManager, LoginLogger logger) {
        this.userManager = userManager;
        this.logger = logger;

        setTitle("Course Recovery System (CRS)");
        setSize(500, 450);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // gradient background
        setContentPane(new GradientPanel());

        buildUI();
    }

    private void buildUI() {
        setLayout(null);  // we will position components manually

        // Title
        JLabel loginTitle = new JLabel(
                "<html><center>COURSE RECOVERY SYSTEM (CRS) <br> FOR EDUCATIONAL INSTITUTIONS</center></html>",
                SwingConstants.CENTER
        );
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loginTitle.setForeground(Color.WHITE);
        loginTitle.setBounds(25, 20, 450, 60);
        add(loginTitle);

        //Username
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(new Color(230, 230, 230));
        lblUser.setBounds(70, 110, 350, 25);
        add(lblUser);
        txtUser = new PlaceholderTextField("Example: admin123");
        txtUser.setBounds(70, 140, 350, 40);
        add(txtUser);

        // Password 
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPass.setForeground(new Color(230, 230, 230));
        lblPass.setBounds(70, 200, 350, 25);
        add(lblPass);
        txtPass = new PlaceholderPasswordField("Password");
        txtPass.setBounds(70, 230, 350, 40);
        add(txtPass);
        // "Forgot Password?" Link
        JLabel lblForgot = new JLabel("Forgot Password?");
        lblForgot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblForgot.setForeground(new Color(200, 200, 200));
        lblForgot.setBounds(320, 275, 130, 20); // Position it below password, aligned right
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect and click action
        lblForgot.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                onForgotPassword(); // Call the logic method
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                lblForgot.setForeground(Color.WHITE); // Highlight on hover
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                lblForgot.setForeground(new Color(200, 200, 200)); // Reset color
            }
        });
        add(lblForgot);
        // LOGIN 
        RoundedButton btnLogin = new RoundedButton("LOGIN");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBounds(70, 300, 350, 50);   // <-- IMPORTANT: size & position
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> doLogin());
        add(btnLogin);
    }

    private void doLogin() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());
        
        User u = userManager.authenticate(username, password);
        if (u == null) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Username/Password OR account is deactivated.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        logger.log(u.getUsername(), "LOGIN");
        dispose();
        new MainMenuFrame(userManager, logger, u).setVisible(true);
    }

    
    //Custom GUI Background//
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(10, 40, 90),
                    0, getHeight(), new Color(5, 10, 25)
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // Rounded text field with placeholder
    class PlaceholderTextField extends JTextField {
        private final String placeholder;

        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setForeground(Color.WHITE);
            setCaretColor(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            super.paintComponent(g2);
            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(new Color(220, 220, 220, 180));
                g2.setFont(getFont());
                g2.drawString(placeholder, 15, getHeight() / 2 + 5);
            }
            g2.dispose();
        }
    }

    //password field 
    class PlaceholderPasswordField extends JPasswordField {
        private final String placeholder;

        public PlaceholderPasswordField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setForeground(Color.WHITE);
            setCaretColor(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255, 255, 255, 60));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

            super.paintComponent(g2);

            if (getPassword().length == 0 && !isFocusOwner()) {
                g2.setColor(new Color(220, 220, 220, 180));
                g2.setFont(getFont());
                g2.drawString(placeholder, 15, getHeight() / 2 + 5);
            }

            g2.dispose();
        }
    }
    private void onForgotPassword() {
        // 1. Ask for Username
        String username = JOptionPane.showInputDialog(this, 
                "Enter your Username:", 
                "Reset Password - Step 1/2", 
                JOptionPane.QUESTION_MESSAGE);

        if (username == null || username.trim().isEmpty()) return;

        // 2. Check if User exists immediately
        User user = userManager.findByUsername(username.trim());
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Username not found in the system.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Ask for Email (Since we don't have it on file)
        String email = JOptionPane.showInputDialog(this, 
                "Enter the Email address to receive the new password:", 
                "Reset Password - Step 2/2", 
                JOptionPane.QUESTION_MESSAGE);

        if (email == null || email.trim().isEmpty()) return;

        // 4. Generate a new Random Password
        String newPassword = generateRandomPassword(8);

        // 5. Update the User and SAVE to file
        // NOTE: You must ensure your User class has setPassword() 
        // and UserManager has a save() or saveData() method.
        user.setPassword(newPassword); 
        userManager.save(); // <--- CRITICAL: Saves the new password to your .dat file

        // 6. Send Email in a background thread
        new Thread(() -> {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            String subject = "Password Reset - Course Recovery System";
            String message = "Dear " + user.getUsername() + ",\n\n"
                           + "Your password has been successfully reset.\n"
                           + "Your New Temporary Password is: " + newPassword + "\n\n"
                           + "Please log in and change this password as soon as possible.\n\n"
                           + "Regards,\nAdmin Team";

            boolean sent = Email.EmailSender.sendEmail(email, subject, message);

            setCursor(Cursor.getDefaultCursor());

            if (sent) {
                JOptionPane.showMessageDialog(this, 
                        "A new password has been sent to " + email, 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Failed to send email. Password was reset but email failed.", 
                        "Connection Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    // Helper method to generate a random 8-character password
    private String generateRandomPassword(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        return sb.toString();
    }

    // Login Button Design
    class RoundedButton extends JButton {
        private boolean isHovered = false; // Track if mouse is over button

        public RoundedButton(String text) {
            super(text);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);

            // Add MouseListener to detect hover state
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    isHovered = true;
                    repaint(); // Redraw the button with the hover color
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    isHovered = false;
                    repaint(); // Redraw the button with the normal color
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            // CHANGE COLOR BASED ON HOVER STATE
            if (isHovered) {
                g2.setColor(new Color(72, 200, 100)); // Lighter Green (Hover)
            } else {
                g2.setColor(new Color(52, 168, 83));  // Original Green (Normal)
            }

            // If you want a "Pressed" effect, you can check model.isPressed()
            if (getModel().isPressed()) {
                g2.setColor(new Color(40, 130, 65));  // Darker Green (Click)
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

            super.paintComponent(g2);
            g2.dispose();
        }
    }
}