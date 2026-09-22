```groovy
def call() {

    node {

        // =========================
        // Read Configuration
        // =========================

        def config = libraryResource('deployment.conf')
        def cfg = [:]

        config.split('\n').each { line ->
            line = line.trim()

            if (line && !line.startsWith('#')) {

                def parts = line.split('=', 2)

                if (parts.size() == 2) {
                    cfg[parts[0].trim()] = parts[1].trim()
                }
            }
        }

        stage('Read Configuration') {

            echo "Environment: ${cfg.ENVIRONMENT}"
            echo "Code Base Path: ${cfg.CODE_BASE_PATH}"
            echo "Slack Channel: ${cfg.SLACK_CHANNEL_NAME}"
            echo "Approval Required: ${cfg.KEEP_APPROVAL_STAGE}"
            echo "Action Message: ${cfg.ACTION_MESSAGE}"
        }


        // =========================
        // Clone Cassandra Tool
        // =========================

        stage('Clone Cassandra Tool') {

            dir('cassandra-tool') {

                sh '''
                    git clone -b abubakar git@github.com:OT-MyGurukulam/Ansible_35.git .
                '''
            }

            echo 'Cassandra Ansible tool cloned successfully'
        }


        // =========================
        // User Approval
        // =========================

        if (cfg.KEEP_APPROVAL_STAGE == 'true') {

            stage('User Approval') {

                input message: "Do you want to deploy Cassandra to ${cfg.ENVIRONMENT}?",
                      ok: 'Deploy'
            }
        }


        // =========================
        // Playbook Execution
        // =========================

        stage('Playbook Execution') {

            dir("cassandra-tool/${cfg.CODE_BASE_PATH}") {

                sh '''
                    ansible-playbook -i inventory site.yml
                '''
            }
        }


        // =========================
        // Notification
        // =========================

        stage('Notification') {

            echo "Slack Channel: ${cfg.SLACK_CHANNEL_NAME}"
            echo "Message: ${cfg.ACTION_MESSAGE}"

            // Slack notification will be added here
        }
    }
}
```
