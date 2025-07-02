package com.bankai.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double balance;
    private boolean isActive;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }    

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void deposit(double value){
        this.balance += value;
    }

    public void draw(double value){
        this.balance -= value;
    }

    public boolean isActive(){
        return isActive;
    }

    public void activateDeactivate(){
        this.isActive  = !this.isActive;
    }

}
