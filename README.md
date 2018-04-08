# reCall
Interpreter for reCall, a scripting language.

reCall is a dynamically typed language with imperative and functional capabilities. For and while loops are not supported, so only recursion and implicit loops can be used. Popular languages such as Java, C++, and Python inspired this project.

The interpreter is written in pure Java, and it uses recursion two levels of recursion to parse the code: one for if/else and function blocks, and another for evaluating expressions.

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

Operators are basic functions on variables or literal values. Basic mathematical operators include `+`, `-`, `*`, `/`, `//`, `%`, and `**`. They stand for addition, subtraction, multiplication, division, floor division, modulo (finding the remainder in division), and exponentiate (a to the power of b). There are also comparison operators like `==`, `!=`, `>`, and `<`. Those are only a few of the many operators in reCall. Operators are placed between two variables or literal values, like so: `a + b` or `1 + 2`. The only exception is unary operators, which work on one variable or literal: `-a` or `-1`. Note that some operators have different functions for different types of objects. For example, adding two strings concatenates them (`"abc" + "cba"` results in `"abccba"`).

Two basic types of statements are set statements and expressions. The set statement includes an expression statement. The set statement acts on a variable and sets that variable to the value calculated from the expression. If a variable was not previously defined and it is set, the it will be automatically defined. Expressions can also use the values of defined variables. Here are some examples of set statements, expressions, and variables. Note that the `#` denoted a comment, which tells the interpreter to ignore everything after it on the same line.
```
a = 1 + 2 # a = 3
b = a + 1 # b = 4
c = a + b + 1 # c = 8
d = 2 ** 10 # d = 1024
```
Any binary operator can be placed before the `=` sign to simplfy the code. For example, `a = a + 1` and `a += 1` yields the same result.

### Strings, Lists, and Maps
String literals are defined with double quotes, which looks like this: `"hello"`. A string is basically a list of characters in order. These characters can be accessed, but they cannot be changed. This is to make strings behave like the immutable numbers. Changes to strings creates new strings.

List literals are defined like this: `[1, 2, 3]` (items are separated by commas). They can also be multidimentional. For example, this is valid: `[1, 2, [3, 4]]`. Any object (other than functions) can be placed in a list. Lists maintain their items order, and they can be expanded or shrunk. An item can be accessed through its index in the list (indexed from 0).

Map literals are defined like so: `{"key": "value", "key 2": 3}`. Each of the items (which are separated by commas) consists of a key-value pair. The main differences between lists and maps are that lists maintain their order while maps do no necessarily maintain their order. Also, maps are accessed with the key values, unlike the indexes using in lists.

All three of these can be accessed or directly changed (except strings) with `[]`. For strings and lists, an index is specified. For maps, the key is used. An example to make it clear:
```
i = 1
s = "hello world"
s[0] # h
s[i] # e

l = [1, 2, [3, 4]]
l[i] # 2
l[2][0] # 3, this accesses the first element of the third element (which is a list) in the list l
l[3] = 3 # list becomes [1, 2, 3]
l[5] = 1 # bad! out of bounds!

m = {"key": "val", i: 3}
m["key"] # "val"
m[1] # 3
m["here"] = "there" # this is ok! "here": "there" will be placed in the map!
```
Lists and strings can be sliced using the `[start:end:step]` operator. Note that the end index is exclusive and the start index is inclusive. How this works is demostrated below. Of course, all of the numbers can be replaced with variables or expressions.
```
s = "hello world"
l = [1, 2, 3, 4, 5]

s[1:8:2] # produces "el o"
s[1:] # "ello world", the last colon can be omitted and not specifying a number means to go until the start or end
s[:] # "hello world"
s[1:-1] # "ello worl" negative indexes starts counting from the last element

l[::2] # [1, 3, 5]
l[:-2] # [1, 2, 3]
```

### Branching (if/else)
Code in reCall is ran line by line, from the top to the bottom. However, that flow can be changed using conditional branching, which is commonly known as if/else statements. Basically, one block of code will only be executed if and only if some condition is true. Here is the basic template of if/else blocks in reCall:
```
1st boolean ?
	# if 1st boolean is true
else 2nd boolean ?
	# if 1st boolean is false and 2nd boolean is true
else
	# if neither booleans are true
```
Of course, the else blocks are optional. The booleans can be expressions, variables, or literals. Note that all the code within a if/else block is tabbed in. The amount of tabbing does not matter, as long as it is consistent throughout the program.

Since reCall does not have boolean values, numbers are used to represent booleans. 0 is false, and everything else is true. Some objects can evaluate to a boolean value. For example, an empty list or an empty string is false. Comparison operators produces boolean values (`0 == 1` produces 0, obviously).

If an if/else statement is needed in one line, then the ternary operator variant of the if/else statement can be used. It is basically the same thing as the usual if/else: `1 == 1 ? "if boolean is true" : "if boolean is false"`. That whole statement, when evaluated, will produce the string `"if boolean is true"`.

Each block of code has its own scope.

### Functions
reCall's functions are divided into two categories: user (programmer) defined and built-in functions. They can both be called in the same way. User defined functions have higher precedance than built-in functions, so built-in functions can be overridden by user defined functions.

## All Operator in Decreasing Precedance


## All Built-in Functions

