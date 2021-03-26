pipeline {
    agent {
        docker {
            image 'maven:3.3-jdk-8'
            args '-u root:root'
        }
    }
    stages {
        stage("Hello") {
            steps {
                sh "mvn --version"
            }
        }
    }
}