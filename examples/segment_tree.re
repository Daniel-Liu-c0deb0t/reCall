#segment tree with range sum queries and point increment updates

n = 10
seg = n * 4 * [0]

query = (l, r, ql, qr, i) ->
	l >= ql && qr >= r ?
		return seg[i]
	r < ql || l > qr ?
		return 0
	m = (l + r) // 2
	return query(l, m, ql, qr, i * 2 + 1) + query(m + 1, r, ql, qr, i * 2 + 2)

update = (l, r, ui, i, val) ->
	r < ui || l > ui ?
		return
	l == r ?
		seg[i] += val
		return
	m = (l + r) // 2
	update(l, m, ui, i * 2 + 1, val)
	update(m + 1, r, ui, i * 2 + 2, val)
	seg[i] = seg[i * 2 + 1] + seg[i * 2 + 2]

#array: [1, 2, 3, 4, 5]
update(0, n - 1, 0, 0, 1)
update(0, n - 1, 1, 0, 2)
update(0, n - 1, 2, 0, 3)
update(0, n - 1, 3, 0, 4)
update(0, n - 1, 4, 0, 5)
write(query(0, n - 1, 0, 4, 0))
write(query(0, n - 1, 2, 3, 0))
write(query(0, n - 1, 1, 1, 0))