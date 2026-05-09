/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Login;

/**
 *
 * @author 2ndUF
 */
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class LoginLogger {
    private String logFile;

    public LoginLogger(String logFile) {
        this.logFile = logFile;
    }

    public void log(String username, String action) {
        long ts = System.currentTimeMillis();
        String binaryTs = Long.toBinaryString(ts);   // timestamp in binary

        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.write(username + "," + action + "," + ts + "," + binaryTs + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<String> getRecentLogs(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}