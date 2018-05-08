package com.nordea.devtool

import groovy.transform.TypeChecked

@TypeChecked
class CouldNotUploadException extends RuntimeException {
    CouldNotUploadException(String var1) {
        super(var1)
    }
}
