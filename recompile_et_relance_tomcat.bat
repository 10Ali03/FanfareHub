@echo off
setlocal EnableExtensions EnableDelayedExpansion

REM ===============================================================
REM Recompile les classes Java du projet puis redemarre Tomcat
REM ===============================================================

set "SCRIPT_DIR=%~dp0"
set "APP_DIR=%SCRIPT_DIR%"
set "CLASSES_DIR=%APP_DIR%WEB-INF\classes"
set "LIB_DIR=%APP_DIR%WEB-INF\lib"
set "SOURCES_FILE=%TEMP%\fanfarehub_sources_%RANDOM%.txt"

if not exist "%CLASSES_DIR%" (
  echo [ERREUR] Dossier introuvable: "%CLASSES_DIR%"
  pause
  exit /b 1
)

if "%CATALINA_HOME%"=="" (
  if exist "C:\Program Files\Apache Software Foundation\Tomcat 10.1\bin\catalina.bat" (
    set "CATALINA_HOME=C:\Program Files\Apache Software Foundation\Tomcat 10.1"
  ) else if exist "C:\Program Files\Apache Software Foundation\Tomcat 11.0\bin\catalina.bat" (
    set "CATALINA_HOME=C:\Program Files\Apache Software Foundation\Tomcat 11.0"
  )
)

if not exist "%CATALINA_HOME%\bin\catalina.bat" (
  echo [ERREUR] Tomcat introuvable.
  echo Definis CATALINA_HOME.
  pause
  exit /b 1
)

where javac >nul 2>nul
if errorlevel 1 (
  echo [ERREUR] javac introuvable. Installe un JDK et ajoute-le au PATH.
  pause
  exit /b 1
)

echo [1/3] Recherche des fichiers Java...
dir /s /b "%CLASSES_DIR%\*.java" > "%SOURCES_FILE%"

for %%A in ("%SOURCES_FILE%") do (
  if %%~zA EQU 0 (
    echo [INFO] Aucun fichier .java trouve dans "%CLASSES_DIR%".
    del "%SOURCES_FILE%" >nul 2>nul
    goto RESTART_TOMCAT
  )
)

echo [2/3] Compilation...
set "CP=%CLASSES_DIR%;%LIB_DIR%\*;%CATALINA_HOME%\lib\servlet-api.jar"
javac -encoding UTF-8 -cp "%CP%" -d "%CLASSES_DIR%" @"%SOURCES_FILE%"

if errorlevel 1 (
  echo [ERREUR] Echec compilation.
  del "%SOURCES_FILE%" >nul 2>nul
  pause
  exit /b 1
)

del "%SOURCES_FILE%" >nul 2>nul

:RESTART_TOMCAT
echo [3/3] Redemarrage de Tomcat...
call "%CATALINA_HOME%\bin\catalina.bat" stop
timeout /t 3 /nobreak >nul
call "%CATALINA_HOME%\bin\catalina.bat" start

if errorlevel 1 (
  echo [ERREUR] Impossible de redemarrer Tomcat.
  pause
  exit /b 1
)

echo.
echo [OK] Compilation terminee et Tomcat redemarre.
echo.
echo Teste ensuite :
echo http://localhost:8080/FanfareHub/vue/connexion.jsp
echo.

pause
exit /b 0