@echo off
cls
echo Generating Change Logs
echo ~~~~~~~~~~~~~~~~~~~~~~
echo.
echo Generate the change log to create the existing database schema. Note that this does not export the following types of objects:
echo - Stored procedures, functions, packages
echo - Triggers
echo.

REM Derive the date and time parts of today to use in the change log file name.
for /f "tokens=1-3 delims=/-" %%a in ("%DATE%") do (set adate=%%c%%b%%a)
for /f "tokens=1-3 delims=/:/." %%a in ("%TIME%") do (set atime=%%a%%b%%c)

REM dbms
echo Specify the DBMS
echo ----------------
echo Change logs can be generated for the following databases:
echo - 'mssql'     Microsoft SQL Server (default)
echo - 'oracle'    Oracle
echo.
set "dbms=mssql"
set /p dbms=Enter value for dbms: 
echo.

REM hostname
echo Specify the Hostname
echo --------------------
echo Host system for the database.
echo.
set /p hostname=Enter the hostname: 
echo.

REM port
echo Specify the Port
echo ----------------
echo Listener port.
echo.
if %dbms% == mssql (
	set "port=1433"
) else (
	set "port=1521"
)
set /p port=Enter the port [%port%]: 
echo.

REM sid / database
if %dbms% == mssql (
	echo Specify the Database Name
	echo -------------------------
) else (
	echo Specify the SID / Service Name
	echo ------------------------------
)
echo Database name.
echo.
if %dbms% == mssql (
	set /p database=Enter the database: 
) else (
	set /p database=Enter the SID: 
)
echo.

REM username
echo Specify the Username
echo --------------------
echo Name of the database user for the connection.
echo.
set /p username=Enter the username: 
echo.

REM password
echo Specify the Password
echo --------------------
echo Password associated with the specified database user.
echo.
set /p password=Enter the password: 
echo.

REM Preapare the required attributes based on the user input.
set "change_log_file=out/db-change-log-%adate%_%atime%.xml"
if %dbms% == mssql (
	set "driver=com.microsoft.sqlserver.jdbc.SQLServerDriver"
	set "classpath=lib/snakeyaml-1.17.jar;lib/pennapps-liquibase-ext-1.3.0.jar;lib/sqljdbc4-4.0.2206.100.jar"
	set "url=jdbc:sqlserver://%hostname%:%port%;database=%database%"
) else (
	set "driver=oracle.jdbc.driver.OracleDriver"
	set "classpath=lib/snakeyaml-1.17.jar;lib/pennapps-liquibase-ext-1.3.0.jar;lib/ojdbc-6.jar"
	set "url=jdbc:oracle:thin:@%hostname%:%port%:%database%"
)

REM Execute the command
java -jar liquibase-core-3.5.3.jar --driver=%driver% --classpath="%classpath%" --changeLogFile="%change_log_file%" --url="%url%" --username=%username% --password=%password% generateChangeLog

echo.
pause
exit
