/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UserManagement;

import java.io.Serializable;

public class User implements Serializable {
    
    private String username;
    private String password;   // plain text for assignment
    private Role role;
    private boolean active = true;  // user is active by default

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // ===== GETTERS =====
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    // ===== SETTERS =====
    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // ===== HOW USER APPEARS IN LISTS =====
    @Override
    public String toString() {
        return active ? username : username + " (INACTIVE)";
    }
}

