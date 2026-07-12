package za.ac.cput.domain;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public abstract class User {
    @Id
    protected String userId;
    protected String name;
    protected String email;
    protected String passwordHash;
    protected String phoneNumber;
    protected RoleEnum role;

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    // Backward-compatible alias used by current tests.
    public String getPassword() {
        return passwordHash;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public RoleEnum getRole() {
        return role;
    }
}
