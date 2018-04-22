delete("reCall_repl_temp.re")
w = newFileWriter("reCall_repl_temp.re")

handle = (s) ->
	write(w, "write(" + s + ")")
	flush(w)
	exec("java -jar " + INTERPRETER_DIR + "/reCall_interpreter.jar reCall_repl_temp.re")

# keep reading lines
generate((i) -> 1, (i) -> handle(read(">>> ")))