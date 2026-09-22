def call() {
    pipeline {
        agent any

        stages {
            stage('Test Shared Library') {
                steps {
                    echo "Ansible Shared Library loaded successfully"
                }
            }
        }
    }
}
