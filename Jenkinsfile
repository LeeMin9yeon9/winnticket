pipeline {
    agent any

    tools {
        jdk 'JDK-17'  // Jenkins > Global Tool Configuration에서 설정한 JDK 이름
    }

    environment {
        DEPLOY_SERVER = '13.209.91.167'
        DEPLOY_USER   = 'ubuntu'
        DEPLOY_PATH   = '/home/ubuntu/winnticket-api'
        JAR_NAME      = 'winnticket.jar'
        SSH_CREDENTIALS_ID = 'winnticket-ssh-key'
        SPRING_PROFILE = 'dev'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 소스코드 체크아웃...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '🔨 Gradle 빌드...'
                sh 'java -version'
                sh 'chmod +x gradlew'
                sh './gradlew clean bootJar -x test'
            }
        }

        stage('Deploy') {
            steps {
                echo '🚀 개발서버에 배포...'
                sshagent(credentials: [env.SSH_CREDENTIALS_ID]) {
                    // 1. 배포 디렉토리 생성
                    sh """
                        ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_SERVER} '
                            mkdir -p ${DEPLOY_PATH}
                        '
                    """

                    // 2. 기존 jar 백업
                    sh """
                        ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_SERVER} '
                            if [ -f ${DEPLOY_PATH}/${JAR_NAME} ]; then
                                cp ${DEPLOY_PATH}/${JAR_NAME} ${DEPLOY_PATH}/${JAR_NAME}.backup
                            fi
                        '
                    """

                    // 3. 새 jar 전송
                    sh """
                        scp -o StrictHostKeyChecking=no build/libs/*.jar ${DEPLOY_USER}@${DEPLOY_SERVER}:${DEPLOY_PATH}/${JAR_NAME}
                    """

                    // 4. 애플리케이션 재시작
                    sh """
                        ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_SERVER} '
                            # 기존 프로세스 종료
                            PID=\$(pgrep -f "${JAR_NAME}" || true)
                            if [ -n "\$PID" ]; then
                                echo "기존 프로세스 종료: \$PID"
                                kill \$PID
                                sleep 5
                                # 강제 종료 (아직 살아있으면)
                                kill -9 \$PID 2>/dev/null || true
                            fi

                            # 새 프로세스 시작
                            cd ${DEPLOY_PATH}
                            nohup java -jar -Dspring.profiles.active=${SPRING_PROFILE} \
                                -Xms512m -Xmx1024m \
                                ${JAR_NAME} > app.log 2>&1 &

                            # 시작 확인 (최대 30초 대기)
                            for i in \$(seq 1 30); do
                                sleep 1
                                if curl -sf http://localhost:8080/api/common/status > /dev/null 2>&1; then
                                    echo "✅ 애플리케이션 시작 완료 (\${i}초)"
                                    exit 0
                                fi
                            done
                            echo "⚠️ 시작 확인 타임아웃 (로그 확인 필요)"
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo '✅ 백엔드 배포 성공!'
        }
        failure {
            echo '❌ 백엔드 배포 실패!'
            // 롤백: 백업 jar로 복원
            sshagent(credentials: [env.SSH_CREDENTIALS_ID]) {
                sh """
                    ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_SERVER} '
                        if [ -f ${DEPLOY_PATH}/${JAR_NAME}.backup ]; then
                            # 실패한 프로세스 종료
                            PID=\$(pgrep -f "${JAR_NAME}" || true)
                            if [ -n "\$PID" ]; then kill -9 \$PID 2>/dev/null || true; fi

                            # 백업으로 복원
                            mv ${DEPLOY_PATH}/${JAR_NAME}.backup ${DEPLOY_PATH}/${JAR_NAME}
                            cd ${DEPLOY_PATH}
                            nohup java -jar -Dspring.profiles.active=${SPRING_PROFILE} \
                                ${JAR_NAME} > app.log 2>&1 &
                            echo "🔄 롤백 완료"
                        fi
                    ' || true
                """
            }
        }
    }
}
