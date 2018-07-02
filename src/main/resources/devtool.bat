@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  devtool startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and DEVTOOL_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

set JAVA_EXE="%DIRNAME%\\..\\jre\\bin\\java.exe"

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\\lib\\devtool-1.38.jar;%APP_HOME%\\lib\\groovy-all-2.4.15.jar;%APP_HOME%\\lib\\commons-cli-1.4.jar;%APP_HOME%\\lib\\ant-1.9.4.jar;%APP_HOME%\\lib\\jansi-1.16.jar;%APP_HOME%\\lib\\ant-launcher-1.9.4.jar

@rem Execute devtool
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %DEVTOOL_OPTS%  -classpath "%CLASSPATH%" com.nordea.devtool.Devtool %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable DEVTOOL_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%DEVTOOL_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
