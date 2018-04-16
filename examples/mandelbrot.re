# this may take a long time to run...

m = (x, y, iter) ->
	arr = generate([x, y, 0], \
				(i, p) -> i < iter && p[0] ** 2 + p[1] ** 2 < 4, \
				(i, p) -> [p[0] ** 2 - p[1] ** 2 + x, 2 * p[0] * p[1] + y, i])
	c = arr[2] == iter - 1 ? 0 else arr[2] + 1
	c = mapRange(c, 0, iter, 50, 250) # scale iteration to color value
	return [c / 1.5, c / 1.5, c]

iter = 100
width = 300
height = 300

mapRange = (x, a1, b1, a2, b2) ->
	return (x - a1) / (b1 - a1) * (b2 - a2) + a2

win = newWindow("Mandelbrot", width, height, 3, \
			(x, y) -> m(mapRange(x, 0, width, -2, 0.5), mapRange(y, 0, height, -1.25, 1.25), iter))

write("mandelbrot.png", "png", win)

write("Drawing ended!")