import io.vertx.core.Vertx
import io.vertx.core.parsetools.RecordParser
import io.vertx.kotlin.core.file.openOptionsOf
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.toReceiveChannel
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
  val options = openOptionsOf(read = true, write = false, create = false)
  val file = vertx.fileSystem().open(table, options).await()
  try {
    val recordParser = RecordParser.newDelimited("\n", file)
    val channel = recordParser.toReceiveChannel(vertx)

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
    file.close().await()
  }
}

suspend fun splitOverlapTable(output: Argument, processChain: ProcessChain,
    vertx: Vertx): List<Any> {
  val mOverlaps = processChain.executables.find { e -> e.arguments.any { it === output } }!!
  val projectedImagesTable = mOverlaps.arguments.find { it.id == "image_table" }!!.variable.value
  val projectedImages = parseTable(projectedImagesTable, vertx) { it.getValue("fname") }

  return parseTable(output.variable.value, vertx) { entry ->
    val plus = entry.getValue("plus")
    val minus = entry.getValue("minus")
    val absolutePlus = projectedImages.find { it.endsWith(plus) }!!
    val absoluteMinus = projectedImages.find { it.endsWith(minus) }!!
    listOf(absolutePlus, absoluteMinus)
  }
}
