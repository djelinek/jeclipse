@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')
@Grab(group='oauth.signpost', module='signpost-core', version='1.2.1.2')
@Grab(group='oauth.signpost', module='signpost-commonshttp4', version='1.2.1.2')

import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*

def username = System.console().readLine "Username: "
def password = System.console().readPassword "Password: "

def owner = "apodhrad"
def repo = "jeclipse"

def client = new RESTClient("https://api.github.com/repos/${owner}/${repo}/").with {
	headers.'User-Agent' = 'Mozilla/5.0'

	if (username && password) {
		headers['Authorization'] = 'Basic '+"${username}:${password}".getBytes('iso-8859-1').encodeBase64()
	}

	it
}

def issue = System.console().readLine "Issue: "
def message = [ issue: issue.toString(), head: username.toString() + ":JECLIPSE-" + issue.toString(), base: "master" ] 

def response =  client.post(
    path: "pulls", 
    body: message,
    contentType: JSON,
    requestContentType: JSON
)

println response.headers.Status
