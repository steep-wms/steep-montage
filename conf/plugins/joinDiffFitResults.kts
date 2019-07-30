import helper.FileSystemUtils
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.parsetools.RecordParser
import io.vertx.kotlin.core.file.OpenOptions
import io.vertx.kotlin.core.file.closeAwait
import io.vertx.kotlin.core.file.openAwait
import io.vertx.kotlin.core.file.readFileAwait
import io.vertx.kotlin.core.file.writeFileAwait
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.toChannel
import model.processchain.Argument
import model.processchain.ProcessChain

/**
 * Parse table header and return a list of pairs representing the
 * column name and the width in characters
 */
fun parseHeader(line: String): List<Pair<String, Int>> {
  val header = mutableListOf<Pair<String, Int>>()
  val cols = line.split('|')
  for (i in 1 until cols.size - 1) {
    val name = cols[i]
    header.add(name.trim() to name.length)
  }
  return header
}

/**
 * Parse a table [line] according to columns in the given [header]
 */
fun parseLine(line: String, header: List<Pair<String, Int>>): Map<String, String> {
  val values = mutableMapOf<String, String>()
  var pos = 1
  for (col in header) {
    val value = line.substring(pos, (pos + col.second).coerceAtMost(line.length))
    values[col.first] = value.trim()
    pos += 1 + col.second
  }
  return values
}

/**
 * Parse a [table] file and call the given function [f] for every record. Collect
 * the results in a list.
 */
suspend fun <T> parseTable(table: String, vertx: Vertx, f: (Map<String, String>) -> T): List<T> {
  // open table
  val options = OpenOptions(read = true, write = false, create = false)
  val file = vertx.fileSystem().openAwait(table, options)
  try {
    val recordParser = RecordParser.newDelimited("\n", file)
    val channel = recordParser.toChannel(vertx)

    // read table line by line
    val result = mutableListOf<T>()
    val header = mutableListOf<Pair<String, Int>>()
    for (buf in channel) {
      val line = buf.toString()
      if (header.isEmpty()) {
        header.addAll(parseHeader(line))
      } else if (line[0] != '|') {
        result.add(f(parseLine(line, header)))
      }
    }

    return result
  } finally {
    file.closeAwait()
  }
}

suspend fun joinDiffFitResults(output: Argument, processChain: ProcessChain,
    vertx: Vertx): List<Any> {
  val exec = processChain.executables.find { e -> e.arguments.any { it === output } }!!
  val inputDirectory = exec.arguments.find { it.id == "input_directory" }!!.variable.value
  val inputFiles = FileSystemUtils.readRecursive(inputDirectory, vertx.fileSystem())
      .filter { it.endsWith(".fits") && !it.endsWith("_area.fits") }
  val imageTable = exec.arguments.find { it.id == "image_table" }!!.variable.value
  val outputFile = output.variable.value

  val projectedImages = parseTable(imageTable, vertx) {
    it.getValue("fname") to it.getValue("cntr") }.toMap()

  val fs = vertx.fileSystem()
  val entries = inputFiles.map { inputFile ->
    val obj = fs.readFileAwait(inputFile).toJsonObject()
    val cntr1 = projectedImages.getValue(obj.getString("plus"))
    val cntr2 = projectedImages.getValue(obj.getString("minus"))
    json {
      obj(
          "cntr1" to cntr1,
          "cntr2" to cntr2,
          "stat" to obj.getString("stat")
      )
    }
  }

  val statWidth = entries.map { it.getString("stat").length }.max() ?: 0

  val result = StringBuilder()
  result
      .append("|")
      .append("cntr1".padStart(20))
      .append("|")
      .append("cntr2".padStart(20))
      .append("|")
      .append("stat".padStart(statWidth))
      .append("|\n")
  for (e in entries) {
    result
        .append(" ")
        .append(e.getString("cntr1").padStart(20))
        .append(" ")
        .append(e.getString("cntr2").padStart(20))
        .append(" ")
        .append(e.getString("stat").padStart(statWidth))
        .append("\n")
  }

  fs.writeFileAwait(outputFile, Buffer.buffer(result.toString()))

  return listOf(outputFile)
}
