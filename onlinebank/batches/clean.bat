set app_home=C:\Users\395168\git-workingdir\kingshukpocrepo\onlinebank
rmdir/S %CATALINA_HOME%\webapps\onlinebank
del %CATALINA_HOME%\webapps\onlinebank.war
rmdir %CATALINA_HOME%\work\Catalina\localhost\onlinebank
copy %app_home%\target\onlinebank.war %CATALINA_HOME%\webapps\
