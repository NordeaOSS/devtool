package com.nordea.devtool

import groovy.transform.TypeChecked

@TypeChecked
class CurlWrapper {

    private Debugger debugger

    CurlWrapper(Debugger debugger) {
        this.debugger = debugger
    }

    Process runCurlCommand(String curlString) {
        debugger.debugln "curlCommand = $curlString"
        Process p = curlString.execute()

        def outputStream = new StringBuffer()
        def errorStream = new StringBuffer()
        p.waitForProcessOutput(outputStream, errorStream)

        debugger.debugln "$outputStream"
        debugger.debugln "errorStream = $errorStream"
        debugger.debugln "errorId = ${p.exitValue()}"
        return p
    }

}
