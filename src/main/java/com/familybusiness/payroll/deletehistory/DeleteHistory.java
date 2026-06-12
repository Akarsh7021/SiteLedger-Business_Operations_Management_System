package com.familybusiness.payroll.deletehistory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "delete_history")
public class DeleteHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String itemType;

    @Column(nullable = false, length = 255)
    private String itemName;

    @Column(length = 1000)
    private String details;

    @Column(nullable = false)
    private LocalDateTime deletedAt;

    public Long getId() {
        return id;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    @PrePersist
    private void applyDefaults() {
        if (deletedAt == null) {
            deletedAt = LocalDateTime.now();
        }
    }
}
