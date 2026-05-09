/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UserManagement;

/**
 *
 * @author 2ndUF
 */
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private List<User> users = new ArrayList<>();
    private String filePath;

    public UserManager(String filePath) {
        this.filePath = filePath;
    }

    public synchronized void load() {
        File f = new File(filePath);
        if (!f.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                users = (List<User>) obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public synchronized User findByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) return u;
        }
        return null;
    }

    public synchronized boolean addUser(String username, String password, Role role) {
        if (findByUsername(username) != null) return false;
        users.add(new User(username, password, role));
        return true;
    }

    public synchronized boolean changePassword(String username, String newPass) {
        User u = findByUsername(username);
        if (u == null) return false;
        u.setPassword(newPass);
        return true;
    }

    public synchronized boolean changeRole(String username, Role newRole) {
        User u = findByUsername(username);
        if (u == null) return false;
        u.setRole(newRole);
        return true;
    }
    public synchronized void deactivateUser(String username) {
    User u = findByUsername(username);
    if (u != null) {
        u.setActive(false);      // or whatever flag you use
    }
}
   
    public synchronized boolean deleteUser(String username) {
        User u = findByUsername(username);
        if (u == null) return false;
        
        users.remove(u);
        
        return true;
    
    }
    public synchronized User authenticate(String username, String password) {
        User u = findByUsername(username);
        if (u != null && u.isActive() && u.getPassword().equals(password)) {
        return u;
    }
    return null;
}
}
