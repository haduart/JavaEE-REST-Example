package com.haduart.rest.decision;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "Members", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @NotEmpty
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotNull
    @Digits(fraction = 0, integer = 12)
    private Integer amount = 0;

    public User() {
    }

    public User(String email, Integer amount) {
        this.email = email;
        this.amount = amount;
    }

    public void increaseAmount(int amount) {
        this.amount += amount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!amount.equals(user.amount)) return false;
        if (!email.equals(user.email)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = email.hashCode();
        result = 31 * result + amount.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Member{" +
                "email='" + email + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}