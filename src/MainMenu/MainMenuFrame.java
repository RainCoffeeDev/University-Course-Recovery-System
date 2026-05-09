package MainMenu;

import AcademicReport.AcademicReportPanel;
import CourseRecovery.CourseRecoveryPanel;
import CourseRecovery.PlanDetailsPanel;
import EligibilityandEnrolment.StudentEligibilityApp;
import UserManagement.User;
import UserManagement.UserManagementPanel;
import UserManagement.UserManager;
import Login.LoginLogger;
import Login.LoginFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Main menu / dashboard frame for the Course Recovery System.
 */
public class MainMenuFrame extends JFrame {

    private final UserManager userManager;
    private final LoginLogger logger;
    private final User loggedInUser;

    private JPanel contentPanel;          // right side, uses CardLayout

    // Make these public so other panels (like PlanDetailsPanel) can use them
    public static final String CARD_HOME         = "HOME";
    public static final String CARD_USER         = "USER";
    public static final String CARD_RECOVERY     = "RECOVERY";
    public static final String CARD_REPORT       = "REPORT";
    public static final String CARD_TASK_DETAILS = "TASK_DETAILS";
    public static final String CARD_ELIGIBILITY  = "ELIGIBILITY";

    // References to panels we switch between
    private UserManagementPanel userPanel;
    private CourseRecoveryPanel recoveryPanel;
    private PlanDetailsPanel detailsPanel;
    private AcademicReportPanel reportPanel;
    private StudentEligibilityApp eligibilityPanel;
    
    // NAVIGATION BUTTONS (Class Fields)
    private RoundedHoverButton btnHome;
    private RoundedHoverButton btnUserMgmt;
    private RoundedHoverButton btnReport;
    private RoundedHoverButton btnEligibility;
    private RoundedHoverButton btnRecovery;

    public MainMenuFrame(UserManager userManager, LoginLogger logger, User user) {
        this.userManager = userManager;
        this.logger = logger;
        this.loggedInUser = user;

        setTitle("Course Recovery System - Main Menu");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        GradientPanel root = new GradientPanel();
        setContentPane(root);
        root.setLayout(new BorderLayout());

        buildSidebar(root);      // 1. Create the buttons
        buildContentArea(root);  // 2. Create the panels
        applyPermissions();      // 3. Hide buttons based on Role
    }

    /* =================== SHARED CUSTOM COMPONENTS =================== */

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

    // keep RoundedHoverButton INSIDE MainMenuFrame so others can reuse via
    // MainMenuFrame.RoundedHoverButton
    public static class RoundedHoverButton extends JButton {
        private final Color normalColor = new Color(0, 155, 155);
        private final Color hoverColor  = new Color(0, 200, 200);

