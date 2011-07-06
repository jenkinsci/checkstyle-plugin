rm -rf $HUDSON_HOME/plugins/checkstyle*

mvn install || { echo "Build failed"; exit 1; }

cp -f target/*.hpi $HUDSON_HOME/plugins/

cd $HUDSON_HOME
java -jar jenkins.war
