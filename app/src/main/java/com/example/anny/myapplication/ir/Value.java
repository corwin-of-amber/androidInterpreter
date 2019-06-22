package com.example.anny.myapplication.ir;

public class Value {
    public Class<?> clazz;
    public Object value;
    public boolean isArray;
    public boolean isClass;

    public Value(Class<?> clazz, Object value){
        this.clazz = clazz;
        this.value = value;
        this.isArray = clazz.isArray();
        this.isClass = value == null;
    }

    public String toString(){
        return "class: " + clazz.toString() + ", value: " + (value != null ? value.toString() : "null");
    }
}

