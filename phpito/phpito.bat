@echo OFF

set BIN_PHPITO=bin
set UTILS=ext\utils
set SWT_GEN=ext\swt\gen
set LAUNCHER_PHPITO=phpito.view.LauncherPHPito
set PHPITO_DIR=%~dp0

if exist "%SYSTEMDRIVE%\Program Files (x86)" (
	set SWT_JAR=ext\swt\swt-win-x64.jar
) else (
	set SWT_JAR=ext\swt\swt-win-x86.jar
)

cd "%PHPITO_DIR%" && java -cp "%BIN_PHPITO%";"%UTILS%\*";"%SWT_GEN%\*";"%SWT_JAR%" %LAUNCHER_PHPITO% %1
