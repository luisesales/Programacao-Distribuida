package com.bankai.operator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name = "BankAI";

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public Long getId(){
        return id;
    }
}