project( RapidSmith )
cmake_minimum_required( VERSION 2.8.11 )

set( JAVA /usr/local/opt/java/bin/java )
set( LIB_DIR ./lib/thin )
set( QT_DIR  /usr/local/lib )
set( JAR_FILE target/rapidSmith-1.0-SNAPSHOT-jar-with-dependencies.jar )
set( RAPIDSMITH_PATH . )
set( JAVA_PACKAGE edu.byu.ece.rapidSmith )
set( JAVA_OPTS -XstartOnFirstThread -Djava.library.path=${LIB_DIR}:${QT_DIR} )

find_package( Qt6 REQUIRED )
find_package( Qt6Core REQUIRED )
find_package( Qt6Gui REQUIRED )
find_package( Qt6Widgets REQUIRED )

# TODO: Add dependencies to existing targets
# - jar: `mvn package`
# - qtjambi fat libs: `mvn dependency:copy-dependencies`
# - qtjambi thin libs in LIB_DIR: `lipo <fat_lib> -extract <arch> -output <output_file>`
# - qtjambi thin libs symlinks in LIB_DIR:
#     libQtJambi.X.jnilib -> libQtJambi.X.X.X.jnilib
#     QtCore.framework -> /usr/local/lib/QtCore.framework/ 
#     QtGui.framework -> /usr/local/lib/QtGui.framework/ 
#     QtWidgets.framework -> /usr/local/lib/QtWidgets.framework/ 

add_custom_target( javadoc mvn javadoc:javadoc )

add_custom_target( device_browse RAPIDSMITH_PATH=${RAPIDSMITH_PATH} ${JAVA} ${JAVA_OPTS} -cp ${JAR_FILE} ${JAVA_PACKAGE}.device.browser.DeviceBrowser)

