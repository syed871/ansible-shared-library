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
            echo "Slack Channel: ${cfg.SLACK_CHANNEL_NAME}"
            echo "Approval Required: ${cfg.KEEP_APPROVAL_STAGE}"

        }

        stage('Clone Cassandra Tool') {

            dir('cassandra-tool') {

                git branch: 'abubakar',
                    url: 'https://github.com/OT-MyGurukulam/Ansible_35.git'

            }

            echo 'Cassandra Ansible tool cloned successfully'
        }
    }
}
