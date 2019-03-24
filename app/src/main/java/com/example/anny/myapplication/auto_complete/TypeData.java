package com.example.anny.myapplication.auto_complete;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TypeData {
    public String name;
    public Stack<Class<?>> expression;
    public List<TypeData> arguments;

    public TypeData() {
        this.name = "";
        this.expression = new Stack<>();
        this.arguments = new ArrayList<>();
    }

    public boolean isEmpty(){
        return expression.isEmpty();
    }

    public Class<?> getType(){
        return expression.peek();
    }
}
