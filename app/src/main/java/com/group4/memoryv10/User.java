package com.group4.memoryv10;

import com.firebase.ui.auth.data.model.PhoneNumber;

public class User {
    private String id;
    private String name;
    private String surname;
    private int age;
    private String address;
    private String phone;
    private String disease;
    private String diseaseStage;
    private int mmseScore;
    private String caretaker;
    private String caretakerPhone;
    private int caretakerPin;

    public User(String id, String name, String surname, int age, String address, String phone, String disease, String diseaseStage, int mmseScore, String caretaker, String caretakerPhone, int caretakerPin) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.address = address;
        this.phone = phone;
        this.disease = disease;
        this.diseaseStage = diseaseStage;
        this.mmseScore = mmseScore;
        this.caretaker = caretaker;
        this.caretakerPhone = caretakerPhone;
        this.caretakerPin = caretakerPin;
    }


    public String toString(){
        return this.id + "." + name + "- "+ surname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getDiseaseStage() {
        return diseaseStage;
    }

    public void setDiseaseStage(String diseaseStage) {
        this.diseaseStage = diseaseStage;
    }

    public int getMmseScore() {
        return mmseScore;
    }

    public void setMmseScore(int mmseScore) {
        this.mmseScore = mmseScore;
    }

    public String getCaretaker() {
        return caretaker;
    }

    public void setCaretaker(String caretaker) {
        this.caretaker = caretaker;
    }

    public String getCaretakerPhone() {
        return caretakerPhone;
    }

    public void setCaretakerPhone(String caretakerPhone) {
        this.caretakerPhone = caretakerPhone;
    }

    public int getCaretakerPin() {
        return caretakerPin;
    }

    public void setCaretakerPin(int caretakerPin) {
        this.caretakerPin = caretakerPin;
    }
}
