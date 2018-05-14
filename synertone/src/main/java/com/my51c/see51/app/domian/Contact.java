package com.my51c.see51.app.domian;

public class Contact {
    Integer id;
    String name;
    String pingyin;
    String phone;
    String address;

    public Contact() {

    }

    public Contact(String id) {
        this.id = Integer.parseInt(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPingyin() {
        return pingyin;
    }

    public void setPingyin(String pingyin) {
        this.pingyin = pingyin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Contact [id=" + id + ", name=" + name + ", pingyin=" + pingyin
                + ", phone=" + phone + ", address=" + address + "]";
    }
}
