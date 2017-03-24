@echo off

cls
echo ------------------------------------------------------------------------
echo Installing services...
echo ------------------------------------------------------------------------

set PFF_ROOT=%~dp0
set PFF_JVM_ROOT="C:\Program Files\Java\jre7\bin\server\jvm.dll"
set /p PFF_JVM_ROOT=Specify the full path to the jvm.dll ["C:\Program Files\Java\jre7\bin\server\jvm.dll"] : 
echo.

echo - pennApps PFF Engine...
start /WAIT /MIN %PFF_ROOT%services\installPFFE.bat "%PFF_ROOT%" %PFF_JVM_ROOT%

echo ------------------------------------------------------------------------
echo Process completed.
echo ------------------------------------------------------------------------
echo.
pause
