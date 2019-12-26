package jmeter.service;

import jmeter.bean.AssertionBean;
import jmeter.bean.ResponseField;
import jmeter.bean.ResponsePatternType;
import jmeter.exception.AppException;
import org.apache.jmeter.assertions.ResponseAssertion;
import org.apache.jmeter.assertions.gui.AssertionGui;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.report.config.ConfigurationException;
import org.apache.jmeter.report.dashboard.GenerationException;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
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
        LOGGER.info( "Running test plan " + testPlanTree.getArray()[0] );
        engine.run();
    }


    @Override
    public LoopController loop( int loopCount, boolean first ) {
        LoopController loopController = new LoopController();
        loopController.setLoops( loopCount );
        loopController.setFirst( first );
        loopController.initialize();
        loopController.setProperty( TestElement.TEST_CLASS, LoopController.class.getName() );
        loopController.setProperty( TestElement.GUI_CLASS, LoopControlPanel.class.getName() );
        return loopController;
    }

    @Override
    public ThreadGroup threadGroup( String name, int numThreads, int rumpUp ) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName( name );
        threadGroup.setNumThreads( numThreads );
        threadGroup.setRampUp( rumpUp );
        threadGroup.setProperty( TestElement.TEST_CLASS, ThreadGroup.class.getName() );
        threadGroup.setProperty( TestElement.GUI_CLASS, ThreadGroupGui.class.getName() );
        return threadGroup;
    }

    @Override
    public TestPlan testPlan( String name ) {
        TestPlan testPlan = new TestPlan( name );
        testPlan.setProperty( TestElement.TEST_CLASS, TestPlan.class.getName() );
        testPlan.setProperty( TestElement.GUI_CLASS, TestPlanGui.class.getName() );
        testPlan.setUserDefinedVariables( ( Arguments ) new ArgumentsPanel().createTestElement() );
        return testPlan;
    }

    @Override
    public HashTree testPlanTree() {
        return new HashTree();
    }

    @Override
    public HTTPSamplerProxy httpSamplerProxy() {
        HTTPSamplerProxy httpSamplerProxy = new HTTPSamplerProxy();
        httpSamplerProxy.setProperty( TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName() );
        httpSamplerProxy.setProperty( TestElement.GUI_CLASS, HttpTestSampleGui.class.getName() );
        return httpSamplerProxy;
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
        String summariserName = JMeterUtils.getPropDefault( "summariser.name", "summary" );
        if( summariserName.length() > 0 ) {
            return new Summariser( summariserName );
        }
        throw new AppException( MessageFormat.format( "summariserName length is {0}", summariserName.length() ) );
    }

    @Override
    public ResultCollector resultCollector( Summariser summer, String reportFile ) {
        ResultCollector report = new CustomResultCollector( summer );
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

    @Override
    public ResponseAssertion responseAssertion( AssertionBean assertionsBean ) {

        ResponseAssertion assertion = new ResponseAssertion();
        assertion.setProperty( TestElement.TEST_CLASS, ResponseAssertion.class.getName() );
        assertion.setProperty( TestElement.GUI_CLASS, AssertionGui.class.getName() );
        assertion.setName( "Response Assertion" );
        assertion.setEnabled( true );

        String testString = assertionsBean.getTestString();

        LOGGER.log( Level.INFO, "Add response assertion for {0}", testString );

        setResponseField( assertionsBean.getResponseField(), assertion );

        setResponsePatternType( assertionsBean.getResponsePatternType(), assertion );

        if( assertionsBean.isNot() ) assertion.setToNotType();
        assertion.addTestString( testString );
        return assertion;
    }

    private void setResponseField( ResponseField responseField, ResponseAssertion assertion ) {
        
        UtilService.ResponseFieldProcessor responseFieldProcessor = UtilService.getResponseFieldProcessors().get( responseField );
        if (responseFieldProcessor == null) {
            throw new AppException("Unknown responseFieldProcessor for responseField " + responseField);
        }
        // set responseField type to assertion
        responseFieldProcessor.processResponseField(assertion);
    }

    private void setResponsePatternType( ResponsePatternType responsePatternType, ResponseAssertion assertion ) {

        UtilService.ResponsePatternTypeProcessor responsePatternTypeProcessor = UtilService.getResponsePatternTypeProcessors().get( responsePatternType );
        if( responsePatternTypeProcessor == null ) {
            throw new AppException("Unknown responsePatternTypeProcessor for responsePatternType " + responsePatternType);
        }
        // set responsePatternType to assertion
        responsePatternTypeProcessor.processResponsePatternType( assertion );
    }
}
