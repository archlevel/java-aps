package com.anjuke.aps;

public class RecursiveTestBean {

    private TestBean testBean;

    public RecursiveTestBean(TestBean testBean) {
        super();
        this.testBean = testBean;
    }

    public TestBean getTestBean() {
        return testBean;
    }

    public void setTestBean(TestBean testBean) {
        this.testBean = testBean;
    }

    @Override
    public String toString() {
        return "RecursiveTestBean [testBean=" + testBean + "]";
    }

}
