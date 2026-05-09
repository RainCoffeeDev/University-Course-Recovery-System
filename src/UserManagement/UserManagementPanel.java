package UserManagement;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import MainMenu.MainMenuFrame.RoundedHoverButton;

public class UserManagementPanel extends JPanel {

    private final UserManager userManager;
    private DefaultListModel<User> listModel;
    private JList<User> lstUsers;
    private RoundedHoverButton btnToggleActive;

    public UserManagementPanel(UserManager userManager) {
        this.userManager = userManager;

        setOpaque(false);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("User Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JLabel desc = new JLabel("Add, update, activate/deactivate and delete user accounts.");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        desc.setForeground(new Color(210, 210, 210));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(title);
        top.add(Box.createVerticalStrut(3));
        top.add(desc);

        add(top, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        lstUsers = new JList<>(listModel);
        lstUsers.setFont(new Font("Consolas", Font.PLAIN, 13));
        lstUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstUsers.setBackground(new Color(10, 20, 40));
        lstUsers.setForeground(Color.WHITE);
        lstUsers.setSelectionBackground(new Color(0, 160, 160));
        lstUsers.setSelectionForeground(Color.WHITE);

        lstUsers.addListSelectionListener(e -> updateToggleButtonLabel());

        JScrollPane scroll = new JScrollPane(lstUsers);
        scroll.getViewport().setBackground(new Color(15, 30, 60));
        add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnPanel.setOpaque(false);

        RoundedHoverButton btnAdd    = new RoundedHoverButton("Add");
        RoundedHoverButton btnUpdate = new RoundedHoverButton("Update");
        btnToggleActive              = new RoundedHoverButton("Deactivate");
        RoundedHoverButton btnDelete = new RoundedHoverButton("Delete User");

        btnAdd.setPreferredSize(new Dimension(110, 32));
        btnUpdate.setPreferredSize(new Dimension(110, 32));
        btnToggleActive.setPreferredSize(new Dimension(150, 32));
        btnDelete.setPreferredSize(new Dimension(130, 32));

        btnAdd.addActionListener(e -> addUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnToggleActive.addActionListener(e -> toggleUserActive());
        btnDelete.addActionListener(e -> deleteUser());

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnToggleActive);
        btnPanel.add(btnDelete);

        add(btnPanel, BorderLayout.SOUTH);

        loadUsers();
    }

    private void loadUsers() {
        listModel.clear();
        List<User> users = userManager.getAllUsers();
        for (User u : users) listModel.addElement(u);
    }

    private User getSelectedUser() {
        User selected = lstUsers.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a user first.");
        }
        return selected;
    }

    private void addUser() {
        String username = JOptionPane.showInputDialog(this, "New username:");
        if (username == null || username.trim().isEmpty()) return;

        String password = JOptionPane.showInputDialog(this, "Initial password:");
        if (password == null || password.trim().isEmpty()) return;

        Role role = askRole();
        if (role == null) return;

        if (!userManager.addUser(username.trim(), password.trim(), role)) {
            JOptionPane.showMessageDialog(this, "User already exists.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            userManager.save();
            loadUsers();
        }
    }

    private void updateUser() {
        User selected = getSelectedUser();
        if (selected == null) return;

        String[] options = {"Change Role", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "What do you want to update?",
                "Update User",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]
        );

        if (choice == 0) {
            Role newRole = askRole();
            if (newRole != null) {
                userManager.changeRole(selected.getUsername(), newRole);
                userManager.save();
                loadUsers();
            }
        }
    }

    private void updateToggleButtonLabel() {
        User selected = lstUsers.getSelectedValue();
        if (selected == null) {
            btnToggleActive.setText("Activate / Deactivate");
            return;
        }
        if (selected.isActive()) {
            btnToggleActive.setText("Deactivate");
        } else {
            btnToggleActive.setText("Activate");
        }
    }

    private void toggleUserActive() {
        User selected = lstUsers.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a user first.");
            return;
        }

        boolean currentlyActive = selected.isActive();
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Do you want to " + (currentlyActive ? "deactivate" : "activate") +
                        " user '" + selected.getUsername() + "'?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            selected.setActive(!currentlyActive);
            userManager.save();
            loadUsers();
            updateToggleButtonLabel();
        }
    }

    private void deleteUser() {
        User selected = getSelectedUser();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Permanently delete user '" + selected.getUsername() + "'?\nThis cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (userManager.deleteUser(selected.getUsername())) {
                userManager.save();
                loadUsers();
                JOptionPane.showMessageDialog(this, "User deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Role askRole() {
        Object[] roles = {"Course Admin", "Academic Officer"};
        Object choice = JOptionPane.showInputDialog(
                this,
                "Select role:",
                "Role",
                JOptionPane.PLAIN_MESSAGE,
                null, roles, roles[0]
        );
        if (choice == null) return null;
        return choice.equals("Course Admin") ? Role.COURSE_ADMIN : Role.ACADEMIC_OFFICER;
    }
}
