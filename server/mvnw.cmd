@echo off
set DIR=%~dp0
set DIR=%DIR:~0,-1%
java -cp "%DIR%\.mvn\wrapper\maven-wrapper.jar" -Dmaven.multiModuleProjectDirectory="%DIR%" org.apache.maven.wrapper.MavenWrapperMain %*
