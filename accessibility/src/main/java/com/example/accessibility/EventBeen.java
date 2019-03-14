package com.example.accessibility;

/**
 * Created by zhangheng1 on 2018/11/7.
 */

public class EventBeen {

    private int event;
    private String id;
    private String packagename;
    private String classname;
    private String text;
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public EventBeen(int event, String id, String packagename, String classname, String text) {
        this.event = event;
        this.id = id;
        this.packagename = packagename;
        this.classname = classname;
        this.text = text;
    }

    public EventBeen(int event, String id, String packagename, String classname, String text, int x, int y) {
        this.event = event;
        this.id = id;
        this.packagename = packagename;
        this.classname = classname;
        this.text = text;
        this.x = x;
        this.y = y;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    @Override
    public String toString() {
        return "EventBeen{" +
                "event=" + event +
                ", id='" + id + '\'' +
                ", packagename='" + packagename + '\'' +
                ", classname='" + classname + '\'' +
                ", text='" + text + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
