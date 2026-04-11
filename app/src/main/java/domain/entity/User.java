package domain.entity;

import java.time.Instant;

public class User extends BaseEntity {
    private String login;
    private String passwordHash;

    public User() {}

    public User(String login, String passwordHash) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.setCreatedAt(Instant.now());
        this.setUpdatedAt(Instant.now());
    }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}