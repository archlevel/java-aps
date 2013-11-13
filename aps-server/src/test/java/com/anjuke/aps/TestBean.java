package com.anjuke.aps;

public class TestBean {

    private final String name;

    public TestBean(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "TestBean [name=" + name + "]";
    }

}
