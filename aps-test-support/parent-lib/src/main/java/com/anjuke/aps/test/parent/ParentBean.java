package com.anjuke.aps.test.parent;


public class ParentBean {
    public ParentBean() {
        System.out.println("============parent create");
    }

    private String name="parent";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




}
