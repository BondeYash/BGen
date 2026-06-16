package com.banking.platform.users;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @Column(name = "email", nullable = false, updatable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "customer_id", updatable = false)
    private UUID customerId;     // nullable link to a Customer (null for staff)

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected User() { /* JPA only */ }

    private User(UUID tenantId, String email, String passwordHash,
                 String fullName, UUID customerId, Set<Role> roles) {
        this.tenantId = tenantId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.customerId = customerId;
        this.roles = roles;
        this.enabled = true;
    }

    public static User create(UUID tenantId, String email, String passwordHash,
                              String fullName, UUID customerId, Set<Role> roles) {
        return new User(tenantId, email, passwordHash, fullName, customerId, roles);
    }

    public UUID getId()           { return id; }
    public UUID getTenantId()     { return tenantId; }
    public String getEmail()      { return email; }
    public String getPasswordHash(){ return passwordHash; }
    public String getFullName()   { return fullName; }
    public UUID getCustomerId()   { return customerId; }
    public boolean isEnabled()    { return enabled; }
    public Set<Role> getRoles()   { return roles; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
