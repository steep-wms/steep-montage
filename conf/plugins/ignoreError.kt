import helper.OutputCollector
import helper.Shell.ExecutionException
import model.processchain.Executable
import runtime.OtherRuntime

fun ignoreError(executable: Executable, outputCollector: OutputCollector) {
  try {
    OtherRuntime().execute(executable, outputCollector)
  } catch (e: ExecutionException) {
    if (e.exitCode != 1) {
      throw e
    }
  }
}
