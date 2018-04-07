qSort = (arr) ->
	!arr ? # empty array
		return []
	
	f = arr[0] # partition
	s = qSort(filter(arr[1:], (i, x) -> x < f)) # smaller
	l = qSort(filter(arr[1:], (i, x) -> x >= f)) # larger
	
	return s + [f] + l # merge sorted arrays

arr = [5, 4, 3, 3, 1, 2, 7, 6, 10, -10]
write("Original: %s" % [arr])
write("Sorted: %s" % [qSort(arr)])