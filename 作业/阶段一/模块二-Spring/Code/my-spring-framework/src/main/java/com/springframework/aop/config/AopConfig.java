package com.springframework.aop.config;

public class AopConfig {
    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private Object aspectObject;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
    private String aspectAround;

    public String getPointCut() {
        return pointCut;
    }

    public void setPointCut(String pointCut) {
        this.pointCut = pointCut;
    }

    public String getAspectBefore() {
        return aspectBefore;
    }

    public void setAspectBefore(String aspectBefore) {
        this.aspectBefore = aspectBefore;
    }

    public String getAspectAfter() {
        return aspectAfter;
    }

    public void setAspectAfter(String aspectAfter) {
        this.aspectAfter = aspectAfter;
    }

    public String getAspectClass() {
        return aspectClass;
    }

    public Object getAspectObject() {
        return aspectObject;
    }

    public void setAspectObject(Object aspectObject) {
        this.aspectObject = aspectObject;
    }

    public void setAspectClass(String aspectClass) {
        this.aspectClass = aspectClass;
    }

    public String getAspectAfterThrow() {
        return aspectAfterThrow;
    }

    public void setAspectAfterThrow(String aspectAfterThrow) {
        this.aspectAfterThrow = aspectAfterThrow;
    }

    public String getAspectAfterThrowingName() {
        return aspectAfterThrowingName;
    }

    public void setAspectAfterThrowingName(String aspectAfterThrowingName) {
        this.aspectAfterThrowingName = aspectAfterThrowingName;
    }

    public String getAspectAround() {
        return aspectAround;
    }

    public void setAspectAround(String aspectAround) {
        this.aspectAround = aspectAround;
    }
}
