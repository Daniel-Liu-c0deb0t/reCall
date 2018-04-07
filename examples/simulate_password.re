accounts = {"hello": "hello", "user": "pass", "what": "hi"}

fail = 1
loop = (n) ->
	n >= 3 ? # only three tries allowed
		return
	
	u = read("Enter your username: ")
	
	contains(accounts, u) ?
		p = read("Enter your password: ")
		
		accounts[u] == p ?
			write("Access granted!")
			fail = 0
			return
		
		write("Wrong password!")
	else
		write("Username not found!")
	
	loop(n + 1)

loop(0)

fail ?
	write("Access denied!")