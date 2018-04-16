# recursive method

reader = newFileReader("test_in.txt")
writer = newFileWriter("test_out.txt")

write(reader + "") # must cast to string in this case
write(writer + "")

while = () ->
	!hasNext(reader) ? # stop if EOF is reached
		return
	
	write(writer, read(reader)) # directly write the line that is read
	
	while()

while()

close(reader) # remember to close the reader and writer!
close(writer)

write("File copy done!")


# alternative functional method

reader = newFileReader("test_in.txt")
writer = newFileWriter("test_out.txt")

# this reads the whole file into an array!
map(generateList(read(reader), (i, p) -> hasNext(reader), (i, p) -> read(reader)), (i, x) -> write(writer, x))

close(reader)
close(writer)

write("File copy done!")