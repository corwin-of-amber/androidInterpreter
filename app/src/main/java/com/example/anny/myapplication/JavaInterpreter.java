// =================================
//        IMPORTS
// =================================

package com.example.anny.myapplication;

import java.lang.*;
import java.util.*;
import java.lang.reflect.*;
import java.io.*;
import android.util.Log;
import org.apache.commons.lang3.reflect.*;

import com.example.anny.myapplication.ir.*;
import com.example.anny.myapplication.parser.*;

public class JavaInterpreter {

  // =================================
  //        CLASS VARIABLES
  // =================================
  public static Parser parser;
  public static String mypackage;
  public static List<String> packages;

  // =================================
  //              MAIN
  // =================================
  public static void initialize(Object thisObj, String packagename){
    Value thisVal = new Value(thisObj.getClass(), thisObj);
    mypackage = packagename;
    packages = Arrays.asList(
              mypackage + ".",
              "java.lang.",
              "java.util.",
              "android.widget.",
              "android.util.",
              "android.app.",
              "android.view.",
              "android.content.",
              "android.os."
      );
    InputStream targetStream = new ByteArrayInputStream("".getBytes());
    parser = new Parser(targetStream );
    parser.variables.put("this", thisVal);
  }

  public static void parseFunc(String s) throws ParseException {

    InputStream targetStream = new ByteArrayInputStream(s.getBytes());
    parser.ReInit(targetStream);
    parser.Body();
  }

  // =================================
  //        HELPER FUNCTIONS
  // =================================

  public static Object getNewInstance(final Class<?> clazz, List<Value> constructorParameters) throws ParseException{
    Class<?> [] parameterTypes = new Class[constructorParameters.size()];
    Object [] parameters = new Object[constructorParameters.size()];
    for(int i =  0; i < constructorParameters.size(); i++) {
        parameterTypes[constructorParameters.size() - i - 1] = constructorParameters.get(i).clazz;
        parameters[constructorParameters.size() - i - 1] = constructorParameters.get(i).value;
    }
    try{
        //Constructor constructor = clazz.getConstructor(parameterTypes);
        //return constructor.newInstance(parameters);
        return ConstructorUtils.invokeConstructor(clazz, parameters);

    }
    catch(Exception e){
        throw new ParseException("failed to create instance: " + e.getCause());
    }
  }

  public static Value invokeStaticMethod(final Class<?> clazz, MethodClass m, Object o) throws ParseException{
    List<Value> methodParameters = m.params;
    Class<?> [] parameterTypes = new Class[methodParameters.size()];
    Object [] parameters = new Object[methodParameters.size()];
    for(int i =  0; i < methodParameters.size(); i++) {
        parameterTypes[methodParameters.size() - i - 1] = methodParameters.get(i).clazz;
        parameters[methodParameters.size() - i - 1] = methodParameters.get(i).value;
        System.out.println("debug param : " + parameters[methodParameters.size() - i - 1]);
        System.out.println("debug type : " + parameterTypes[methodParameters.size() - i - 1]);
    }
    try{
      Object res = null;
      if (o == null) {
          res = MethodUtils.invokeStaticMethod(clazz, m.methodName, parameters);
      }
      else {
          res = MethodUtils.invokeMethod(o, m.methodName, parameters);
      }
      if (res == null){
        return null;
      }
      return new Value(res.getClass(), res);
    }
    catch(Exception e){
        if(e == null || e.getMessage() == null){
            return null;
        }
        Log.d("fetch","var "  + o);
        Log.d("fetch","method "  + m.methodName);
        for (Object p : parameters){
          Log.d("fetch","param value: " + p +" "+ p.getClass().getSimpleName());
        }
        Log.d("fetch"," value of color : "+String.valueOf(MainActivity.mContext.getResources().getColor(R.color.colorAccent)));
        Log.d("fetch",  clazz.getSimpleName());
        for(Method meth : clazz.getMethods()){
            if(meth.getName().equals(m.methodName)){
                Log.d("fetch","============expected params:=============");
                for(Class<?> paramType : meth.getParameterTypes()){
                    Log.d("fetch",paramType.getSimpleName());
                }
                throw new ParseException("failed to invoke method: "+m.methodName+" on "+clazz.getSimpleName()+" \u005cn Wrong parameters");
            }
        }
        throw new ParseException("failed to invoke method: " + m.methodName+" on "+clazz.getSimpleName()+" \u005cn No such method found");
    }
  }

  public static Class<?> checkClass(Token className) throws ParseException{
    Log.d("fetch","searching for class name "+className);
    for (String pkg : packages){
      Log.d("fetch", " checking pck "+pkg);
      Class<?> clazz = classExists(pkg + className);
      if (clazz != null ){
        Log.d("fetch"," found class in pckg :"+pkg + clazz.getSimpleName());
        return clazz;
      }
    }
    throw new ParseException("Encountered '" + className + "' at column " + className.beginColumn + ". symbol doesnt exist");

  }

  static Class<?> classExists(String fullClassName) {
      try {
          return Class.forName(fullClassName);
      } catch (ClassNotFoundException e) {
          return null;
      }
  }
}
