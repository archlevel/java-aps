package com.anjuke.aps.server.spring.service;

public class TestBean {

    private int id;
    private String name;
    private TestInnerBean inner;

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

    public TestInnerBean getInner() {
        return inner;
    }

    public void setInner(TestInnerBean inner) {
        this.inner = inner;
    }

    @Override
    public String toString() {
        return "TestBean [id=" + id + ", name=" + name + ", inner=" + inner
                + "]";
    }



}
