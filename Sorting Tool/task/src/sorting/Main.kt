package sorting
import java.io.File
import kotlin.math.round
import kotlin.system.exitProcess
import kotlin.text.StringBuilder


val VALID_ARGS = listOf("-dataType", "-sortingType", "-inputFile", "-outputFile")
val scanner = java.util.Scanner(System.`in`)

fun main(args: Array<String>) {
    val (dataType, sortingType, io) = parseArgs(args)
    when (dataType) {
        "word" -> handleWords(sortingType, io)
        "long" -> handleLongs(sortingType, io)
        "line" -> handleLines(sortingType, io)
    }
}

private fun parseArgs(args: Array<String>): Triple<String, String, Pair<String, String>> {
    val map = args.fold(Pair(emptyMap<String, List<String>>(), "")) { (map, lastKey), elem ->
        if (elem.startsWith("-"))  Pair(map + (elem to emptyList()), elem)
        else Pair(map + (lastKey to map.getOrDefault(lastKey, emptyList()) + elem), lastKey)
    }.first

    val sortingType: String = if ("-sortingType" in map) {
        if (map["-sortingType"]?.isNotEmpty() == true)
            map["-sortingType"]?.first() ?: "natural"
        else {
            println("No sorting type defined!")
            exitProcess(1)
        }
    } else {
        "natural"
    }

    val dataType: String = if ("-dataType" in map) {
        if (map["-dataType"]?.isNotEmpty() == true)
            map["-dataType"]?.first() ?: "word"
        else {
            println("No data type defined!")
            exitProcess(1)
        }
    } else {
        "word"
    }

    val inputFile: String = if ("-inputFile" in map) {
        if (map["-inputFile"]?.isNotEmpty() == true)
            map["-inputFile"]?.first() ?: "console"
        else {
            println("No input file defined!")
            exitProcess(1)
        }
    } else {
        "console"
    }

    val outputFile: String = if ("-outputFile" in map) {
        if (map["-outputFile"]?.isNotEmpty() == true)
            map["-outputFile"]?.first() ?: "console"
        else {
            println("No output file defined!")
            exitProcess(1)
        }
    } else {
        "console"
    }

    for (arg in map.keys) {
        if (arg !in VALID_ARGS) {
            println("\"$arg\" is not a valid parameter. It will be skipped.")
        }
    }

    return Triple(dataType, sortingType, Pair(inputFile, outputFile))
}

fun handleLines(sortingType: String, io: Pair<String, String>) {
    val lines = mutableListOf<String>()
    val (inputFile, outputFile) = io

    if (inputFile != "console") {
        File(inputFile).forEachLine { lines.add(it) }
    } else {
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine())
        }
    }


    when (sortingType) {
        "natural" -> lines.sort()
        "byCount" -> {
            lines.sort()
            lines.sortBy { lines.count(it::equals) }
        }
    }

    val output = StringBuilder()
    output.append("Total lines: ${lines.size}.\n")

    if (sortingType == "natural") {
        output.append("Sorted data:\n")
        for (line in lines) {
            output.append("${line}\n")
        }
    } else {
        for (line in lines.distinct()) {
            output.append("$line: ${lines.count(line::equals)} time(s), ${(lines.count(line::equals) / lines.size.toDouble() * 100).toInt()}%\n")
        }
    }

    printResult(outputFile, output)
}

fun handleWords(sortingType: String, io: Pair<String, String>) {
    val words = mutableListOf<String>()
    val (inputFile, outputFile) = io

    if (inputFile != "console") {
        File(inputFile).forEachLine {
            line -> run {
                val content = line.split("\\s+".toRegex())
                content.forEach { words.add(it) }
            }
        }
    } else {
        while (scanner.hasNext()) {
            words.add(scanner.next())
        }
    }

    when (sortingType) {
        "natural" -> words.sort()
        "byCount" -> {
            words.sort()
            words.sortBy { words.count(it::equals) }
        }
    }

    val output = StringBuilder()
    output.append("Total words: ${words.size}.\n")

    if (sortingType == "natural") {
        output.append("Sorted data: ")
        output.append(words.joinToString(" "))
    } else {
        for (word in words.distinct()) {
            output.append("$word: ${words.count(word::equals)} time(s), ${(words.count(word::equals) / words.size.toDouble() * 100).toInt()}%\n")
        }
    }

    printResult(outputFile, output)
}

fun handleLongs(sortingType: String, io: Pair<String, String>) {
    val integers = mutableListOf<Int>()
    val (inputFile, outputFile) = io
    if (inputFile != "console") {
        File(inputFile).forEachLine {
                line -> run {
                    val content = line.split("\\s+".toRegex())
                    for (word in content) {
                        if (word.matches(Regex("-?[0-9]+"))) {
                            integers.add(word.toInt())
                        } else {
                            println("\"$word\" is not a long. It will be skipped.")
                        }
                    }
                }
        }
    } else {
        while (scanner.hasNext()) {
            val nextElement = scanner.next()
            if (nextElement.matches(Regex("-?[0-9]+"))) {
                integers.add(nextElement.toInt())
            } else {
                println("\"$nextElement\" is not a long. It will be skipped.")
            }
        }
    }

    when (sortingType) {
        "natural" -> integers.sort()
        "byCount" -> {
            integers.sort()
            integers.sortBy { integers.count(it::equals) }
        }
    }

    val output = StringBuilder()
    output.append("Total numbers: ${integers.size}.\n")
    if (sortingType == "natural") {
        output.append("Sorted: ${integers.sorted().joinToString(separator = " ")}")
    } else {
        for (integer in integers.distinct()) {
            output.append("$integer: ${integers.count(integer::equals)} time(s), ${round(integers.count(integer::equals) / integers.size.toDouble() * 100).toInt()}%\n")
        }
    }

    printResult(outputFile, output)

}

private fun printResult(outputFile: String, output: StringBuilder) {
    if (outputFile != "console") {
        val file = File(outputFile)
        file.createNewFile()
        file.writeText(output.toString())
    } else {
        println(output.toString())
    }
}
