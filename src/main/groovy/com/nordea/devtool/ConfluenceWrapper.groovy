package com.nordea.devtool

import groovy.transform.TypeChecked

@TypeChecked
class ConfluenceWrapper {

    private CurlWrapper curlWrapper
    private String confluenceUrl
    private String spaceName

    ConfluenceWrapper(String confluenceUrl, String spaceName, CurlWrapper curlWrapper) {
        this.spaceName = spaceName
        this.confluenceUrl = confluenceUrl
        this.curlWrapper = curlWrapper
    }

    def createBlogPost(String toolName, String toolVersion, String username, String password) {
        println "Creating blogpost..."

        def uploadCommand = """curl -g -k -u $username:$password -H "Content-Type:application/json" -X POST -d "{\\"type\\":\\"blogpost\\",\\"title\\":\\"new tool/version added to devtool: $toolName-$toolVersion\\",\\"space\\":{\\"key\\":\\"$spaceName\\"},\\"body\\":{\\"storage\\":{\\"value\\":\\"install new version with: devtool -install $toolName\\",\\"representation\\":\\"storage\\"}}}" $confluenceUrl/rest/api/content/"""
        def process = curlWrapper.runCurlCommand(uploadCommand)
        if (process.exitValue() != 0) {
            println "Problem with creating blogpost"
        }
    }

}
