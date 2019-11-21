#!/bin/bash

BIN_PHPITO=bin
UTILS=ext/utils
SWT_GEN=ext/swt/gen
LAUNCHER_PHPITO=phpito.view.LauncherPHPito

# if [ -z `getconf LONG_BIT | grep 64` ] ; then
# 	SWT_JAR=ext/swt/swt-linux-x86.jar
# else
# 	SWT_JAR=ext/swt/swt-linux-x64.jar
# fi

case "$OSTYPE" in
	linux-gnu*) SWT_JAR=ext/swt/swt-linux-x64.jar;;
	# TODO test mac, cygwin and mingw
	darwin*) SWT_JAR=ext/swt/swt-mac-x64.jar; echo 'mac';;
	cygwin*) SWT_JAR=ext/swt/swt-win-x64.jar; echo 'cygwin';; # POSIX compatibility layer and Linux environment emulation for Windows
	msys*) SWT_JAR=ext/swt/swt-win-x64.jar; echo 'mingw';; # Lightweight shell and GNU utilities compiled for Windows (part of MinGW)
	*) echo "ERROR!!! Unknown OS type."; exit 1;;
esac

EXC="$0"

while [ -h "$EXC" ] ; do
  ls=`ls -ld "$EXC"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    EXC="$link"
  else
    EXC=`dirname "$EXC"`/"$link"
  fi
done

PHPITO_DIR=`dirname "$EXC"`

cd "$PHPITO_DIR" && java -cp "$BIN_PHPITO":"$UTILS"/*:"$SWT_GEN"/*:"$SWT_JAR" "$LAUNCHER_PHPITO" $1
