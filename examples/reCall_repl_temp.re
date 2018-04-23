_ = f = (n, CACHE = INF) -> n <= 2 ? 1 else f(n - 1) + f(n - 2)
_ = f(10)
_ = f = ((n, CACHE = INF) -> n <= 2 ? 1 else f(n - 1) + f(n - 2))
write(f(10))
