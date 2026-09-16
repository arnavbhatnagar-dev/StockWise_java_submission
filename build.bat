@echo off
setlocal

if not exist "lib\mysql-connector-j-26.7.0.jar" (
    echo MySQL Connector/J was not found in the lib folder.
    exit /b 1
)

if exist out rmdir /s /q out
mkdir out

del /q sources.txt 2>nul
for /r src %%f in (*.java) do echo "%%f" >> sources.txt
javac -encoding UTF-8 -cp "lib\mysql-connector-j-26.7.0.jar" -d out @sources.txt
set BUILD_RESULT=%ERRORLEVEL%
del sources.txt

if not %BUILD_RESULT%==0 (
    echo Build failed.
    exit /b %BUILD_RESULT%
)

echo Build completed successfully.
