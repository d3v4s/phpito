#!/bin/bash

BIN_PHPITO="bin"
UTILS="ext/utils"
SWT_GEN="ext/swt/gen"
LUNCHER_PHPITO="it.phpito.view.LuncherPHPito"

[ -z `getconf LONG_BIT | grep 64` ]  && SWT_JAR="ext/swt/swt_linux_gtk_x86.jar" || SWT_JAR="ext/swt/swt_linux_gtk_x64.jar"

java -cp $BIN_PHPITO:$UTILS/*:$SWT_GEN/*:$SWT_JAR $LUNCHER_PHPITO

#java -cp /home/hxs/git-work/phpito/phpito/bin:
#/home/hxs/git-work/phpito/phpito/ext/utils/as-utils-1.0.jar:
#/home/hxs/git-work/phpito/phpito/ext/swt/swing2swt.jar:
#/home/hxs/git-work/phpito/phpito/ext/gen/com.ibm.icu_63.1.0.v20181030-1705.jar:
#/home/hxs/git-work/phpito/phpito/ext/gen/org.eclipse.core.commands_3.9.200.v20180827-1727.jar:
#/home/hxs/git-work/phpito/phpito/ext/gen/org.eclipse.core.runtime_3.15.100.v20181107-1343.jar:
#/home/hxs/git-work/phpito/phpito/ext/gen/org.eclipse.equinox.common_3.10.200.v20181021-1645.jar:
#/home/hxs/git-work/phpito/phpito/ext/gen/org.eclipse.equinox.registry_3.8.200.v20181008-1820.jar:
#/home/hxs/git-work/phpito/phpito/ext/gen/org.eclipse.jface_3.15.0.v20181123-1505.jar:
#/home/hxs/git-work/phpito/phpito/ext/gen/org.eclipse.jface.text_3.15.0.v20181119-1708.jar:
#/home/hxs/git-work/phpito/phpito/ext/gen/org.eclipse.osgi_3.13.200.v20181130-2106.jar:
#/home/hxs/git-work/phpito/phpito/ext/gen/org.eclipse.text_3.8.0.v20180923-1636.jar:
#/home/hxs/git-work/phpito/phpito/ext/gen/org.eclipse.ui.forms_3.7.400.v20181123-1505.jar:
#/home/hxs/git-work/phpito/phpito/ext/gen/org.eclipse.ui.workbench_3.112.100.v20181127-1518.jar:
#/home/hxs/git-work/phpito/phpito/ext/swt/swt_linux_gtk_x64.jar:
#/home/hxs/git-work/phpito/phpito/ext/utils/jun-1.0.jar
#it.phpito.view.LuncherPHPito
