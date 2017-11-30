echo off

set /p install_path="Enter devtool install folder (e.g. c:\tools)# "
echo %install_path% XXX
if "%install_path%" == "" (
	setx devtool_tools c:\tools
	set devtool_tools c:\tools
) else (
	setx devtool_tools "%install_path%"
	set devtool_tools "%install_path%"
)

mkdir "%install_path%"
cd installer\bin
call devtool -install devtool

pause