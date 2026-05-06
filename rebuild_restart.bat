@echo off
setlocal

REM Base: dossier du script (racine FanfareHub)
set "APP_HOME=%~dp0"
if "%APP_HOME:~-1%"=="\" set "APP_HOME=%APP_HOME:~0,-1%"

REM Tomcat = dossier parent de webapps
for %%I in ("%APP_HOME%\..\..") do set "TOMCAT_HOME=%%~fI"
set "SRC_DIR=%APP_HOME%\WEB-INF\classes"
set "WEBINF_LIB=%APP_HOME%\WEB-INF\lib"

set "CATALINA_HOME=%TOMCAT_HOME%"
set "CATALINA_BASE=%TOMCAT_HOME%"

set "LIB_SERVLET=%TOMCAT_HOME%\lib\servlet-api.jar"
if not exist "%LIB_SERVLET%" set "LIB_SERVLET=%TOMCAT_HOME%\lib\jakarta.servlet-api.jar"

if not exist "%SRC_DIR%" (
  echo Dossier source introuvable: "%SRC_DIR%"
  exit /b 1
)

if not exist "%LIB_SERVLET%" (
  echo Aucun JAR servlet trouve dans "%TOMCAT_HOME%\lib"
  exit /b 1
)

REM Classpath: servlet-api + classes + jars de WEB-INF/lib (ex: postgresql)
set "CP=%LIB_SERVLET%;%SRC_DIR%"
if exist "%WEBINF_LIB%" set "CP=%CP%;%WEBINF_LIB%\*"

echo [1/3] Compilation des fichiers Java...
echo Utilisation du classpath: "%CP%"
for /R "%SRC_DIR%" %%f in (*.java) do (
  javac -encoding UTF-8 -cp "%CP%" -d "%SRC_DIR%" "%%f"
  if errorlevel 1 (
    echo Erreur de compilation sur: %%f
    exit /b 1
  )
)

echo [2/3] Arret Tomcat...
call "%TOMCAT_HOME%\bin\shutdown.bat"

timeout /t 3 /nobreak >nul

echo [3/3] Demarrage Tomcat...
call "%TOMCAT_HOME%\bin\startup.bat"
if errorlevel 1 (
  echo Echec du demarrage Tomcat.
  exit /b 1
)

echo Termine.
endlocal
exit /b 0
