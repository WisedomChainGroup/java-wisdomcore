package org.wisdom.command;

import java.util.List;

public class Dllparameter {

    private String jarname;

    private String classpackage;

    private String methodname;

    private List<Integer> classtype;
    private List<Object> objectList;

    public Dllparameter() {
    }

    public String getJarname() {
        return jarname;
    }

    public void setJarname(String jarname) {
        this.jarname = jarname;
    }

    public String getClasspackage() {
        return classpackage;
    }

    public void setClasspackage(String classpackage) {
        this.classpackage = classpackage;
    }

    public String getMethodname() {
        return methodname;
    }

    public void setMethodname(String methodname) {
        this.methodname = methodname;
    }

    public List<Integer> getClasstype() {
        return classtype;
    }

    public void setClasstype(List<Integer> classtype) {
        this.classtype = classtype;
    }

    public List<Object> getObjectList() {
        return objectList;
    }

    public void setObjectList(List<Object> objectList) {
        this.objectList = objectList;
    }
}
