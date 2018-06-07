n = num(read("What is your number? "))
res = generate(1, \
			(i, x) -> i * i <= n, \
			(i, x) -> x && (i <= 1 ? 1 else n % i))
res ?
	write(n + " is prime!")
else
	write(n + " is composite!")