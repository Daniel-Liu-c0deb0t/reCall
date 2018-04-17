# singly linked list implementation!

Sentinel = class ->
Node = class -> val, next
LinkedList = class -> head, len = 0

LinkedList.LinkedList = () ->
	this.head = Sentinel()

LinkedList.push = (val) ->
	n = Node(val, this.head)
	this.head = n

LinkedList.pop = () ->
	this.head !== Sentinel ?
		this.head = this.head.next

LinkedList.remove = (val) ->
	f = (n, p) -> # recursion to find the node to be removed
		n === Sentinel ?
			return
		n.val == val ?
			p === Sentinel ?
				this.head = n.next
			else
				p.next = n.next
			return
		f(n.next, n)
	f(this.head, Sentinel())

LinkedList.list = () ->
	res = generateList(this.head, (i, p) -> p.next !== Sentinel, (i, p) -> p.next)
	res = map(res, (i, x) -> x.val)
	return res

LinkedList.str = () ->
	f = (n) ->
		n === Sentinel ?
			return ""
		return n.val + " " + f(n.next)
	return f(this.head)

l = LinkedList()
l.push(1)
l.push(2)
l.push(3)
write(l.str())
write(l.list())
l.pop()
l.pop()
write(l.str())
l.push(2)
l.push(3)
write(l.str())
l.remove(2)
write(l.str())
write(l.list())