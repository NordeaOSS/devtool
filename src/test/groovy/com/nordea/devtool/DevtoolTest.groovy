/*
 *
 * Copyright (c) 2017. Nordea Bank AB
 * Licensed under the MIT license (LICENSE.txt)
 *
 */

package com.nordea.devtool

import groovy.transform.TypeChecked
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

@TypeChecked
class DevtoolTest {
    def sepChar = File.separatorChar
    def pathSepChar = File.pathSeparatorChar

    @Rule
    public TemporaryFolder srcFolder = new TemporaryFolder()

    @Rule
    public TemporaryFolder destFolder = new TemporaryFolder()

    String mockedSrcDir
    String mockedDestDir
    List<String> mockedGetVersionsForRemoteTool_versions

    Devtool devtool

    @Before
    void setupFolder() {
        mockedSrcDir = srcFolder.root.getAbsolutePath()
        mockedDestDir = destFolder.root.getAbsolutePath()
    }

    @Before
    void init() {
        ToolsRepository repository = createMockedRepository()


        devtool = new Devtool() {

            @Override
            String getToolsDestinationDir() {
                return mockedDestDir
            }

            @Override
            List<String> getVersionsForRemoteTool(String toolName) {
                return mockedGetVersionsForRemoteTool_versions
            }

            @Override
            List<String> getRemoteSortedToolsList() {
                return ["ant"]
            }

            @Override
            void installTool(String toolName, String toolVersion) {
            }
        }

        devtool.toolsRepository = repository

    }


    @Test
    void testLogArgs() throws Exception {
        devtool.logArgs("test.txt", "test args", "John Doe")
        new File('test.txt').deleteOnExit()
    }

    @Test
    void testGetLatestVersionForToolWithoutVersionButWitSubdir() throws Exception {
        def folder = srcFolder.newFolder("toolWithoutVersion")
        new File(folder, "someSubdir").mkdir()
        new File(folder, "someSubdir2").mkdir()
        assertEquals("0", devtool.getLatestVersionForToolFromLocal(devtool.toolsRepository.getToolsSourceDir(), "toolWithoutVersion"))
    }

    @Test
    void testGetVersionFilenameFilter() throws Exception {
        srcFolder.newFolder("a")
        srcFolder.newFolder("1")
        srcFolder.newFolder("b")
        srcFolder.newFolder("2.2.2")

        def versions = srcFolder.getRoot().list(devtool.getVersionFilenameFilter())

        String[] versionsExpected = ["1", "2.2.2"]
        assertEquals(versionsExpected, versions)
    }

    @Test
    void testGetLatestVersionForLocalTool() throws Exception {
        setupMavenToolWithMultipleVersions()
        assertEquals("1.1.10", devtool.getLatestVersionForToolFromLocal(devtool.toolsRepository.getToolsSourceDir(), "maven"))
    }

    @Test
    void testCreateSortableNumber() {
        assertEquals(1000000000L, devtool.createSortableNumber("1"))
        assertEquals(1000000000L, devtool.createSortableNumber("1.0"))
        assertEquals(1001000000L, devtool.createSortableNumber("1.1"))
        assertEquals(1006002001L, devtool.createSortableNumber("1.6.20.1"))
        assertEquals(3006002013L, devtool.createSortableNumber("3.6.20.13"))
    }

    @Test
    void testSortVersionNumbers() {
        assertEquals(["1.8.144", "1.9"], devtool.sortVersionNumbers(["1.9", "1.8.144"]))
    }


