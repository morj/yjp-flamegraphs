package com.github.morj.flamegraphs

import java.io.*

val functionArgs = "\\s*\\([^\\)]*\\)\\s*".toRegex()
val quote = "\""

val usage = """
  Utility for generating flame graphs from yjp snapshots

  Usage:
  1. generate csv dump via `java -jar -Dexport.call.tree.cpu -Dexport.csv ~/yjp/lib/yjp.jar -export ~/Snapshots/abc.snapshot ~/TempFolderName/`
  2. run this program via `java -jar yjp-flamegraphs.jar [TempFolderName] [ThreadName] > /path/to/stacks-dump.txt`
  3. generate flame graph with https://github.com/brendangregg/FlameGraph
     `./flamegraph.pl --width=1900 /path/to/stacks-dump.txt > /path/to/stacks.svg`
"""

fun main(args: Array<String>) {
    if (args.size < 2) {
        println(usage)
        return
    }

    val reader = BufferedReader(InputStreamReader(FileInputStream(args[0] + File.separator + "Call-tree--by-thread.csv"), "UTF-8"))
    val writer = BufferedWriter(OutputStreamWriter(System.out, "UTF-8"))

    var consume = false
    var found = false
    val stack = arrayListOf("")

    while (true) {
        val line = reader.readLine() ?: break
        val values = line.split("\",\\s?\"".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
        if (values.size < 4 || values[2].startsWith("O")) {
            continue
        }

        val name = values[0].removePrefix(quote)

        if (values[2].isEmpty()) {
            stack[0] = name.split(' ').first()
            consume = name.startsWith(args[1])
            if (consume) {
                found = true
            }
        } else if (consume) {
            val (ownTime, parsedLevel) = values.drop(2).map { Integer.parseInt(it.removeSuffix(quote)) }
            val level = parsedLevel - 1

            sanitize(name).let {
                if (level == stack.size) {
                    stack.add(it)
                } else {
                    stack[level] = it
                }
            }
            writer.append(stack[0])
            (1..level).forEach { i ->
                writer.append(';').append(stack[i])
            }
            writer.append(' ').append(ownTime.toString()).append('\n')
        }
    }

    if (!found) {
        System.err.println("No threads with name starting with ${args[1]}")
    }

    writer.close()
}

private fun sanitize(name: String): String {
    val tokens = name.replace(functionArgs, "").split(' ').last().split('.')
    val size = tokens.size
    if (size < 2) {
        return tokens.first()
    }
    return tokens[size - 2] + '.' + tokens[size - 1]
}
