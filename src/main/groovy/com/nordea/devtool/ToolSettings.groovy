/*
 *
 * Copyright (c) 2017. Nordea Bank AB
 * Licensed under the MIT license (LICENSE.txt)
 *
 */

package com.nordea.devtool

import groovy.transform.TypeChecked

@TypeChecked
class ToolSettings {

    /**
     * List of paths for a given tool. Ie bin dir
     */
    List<String> paths = new ArrayList<>()

    /**
     * List of specific variables for a tool
     */
    List<String> toolVariables = new ArrayList<>()

    String comment

    @Override
    public String toString() {
        return "Settings{" +
                "paths=" + paths +
                ", toolVariables=" + toolVariables +
                ", comment='" + comment + '\'' +
                "} ";
    }
}
