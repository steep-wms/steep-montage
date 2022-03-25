import io.vertx.core.Vertx
import model.processchain.Executable
import model.processchain.ProcessChain

fun needsProcessing(e: Executable): Boolean {
  return e.serviceId == "download_band" || e.serviceId == "mProjExec"
}

suspend fun removeId(processChains: List<ProcessChain>, vertx: Vertx): List<ProcessChain> {
  if (!processChains.any { pc -> pc.executables.any(::needsProcessing) }) {
    return processChains
  }

  val results = processChains.map { pc ->
    pc.copy(executables = pc.executables.map { e ->
      if (needsProcessing(e)) {
        e.copy(arguments = e.arguments.map { arg ->
          if (arg.id == "output_directory" || arg.id == "rawdir") {
            val newValue = arg.variable.value.substring(0, arg.variable.value.lastIndexOf("/") + 1)
            arg.copy(variable = arg.variable.copy(value = newValue))
          } else {
            arg
          }
        })
      } else {
        e
      }
    })
  }

  return results
}
