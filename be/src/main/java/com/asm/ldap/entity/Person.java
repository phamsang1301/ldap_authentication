package com.asm.ldap.entity;

import lombok.Data;

@Data
public class Person {
    private String uid;
    private String userPassword;
    private String cn;
    private String givenName;
    private String sn;
    private String gender;
    private String mail;
}
