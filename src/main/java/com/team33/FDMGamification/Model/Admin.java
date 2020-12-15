package com.team33.FDMGamification.Model;


import javax.persistence.*;

@Entity(name="Admin")
@Table(name="Admin")
public class Admin{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Integer id;

    private String username;

    private String firstname;

    private String lastname;

    private String password;

    @Column(name = "phone_number")
    private String phoneNo;

    public Admin(String username, String firstname, String lastname, String password, String phoneNo){
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneNo = phoneNo;
        this.username = username;
        this.password = password;
    }

    public Admin(){
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", password='" + password + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                '}';
    }
}
