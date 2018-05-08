package com.nordea.devtool

import groovy.transform.TypeChecked

@TypeChecked
interface ToolsRepository {
    void uploadToolsAndVersionsFile(String userName, String password, File buildToolsAndVersionsFile)

    void deleteOldToolsAndVersionsFile(String userName, String password)

    void downloadToolsAndVersionsToTempfile(File tempFile)

    String getToolsSourceDir()

    boolean isUserNameAndPasswordNeededForRepository()
    /**
     *  The impl of this method should just ignore the username/password if not needed
     */
    void uploadTool(String toolName, String toolVersion, String toolpath, String username, char[] password)

    String getRepositoryName()
}