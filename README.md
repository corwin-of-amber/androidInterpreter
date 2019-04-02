# AInterpreter
Run Android code on-the-go after the apk already installed!

---

## Table of Contents

- [Installation and Setup](#installation-and-setup)
- [Getting Started](#getting-started)
- [Possible Features](#possible-features)

---

## Installation and Setup
### Cloning
- 👯 Clone this repo to your local machine using `https://github.com/anny234/probe_design.git`
- **HACK AWAY!** 🔨🔨🔨

*In case of emergency use* [*JavaCC*](https://javacc.org/) :rotating_light:

### Setup
- Open the project in [Android Studio](https://developer.android.com/studio). 

1. If you are using Android Studio emulator device, run: 
```
adb forward tcp:8080 tcp:8080
```
2. Point your online browser to `YourDevice_IP_address:8080` to start coding, 
**OR** simply type the code inside the textArea in the app and Press 'Run' !

* If you are using your own Android device, you can skip the first step.
* Please make sure both the computer and the device are connected to the same network.

---
## Getting Started

**Please note that only code that uses the packages defined in the Packages array in JavaInterpreter.java will be able to autocomplete and run (so add the relevant packages to you there).**

#### Object Decleration and Instantiation
As you can see in the code below, you can override variables in the program any time. Meaning, the type of a variable can be changed during your coding.
```java
int a = 5;        // a is an int
a = 7;            // a is an int (inferred)
String j;         // j is a String
j = 7.7;          // j is a double
```
#### Assigments
```java
j = "hello world"; // j is a String
a = j;             // a is a String "hello world"
```
#### Static Method Invocation
```java
b = Toast.makeText(this,"hello world", Toast.LENGTH_LONG);
b.show();
```
**OR**
```java
Toast.makeText(this,"hello world", Toast.LENGTH_LONG).show();
```
#### Accessing static fields
```java
int length_long = Toast.LENGTH_LONG;
```
#### Arrays
The language now supports declaring arrays:
```java
a = new Integer[3]; // a is an Integer array of length 3
b = new int[3];     // fail - no arrays on primitive language types
```
#### Autocomplete
In the bottom of the page you will see the suggestions tab. If you want to get autocomplete suggetstions simply click the 'TAB' key to get suggestions of possible code choices. If you already started typing the autocomplete will return relevant results matching to that prefix. If you see suggestion you like, click on it and it will be written to the terminal.

---
## Possible Features
1. *Extending the language* - Right now, users still can't define their own classes or functions, control flow, or import packages. Adding any other language features will give the use more power to test functions on the go.
2. *Syntax Highlighting* - Why not coloring different keywords by their meaning to the language as we know from other IDEs.
3. *Markdown Errors(in line)* - Whenever error occures due to illegal character or bad name, markdown to the user the word in which the error occured for better finding and correcting.
4. *Implementing History* - In many terminal you can press the 'UP' button to see previous commands.

---
## Contributers

| Ofir Alexi | Anny Firer |
|:---:| :---:|
| [![Ofir Alexi](https://avatars3.githubusercontent.com/u/37834992?v=4&s=200)](https://avatars3.githubusercontent.com/u/37834992?v=4&s=200) | [![Anny Firer](https://avatars2.githubusercontent.com/u/33667684?v=4&s=200)](https://avatars2.githubusercontent.com/u/33667684?v=4&s=200)  |
| <a href="http://github.com/ofiralexi" target="_blank">`github.com/ofiralexi`</a> | <a href="http://github.com/anny234" target="_blank">`github.com/anny234`</a> |

