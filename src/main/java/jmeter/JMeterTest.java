package jmeter;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import jmeter.service.EndpointService;
import jmeter.service.EndpointServiceImpl;
import jmeter.service.JMeterService;
import jmeter.service.JMeterServiceImpl;
import jmeter.service.PropertyService;
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

        // Construct Test Plan from previously initialized elements
        testPlanTree.add( testPlan );

        HashTree threadGroupHashTree = testPlanTree.add( testPlan, threadGroup );

        Stream<HTTPSamplerProxy> httpSamplerProxyStream = endpointService.getEndpoints().stream().map( endpoint -> {
            HTTPSamplerProxy httpSampler = jMeterService.httpSamplerProxy();
            httpSampler.setName( endpoint.getPath() );
            httpSampler.setDomain( endpoint.getDomain() );
            httpSampler.setPort( endpoint.getPort() );
            httpSampler.setPath( endpoint.getPath() );
            httpSampler.setMethod( endpoint.getMethod() );
            return httpSampler;
        } );
        
        threadGroupHashTree.add( httpSamplerProxyStream.toArray() );
        
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
