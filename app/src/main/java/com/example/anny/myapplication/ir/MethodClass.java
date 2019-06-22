package com.example.anny.myapplication.ir;

import java.util.List;

public class MethodClass {
    public String methodName;
    public List<Value> params;

    public MethodClass(String methodName, List<Value> params){
        this.methodName = methodName;
        this.params = params;
    }
}
