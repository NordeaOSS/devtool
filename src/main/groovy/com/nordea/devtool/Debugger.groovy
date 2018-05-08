package com.nordea.devtool

import groovy.transform.TypeChecked

@TypeChecked
class Debugger {

    boolean debugEnabled

    Debugger(boolean debugEnabled) {
        this.debugEnabled = debugEnabled
    }

    def debugln(String debugString) {
        if (debugEnabled) {
            println "DEBUG: " + debugString
        }
    }
}
