rm -rf $JENKINS_HOME/plugins/checkstlye*

mvn clean install
cp -f target/*.hpi $JENKINS_HOME/plugins/

cd $JENKINS_HOME
./go.sh
