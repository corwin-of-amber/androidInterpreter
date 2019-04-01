# AInterpreter
Run Android code on-the-go after the apk already installed!

---

## Table of Contents

- [Installation and Setup](#installation-and-setup)
- [Getting Started](#getting-started)
- [Possible Features](#possible-features)
- [Contributers](#Contributers)

---

## Installation and Setup
### Cloning
- 👯 Clone this repo to your local machine using `https://github.com/anny234/probe_design.git`
- **HACK AWAY!** 🔨🔨🔨

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

You can run any Android code that matches the packages restriction, in one of these different forms:
```java
// Don't forget ';' it is mandatory in the language !
a = 5; // a will be int
int a = 7; // a is still an int
a = new Integer(5); // a is now Integer
a = "hello world"; // a is now String
my_button = this.findViewById(R.id.button);
```

* If you want to get Autocomplete suggestions (in the lower side of the page) click the 'TAB' key to get suggestions of possible code choices. If you see suggestion you like, click on it and it will be filled to the terminal.

---
## Possible Features
1. *Extending the language* - Right now, users still can't define their own classes or functions. Adding any other language features will give the use more power to test functions on the go.
2. *Syntax Highlighting* - Why not coloring different keywords by their meaning to the language as we know from other IDEs.
3. *Markdown Errors(in line)* - Whenever error occures due to illegal character or bad name, markdown to the user the word in which the error occured for better finding and correcting.
4. *Implementing History* - In many terminal you can press the 'UP' button to see previous commands.

---
## Contributers

| Ofir Alexi | Anny Firer |
|:---:| :---:|
| [![Ofir Alexi](https://avatars3.githubusercontent.com/u/37834992?v=4&s=200)](https://avatars3.githubusercontent.com/u/37834992?v=4&s=200) | [![Anny Firer](https://avatars2.githubusercontent.com/u/33667684?v=4&s=200)](https://avatars2.githubusercontent.com/u/33667684?v=4&s=200)  |
| <a href="http://github.com/ofiralexi" target="_blank">`github.com/ofiralexi`</a> | <a href="http://github.com/anny234" target="_blank">`github.com/anny234`</a> |

