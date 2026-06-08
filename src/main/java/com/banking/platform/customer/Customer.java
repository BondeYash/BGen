package com.banking.platform.customer;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @Column(name = "customer_number", nullable = false, length = 20, updatable = false)
    private String customerNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private CustomerType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CustomerStatus status;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "registration_no", length = 50)
    private String registrationNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", nullable = false, length = 20)
    private KycStatus kycStatus;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Customer() {
        // JPA only
    }

    // private — construction goes through the factory
    private Customer(UUID tenantId, String customerNumber, CustomerType type,
                     String fullName, String email, String phone,
                     LocalDate dateOfBirth, String registrationNo) {
        this.tenantId = tenantId;
        this.customerNumber = customerNumber;
        this.type = type;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.registrationNo = registrationNo;
        this.status = CustomerStatus.PENDING;        // forced initial state
        this.kycStatus = KycStatus.NOT_VERIFIED;     // forced initial state
    }

    // Factory: the ONLY way to make a new customer
    public static Customer create(UUID tenantId, String customerNumber, CustomerType type,
                                  String fullName, String email, String phone,
                                  LocalDate dateOfBirth, String registrationNo) {
        return new Customer(tenantId, customerNumber, type,
                fullName, email, phone, dateOfBirth, registrationNo);
    }

    // ---- lifecycle transitions (the state machine) ----

    public void activate() {
        if (status != CustomerStatus.PENDING && status != CustomerStatus.SUSPENDED) {
            throw new InvalidCustomerStateException(
                    "Cannot activate a customer in status " + status);
        }
        this.status = CustomerStatus.ACTIVE;
    }

    public void suspend() {
        if (status != CustomerStatus.ACTIVE) {
            throw new InvalidCustomerStateException(
                    "Only ACTIVE customers can be suspended (was " + status + ")");
        }
        this.status = CustomerStatus.SUSPENDED;
    }

    public void close() {
        if (status == CustomerStatus.CLOSED) {
            throw new InvalidCustomerStateException("Customer already CLOSED");
        }
        this.status = CustomerStatus.CLOSED;
    }

    public void changeContact (String newEmail , String newPhone) {

        if (newEmail != null) {
            this.email = newEmail;
        }
        if (newPhone != null) {
            this.phone = newPhone;
        }

    }

    // ---- getters (needed to map entity -> response DTO). NO setters. ----

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public String getCustomerNumber() { return customerNumber; }
    public CustomerType getType() { return type; }
    public CustomerStatus getStatus() { return status; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getRegistrationNo() { return registrationNo; }
    public KycStatus getKycStatus() { return kycStatus; }
    public long getVersion() { return version; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
