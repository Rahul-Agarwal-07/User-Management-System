package com.cleanarch.domain.model;

import com.cleanarch.domain.exception.InvalidUserDataException;

import java.time.Instant;
import java.util.UUID;

import static org.apache.logging.log4j.util.Strings.isBlank;

public class User {

    private final UUID id;
    private String name;
    private String email;
    private String phone;
    private String passHash;
    private UserStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private User(
            UUID id,
            String name,
            String email,
            String phone,
            String passHash,
            UserStatus status,
            Instant createdAt,
            Instant updatedAt
    ){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.passHash = passHash;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User create(
            String name,
            String email,
            String passHash,
            String phone
    ){
        if(isBlank(name)) throw new InvalidUserDataException("Name cannot be blank");
        if(isBlank(email)) throw new InvalidUserDataException("Email cannot be blank");
        if(isBlank(passHash)) throw new InvalidUserDataException("Password cannot be blank");

        return new User(
                UUID.randomUUID(),
                name.trim(),
                email.trim().toLowerCase(),
                phone,
                passHash,
                UserStatus.UNVERIFIED,
                Instant.now(),
                null
        );
    }

    public static User restore(
            UUID id,
            String name,
            String email,
            String phone,
            String passHash,
            UserStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new User(id, name, email, phone, passHash, status, createdAt, updatedAt);
    }

    public void activate()
    {
        if(this.status == UserStatus.DELETED)
            throw new InvalidUserDataException("Deleted User cannot be activated");

        this.status = UserStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate()
    {
        if(this.status == UserStatus.DELETED)
            throw new InvalidUserDataException("Deleted User cannot be deactivated");

        this.status = UserStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public void markDeleted()
    {
        this.status = UserStatus.DELETED;
        this.updatedAt = Instant.now();
    }

    public void changePasswordHash(String newHash)
    {
        if(isBlank(newHash)) throw new InvalidUserDataException("Password Hash cannot be empty");

        this.passHash = newHash;
        this.updatedAt = Instant.now();
    }

    public void changeEmail(String newEmail)
    {
        if(isBlank(newEmail)) throw new InvalidUserDataException("Email cannot be empty");

        if(this.status == UserStatus.DELETED)
            throw new InvalidUserDataException("Deleted User cannot be change email");

        this.email = newEmail.trim().toLowerCase();
        this.updatedAt = Instant.now();
    }

    public static boolean isBlank(String s)
    {
        return s == null || s.trim().isEmpty();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passHash; }
    public String getPhone() { return phone; }
    public UserStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

}
