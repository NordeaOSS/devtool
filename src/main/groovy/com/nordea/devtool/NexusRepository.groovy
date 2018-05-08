package com.nordea.devtool

import groovy.transform.TypeChecked

@TypeChecked
class NexusRepository implements ToolsRepository {

    private String nexusRepository
    private String nexusServerUrl
    private CurlWrapper curlWrapper
    private Debugger debugger

    NexusRepository(String nexusRepository, String nexusServerUrl, Debugger debugger) {
        this.debugger = debugger
        this.nexusServerUrl = nexusServerUrl
        this.nexusRepository = nexusRepository

        this.curlWrapper = new CurlWrapper(debugger)
    }

    @Override
    void uploadToolsAndVersionsFile(String nexusUserName, String nexusPassword, File buildToolsAndVersionsFile) {
        def process = curlWrapper.runCurlCommand("curl -v -u $nexusUserName:$nexusPassword --upload-file $buildToolsAndVersionsFile.absolutePath " + "$nexusServerUrl/nexus/content/repositories/$nexusRepository/devtool/toolsandversions/1.0/toolsandversions-1.0.txt")
        if (process.exitValue() != 0) {
            println "Problem with uploading ToolsAndVersionsFile"
        }
    }

    @Override
    void deleteOldToolsAndVersionsFile(String nexusUserName, String nexusPassword) {
        def process = curlWrapper.runCurlCommand("curl --request DELETE --user \"$nexusUserName:$nexusPassword\" $nexusServerUrl/nexus/content/repositories/$nexusRepository/devtool/toolsandversions/1.0/toolsandversions-1.0.txt")
        if (process.exitValue() != 0) {
            println "Problem with deleting old ToolsAndVersionsFile"
        }
    }

    @Override
    void downloadToolsAndVersionsToTempfile(File tempFile) {
        def process = curlWrapper.runCurlCommand("curl $nexusServerUrl/nexus/content/repositories/$nexusRepository/devtool/toolsandversions/1.0/toolsandversions-1.0.txt --output " + tempFile.absolutePath)
        if (process.exitValue() != 0) {
            println "Problem with downloading the list of tools and versions"
            System.exit(0)
        }
    }

    @Override
    String getToolsSourceDir() {
        return "$nexusServerUrl/nexus/service/local/repositories/$nexusRepository/content/devtool"
    }

    @Override
    boolean isUserNameAndPasswordNeededForRepository() {
        return true
    }

    @Override
    void uploadTool(String toolName, String toolVersion, String toolpath, String username, char[] password) {
        def uploadCommand = "curl --insecure -v -F r=$nexusRepository -F hasPom=false -F e=zip -F g=devtool -F a=$toolName -F v=$toolVersion -F process=zip -F file=@$toolpath -u $username:$password https://ninja-nexus.oneadr.net/nexus/service/local/artifact/maven/content"
        debugger.debugln "uploadCommand = $uploadCommand"
        Process process = uploadCommand.execute()

        def outputStream = new StringBuffer()
        def errorStream = new StringBuffer()
        process.waitForProcessOutput(outputStream, errorStream)

        println "$outputStream"
        debugger.debugln "errorStream = $errorStream"
        debugger.debugln "errorId = ${process.exitValue()}"

        if (process.exitValue() == 26) {
            throw new CouldNotUploadException("Could not open file: $toolpath")
        } else if (errorStream.contains("401 Unauthorized")) {
            throw new CouldNotUploadException("Incorrect nexusPassword!")
        } else if (process.exitValue() != 0) {
            throw new CouldNotUploadException("An unknown error occured while uploading tool.")
        }
    }

    @Override
    String getRepositoryName() {
        return "Nexus"
    }
}
