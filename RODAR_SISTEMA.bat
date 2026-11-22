@echo off
chcp 65001 >nul
title FinanControl - Inicializando Sistema...
echo ============================================================
echo   Iniciando o FinanControl (JavaFX + H2 Database)...
echo   (Na primeira vez pode levar alguns segundos baixando bibliotecas)
echo ============================================================
cd /d "%~dp0"
java "-Dmaven.multiModuleProjectDirectory=%~dp0" -cp "%~dp0.mvn\wrapper\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain javafx:run
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Ocorreu um erro ao iniciar o sistema.
    pause
)
