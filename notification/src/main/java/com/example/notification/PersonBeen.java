package com.example.notification;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhangheng1 on 2018/10/9.
 */

public class PersonBeen implements Parcelable {
    private String name;
    private String sex;
    private int age;

    @Override
    public String toString() {
        return "PersonBeen{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public PersonBeen() {

    }

    public PersonBeen(String name, String sex, int age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);                    //对应着 String name;
        dest.writeString(sex);                    //对应着 String name;
        dest.writeInt(age);                        //对应着 Int age;

    }
    public PersonBeen(Parcel source) {
        name = source.readString();
        sex = source.readString();
        age = source.readInt();
    }


    public static final Creator<PersonBeen> CREATOR = new Creator<PersonBeen>() {

        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public PersonBeen[] newArray(int size) {
            return new PersonBeen[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public PersonBeen createFromParcel(Parcel source) {
            return new PersonBeen(source);
        }
    };


}
