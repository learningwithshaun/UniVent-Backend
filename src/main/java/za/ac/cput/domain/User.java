package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public abstract class User {
    @Id
    protected String userId;
    protected String name;
    protected String email;
    @com.fasterxml.jackson.annotation.JsonIgnore
    protected String passwordHash;
    protected String phoneNumber;
    @Enumerated(EnumType.STRING)
    protected RoleEnum role;
    @Enumerated(EnumType.STRING)
    protected AccountStatus accountStatus = AccountStatus.ACTIVE;
    protected LocalDateTime createdAt = LocalDateTime.now();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEnabled() {
        return accountStatus == AccountStatus.ACTIVE;
    }

    public void setEnabled(boolean enabled) {
        accountStatus = enabled ? AccountStatus.ACTIVE : AccountStatus.DISABLED;
    }
}
