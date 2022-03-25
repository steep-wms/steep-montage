import io.vertx.core.Vertx
import io.vertx.kotlin.core.file.openOptionsOf
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.await
import model.processchain.Argument
import model.processchain.ProcessChain
import java.io.File

suspend fun createDiffFitInfo(output: Argument, processChain: ProcessChain,
    vertx: Vertx): List<Any> {
  val agentId = vertx.orCreateContext.config().getString(ConfigConstants.AGENT_ID)

  val mDiffFit = processChain.executables.find { e -> e.arguments.any { it === output } }!!
  val inputImages = mDiffFit.arguments.filter { it.id == "input_image" }.map { it.variable.value }
  val outputStats = mDiffFit.arguments.find { it.id == "output_stats" }!!.variable.value
  val outputImage = mDiffFit.arguments.find { it.id == "output_image" }!!.variable.value

  val result = jsonObjectOf(
      "plus" to inputImages[0],
      "minus" to inputImages[1],
      "stat" to outputStats
  )

  val fs = vertx.fileSystem()
  // fs.writeFileAwait(outputImage, result.toBuffer())
  fs.delete(outputImage).await()
  val areaFile = outputImage.replace(".fits", "_area.fits")
  if (fs.exists(areaFile).await()) {
    fs.delete(areaFile).await()
  }
  val jsonDir = File(File(outputImage).parentFile, "json")
  fs.mkdirs(jsonDir.path).await()
  val f = fs.open(File(jsonDir, "${agentId}.json").path,
      openOptionsOf(append = true)).await()
  f.write(result.toBuffer().appendString("\n"))
  f.close().await()

  return listOf(output.variable.value)
}
