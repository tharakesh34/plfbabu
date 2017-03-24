set PFF_ROOT=%~dp1
set PFF_JVM_ROOT=%~2
set SERVICE_NAME=PFFENGINE

set PR_DESCRIPTION=The PFF Engine will listen requests from the other channels and process / responds to them.
set PR_DISPLAYNAME=pennApps PFF Engine

set PR_INSTALL=%PFF_ROOT%prunsrv.exe
set PR_STARTUP=auto
set PR_JVM=%PFF_JVM_ROOT%

set PR_CLASSPATH=mq-services.jar
set PR_STARTMODE=jvm
set PR_STARTCLASS=com.pennant.pff.channelsinterface.PFFEngine
set PR_STARTPARAMS=start
set PR_STOPMODE=jvm
set PR_STOPCLASS=com.pennant.pff.channelsinterface.PFFEngine
set PR_STOPPARAMS=stop

set PR_LOGPATH=%PFF_ROOT%logs
set PR_LOGPREFIX=%SERVICE_NAME%
set PR_LOGLEVEL=Info
set PR_STDOUTPUT=auto
set PR_STDERROR=auto
set PR_PIDFILE=%SERVICE_NAME%.pid

%PFF_ROOT%prunsrv.exe //IS//%SERVICE_NAME%
exit
