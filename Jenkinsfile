pipeline {
  agent any

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '15'))
    }

    //Bei den Parametern, habe ich mich für den choice-Parameter entschieden, weil ich mehrere Umgebungen zur Auswahl habe
    parameters {
        choice(description: '', name: 'env', choices: 'Testumgebung\nProduktionsumgebung')
    }
  //In dieser Stage, ist konfiguriert, wann und wie die Pipeline getriggered wird
  stages {
    stage('Stage 1') {
      steps {
        notifyBitBucket state: 'INPROGRESS'
      }
    }
    //In dieser Stage, ist der Deploymentschritt der Groovy Skripte definiert
    stage('Stage 2 - Deployment Groovy Skripts') {
    steps {
      script {
          deployRestEndPoint(name, auth, env = '') {
          println "deploying $name to $env"
          String url  = "https://${env}confluence.baloisenet.com/atlassian/rest/scriptrunner/latest/custom/customadmin/com.onresolve.scriptrunner.canned.common.rest.CustomRestEndpoint"
          String scriptText = filePath("src/RESTEndpoints/$name").readToString()
          String payload = """{"FIELD_INLINE_SCRIPT":"${StringEscapeUtils.escapeJavaScript(scriptText)}","canned-script":"com.onresolve.scriptrunner.canned.common.rest.CustomRestEndpoint"}"""
          http_post(url, auth, payload, 'application/json')
          }
        getXsrfToken(auth, env) {
    String url = "http://${env}confluence.baloisenet.com:8080/atlassian/secure/admin/EditAnnouncementBanner!default.jspa"
    HttpCookie.parse('Set-Cookie:' + http_head(url, auth)['Set-Cookie'].join(', ')).find { it.name == 'atlassian.xsrf.token' }.value
        }
      }
    }
    }
  }

 post {
        success {
            notifyBitBucket state: 'SUCCESSFUL'
        }

        fixed {
            mailTo status: 'SUCCESS', actuator: true, recipients: ['jannik.moser@baloise.ch'], logExtract: true
        }

        failure {
            notifyBitBucket state: 'FAILED', description: 'Der Pipelinebuild ist fehlgeschlagen'
            junit allowEmptyResults: true, testResults: '**/target/*-reports/TEST*.xml'
            mailTo status: 'FAILURE', actuator: true, recipients: ['jannik.moser@baloise.ch'], logExtract: true
        }
 }
}
