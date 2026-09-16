@echo off
setlocal

call build.bat
if errorlevel 1 exit /b 1

java -cp "out;lib\mysql-connector-j-26.7.0.jar" com.stockwise.Main
