def call() {

    def config = readFile('config/deployment.conf')
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

}
