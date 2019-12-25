package jmeter.service;

import jmeter.bean.Endpoint;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomResultCollector extends ResultCollector {

    private static final Logger LOGGER = Logger.getLogger( CustomResultCollector.class.getName() );
    private transient EndpointService endpointService;

    public CustomResultCollector( Summariser summer ) {
        super( summer );
        endpointService = new EndpointServiceImpl();
    }

    public CustomResultCollector() {
        super();
    }

    @Override
    public void sampleOccurred( SampleEvent event ) {
        super.sampleOccurred( event );
        SampleResult sampleResult = event.getResult();
        if( sampleResult.isSuccessful() ) {
            LOGGER.log( Level.INFO, "result: {0}", sampleResult );
            LOGGER.log( Level.INFO, "Response time in milliseconds: {0}", sampleResult.getTime() );
            LOGGER.log( Level.INFO, "HttpSample success id: " + sampleResult.getSampleLabel() + ", responseCode: " + sampleResult.getResponseCode() + ", responseMessage: " + sampleResult.getResponseMessage() );
        }
        else {
            LOGGER.warning( "HttpSample failed id: " + sampleResult.getSampleLabel() + ", responseCode: " + sampleResult.getResponseCode() + ", responseMessage: " + sampleResult.getResponseMessage() );
        }
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;
        if( !super.equals( o ) ) return false;
        CustomResultCollector that = ( CustomResultCollector ) o;
        return Objects.equals( endpointService, that.endpointService );
    }

    @Override
    public int hashCode() {
        return Objects.hash( super.hashCode(), endpointService );
    }
}
