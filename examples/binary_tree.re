Sentinel = class ->
Node = class -> left, right, val
Node.Node = (val) ->
	this.val = val
	this.left = Sentinel()
	this.right = Sentinel()

root = Node(1)
root.left = Node(2)
root.right = Node(3)

inorder = (curr) ->
	curr === Sentinel ?
		return []
	res = inorder(curr.left)
	res += [curr.val]
	res += inorder(curr.right)
	return res

write(inorder(root))