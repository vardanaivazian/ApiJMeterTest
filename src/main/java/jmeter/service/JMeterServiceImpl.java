package jmeter.service;

import jmeter.exception.AppException;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.report.config.ConfigurationException;
import org.apache.jmeter.report.dashboard.GenerationException;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.jmeter.JMeter.JMETER_REPORT_OUTPUT_DIR_PROPERTY;

public class JMeterServiceImpl implements JMeterService {

    private static final PropertyService PROP = PropertyService.INSTANCE;
    private static final Logger LOGGER = Logger.getLogger( JMeterServiceImpl.class.getName() );

    @Override
    public StandardJMeterEngine engine() {
        //Set jmeter.home from config.properties file
        File jMeterHome = new File( PROP.get( "jmeter.home" ) );
        String slash = System.getProperty( "file.separator" );

        if( jMeterHome.exists() ) {
            File jMeterProperties = new File( jMeterHome.getPath() + slash + "bin" + slash + "jmeter.properties" );
            if( jMeterProperties.exists() ) {

                //JMeter initialization (properties, log levels, locale, etc)
                JMeterUtils.setJMeterHome( jMeterHome.getPath() );
                JMeterUtils.loadJMeterProperties( jMeterProperties.getPath() );
                JMeterUtils.initLocale();

                //JMeter Engine
                return new StandardJMeterEngine();
            }
        }
        throw new AppException( "jmeter.home property is not set or pointing to incorrect location" );
    }

    @Override
    public void run( StandardJMeterEngine engine, HashTree testPlanTree ) {
        engine.configure( testPlanTree );
        LOGGER.info( "Running test plan " + testPlanTree.getArray()[0]);
        engine.run();
    }


    @Override
    public LoopController loop( int loopCount, boolean first ) {
        LoopController loopController = new LoopController();
        loopController.setLoops( loopCount );
        loopController.setFirst( first );
        loopController.initialize();
        return loopController;
    }

    @Override
    public ThreadGroup threadGroup( String name, int numThreads, int rumpUp ) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName( name );
        threadGroup.setNumThreads( numThreads );
        threadGroup.setRampUp( rumpUp );
        return threadGroup;
    }

    @Override
    public TestPlan testPlan( String name ) {
        return new TestPlan( name );
    }

    @Override
    public HashTree testPlanTree() {
        return new HashTree();
    }

    @Override
    public HTTPSamplerProxy httpSamplerProxy() {
        return new HTTPSamplerProxy();
    }

    @Override
    public void saveTree( HashTree tree, String destination ) {
        try {
            // save generated test plan to JMeter's .jmx file format
            SaveService.saveTree( tree, new FileOutputStream( destination ) );
        }
        catch( IOException e ) {
            LOGGER.severe( e.getMessage() );
        }
    }

    @Override
    public Summariser summer() {
        //add Summarizer output to get test progress in stdout like:
        // summary =      2 in   1.3s =    1.5/s Avg:   631 Min:   290 Max:   973 Err:     0 (0.00%)
        String summariserName = JMeterUtils.getPropDefault( "summariser.name", "summary" );
        if( summariserName.length() > 0 ) {
            return new Summariser( summariserName );
        }
        throw new AppException( MessageFormat.format( "summariserName length is {0}", summariserName.length() ) );
    }

    @Override
    public ResultCollector resultCollector( Summariser summer, String reportFile ) {
        ResultCollector report = new ResultCollector( summer );
        report.setFilename( reportFile );
        return report;
    }

    @Override
    public void addReport( HashTree testPlanTree, Summariser summer, String reportFile ) {
        ResultCollector collector = this.resultCollector( summer, reportFile );
        testPlanTree.add( testPlanTree.getArray()[0], collector );
    }

    @Override
    public void generateDashboardReport( String source, String destination ) {
        JMeterUtils.setProperty( JMETER_REPORT_OUTPUT_DIR_PROPERTY, PROP.get( "test.report.file.html" ) );
        try {
            ReportGenerator generator = new ReportGenerator( source, null );
            generator.generate();
        }
        catch( ConfigurationException | GenerationException e ) {
            LOGGER.log( Level.SEVERE, "Exception during generating dashboard report: {0}", e.getMessage() );
        }
    }
}
