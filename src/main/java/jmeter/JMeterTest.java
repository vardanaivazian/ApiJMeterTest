package jmeter;

import java.util.logging.Level;
import java.util.logging.Logger;
import jmeter.bean.AssertionBean;
import jmeter.bean.AssertionsBean;
import jmeter.bean.Endpoint;
import jmeter.bean.Parameter;
import jmeter.bean.Parameters;
import jmeter.service.EndpointService;
import jmeter.service.EndpointServiceImpl;
import jmeter.service.JMeterService;
import jmeter.service.JMeterServiceImpl;
import jmeter.service.PropertyService;
import org.apache.jmeter.assertions.ResponseAssertion;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jorphan.collections.HashTree;

public class JMeterTest {

    private static final Logger LOGGER = Logger.getLogger( JMeterTest.class.getName() );
    private static final PropertyService PROP = PropertyService.INSTANCE;

    public static void main( String[] argv ) {

        EndpointService endpointService = new EndpointServiceImpl();
        JMeterService jMeterService = new JMeterServiceImpl();

        StandardJMeterEngine engine = jMeterService.engine();

        LoopController loopController = jMeterService.loop( Integer.parseInt( PROP.get( "test.loops" ) ), true );
        
        ThreadGroup threadGroup = jMeterService.threadGroup( "Sample Thread Group", Integer.parseInt( PROP.get( "test.numThreads" ) ), Integer.parseInt( PROP.get( "test.rampUp" ) ) );
        threadGroup.setSamplerController( loopController );

        TestPlan testPlan = jMeterService.testPlan( "Create JMeter Script From Java Code" );

        HashTree testPlanTree = jMeterService.testPlanTree();
        
        testPlanTree.add( testPlan );

        HashTree threadGroupHashTree = testPlanTree.add( testPlan, threadGroup );

        for( Endpoint endpoint : endpointService.getEndpoints() ) {
            
            HTTPSamplerProxy httpSampler = jMeterService.httpSamplerProxy();
            httpSampler.setConnectTimeout(endpoint.getConnectTimeout() == null ? PROP.get( "test.default.connectTimeout" ) : endpoint.getConnectTimeout());
            httpSampler.setResponseTimeout(endpoint.getResponseTimeout() == null ? PROP.get( "test.default.responseTimeout" ) : endpoint.getResponseTimeout());
            httpSampler.setName( endpoint.getId() == null ? endpoint.getPath() : endpoint.getId() );
            httpSampler.setDomain( endpoint.getDomain() );
            httpSampler.setPort( endpoint.getPort() );
            httpSampler.setPath( endpoint.getPath() );
            httpSampler.setMethod( endpoint.getMethod() );
            Parameters params = endpoint.getParams();

            if( params != null ) {
                for( Parameter param : params.getParams() ) {
                    httpSampler.addArgument( param.getName(), param.getValue() );
                }
            }
            

            HashTree samplerHashTree = threadGroupHashTree.add( httpSampler );
            AssertionsBean endpointAssertions = endpoint.getAssertions();
            
            if( endpointAssertions != null ) {
                for( AssertionBean assertionBean : endpointAssertions.getAssertions() ) {
                    ResponseAssertion assertion = jMeterService.responseAssertion(assertionBean);
                    samplerHashTree.add(assertion);
                }                
            }
        }
        
        //save jmx script
        jMeterService.saveTree( testPlanTree, PROP.get( "test.script.location" ) );

        Summariser summer = jMeterService.summer();
        String reportFile = PROP.get( "test.report.file.jtl" );
        jMeterService.addReport(testPlanTree, summer, reportFile );
        jMeterService.addReport(testPlanTree, summer, PROP.get( "test.report.file.csv" ));
        
        // Run Test Plan
        jMeterService.run( engine, testPlanTree );

        jMeterService.generateDashboardReport( reportFile, PROP.get( "test.report.file.html" ));
        
        LOGGER.log( Level.INFO, "Test completed. See {0} file for results", reportFile );
        LOGGER.log( Level.INFO, "Test completed. See {0} file for results", PROP.get( "test.report.file.csv" ) );
        LOGGER.log( Level.INFO, "Test completed. See html report {0} file for results", PROP.get( "test.report.file.html" ) );
        LOGGER.log( Level.INFO, "JMeter .jmx script is available at {0}", PROP.get( "test.script.location" ) );
        System.exit( 0 );
    }
}
