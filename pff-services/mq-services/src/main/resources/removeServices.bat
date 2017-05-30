@echo off

cls
echo ------------------------------------------------------------------------
echo Removing services...
echo ------------------------------------------------------------------------

set PFF_ROOT=%~dp0

echo - pennApps PFF Engine...
%PFF_ROOT%prunsrv.exe //DS//PFFENGINE

echo ------------------------------------------------------------------------
echo Process completed.
echo ------------------------------------------------------------------------
echo.
pause