    @Test
    void testSetup() {

        def localDevtool = [
                downloadSettingsForTool: { String s -> new ToolSettings() },
                getToolsDestinationDir : { -> mockedDestDir }
        ] as Devtool

        createSourceAndDestDirs()
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            localDevtool.setupTool("ant", "1.1.2")
        }
    }

    @Test
    void testListUpdates() {
        createSourceAndDestDirs()
        mockedGetVersionsForRemoteTool_versions = ["1.1.10"]
        assertEquals("ant 1.1.2 -> 1.1.10\n", devtool.listAndInstallUpdates(false))
    }

    @Test
    void testGetVersionsForTool() {
        mockedGetVersionsForRemoteTool_versions = ["3.0.5"]
        def versions = devtool.getVersionsForRemoteTool("maven")
        assertEquals(1, versions.size())
        assertEquals("3.0.5", versions.get(0))
    }

    @Test
    void testExtractToolName() {
        assertEquals("sometool", devtool.extractToolName("sometool-1.0.zip"))
        assertEquals("test_test", devtool.extractToolName("test_test-11.12120.12.zip"))
        assertEquals("test", devtool.extractToolName("test"))
    }

    @Test
    void testExtractToolVersion() {
        assertEquals("", devtool.extractToolVersion("test"))
        assertEquals("1.0", devtool.extractToolVersion("test-1.0.zip"))
        assertEquals("1.0", devtool.extractToolVersion("asd_asd-1.0.zip"))
        assertEquals("11.12120.12", devtool.extractToolVersion("test_test-11.12120.12.zip"))
        assertEquals("", devtool.extractToolVersion("test_test-.zip"))
    }

    @Test
    void testZippedTool_does_not_have_version_folder() {
        assertEquals(true, devtool.zipped_tool_have_version_folder(getPathFor("validtool-1.0.zip")))
        assertEquals(false, devtool.zipped_tool_have_version_folder(getPathFor("tool_does_not_have_version_folder-1.0.zip")))
    }

    @Test
    void testZippedTool_does_not_only_have_version_folder() {
        assertEquals(true, devtool.zipped_tool_have_version_folder_only(getPathFor("validtool-1.0.zip")))
        assertEquals(false, devtool.zipped_tool_have_version_folder_only(getPathFor("tool_does_not_only_have_version_folder-1.0.zip")))
    }

    @Test
    void testZippedTool_version_does_not_match() {
        assertEquals(true, devtool.zipped_tool_version_does_not_match(getPathFor("validtool-1.0.zip")))
        assertEquals(false, devtool.zipped_tool_version_does_not_match(getPathFor("tool_version_does_not_match-1.0.zip")))
    }

    String getPathFor(String testFile) {
        ClassLoader classLoader = getClass().getClassLoader()
        def path = new File(classLoader.getResource(testFile).getFile()).absolutePath
        return path
    }

    @Test
    void testVerifyToolName() {
        def s = "tool-1.0"
        assertTrue(devtool.verifyToolName(s))

        s = "CRAzytool-1.0.zip"
        assertTrue(devtool.verifyToolName(s))

        s = "crazy-tool-1.0.zip"
        assertFalse(devtool.verifyToolName(s))
    }

    @Test
    void testListAndInstallUpdates() {
        createSourceAndDestDirs()
        mockedGetVersionsForRemoteTool_versions = ["1.1.10"]

        devtool.listAndInstallUpdates(true)
    }

    def createSourceAndDestDirs() {
        new File("${mockedSrcDir}/ant/1.1.10").mkdirs()
        new File("${mockedDestDir}/ant/1.1.2").mkdirs()
    }

    @Test
    void testRemoveDirectoryFromPaths() throws Exception {
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            return // the removeDirectoryFromPaths only works on windows as of now
        }

        String path = "c:\\tools\\devtool\\1.18\\bin;c:\\tools\\git\\1.9.4\\bin;c:\\tools\\groovy\\2.3.6\\bin;c:\\tools\\maven\\3.2.3\\bin;c:\\tools\\jdk\\1.8.0.20.64\\bin;c:\\tools\\nodejs\\0.\n" +
                "10.29\\.;c:\\tools\\gradle\\2.2\\bin;c:\\Users\\johndoe\\AppData\\Roaming\\npm\\;c:\\tools\\ant\\1.7.0\\bin;"

        String expectedPath = "c:\\tools\\devtool\\1.18\\bin;c:\\tools\\git\\1.9.4\\bin;c:\\tools\\groovy\\2.3.6\\bin;c:\\tools\\maven\\3.2.3\\bin;c:\\tools\\jdk\\1.8.0.20.64\\bin;c:\\tools\\nodejs\\0.\n" +
                "10.29\\.;c:\\Users\\johndoe\\AppData\\Roaming\\npm\\;c:\\tools\\ant\\1.7.0\\bin;"

        assertEquals(expectedPath, devtool.removeDirectoryFromPaths("c:\\tools\\gradle", path))
    }

    @Test
    void testRemoveOldToolPaths() throws Exception {
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            return // the removeDirectoryFromPaths only works on windows as of now
        }
        def pathBefore = "some" + pathSepChar
        def pathAfter = "anotherpath" + sepChar + "place" + pathSepChar
        def userPath = pathBefore + mockedDestDir + sepChar + "toolToRemove" + sepChar + "1.0" + pathSepChar + pathAfter
        assertEquals(pathBefore + pathAfter, devtool.removeOldToolPaths("toolToRemove", userPath))

        def userPathWithExtraDir = pathBefore + mockedDestDir + sepChar + "toolToRemove" + sepChar + "1.0" + sepChar + "bin" + pathSepChar + "testpath"
        assertEquals("some;testpath", devtool.removeOldToolPaths("toolToRemove", userPathWithExtraDir))

        def userPathGit = "c:/somte;" + mockedDestDir + sepChar + "git" + sepChar + "1.0" + "\\bin;"
        assertEquals("c:/somte;", devtool.removeOldToolPaths("git", userPathGit))
    }

    private void setupMavenToolWithMultipleVersions() {
        def toolFolder = srcFolder.newFolder("maven")
        new File(toolFolder.getAbsolutePath() + File.separatorChar + "1.1.1").mkdir()
        new File(toolFolder.getAbsolutePath() + File.separatorChar + "1.1.10").mkdir()
        new File(toolFolder.getAbsolutePath() + File.separatorChar + "1.1.2").mkdir()
    }

    private ToolsRepository createMockedRepository() {
        def repository = new ToolsRepository() {
            @Override
            void uploadToolsAndVersionsFile(String userName, String password, File buildToolsAndVersionsFile) {

            }

            @Override
            void deleteOldToolsAndVersionsFile(String userName, String password) {

            }

            @Override
            void downloadToolsAndVersionsToTempfile(File tempFile) {

            }

            @Override
            String getToolsSourceDir() {
                return mockedSrcDir
            }

            @Override
            boolean isUserNameAndPasswordNeededForRepository() {
                return false
            }

            @Override
            void uploadTool(String toolName, String toolVersion, String toolpath, String username, char[] password) {

            }

            @Override
            String getRepositoryName() {
                return "unit test"
            }
        }
        repository
    }

}