package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Audit logger for tracking user activities and security events
 * Provides audit trail for accountability (SC-04)
 */
public class AuditLogger {
    
    private static final String LOG_FILE = "bin/logs/audit.log";
    private static final ConcurrentLinkedQueue<String> logBuffer = new ConcurrentLinkedQueue<>();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Event types for audit logging
     */
    public enum EventType {
        LOGIN_SUCCESS("LOGIN_SUCCESS"),
        LOGIN_FAILED("LOGIN_FAILED"),
        LOGOUT("LOGOUT"),
        USER_CREATE("USER_CREATE"),
        USER_UPDATE("USER_UPDATE"),
        USER_DELETE("USER_DELETE"),
        PASSWORD_CHANGE("PASSWORD_CHANGE"),
        ACCESS_DENIED("ACCESS_DENIED"),
        TRANSACTION_CREATE("TRANSACTION_CREATE"),
        TRANSACTION_DELETE("TRANSACTION_DELETE"),
        SYSTEM_START("SYSTEM_START"),
        SYSTEM_STOP("SYSTEM_STOP");
        
        private final String code;
        
        EventType(String code) {
            this.code = code;
        }
        
        public String getCode() {
            return code;
        }
    }
    
    static {
        // Ensure log directory exists
        new java.io.File("bin/logs").mkdirs();
    }
    
    /**
     * Log an audit event
     * @param eventType The type of event
     * @param username The username (can be null for system events)
     * @param details Additional details about the event
     */
    public static void log(EventType eventType, String username, String details) {
        StringBuilder logEntry = new StringBuilder();
        logEntry.append("[").append(dateFormat.format(new Date())).append("]");
        logEntry.append(" [").append(eventType.getCode()).append("]");
        if (username != null && !username.isEmpty()) {
            logEntry.append(" [user:").append(username).append("]");
        }
        if (details != null && !details.isEmpty()) {
            logEntry.append(" - ").append(details);
        }
        
        String entry = logEntry.toString();
        logBuffer.add(entry);
        
        // Write to file immediately for important events
        if (eventType == EventType.LOGIN_FAILED || 
            eventType == EventType.LOGIN_SUCCESS ||
            eventType == EventType.ACCESS_DENIED ||
            eventType == EventType.USER_DELETE) {
            writeToFile(entry);
        }
    }
    
    /**
     * Log a login success event
     */
    public static void logLoginSuccess(String username) {
        log(EventType.LOGIN_SUCCESS, username, "User logged in successfully");
    }
    
    /**
     * Log a login failure event
     * @param username The username that failed to login
     * @param reason The reason for failure
     */
    public static void logLoginFailed(String username, String reason) {
        log(EventType.LOGIN_FAILED, username, "Login failed: " + reason);
    }
    
    /**
     * Log user creation event
     */
    public static void logUserCreate(String adminUsername, String newUsername) {
        log(EventType.USER_CREATE, adminUsername, "Created user: " + newUsername);
    }
    
    /**
     * Log user update event
     */
    public static void logUserUpdate(String adminUsername, String targetUsername) {
        log(EventType.USER_UPDATE, adminUsername, "Updated user: " + targetUsername);
    }
    
    /**
     * Log user deletion event
     */
    public static void logUserDelete(String adminUsername, String targetUsername) {
        log(EventType.USER_DELETE, adminUsername, "Deleted user: " + targetUsername);
    }
    
    /**
     * Log password change event
     */
    public static void logPasswordChange(String username) {
        log(EventType.PASSWORD_CHANGE, username, "Password changed");
    }
    
    /**
     * Log access denied event
     */
    public static void logAccessDenied(String username, String resource) {
        log(EventType.ACCESS_DENIED, username, "Access denied to: " + resource);
    }
    
    /**
     * Log transaction create event
     */
    public static void logTransactionCreate(String username, int transactionId) {
        log(EventType.TRANSACTION_CREATE, username, "Transaction ID: " + transactionId);
    }
    
    /**
     * Log logout event
     */
    public static void logLogout(String username) {
        log(EventType.LOGOUT, username, "User logged out");
    }
    
    /**
     * Write buffered logs to file
     */
    private static void writeToFile(String entry) {
        String fileName = "bin/logs/audit-" + fileNameFormat.format(new Date()) + ".log";
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            writer.println(entry);
        } catch (IOException e) {
            System.err.println("Failed to write audit log: " + e.getMessage());
        }
    }
    
    /**
     * Flush all buffered logs to file
     */
    public static void flush() {
        while (!logBuffer.isEmpty()) {
            String entry = logBuffer.poll();
            if (entry != null) {
                writeToFile(entry);
            }
        }
    }
    
    /**
     * Get recent audit logs from buffer
     * @param count Number of recent logs to retrieve
     * @return Array of recent log entries
     */
    public static String[] getRecentLogs(int count) {
        String[] logs = new String[Math.min(count, logBuffer.size())];
        int index = 0;
        for (String log : logBuffer) {
            if (index >= count) break;
            logs[index++] = log;
        }
        return logs;
    }
}
