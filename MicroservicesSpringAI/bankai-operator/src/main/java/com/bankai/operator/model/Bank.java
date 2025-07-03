package com.bankai.operator.model;

public class Bank {
    private Long id;
    private String name = "BankAI";

    public void setName(String name){
        this.name = name;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public Long getId(){
        return id;
    }
}
