import helper.OutputCollector
import helper.Shell.ExecutionException
import io.vertx.core.Vertx
import model.processchain.Executable
import runtime.OtherRuntime

fun ignoreError(executable: Executable, outputCollector: OutputCollector, vertx: Vertx) {
  try {
    OtherRuntime().execute(executable, outputCollector)
  } catch (e: ExecutionException) {
    if (e.exitCode != 1) {
      throw e
    }
  }
}
