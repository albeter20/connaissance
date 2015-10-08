echo "building online banking application...."
mvn clean package
echo "now copy files..."
copy target\onlinebanking.war %catalina_home%\webapp\