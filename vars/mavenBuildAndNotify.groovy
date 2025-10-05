// vars/mavenBuildAndNotify.groovy

def call(Map config = [:]) {
    def repoUrl   = config.repoUrl ?: "https://github.com/Gagan543-ui/Jenkin_TSK.git"
    def branch    = config.branch ?: "main"
    def mvnCmd    = config.mvnCmd ?: "clean package"
    def notifyTo  = config.notifyTo ?: "console"
    def buildStatus = "SUCCESS"
    def startTime = System.currentTimeMillis()

    stage('Checkout') {
        echo "📦 Cloning repository: ${repoUrl} (branch: ${branch})"
        git branch: branch, url: repoUrl
    }

    stage('Maven Build') {
        echo "🚀 Running Maven command: mvn ${mvnCmd}"
        try {
            sh "mvn ${mvnCmd}"
        } catch (err) {
            buildStatus = "FAILURE"
            error "❌ Build failed: ${err}"
        }
    }

    // After build
    def duration = (System.currentTimeMillis() - startTime) / 1000
    def jobUrl = env.BUILD_URL ?: "N/A"

    def msg = """
    🔔 *Jenkins Maven Build Notification*
    📂 *Repository:* ${repoUrl}
    🌿 *Branch:* ${branch}
    ⚙️ *Command:* mvn ${mvnCmd}
    📊 *Status:* ${buildStatus}
    ⏱ *Duration:* ${duration} sec
    🔗 *Build URL:* ${jobUrl}
    """.stripIndent()

    def colorCode = (buildStatus == "SUCCESS") ? "#2eb886" : "#e01e5a"

    if (notifyTo == "console") {
        echo msg
    } else if (notifyTo == "slack") {
        slackSend(
            channel: '#build-notifications',
            color: colorCode,
            message: msg
        )
    } else if (notifyTo == "telegram") {
        echo "📩 Sending Telegram notification (to be implemented)"
    } else {
        echo "⚠️ Unknown notification type: ${notifyTo}"
    }
}
