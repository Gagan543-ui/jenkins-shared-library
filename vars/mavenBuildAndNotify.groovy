def call(Map config = [:]) {
    def repoUrl   = config.repoUrl ?: "https://github.com/Gagan543-ui/Jenkin_TSK.git"
    def branch    = config.branch ?: "main"
    def mvnCmd    = config.mvnCmd ?: "clean package"
    def notifyTo  = config.notifyTo ?: "console"
    def buildStatus = "SUCCESS"
    def startTime = System.currentTimeMillis()

    pipeline {
        agent any

        stages {
            stage('Checkout') {
                steps {
                    script {
                        echo "📦 Cloning repository: ${repoUrl} (branch: ${branch})"
                        git branch: branch, url: repoUrl
                    }
                }
            }

            stage('Maven Build') {
                steps {
                    script {
                        echo "🚀 Running Maven command: mvn ${mvnCmd}"
                        try {
                            sh "mvn ${mvnCmd}"
                        } catch (err) {
                            buildStatus = "FAILURE"
                            error "❌ Build failed: ${err}"
                        }
                    }
                }
            }
        }

        post {
            always {
                script {
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

                    if (notifyTo == "console") {
                        echo msg
                    } else if (notifyTo == "slack") {
                        slackSend(
                            channel: '#build-notifications',
                            color: (buildStatus == "SUCCESS") ? "#2eb886" : "#e01e5a",
                            message: msg
                        )
                    } else {
                        echo "⚠️ Unknown notification type: ${notifyTo}"
                    }
                }
            }
        }
    }
}
