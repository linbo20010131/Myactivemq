package com.yr.test;

import java.io.Serializable;

/**
 * @author Administrator
 */
public class Student implements Serializable {
    private int id;
    private String name;
    private String addr;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }


    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", addr='" + addr + '\'' +
                '}';
    }

    public Student(int id, String name, String addr) {
        this.id = id;
        this.name = name;
        this.addr = addr;

    }

    public Student() {
    }
}
