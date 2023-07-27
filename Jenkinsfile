pipeline {
  agent any
  stages {
    stage('RunProject') {
      parallel {
        stage('RunProject') {
          steps {
            bat 'clean test'
          }
        }

        stage('MavenVersion') {
          steps {
            bat 'mvn -q --non-recursive "-Dexec.executable=cmd" "-Dexec.args=/C echo ${project.version}" "org.codehaus.mojo:exec-maven-plugin:1.3.1:exec"'
          }
        }

      }
    }

  }
}