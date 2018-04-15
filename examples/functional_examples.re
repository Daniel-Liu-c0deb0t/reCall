a = [1, 2, 3, 4, 5]

# increment each item
write(map(a, (i, x) -> x + 1))

# print each item
map(a, (i, x) -> write(x))

# print on a single line, separated by spaces
write(reduce(a, (x, y) -> x + " " + y))

# print odds from 1 to 100, separated by spaces
# just using x % 2 works because 0 is false and 1 is true
write(reduce(filter(1..100, (i, x) -> x % 2), (x, y) -> x + " " + y))

# convert a list of strings to numbers
s = ["123", "1.23", "12.3", "1e10"]
n = map(s, (i, x) -> num(x))
write(n)

# print a generated list of 20 random integers
# the first parameter is the first item in the list
write(generateList(randInt(0, 100), 20, (i, p) -> randInt(0, 100)))

# flatten a nested list
a = [1, [2], [3, 4], [5, 6, 7], [8, 9, 10, 11]]
write(flatten(a))

# square every number in a nested list (while preserving structure)
write(deepMap(a, (x) -> x ** 2))

# sum of every number in a nested list
# same as the built-in sum(...) function
write(sum(a))
write(deepReduce(a, (x, y) -> x + y))

# parse a list of names and emails
# input format: "first last email, first last email, ..."
# output format: {"last, first": "email", "last, first": "email", ...}

data = "name what  what@what.com,      hello   world hello@world.com,coding programmer code@website.com,    generic  name generic@email.com"

s = data / regex(",\s*") # comma and any number of spaces
s = map(s, (i, x) -> x / regex("\s+")) # one or more spaces
s = map(s, (i, x) -> ["%s, %s" % [x[1], x[0]], x[2]])
s = listToMap(s)
write(s)