lines = []

handle = (s) ->
	# delete the temp file first
	delete("reCall_repl_temp.re")
	w = newFileWriter("reCall_repl_temp.re")
	
	# print previous code
	map(lines, (i, x) -> write(w, x))
	
	# print current line
	contains(s, "write") ?
		write(w, s)
	else
		write(w, "write(" + s + ")")
	close(w)
	
	# set '_' as a temporary result
	lines += ["_ = " + s]
	
	# run the temporary file
	exec("java -jar " + INTERPRETER_DIR + "/reCall_interpreter.jar reCall_repl_temp.re")

write("Welcome to the reCall interactive (REPL) shell!")
write("Currently, multi-line statements are not allowed!")
write("Unfortunately errors are not supported yet. If a statement does not produce an output then an error has occured!")
write("Try to avoid complicated code to avoid errors!")

# keep reading lines
generate((i) -> 1, (i) -> handle(read("re> ")))