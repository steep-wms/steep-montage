import helper.FileSystemUtils
import io.vertx.core.Vertx
import model.processchain.Argument
import model.processchain.ProcessChain

suspend fun outputFitsImage(output: Argument, processChain: ProcessChain,
    vertx: Vertx): List<String> {
  val result = FileSystemUtils.readRecursive(output.variable.value, vertx.fileSystem())
  return result.flatMap { listOf(it, it.replace("""\.fits$""".toRegex(), "_area.fits")) }
}
