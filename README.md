# reCall
Interpreter for reCall, a scripting language. The interpreter is written in pure Java.

reCall is a dynamically typed language with imperative and functional capabilities. Popular languages such as Java, C++, and Python inspired this project.

## Using the Interpreter
Run the interpreter by downloading the compiled reCall_interpreter.jar and using the following command:
```
java -jar reCall_interpreter.jar /path/to/source/code.re
```
The source code file should end with `.re`, although any extension will work fine.

The following commands can be added to redirect the input and output.

`--output` or `-o`: Redirect stdout to a file. Specify a file path after.

`--input` or `-i`: Change stdin to a file. Specify a file path after.

`--error` or `-e`: Redirect the stderr to a file. Again, specify a file path after.

## reCall Syntax
reCall works on statements and objects. Objects can be defined or changed by statements. Some statement can even redirect the flow of the code. Code can be directly written in a text file and ran using the interpreter.

### Variables and Operators
There are many different types of objects that can be created and manipulated in reCall. Here are the basic types: `string`, `number`, `list`, and `map`. For example, a literal number is just written out that number's value in the code. A literal two would look like `2`. A literal string (a bunch of characters) would look like `"hello world!"`.

A variable acts like a storage area for some sort of object or value. It needs to be defined before it is used. A variable can save any of the types mentioned above. A variable can store a literal value or a calculated value.

Operators are basic functions on variables or 

Two basic types of statements are set statements and expressions. The set statement includes an expression statement. The set statement acts on a variable and sets that variable to the value calculated from the expression. Expressions can also use the values of defined variables.
```

```