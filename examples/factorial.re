fact = (n) ->
	n == 1 ? # base case
		return 1
	
	return n * fact(n - 1)

n = 500
write("The factorial of %d is %d!" % [n, fact(n)])