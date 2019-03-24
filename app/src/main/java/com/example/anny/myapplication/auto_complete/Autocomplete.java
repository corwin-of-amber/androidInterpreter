package com.example.anny.myapplication.auto_complete;

import android.util.Log;
import com.example.anny.myapplication.JavaInterpreter;
import com.example.anny.myapplication.ParseException;
import com.example.anny.myapplication.Token;

import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Autocomplete {
    List<TypeData> types= new ArrayList<>();
    static boolean bad_code = false;

    protected Class<?> isLiteral(String s){
        if(s.matches("(true)|(false)"))
            return types.get(0).expression.push(Boolean.class);
        if(s.matches("\\d+(e\\d+)?"))
            return types.get(0).expression.push(int.class);
        if(s.matches("\\d*\\.\\d+(e\\d+)?"))
            return types.get(0).expression.push(double.class);
        if(s.matches("\"[^\"]*\""))
            return types.get(0).expression.push(String.class);
        return null;
    }


    public Autocomplete(){
        types.add(new TypeData());
    }
    // will be called when there is a '.' in line:
    public Class<?> add_type(String s){
        try {
            if (types.get(0).isEmpty()) { // first appearance of dot
                Class<?> is_literal = isLiteral(s);
                if (is_literal != null){
                    return is_literal;
                }

                // already declared but never ran
                for (TypeData td : types) {
                    if (td.name.equals(s)) {
                        return types.get(0).expression.push(td.getType());
                    }
                }

                // in variables
                if (JavaInterpreter.varibals.containsKey(s))
                {
                    return types.get(0).expression.push(JavaInterpreter.varibals.get(s).clazz);
                }
                // class name
                try {
                    Token t = new Token(0, s);
                    t.beginColumn = 5;
                    return types.get(0).expression.push(JavaInterpreter.checkClass(t));
                } catch (ParseException e) {
                    bad_code = true;
                }
            } else {
                Class<?> clazz = types.get(0).getType();
                for (Field field : clazz.getFields()) {
                    if (field.getName().equals(s)) {
                        return types.get(0).expression.push(field.getType());
                    }
                }
            }
        }
        catch(Exception e){
            throw e;
        }
        return null;
    }

    // function handlers - will be called whenever there is ')' in line
    // assertion - the stacks contains params
    public void func_handler(int arg_num, String function_name) {
        assert (!types.get(arg_num).isEmpty());

        Class<?> function_class = types.get(arg_num).getType();
        List<Class<?>> arg_types = new ArrayList<>();

        for (int i = 0; i < arg_num; i++) {
            arg_types.add(0, types.get(0).getType());
            types.get(arg_num - i ).arguments.add(0, types.get(0));
            types.remove(0);
        }

        try {
            Method m = get_method(function_class,function_name, arg_types.toArray(new Class[arg_types.size()]));
            types.get(0).expression.push(m.getReturnType());
        } catch (NoSuchMethodException e) {
            bad_code = true;
        }

    }


    private boolean check_params(Class<?>[] expected, Class<?>[] actual){
        if (expected.length != actual.length){
            return false;
        }

        for (int i = 0; i < expected.length; i++){
            if (!expected[i].isAssignableFrom(actual[i])){
                return false;
            }
        }
        return true;
    }

    private Method get_method(Class<?> clazz, String func_name, Class<?>[] args) throws NoSuchMethodException{
        if (clazz == null){
            return null;
        }
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(func_name) && check_params(method.getParameterTypes(), args)){
                return method;
            }
        }
        throw new NoSuchMethodException();
    }



    /* will be called when there is a:
    * ';'
    * '(' - only open new stack
    * ','
    in line: */
    public void new_command(String s, String var_name) {
        Log.d("fetch","in new command with s "+s + " and var name " + var_name);
        if (s != null) // '('
        {
            add_type(s);
            if(var_name != null)
                types.get(0).name = var_name;
        }
        types.add(0, new TypeData());
    }

    public void clear(){
        types.clear();
        types.add(new TypeData());
    }



    private void printStack(){
        Log.d("fetch", "====================PRINTING STACK==============================");
        Log.d("fetch", "====== the stack is : (" + String.valueOf(types.size()) + ") =======");
        for(int i = 0; i< types.size(); i ++){
            Log.d("fetch", "i = " + String.valueOf(i)+ ", size of stack : " + String.valueOf(types.get(i).expression.size()));
            Log.d("fetch", "type is : ");
            try {
                Log.d("fetch", types.get(i).getType().getSimpleName());
            }
            catch(Exception e){
                Log.d("fetch", " null ");
            }
        }
        Log.d("fetch","====================================");
    }

    public List<String> DoAutoComplete(String s){
        Log.d("fetch", "IN AUTO COMPLETE trying to complete -> "+s);

        List<String> result = new ArrayList<>();
        // check if already has previous type
        Class<?> relevant_class = null;
        assert(types.size() >= 1);
        if(types.get(0).expression.size() > 0){
            relevant_class = types.get(0).getType();
        }
//        try {
//            Log.d("fetch", "size of types "+types.size());
//            Log.d("fetch","size of stack : " + String.valueOf(types.get(0).expression.size()));
//            relevant_class = types.get(0).getType();
//            if(relevant_class != null){
//                Log.d("fetch", "there is something in stack" + relevant_class.getSimpleName());
//            }
//        }
//        catch(Exception e){ Log.d("fetch", e.getMessage());}
        if(relevant_class == null){
            Log.d("fetch","in1");
            // already declared but never ran
            for (TypeData td : types) {
                if (s == null || td.name.startsWith(s))
                    result.add(td.name + "~" + td.getType());
            }
            // already declared and ran
            for(String var : JavaInterpreter.varibals.keySet()){
                if(s == null || var.startsWith(s)){
                    result.add(var + "~" + JavaInterpreter.varibals.get(var).clazz);
                }
            }

            // check for existing classes in packages
            for(String package_name: JavaInterpreter.packages){
                package_name = package_name.substring(0, package_name.length() - 1);
                Log.d("fetch","iterating over package "+package_name);
                Reflections reflections = new Reflections(package_name);
                Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class);
                Log.d("fetch","============ printing classes ("+String.valueOf(allClasses.size())+") ================");
                for(Class<?> clazz : allClasses){
                    Log.d("fetch", clazz.getSimpleName());
                    if(s == null || clazz.getSimpleName().startsWith(s)){
                        result.add(clazz.getSimpleName() + "~" + package_name);
                    }
                }
            }

        }
        else {
            Field[] all_fields = relevant_class.getFields();
            Method[] all_methods = relevant_class.getMethods();
            for (Field f : all_fields) {
                if (s == null || f.getName().startsWith(s))
                    result.add(f.getName() + "~" + f.getType());
            }
            for (Method m : all_methods) {
                if (s == null || m.getName().startsWith(s)) {
                    String method = m.getName() + "(";
                    for (Class<?> type : m.getParameterTypes()) {
                        method += type.getSimpleName() + ",";
                    }
                    method = method.substring(0, method.length() - 1);
                    method += ") -> " + m.getReturnType();
                    result.add(method);
                }
            }
        }
        return result;
    }

    private void reopen_func(){
        Log.d("fetch", "before removing length of first stack "+ String.valueOf(types.get(0).expression.size()));
        Log.d("fetch","removing "+types.get(0).expression.peek());
        types.get(0).expression.pop(); // return value is unknown
        for(TypeData td : types.get(0).arguments){
            Log.d("fetch","adding to stack again " + td.getType().getSimpleName());
            types.add(0, td);
        }
        types.get(0).arguments.clear();
    }

    public void delete_char(char removed_character){
        if( removed_character == ')' )
        {
            reopen_func();
            return;
        }
        if( removed_character != '.' )
            types.remove(0);

        if( removed_character != '(' )
            types.get(0).expression.pop(); // expression value is unknown

    }

}
