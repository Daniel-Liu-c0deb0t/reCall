# reCall
Interpreter for reCall, a scripting language.

reCall is a dynamically typed language with imperative and functional capabilities. For and while loops are not supported, so only recursion and implicit loops can be used. Popular languages such as Java, C++, and Python inspired this project.

The interpreter is written in pure Java, and it uses recursion two levels of recursion to parse the code: one for if/else and function blocks, and another for evaluating expressions.

**Important note! reCall is not very optimized, so it probably should not be used in production. However, it can be used for automating and scripting purposes.**

---

## Using the Interpreter
Run the interpreter by downloading the compiled `reCall_interpreter.jar` and using the following command:
```
java -Xss10m -jar reCall_interpreter.jar /path/to/source/code.re
```
To test that the installation works, run the `.jar` without any source code file and it will show a greeting.

The source code file should end with `.re`, although any extension will work fine. The `-Xss` command for the Java Runtime Environment allows the program to use more memory for the stack size. reCall is quite memory hungry when doing recursion, so the `-Xss` command is almost necessary.

The following commands can be added to redirect the input and output.

`--output` or `-o`: Redirect stdout to a file. Specify a file path after.

`--input` or `-i`: Change stdin to a file. Specify a file path after.

`--error` or `-e`: Redirect the stderr to a file. Again, specify a file path after.

---

## reCall Syntax
reCall works on statements and objects. Objects can be defined or changed by statements. Some statement can even redirect the flow of the code. Code can be directly written in a text file and ran using the interpreter. Note that spaces do not matter, unless they are indents at the start of each line. Also, variables and functions names are case sensitive!

To get a taste of how reCall looks like, here is the basic hello world program:
```
write("Hello, world!")
```
And here is a more complicated example for finding the factorial of a number:
```
fact = (n) ->
	n == 1 ? # base case
		return 1
	
	return n * fact(n - 1)

n = 250
write("The factorial of %.0f is %.0f!" % [n, fact(n)])
```
Read on for the full tutorial and other details!

### Variables and Operators
There are many different types of objects that can be created and manipulated in reCall. Here are the basic types: `string`, `number`, `list`, and `map`. For example, a literal number is just written out that number's value in the code. A literal two would look like `2`. A literal string (a bunch of characters) would look like `"hello world!"`.

A variable acts like a storage area for some sort of object or value. It needs to be defined before it is used. A variable can save any of the types mentioned above, without needing to explicitly specify what type the variable is (dynamic typing). Each variable can store a literal value or a calculated value.

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

Map literals are defined like so: `{"key": "value", "key 2": 3}`. Each of the items (which are separated by commas) consists of a key-value pair. The main differences between lists and maps are that lists maintain their order while maps do no necessarily maintain their order. Also, maps are accessed with the key values, unlike the indexes using in lists. Every key needs to be unique in a map. To create a set, where values are not necessary, the colons and the values can be omitted to look like this: `{"item 1", "item 2", 3}`. Entries with and without values can be mixed. Keys without their corresponding values just have a dummy value of zero.

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

If an if/else statement is needed in one line, then the ternary operator variant of the if/else statement can be used. It is basically the same thing as the usual if/else: `1 == 1 ? "if boolean is true" else "if boolean is false"`. That whole statement, when evaluated, will produce the string `"if boolean is true"`.

Each block of code has its own scope. This means that variables created within an if/else block **cannot** be accessed outside of it, while the code inside the if/else block can access variables defined outside of it. However, changes applied to variables outside of the if/else block will still stick.

### Functions
reCall's functions are divided into two categories: user (programmer) defined and built-in functions. They can both be called in the same way. User defined functions have higher precedance than built-in functions, so built-in functions can be overridden by user defined functions.

Calling a function is very simple. For example, to calculate the log base 2 of 8, the log function can be used like so: `log(2, 8)`. Parameters that are passed into the function are separated by commas. The parentheses are necessary for function calls without any paramenters! The parameters can be expressions, variables, or literal values. A very useful function is the `write` function, which prints whatever value that is given to it out to the standard output stream.

This is an example of a function definition:
```
function = (a, b, c) ->
	# code goes here!
	# the parameters a, b, and c can be used here!
```
Like if/else statements, the code within the function must be indented. Also, like calling functions, the parameter names are placed in the parentheses, and there can be no parameters. Functions can be defined anywhere, even within another function. The defined function is put inside the `function` variable, and that variable can be used to call the function. In reCall, the function variable behaves like any other variables, and it can be passed as parameter to another function.

Code inside the function is also within the function scope. That means that the parameters and other variables created within the function cannot be accessed outside of it. The general rule is that code within a function or an if/else statement can only access variables in all of the scopes (functions or if/else blocks) that completely encompasses it. For example:
```
a = 1

func2 = (p2) ->
	# can only access p2

func = (p1) ->
	# can access a, func, p1
	1 == 1 ?
		b = 2
		# can access a, func, p1, and b
	func2(4)

func(3)
```
Functions can either return/produce a value or not. To return a value, the `return` keyword can be used like so:
```
add = (a, b) -> # simple addition function
	return a + b
```
When a return is encountered, the function will halt and return to where it was called from, ignoring any code that comes after within the function. Returns are required if functions that produces a value is needed.

