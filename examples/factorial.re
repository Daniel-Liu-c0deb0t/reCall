fact = (n) ->
	n == 1 ? # base case
		return 1
	
	return n * fact(n - 1)

n = 50
write("The factorial of %.0f is %.0f!" % [n, fact(n)])