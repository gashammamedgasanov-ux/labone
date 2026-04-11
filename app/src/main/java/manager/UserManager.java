package manager;

import domain.entity.User;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private final Map<String, User> users = new HashMap<>();
    private User currentUser;
    private long nextId = 1;

    public boolean register(String login, String password) {
        if (users.containsKey(login)) return false;
        String hash = hashPassword(password);
        User user = new User(login, hash);
        user.setId(nextId++);
        users.put(login, user);
        return true;
    }

    public boolean login(String login, String password) {
        User user = users.get(login);
        if (user == null) return false;
        if (user.getPasswordHash().equals(hashPassword(password))) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() { currentUser = null; }
    public User getCurrentUser() { return currentUser; }
    public boolean isAuthenticated() { return currentUser != null; }
    public Map<String, User> getAllUsers() { return users; }

    public void setAll(Map<String, User> usersMap) {
        users.clear();
        users.putAll(usersMap);
        long maxId = 0;
        for (User u : usersMap.values()) {
            if (u.getId() > maxId) maxId = u.getId();
        }
        nextId = maxId + 1;
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка хеширования");
        }
    }
}
