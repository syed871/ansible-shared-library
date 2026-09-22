def call() {

    node {

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
            echo "Approval Required: ${cfg.KEEP_APPROVAL_STAGE}"
        }

        stage('Clone Cassandra Tool') {

            dir('cassandra-tool') {

                sh '''
                    rm -rf *
                    git clone -b abubakar git@github.com:OT-MyGurukulam/Ansible_35.git .
                '''
            }

            echo 'Cassandra Ansible tool cloned successfully'
        }

        if (cfg.KEEP_APPROVAL_STAGE?.toBoolean()) {

            stage('User Approval') {

                input message: "Deploy ${cfg.ENVIRONMENT} environment?",
                      ok: 'Proceed'
            }
        }

        stage('Playbook Execution') {

            dir("cassandra-tool/${cfg.CODE_BASE_PATH}") {

                sh '''
                    ansible-playbook -i inventory site.yml
                '''
            }
        }

        stage('Notification') {

            echo "${cfg.ACTION_MESSAGE}"
            echo "Slack Channel: ${cfg.SLACK_CHANNEL_NAME}"
        }
    }
}
