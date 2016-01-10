import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.*
import groovy.servlet.*

// 9.2 is last version that uses J7
@Grab(group='org.eclipse.jetty.aggregate', module='jetty-all', version='9.2.14.v20151106')
def startJetty() {
    def jetty = new Server(9090)
    
    def handler = new ServletContextHandler(ServletContextHandler.SESSIONS)
    handler.contextPath = '/'
    handler.resourceBase = '.'
    handler.addServlet(GroovyServlet, '*.groovy')
    def filesHolder = handler.addServlet(DefaultServlet, '/')
    filesHolder.setInitParameter('resourceBase', './static')
 
    jetty.handler = handler
    jetty.start()
}

println "Starting Jetty, press Ctrl+C to stop."
startJetty()