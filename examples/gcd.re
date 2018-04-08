gcd = (a, b) ->
	b ? # if b != 0
		return gcd(b, a % b)
	else
		return a

a = 60
b = 256
write("The GCD of %.0f and %.0f is %.0f!" % [a, b, gcd(a, b)])