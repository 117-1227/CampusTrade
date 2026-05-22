@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch Script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM ----------------------------------------------------------------------------

@if "%DEBUG%"=="" @echo off
@setlocal

set "MAVEN_PROJECTBASEDIR=%~dp0"
set "WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"

if not defined JAVA_HOME (
  for %%i in (java.exe) do set "JAVACMD=%%~$PATH:i"
) else (
  set "JAVACMD=%JAVA_HOME%\bin\java.exe"
)

if not exist "%JAVACMD%" (
  echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
  exit /b 1
)

if not exist "%WRAPPER_JAR%" (
  echo ERROR: Could not find Maven wrapper JAR at %WRAPPER_JAR%
  exit /b 1
)

set "MAVEN_OPTS=%MAVEN_OPTS% -Xmx1024m"

"%JAVACMD%" %MAVEN_OPTS% -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %*
