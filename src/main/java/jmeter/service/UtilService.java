package jmeter.service;

import jmeter.bean.AssertionBean;
import jmeter.bean.AssertionsBean;
import jmeter.bean.Endpoint;
import jmeter.bean.Parameter;
import jmeter.bean.Parameters;
import org.apache.jmeter.assertions.ResponseAssertion;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jorphan.collections.HashTree;

public final class UtilService {

    private static final EndpointService endpointService = new EndpointServiceImpl();
    private static final JMeterService jMeterService = new JMeterServiceImpl();
    private static final PropertyService PROP = PropertyService.INSTANCE;

    private UtilService() {}

    public static void addSamplersToTree( HashTree threadGroupHashTree) {

        for( Endpoint endpoint : endpointService.getEndpoints() ) {

            HTTPSamplerProxy httpSampler = getHttpSamplerFromEndpoint( endpoint );

            HashTree samplerHashTree = threadGroupHashTree.add( httpSampler );
            AssertionsBean endpointAssertions = endpoint.getAssertions();

            if( endpointAssertions != null ) {
                for( AssertionBean assertionBean : endpointAssertions.getAssertions() ) {
                    ResponseAssertion assertion = jMeterService.responseAssertion(assertionBean);
                    samplerHashTree.add(assertion);
                }
            }
        }
    }

    private static HTTPSamplerProxy getHttpSamplerFromEndpoint( Endpoint endpoint) {

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
        return httpSampler;
    }
    
}
