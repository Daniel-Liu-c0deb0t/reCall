# standard explicit dynamic programming

fib = (n) ->
	dp = [-1] * (n + 1)
	
	rec = (a) -> # inner function for recursion
		dp[a] != -1 ? # if already calculated
			return dp[a]
		
		a <= 2 ? # if
			dp[a] = 1
		else
			dp[a] = rec(a - 1) + rec(a - 2)
		
		return dp[a]
	
	return rec(n)

n = 2000
write("1st method: the %dth Fibonacci number is %d!" % [n, fib(n)])


# use automatic function caching

n2 = 2000

fib2 = (n, CACHE = n2 + 1) ->
	n <= 2 ?
		return 1
	else
		return fib2(n - 1) + fib2(n - 2)

write("2nd method: the %dth Fibonacci number is %d!" % [n2, fib2(n2)])