        public RoundedHoverButton(String text) {
            super(text);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBackground(normalColor);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) setBackground(hoverColor);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(normalColor);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill = getBackground();
            if (!isEnabled()) {
                fill = new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), 90);
            }
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            super.paintComponent(g2);
            g2.dispose();
        }
    }

    /* =================== LEFT SIDEBAR =================== */

    private void buildSidebar(JPanel root) {
        JPanel sideBar = new JPanel();
        sideBar.setPreferredSize(new Dimension(220, 460));
        sideBar.setBackground(new Color(10, 20, 40, 230));
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel appTitle = new JLabel("<html><b>CRS</b></html>");
        appTitle.setForeground(Color.WHITE);
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        appTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appSub = new JLabel("<html>Course Recovery System<br>for Educational Institutions</html>");
        appSub.setForeground(new Color(180, 180, 180));
        appSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        appSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        sideBar.add(appTitle);
        sideBar.add(appSub);
        sideBar.add(Box.createVerticalStrut(15));

        JLabel lblUser = new JLabel(
                "<html><b>User:</b> " + loggedInUser.getUsername() +
                        "<br><b>Role:</b> " + loggedInUser.getRole() + "</html>"
        );
        lblUser.setForeground(new Color(210, 210, 210));
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        sideBar.add(lblUser);
        sideBar.add(Box.createVerticalStrut(20));

        JLabel navLabel = new JLabel("Modules");
        navLabel.setForeground(new Color(120, 200, 200));
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sideBar.add(navLabel);
        sideBar.add(Box.createVerticalStrut(10));

        Dimension btnSize = new Dimension(180, 36);

        // --- FIXED: Assigning to the correct variables below ---
        
        btnHome = new RoundedHoverButton("Dashboard");
        btnHome.setMaximumSize(btnSize);
        btnHome.addActionListener(e -> showCard(CARD_HOME));
        sideBar.add(btnHome);
        sideBar.add(Box.createVerticalStrut(8));

        btnUserMgmt = new RoundedHoverButton("User Management"); // Fixed variable name
        btnUserMgmt.setMaximumSize(btnSize);
        btnUserMgmt.addActionListener(e -> showCard(CARD_USER));
        sideBar.add(btnUserMgmt);
        sideBar.add(Box.createVerticalStrut(8));

        btnReport = new RoundedHoverButton("Academic Reporting"); // Fixed variable name
        btnReport.setMaximumSize(btnSize);
        btnReport.setEnabled(true);
        btnReport.addActionListener(e -> showCard(CARD_REPORT));
        sideBar.add(btnReport);
        sideBar.add(Box.createVerticalStrut(8));

        btnEligibility = new RoundedHoverButton("Eligibility & Enrolment"); // Fixed variable name
        btnEligibility.setMaximumSize(btnSize);
        btnEligibility.addActionListener(e -> showCard(CARD_ELIGIBILITY));
        sideBar.add(btnEligibility);
        sideBar.add(Box.createVerticalStrut(8));

        btnRecovery = new RoundedHoverButton("Course Recovery Plan"); // Fixed variable name
        btnRecovery.setMaximumSize(btnSize);
        btnRecovery.setEnabled(true);
        btnRecovery.addActionListener(e -> showCard(CARD_RECOVERY));
        sideBar.add(btnRecovery);
        sideBar.add(Box.createVerticalStrut(8));

        sideBar.add(Box.createVerticalGlue());

        RoundedHoverButton btnChangePass = new RoundedHoverButton("Change Password");
        btnChangePass.setMaximumSize(btnSize);
        btnChangePass.addActionListener(e -> changePassword());
        sideBar.add(btnChangePass);
        sideBar.add(Box.createVerticalStrut(8));

        RoundedHoverButton btnLogout = new RoundedHoverButton("Logout");
        btnLogout.setMaximumSize(btnSize);
        btnLogout.addActionListener(e -> logout());
        sideBar.add(btnLogout);

        root.add(sideBar, BorderLayout.WEST);
    }

    /* =================== RIGHT CONTENT AREA =================== */

    private void buildContentArea(JPanel root) {
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        /* ------------ DASHBOARD CARD (HOME) ------------ */
        JPanel home = new JPanel(new BorderLayout(15, 15));
        home.setOpaque(false);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Welcome to Course Recovery System");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel(
                "<html>"
                        + "Manage users, monitor failed courses, and track recovery tasks in one place.<br>"
                        + "Use the modules on the left to navigate through the system."
                        + "</html>");
        lblSub.setForeground(new Color(210, 210, 210));
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        top.add(lblTitle);
        top.add(Box.createVerticalStrut(6));
        top.add(lblSub);

        home.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 3, 15, 0));
        center.setOpaque(false);

        int totalUsers = 0;
        try {
            totalUsers = userManager.getAllUsers().size();
        } catch (Exception ignored) {}

        String roleText = "";
        try {
            roleText = loggedInUser.getRole().toString().replace("_", " ");
        } catch (Exception ignored) {}

        JPanel cardUsers = createInfoCard(
                "Total Users",
                String.valueOf(totalUsers),
                "Users registered in the system."
        );

        JPanel cardModules = createInfoCard(
                "Modules",
                "5",
                "Dashboard, User, Report, Recovery, Eligibility."
        );

        JPanel cardRole = createInfoCard(
                "Logged-in Role",
                roleText,
                "Current permissions for this session."
        );

        center.add(cardUsers);
        center.add(cardModules);
        center.add(cardRole);

        home.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel lblTipsTitle = new JLabel("Quick actions");
        lblTipsTitle.setForeground(new Color(200, 230, 255));
        lblTipsTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTipsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTips = new JLabel(
                "<html>"
                        + "• Use <b>User Management</b> to add or update staff login accounts.<br>"
                        + "• Open <b>Academic Reporting</b> to generate PDF reports and email them to students.<br>"
                        + "• Use <b>Eligibility & Enrolment</b> to quickly check whether a student can progress.<br>"
                        + "• Manage <b>Course Recovery Plan</b> to assign tasks and mark completion."
                        + "</html>");
        lblTips.setForeground(new Color(210, 210, 210));
        lblTips.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTips.setAlignmentX(Component.LEFT_ALIGNMENT);

        bottom.add(lblTipsTitle);
        bottom.add(Box.createVerticalStrut(5));
        bottom.add(lblTips);

        home.add(bottom, BorderLayout.SOUTH);

        contentPanel.add(home, CARD_HOME);

        // ------------ OTHER CARDS (separate classes) ------------
        userPanel        = new UserManagementPanel(userManager);
        recoveryPanel    = new CourseRecoveryPanel(this);   // needs MainMenuFrame to open TaskDetails
        detailsPanel     = new PlanDetailsPanel(this);
        reportPanel      = new AcademicReportPanel();       // keep your existing implementation
        eligibilityPanel = new StudentEligibilityApp();     // keep your existing implementation

        contentPanel.add(userPanel,        CARD_USER);
        contentPanel.add(recoveryPanel,    CARD_RECOVERY);
        contentPanel.add(reportPanel,      CARD_REPORT);
        contentPanel.add(detailsPanel,     CARD_TASK_DETAILS);
        contentPanel.add(eligibilityPanel, CARD_ELIGIBILITY);

        root.add(contentPanel, BorderLayout.CENTER);
        showCard(CARD_HOME);
    }

    private JPanel createInfoCard(String title, String value, String desc) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(5, 5));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 100, 150), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JPanel inner = new JPanel();
        inner.setLayout(new BorderLayout(4, 4));
        inner.setBackground(new Color(10, 25, 55, 210));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(new Color(190, 220, 255));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblValue = new JLabel(value, SwingConstants.LEFT);
        lblValue.setForeground(Color.WHITE);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel lblDesc = new JLabel("<html>" + desc + "</html>");
        lblDesc.setForeground(new Color(200, 200, 210));
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        inner.add(lblTitle, BorderLayout.NORTH);
        inner.add(lblValue, BorderLayout.CENTER);
        inner.add(lblDesc, BorderLayout.SOUTH);

        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    public void showCard(String name) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
    }

    /** Called from CourseRecoveryPanel when "Manage Tasks" is clicked */
    public void openTaskDetails(String studentId, String courseId) {
        detailsPanel.setTarget(studentId, courseId);
        showCard(CARD_TASK_DETAILS);
    }

    /* =================== PERMISSIONS & ACTIONS =================== */
    
    private void applyPermissions() {
        // Safe check to avoid null pointer if user is null (though it shouldn't be)
        if (loggedInUser == null) return;
        
        String role = loggedInUser.getRole().toString(); 

        // 1. Default: Hide restricted modules first to be safe
        btnUserMgmt.setVisible(false);
        btnEligibility.setVisible(false);
        btnRecovery.setVisible(false);
        // Everyone sees Dashboard and Report
        btnHome.setVisible(true);
        btnReport.setVisible(true); 

        // 2. COURSE ADMIN Permissions
        // Access: User Management, Academic Reporting
        if (role.equalsIgnoreCase("COURSE_ADMIN")) {
            btnUserMgmt.setVisible(true);
            // btnEligibility & btnRecovery remain hidden
        } 
        
        // 3. ACADEMIC OFFICER Permissions
        // Access: Academic Reporting, Eligibility, Course Recovery
        else if (role.equalsIgnoreCase("ACADEMIC_OFFICER")) {
            btnEligibility.setVisible(true);
            btnRecovery.setVisible(true);
            // btnUserMgmt remains hidden
        }
    }

    private void changePassword() {
        String newPass = JOptionPane.showInputDialog(this, "Enter new password:");
        if (newPass != null && !newPass.trim().isEmpty()) {
            userManager.changePassword(loggedInUser.getUsername(), newPass.trim());
            userManager.save();
            JOptionPane.showMessageDialog(this, "Password changed successfully!");
        }
    }

    private void logout() {
        logger.log(loggedInUser.getUsername(), "LOGOUT");
        userManager.save();
        dispose();
        new LoginFrame(userManager, logger).setVisible(true);
    }
}