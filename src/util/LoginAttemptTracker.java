package util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Login attempt tracker for brute-force protection
 * Tracks failed login attempts per username
 */
public class LoginAttemptTracker {
    
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 30 * 60 * 1000; // 30 minutes
    
    private static final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();
    
    /**
     * Record of login attempts for a username
     */
    private static class LoginAttempt {
        private final AtomicInteger failedCount = new AtomicInteger(0);
        private long lastFailedTime = 0;
        private long lockoutUntil = 0;
        
        public int getFailedCount() {
            return failedCount.get();
        }
        
        public void incrementFailed() {
            failedCount.incrementAndGet();
            lastFailedTime = System.currentTimeMillis();
        }
        
        public void reset() {
            failedCount.set(0);
            lastFailedTime = 0;
            lockoutUntil = 0;
        }
        
        public boolean isLockedOut() {
            if (failedCount.get() >= MAX_ATTEMPTS) {
                if (System.currentTimeMillis() - lastFailedTime > LOCKOUT_DURATION_MS) {
                    // Lockout expired, reset
                    reset();
                    return false;
                }
                return true;
            }
            return false;
        }
        
        public long getLockoutRemaining() {
            if (isLockedOut()) {
                long remaining = lockoutUntil - System.currentTimeMillis();
                return Math.max(0, remaining);
            }
            return 0;
        }
        
        public void setLockout() {
            lockoutUntil = lastFailedTime + LOCKOUT_DURATION_MS;
        }
    }
    
    /**
     * Record a failed login attempt
     * @param username The username that failed to login
     * @return true if account is now locked, false otherwise
     */
    public static boolean recordFailedAttempt(String username) {
        LoginAttempt attempt = attempts.computeIfAbsent(username, k -> new LoginAttempt());
        attempt.incrementFailed();
        
        if (attempt.getFailedCount() >= MAX_ATTEMPTS) {
            attempt.setLockout();
            return true;
        }
        return false;
    }
    
    /**
     * Check if an account is locked out
     * @param username The username to check
     * @return true if locked out, false otherwise
     */
    public static boolean isLockedOut(String username) {
        LoginAttempt attempt = attempts.get(username);
        return attempt != null && attempt.isLockedOut();
    }
    
    /**
     * Get remaining lockout time in seconds
     * @param username The username to check
     * @return Remaining lockout time in seconds, 0 if not locked
     */
    public static int getRemainingLockoutSeconds(String username) {
        LoginAttempt attempt = attempts.get(username);
        if (attempt != null && attempt.isLockedOut()) {
            return (int) ((attempt.getLockoutRemaining() + 999) / 1000);
        }
        return 0;
    }
    
    /**
     * Get number of remaining attempts before lockout
     * @param username The username to check
     * @return Number of remaining attempts
     */
    public static int getRemainingAttempts(String username) {
        LoginAttempt attempt = attempts.get(username);
        if (attempt == null) {
            return MAX_ATTEMPTS;
        }
        return Math.max(0, MAX_ATTEMPTS - attempt.getFailedCount());
    }
    
    /**
     * Reset failed login attempts after successful login
     * @param username The username that successfully logged in
     */
    public static void resetAttempts(String username) {
        LoginAttempt attempt = attempts.get(username);
        if (attempt != null) {
            attempt.reset();
        }
    }
    
    /**
     * Clear all tracking data (admin only)
     */
    public static void clearAll() {
        attempts.clear();
    }
}
