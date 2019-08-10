import io.vertx.core.Vertx
import io.vertx.kotlin.core.file.OpenOptions
import io.vertx.kotlin.core.file.closeAwait
import io.vertx.kotlin.core.file.deleteAwait
import io.vertx.kotlin.core.file.existsAwait
import io.vertx.kotlin.core.file.mkdirsAwait
import io.vertx.kotlin.core.file.openAwait
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import java.io.File
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

  val fs = vertx.fileSystem()
  // fs.writeFileAwait(outputImage, result.toBuffer())
  fs.deleteAwait(outputImage)
  val areaFile = outputImage.replace(".fits", "_area.fits")
  if (fs.existsAwait(areaFile)) {
    fs.deleteAwait(areaFile)
  }
  val jsonDir = File(File(outputImage).parentFile, "json")
  fs.mkdirsAwait(jsonDir.path)
  val f = fs.openAwait(File(jsonDir, "${Main.agentId}.json").path,
      OpenOptions(append = true))
  f.write(result.toBuffer().appendString("\n"))
  f.closeAwait()

  return listOf(output.variable.value)
}
