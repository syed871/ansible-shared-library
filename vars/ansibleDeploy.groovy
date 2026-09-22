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
        echo "Action Message: ${cfg.ACTION_MESSAGE}"
    }

    stage('Clone Cassandra Tool') {

        dir('cassandra-tool') {

            sh '''
                git clone -b abubakar git@github.com:OT-MyGurukulam/Ansible_35.git .
            '''
        }

        echo 'Cassandra Ansible tool cloned successfully'
    }

    if (cfg.KEEP_APPROVAL_STAGE == 'true') {

        stage('User Approval') {

            input message: "Do you want to deploy Cassandra to ${cfg.ENVIRONMENT}?",
                  ok: 'Deploy'
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

        echo "Slack Channel: ${cfg.SLACK_CHANNEL_NAME}"
        echo "Message: ${cfg.ACTION_MESSAGE}"

    }
}
```

}
