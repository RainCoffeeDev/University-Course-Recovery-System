/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MainMenu;

/**
 *
 * @author 2ndUF
 */
import UserManagement.Role;
import UserManagement.UserManager;
import Login.LoginLogger;
import Login.LoginFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserManager userManager = new UserManager("users.dat");
            userManager.load();

            // bootstrap some sample users if file is empty
            if (userManager.getAllUsers().isEmpty()) {
                userManager.addUser("admin", "admin123", Role.COURSE_ADMIN);
                userManager.addUser("officer", "off123", Role.ACADEMIC_OFFICER);
                userManager.save();
            }

            LoginLogger logger = new LoginLogger("login_log.txt");
            new LoginFrame(userManager, logger).setVisible(true);
        });
    }
}