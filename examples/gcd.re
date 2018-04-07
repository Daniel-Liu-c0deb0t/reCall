gcd = (a, b) ->
	b ? # if b != 0
		return gcd(b, a % b)
	else
		return a

a = 60
b = 256
write("The GCD of %d and %d is %d!" % [a, b, gcd(a, b)])