Lambda or inline functions are functions that can only take up one line of code in reCall, similar to the ternary operator. They can be encorporated into expressions or function calls. Lambda functions have an implicit return, so the `return` keyword is not needed at all. For example:
```
f = (n) -> n + 1
write(f(1)) # output: 2

arr = [1, 2, 3, 4, 5]
# the filter function only keeps items where the inline function evaluates to true
filter(arr, (i, x) -> x > 3) # produces [4, 5] using inline callback function
```
A cool feature for functions that do not depend on the global state (it only uses its parameters and variables defined within the function) is the ability to cache function calls. That makes some dynamic programming or memoization tasks much easier to write. Example of a function with automatic caching:
```
f = (n, CACHE = INF) ->
	# do something here
```
The `INF` indicates that there should not be a limit to the cache size. The specified cache size gives an upper limit to the number of function calls that can be saved. It can be changed by specifying a variable, literal, or expression after the `CACHE` keyword. Also note that the `CACHE` keyword has to appear as the last "parameter" of the function.

---

## All Object Types
Type | Examples | Use | Notes
--- | --- | --- | ---
Number | `1`, `2`, `100`, `3.14`, `3 + 3.5`, `1e-9` | Represents a floating point (decimal) or integer number. | reCall offers *almost* arbitrary precision numbers! Numbers can only have up to 100 digits of precision (a double in Java has around 15), but the exponent (scale) can be very, very large (or small)!
String | `"Hello, world!"`, `"123"`, `"a" + "b"` | Represents a string of characters. | String behave like lists, but they are immutable.
List | `[1, 2, 3]`, `[[1, 2], [3, 4]]`, `[1, 2] + [3, 4]`, `["hello", 1, 1.234]` | Represents a list objects that can be any type. | Lists can be changed using the `listName[index]` operation.
Map | `{"hello": 1, 2: 3, 4: [1, 2, 3]}` | Represents key-value pairs, where each value can be accessed by its corresponding (unique) key. | Maps can be changed using the `mapName[key]` operation. The value can be omitted, in which case the dummy value of `0` will be used.
File Reader | `reader = fileReader(...)` | Represents a file reader object for a file. | Look at the functions section for functions that act of a file reader.
File Writer | `writer = fileWriter(...)` | Represents a file writer object for a file. | Look at the functions section for functions that act of a file writer.
Function | `square = (a) -> a * a` | Represents a function that can be called. | Functions can call itself to loop some code. However, the call stack size might get too big and the program can crash. This only happens when there are **a lot** of repeated function calls.
Window | `win = window(...)` | Creates a rudimentary window that can be drawn on. | Drawing is very, very simple. For each pixel, the specified callback function is used to determine the color at that pixel.

---

## All Built-in Variables
Name | Value | Use
--- | --- | ---
PI | 3.1415... | 100 digits of PI... for fun?
E | 2.7182... | 100 digits of E... for more fun?

---

## All Operators
Precedance | Operators | Description | Details
--- | --- | --- | ---
| `[]`, `{}`, `()` | Parantheses, function calls, list literals, map literals, etc. |
| `-` | unary negation (eg. `-1`, `-3.5`) |
| `!` | unary logical not (eg. `!1` = `0`, `!0` = `1`) |
| `**` | exponentiate/power function. (eg. `2 ** 4` = `16`) |
| `*` | multiply | Can also duplicate strings and arrays (performs a deep copy!, eg. `[1, 2, 3] * 2` = `[1, 2, 3, 1, 2, 3]`)
| `/` | divide | Can also split one string by another (eg. `"hello world" / " "` = `["hello", "world"]`)
| `//` | floor division | Divide, but round down (floor) to an integer
| `%` | modulo/remainder | Can also be used in string formatting (eg. `"List: %s" % [1, 2, 3]` = `"List: [1, 2, 3]"`, usually `%s` (for strings) and `%f` (for numbers) are used)
| `+` | addition | Can also be used to concatenate lists, strings, and merge maps.
| `-` | subtraction | Can also be used to remove a item from a list or map. (eg. `[1, 2, 3] - [2, 3]` = `[1]`, {1: 2, 3: 4} - {1} = {3: 4})
| `>`, `<`, `>=`, `<=` | greater than, less than, greater than or equal to, less than or equal to | Strings and numbers can be compared. Lists can also be compared. Strings and lists are compared by lexicographical order.
| `==`, `!=` | equals, not equals | When matching two strings, one string can be a regex pattern. If both or none of the strings are regex then character by character matching is used.
| `&&` | logical and | True only if both sides evaluate to true (non-zero) values. Note that this operator short circuits, so expressions separated by the `&&` are evaluated from left to right, and if one expression is false, it stops evaluating the others.
| `||` | logical or | True if either side evaluates to true (non-zero) values. Note that this operator short circuits, so expressions separated by the `||` are evaluated from left to right, and if one expression is true, it stops evaluating the others.
| `?` and `else` | ternary operator | Basically a one line, condensed if/else statement.
| `=` and variants (`+=`, `*=`, etc.) | set operator | sets a variable or a list/map item to some value | Example: `a += 1` = `a = a + 1`.

Other than `=` all other operators do no change the state of the object being operated on. A new object is created instead.

---

## All Built-in Functions

