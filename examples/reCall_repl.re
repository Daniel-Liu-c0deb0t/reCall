_ = 0
handle = (__) -> # use "__" for the input expression
	_ = eval(__)
	write(_)

# keep reading lines
generate((i) -> 1, (i) -> handle(read(">>> ")))