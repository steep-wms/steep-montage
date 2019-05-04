import io.vertx.core.Vertx
import io.vertx.kotlin.core.file.writeFileAwait
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import model.processchain.Argument
import model.processchain.ProcessChain

suspend fun createDiffFitInfo(output: Argument, processChain: ProcessChain,
    vertx: Vertx): List<Any> {
  val mDiffFit = processChain.executables.find { e -> e.arguments.any { it === output } }!!
  val inputImages = mDiffFit.arguments.filter { it.id == "input_image" }.map { it.variable.value }
  val outputStats = mDiffFit.arguments.find { it.id == "output_stats" }!!.variable.value
  val outputImage = mDiffFit.arguments.find { it.id == "output_image" }!!.variable.value

  val result = json {
    obj(
        "plus" to inputImages[0],
        "minus" to inputImages[1],
        "stat" to outputStats
    )
  }

  vertx.fileSystem().writeFileAwait(outputImage, result.toBuffer())

  return listOf(output.variable.value)
}
