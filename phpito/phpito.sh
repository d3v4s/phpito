#!/bin/bash

BIN_PHPITO=bin
UTILS=ext/utils
SWT_GEN=ext/swt/gen
LAUNCHER_PHPITO=it.phpito.view.LauncherPHPito

[ -z `getconf LONG_BIT | grep 64` ] && SWT_JAR=ext/swt/swt_linux_gtk_x86.jar || SWT_JAR=ext/swt/swt_linux_gtk_x64.jar

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

cd $PHPITO_DIR && java -cp "$BIN_PHPITO":"$UTILS"/*:"$SWT_GEN"/*:"$SWT_JAR" "$LAUNCHER_PHPITO"
