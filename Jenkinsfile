library identifier: 'WorkflowLibsShared@master', retriever: modernSCM(
	[$class: 'GitSCMSource', remote: 'https://git.balgroupit.com/CICD-DevOps/WorkflowLibsShared.git']
  )

import groovy.json.StringEscapeUtils
pipeline {
  agent any

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '15'))
    }

    //Bei den Parametern, habe ich mich für den choice-Parameter entschieden, weil ich mehrere Umgebungen zur Auswahl habe
    parameters {
        choice(description: '', name: 'env', choices: 'Testumgebung\nProduktionsumgebung')
        string(name: 'name', defaultValue: 'Name von RESTEndpoint', description: '')
    }

  //In dieser Stage, ist konfiguriert, wann und wie die Pipeline getriggered wird
  stages {
    stage('Stage 1') {
      steps {
        notifyBitBucket state: 'INPROGRESS'
      }
    }
    //In dieser Stage,wird der Deployment-Schritt aufgerufen 
    stage('Stage 2 - Deployment Groovy Skripts') {
    steps {
      deployRestEndPoint (params.name, '-test')
    }
    }
  }

  
//Postmethode in welcher die verschiedenen Szenarien der Pipeline definiert sind
 post {
//Mitteillung an Bitbucket 
        success {
            notifyBitBucket state: 'SUCCESSFUL'
        }
//Benachrichtigung wenn die Pipeline ohne Fehler durchläuft 
        fixed {
            emailext body: '''Hallo Jannik! Der Build der Pipeline ist vollständig durchgelaufen und die RESTEndpoints wurden deployed
            Mit freundlichen Grüssen''', subject: 'Automatisierte Verteilung von Atlassian Tool Updates Jira', to: 'jannik.moser@baloise.ch'
        }
//Benachrichtigung wenn die Pipeline nicht ohne Fehler durchläuft
        failure {
// Benachrichtigung an Bitbucket
            notifyBitBucket state: 'FAILED', description: 'Der Pipelinebuild ist fehlgeschlagen'
//Speichert einen JUnit Report unter als TEST.xml ab
            junit allowEmptyResults: true, testResults: '**/src/*reports/TEST*.xml'
            emailext attachLog: true, body: '''Hallo Jannik! Der Build der Pipeline ist fehlgeschlagen.
      Bitte überprüfe die Logfiles, welche sich im Anhang der Mail befinden.
      Mit freundlichen Grüssen''', subject: 'Automatisierte Verteilung von Atlassian Tool Updates Jira', to: 'jannik.moser@baloise.ch'
        }
 }
}



// Mehtode um die RESTEndpoints zu deployen
def deployRestEndPoint(name, env = '') {
println "deploying $name to $env"
String url = "https://${env}confluence.baloisenet.com/atlassian/rest/scriptrunner/latest/custom/customadmin/com.onresolve.scriptrunner.canned.common.rest.CustomRestEndpoint"
String scriptText = filePath("src/RESTEndpoints/$name").readToString()
String payload = """{"FIELD_INLINE_SCRIPT":"${StringEscapeUtils.escapeJavaScript(scriptText)}","canned-script":"com.onresolve.scriptrunner.canned.common.rest.CustomRestEndpoint"}"""
String auth = "admin_b037982:B037982"
echo 'ServerResponse ' + http_post(url, auth, payload, 'application/json')
}
          
def getXsrfToken(env) {
        String url = "http://${env}confluence.baloisenet.com:8080/atlassian/secure/admin/EditAnnouncementBanner!default.jspa"
        HttpCookie.parse('Set-Cookie:' + http_head(url)['Set-Cookie'].join(', ')).find { it.name == 'atlassian.xsrf.token' }.value
        }

