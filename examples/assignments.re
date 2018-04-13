Assignment = class -> name, pages, dueTomorrow; static doIt
Assignment.doIt = (time) ->
	write("The assignment, " + this.name + ", was done in " + time + " minutes!")

assign = () ->
	res = []
	res += [Assignment("Homework 1", 5, 1)]
	res += [Assignment("Homework 2", 10, 0)]
	res += [Assignment("Project", 5, 0)]
	res += [Assignment("Classwork 1", 3, 1)]
	res += [Assignment("Classwork 2", 10, 1)]
	return res

list = assign() # make a few assignments in a list

# lets see what we need to do
map(list, (i, x) -> write((x.dueTomorrow ? "Gotta do %.0f pages of my %s before tomorrow!" \
										else "Gotta do %.0f pages of my %s... not!") % [x.pages, x.name]))

# ok, we are only doing the ones that are due tomorrow
map(filter(list, (i, x) -> x.dueTomorrow), (i, x) -> x.doIt(randInt(0, 120)))