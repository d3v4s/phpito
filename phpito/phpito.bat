@echo OFF

set BIN_PHPITO=bin
set UTILS=ext\utils
set SWT_GEN=ext\swt\gen
set LAUNCHER_PHPITO=phpito.view.LauncherPHPito
set PHPITO_DIR=%~dp0

if exist "%SYSTEMDRIVE%\Program Files (x86)" (
	set SWT_JAR=ext\swt\swt_win32_x64.jar
	set JASWT_JAR=ext/jaswt/jaswt_linux_x64-1.0.jar
) else (
	set SWT_JAR=ext\swt\swt_win32_x86.jar
	set JASWT_JAR=ext/jaswt/jaswt_linux_x86-1.0.jar
)

cd %PHPITO_DIR% && java -cp %BIN_PHPITO%;%UTILS%\*;%SWT_GEN%\*;%SWT_JAR%;%JASWT_JAR% %LAUNCHER_PHPITO% %1
