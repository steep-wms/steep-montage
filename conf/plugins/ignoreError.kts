import helper.Shell.ExecutionException
import io.vertx.core.Vertx
import model.processchain.Executable
import runtime.OtherRuntime

fun ignoreError(executable: Executable, outputLinesToCollect: Int, vertx: Vertx): String {
  return try {
    OtherRuntime().execute(executable, outputLinesToCollect)
  } catch (e: ExecutionException) {
    if (e.exitCode == 1) {
      ""
    } else {
      throw e
    }
  }
}
