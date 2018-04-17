r = newFileReader("test_data.csv")

# read file
lines = generateList((i) -> hasNext(r), (i) -> read(r))

# split by commas
lines = map(lines, (i, x) -> x / regex(",\s*"))

# find max item length
maxWidth = len(deepReduce("", lines, (x, y) -> len(x) > len(y) ? x else y)) + 5

# pad with spaces
lines = deepMap(lines, (x) -> " " * (maxWidth - len(x)) + x)

# separate by pipe characters
lines = map(lines, (i, x) -> reduce(x, (a, b) -> a + " | " + b))

write(reduce("", lines, (a, b) -> a + "\n" + b))