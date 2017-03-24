@echo off
cls
echo - Generating change logs...
echo.

for /f "tokens=1-3 delims=/-" %%a in ("%DATE%") do (set adate=%%c%%b%%a)
for /f "tokens=1-3 delims=/:/." %%a in ("%TIME%") do (set atime=%%a%%b%%c)

REM dbms = mssql / oracle
set "dbms=mssql"
set "url=jdbc:sqlserver://192.168.1.19:1433;database=PFFAHB"
set "username=PFFAHBAdmin"
set "password=pff@alhilal"
set "change_log_file=db-change-log-%adate%_%atime%.xml"

REM Derive the driver and classpath based on the dbms specified.
if %dbms% == mssql (
	set "driver=com.microsoft.sqlserver.jdbc.SQLServerDriver"
	set "classpath=snakeyaml-1.17.jar;sqljdbc4-4.0.2206.100.jar"
) else (
	set "driver=oracle.jdbc.driver.OracleDriver"
	set "classpath=snakeyaml-1.17.jar;ojdbc-6.jar"
)

java -jar liquibase-core-3.5.3.jar --driver=%driver% --classpath="%classpath%" --changeLogFile="%change_log_file%" --url="%url%" --username=%username% --password=%password% generateChangeLog

echo.
pause
exit
