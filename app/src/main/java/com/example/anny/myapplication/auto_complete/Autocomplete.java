package com.example.anny.myapplication.auto_complete;

import android.content.pm.PackageManager;
import android.util.Log;
import com.example.anny.myapplication.JavaInterpreter;
import com.example.anny.myapplication.MainActivity;
import com.example.anny.myapplication.ParseException;
import com.example.anny.myapplication.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

public class Autocomplete {
    // ==================================
    //          CLASS VARIABLES
    // ==================================
    private List<TypeData> types= new ArrayList<>();
    public static boolean bad_code = false;
    private Set<String> classes = new HashSet<>();

    // ==================================
    //         PRIVATE FUNCTIONS
    // ==================================
    private Class<?> isLiteral(String s){
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

    private void reopen_func(){
//        Log.d("fetch", "before removing length of first stack "+ String.valueOf(types.get(0).expression.size()));
//        Log.d("fetch","removing "+types.get(0).expression.peek());
        types.get(0).expression.pop(); // return value is unknown
        for(TypeData td : types.get(0).arguments){
//            Log.d("fetch","adding to stack again " + td.getType().getSimpleName());
            types.add(0, td);
        }
        types.get(0).arguments.clear();
    }

    private Set<String> FindAllClasses() throws PackageManager.NameNotFoundException, IOException {
        Set<String> classes = new HashSet<>();
        String filepath = MainActivity.mContext.getPackageManager().getApplicationInfo(MainActivity.mContext.getPackageName(), 0).sourceDir;
        JarFile apkFile = new JarFile(filepath);
        Enumeration<JarEntry> entries = apkFile.entries();

        while(entries.hasMoreElements()) {
            String entry = entries.nextElement().getName();
            if(!entry.endsWith(".dex")) continue;

            ZipEntry zipEntry = apkFile.getEntry(entry);
            BufferedReader reader = new BufferedReader(new InputStreamReader(apkFile.getInputStream(zipEntry)));

            while(reader.ready()) {
                String line  = reader.readLine().trim();
                Matcher m = Pattern.compile("L([A-Za-z0-9]+(/[A-Za-z0-9]+)+(\\$[A-Za-z0-9]+)?)").matcher(line);
                while(m.find()) classes.add(m.group(1).replace("/", ".").replace("$", "."));
            }
        }

        return classes;
    }

    //              CTOR
    public Autocomplete(){
        types.add(new TypeData());
        try {
            classes = FindAllClasses();
            Log.d("fetch", "--- found " + classes.size() + " classes ----");
            for(String clazz : classes)
                if(clazz.contains("android."))
                    Log.d("clazz", clazz);
        }
        catch(Exception e){
            Log.d("fetch", "didnt find classes by package");
        }
    }

    // ==================================
    //           FUNCTIONS
    // ==================================
    // will be called when there is a '.' in line:
    public Class<?> add_type(String s){
        printStack();
        Log.d("fetch","================== adding to stack "+s+"===================");
        try {
            if (types.get(0).isEmpty()) { // first appearance of dot
                Log.d("fetch","should get in here - stack is empty");
                Class<?> is_literal = isLiteral(s);
                if (is_literal != null){
                    Log.d("fetch","is literal - Y ?");
                    return is_literal;
                }

                // already declared but never ran
                for (TypeData td : types) {
                    if (td.name.equals(s)) {
                        Log.d("fetch","already declared and never ran");
                        return types.get(0).expression.push(td.getType());
                    }
                }

                // in variables
                if (JavaInterpreter.varibals.containsKey(s))
                {
                    Log.d("fetch","it is in variables...Y ?");
                    return types.get(0).expression.push(JavaInterpreter.varibals.get(s).clazz);
                }
                // class name
                try {
                    Token t = new Token(0, s);
                    t.beginColumn = 5;
                    return types.get(0).expression.push(JavaInterpreter.checkClass(t));
                } catch (ParseException e) {
                    Log.d("fetch","didnt find class");
                    bad_code = true;
                }
            } else {
                Class<?> clazz = types.get(0).getType();
                for (Field field : clazz.getFields()) { // some field name
                    if (field.getName().equals(s)) {
                        return types.get(0).expression.push(field.getType());
                    }
                }

                for(Class<?> clas : clazz.getDeclaredClasses()){ // some inner class
                    if(clas.getSimpleName().equals(s)){
                        return types.get(0).expression.push(clas);
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
        printStack();
        assert (!types.get(arg_num).isEmpty());
//        Log.d("fetch"," in func_handler "+function_name+" arg_num = "+String.valueOf(arg_num));
        if(arg_num == 0){ // no argument one empty stack
            types.remove(0);
        }
        Class<?> function_class = types.get(arg_num).getType();
//        Log.d("fetch","in "+function_class.getSimpleName());
        List<Class<?>> arg_types = new ArrayList<>();

        for (int i = 0; i < arg_num; i++) {
            arg_types.add(0, types.get(0).getType());
            types.get(arg_num - i ).arguments.add(0, types.get(0));
            types.remove(0);
        }

        try {
            Method m = get_method(function_class,function_name, arg_types.toArray(new Class[arg_types.size()]));
//            Log.d("fetch", "found method "+m.getName());
            types.get(0).expression.push(m.getReturnType());
        } catch (NoSuchMethodException e) {
//            Log.d("fetch", "didnt find method ! :O");
            bad_code = true;
        }

    }

    /* will be called when there is a:
    * ';'
    * '(' - only open new stack
    * ','
    in line: */
    public void new_command(String s, String var_name) {
        printStack();
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
        Log.d("fetch","CLEARNINGGINGNGGINGIGN");
        types.clear();
        types.add(new TypeData());
    }

    public List<String> DoAutoComplete(String s){
        Log.d("fetch", "IN AUTO COMPLETE trying to complete -> "+s);
//        printStack();
        if (s == null){
            s = "";
        }
        s = s.trim();

        List<String> result = new ArrayList<>();
        assert(types.size() >= 1);

        if(types.get(0).expression.size() == 0){ // no known sub-type yet
            Log.d("fetch","in1");
            // already declared but never ran
            for (TypeData td : types) {
                if (( td.expression.size() > 0 && !td.name.equals(""))  && (s.equals("") || td.name.startsWith(s)))
                    result.add(td.name + "~" + td.getType());
            }
            // already declared and ran
            for(String var : JavaInterpreter.varibals.keySet()){
                if(var.startsWith(s)){
                    result.add(var + "~" + JavaInterpreter.varibals.get(var).clazz);
                }
            }
            // some class name if I would know how to check it
            Log.d("fetch", "gonna iteate over "+classes.size()+"classes");
            for(String cname : classes){
                String simple_class_name = cname.substring(cname.lastIndexOf(".") + 1,cname.length());
                String package_name = cname.substring(0,cname.lastIndexOf("."));
                if(simple_class_name.startsWith(s))
                    result.add(simple_class_name + "~" + package_name);
            }
        }
        else {
            Log.d("fetch","in2");
            Class<?> relevant_class = types.get(0).getType();
            Log.d("fetch","relevant class"+relevant_class.getSimpleName());
            Field[] all_fields = relevant_class.getFields();
            Method[] all_methods = relevant_class.getMethods();
//            Log.d("fetch","----- fields -----");
            for (Field f : all_fields) {
//                Log.d("fetch","iterating field "+f.getName());
                if (f.getName().startsWith(s))
                    result.add(f.getName() + "~" + f.getType());
            }
//            Log.d("fetch","----- methods -----");
            for (Method m : all_methods) {
//                Log.d("fetch","iterating method "+m.getName());
                if (m.getName().startsWith(s)) {
                    String method = m.getName() + "(";
                    int num_parms = 0;
                    for (Class<?> type : m.getParameterTypes()) {
                        num_parms ++;
                        method += type.getSimpleName() + ",";
                    }
                    if(num_parms > 0)
                        method = method.substring(0, method.length() - 1);
                    method += ")~" + m.getReturnType().getSimpleName();
                    result.add(method);
                }
            }
//            Log.d("fetch","----- INNER CLASSES -----");
            for(Class<?> clz : relevant_class.getDeclaredClasses()){
                if(clz.getSimpleName().startsWith(s)){
                    String fullClassName = clz.getName();
                    result.add(clz.getSimpleName()+"~"+fullClassName.substring(0,fullClassName.lastIndexOf(".")));
                }
            }
        }

        if(result.size() == 0) //didn't find anything
        {
            result.add("No results found.");
        }
        return result ;
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

    public static boolean isBad_code() {
        return bad_code;
    }
}
