rm -rf $HUDSON_HOME/plugins/checkstyle*

mvn install
cp -f target/*.hpi $HUDSON_HOME/plugins/

cd $HUDSON_HOME
java -jar hudson.war
