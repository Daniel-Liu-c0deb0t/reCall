lines = []
handle = (s) ->
	delete("reCall_repl_temp.re")
	w = newFileWriter("reCall_repl_temp.re")
	map(lines, (i, x) -> write(w, x))
	write(w, "write(" + s + ")")
	close(w)
	lines += ["_ = " + s]
	exec("java -jar " + INTERPRETER_DIR + "/reCall_interpreter.jar reCall_repl_temp.re")

# keep reading lines
generate((i) -> 1, (i) -> handle(read("re> ")))