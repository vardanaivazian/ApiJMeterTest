package jmeter.service;

import jmeter.bean.AssertionBean;
import jmeter.bean.AssertionsBean;
import jmeter.bean.Endpoint;
import jmeter.bean.Parameter;
import jmeter.bean.Parameters;
import jmeter.bean.ResponseField;
import jmeter.bean.ResponsePatternType;
import org.apache.jmeter.assertions.ResponseAssertion;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jorphan.collections.HashTree;

import java.util.EnumMap;
import java.util.Map;

public final class UtilService {

    private static final EndpointService endpointService = new EndpointServiceImpl();
    private static final JMeterService jMeterService = new JMeterServiceImpl();
    private static final PropertyService PROP = PropertyService.INSTANCE;
    private static EnumMap<ResponseField, ResponseFieldProcessor> responseFieldProcessors;
    private static EnumMap<ResponsePatternType, ResponsePatternTypeProcessor> responsePatternTypeProcessors;

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


    interface ResponseFieldProcessor {
        void processResponseField(ResponseAssertion assertion);
    }

    public static Map<ResponseField, ResponseFieldProcessor> getResponseFieldProcessors() {
        if( responseFieldProcessors == null ) {
            responseFieldProcessors = new EnumMap<>(ResponseField.class);
            responseFieldProcessors.put( ResponseField.TEXT, ResponseAssertion::setTestFieldResponseData );
            responseFieldProcessors.put( ResponseField.DOCUMENT, ResponseAssertion::setTestFieldResponseDataAsDocument );
            responseFieldProcessors.put( ResponseField.URL, ResponseAssertion::setTestFieldURL );
            responseFieldProcessors.put( ResponseField.RESPONSE_CODE, ResponseAssertion::setTestFieldResponseCode );
            responseFieldProcessors.put( ResponseField.RESPONSE_MESSAGE, ResponseAssertion::setTestFieldResponseMessage );
            responseFieldProcessors.put( ResponseField.RESPONSE_HEADERS, ResponseAssertion::setTestFieldResponseHeaders );
        }
        return responseFieldProcessors;
    }
    
    interface ResponsePatternTypeProcessor {
        void processResponsePatternType(ResponseAssertion assertion);
    }
    
    public static Map<ResponsePatternType, ResponsePatternTypeProcessor> getResponsePatternTypeProcessors() {
        if( responsePatternTypeProcessors == null ) {
            responsePatternTypeProcessors = new EnumMap<>( ResponsePatternType.class );
            responsePatternTypeProcessors.put( ResponsePatternType.CONTAINS, ResponseAssertion::setToContainsType );
            responsePatternTypeProcessors.put( ResponsePatternType.MATCHES, ResponseAssertion::setToMatchType );
            responsePatternTypeProcessors.put( ResponsePatternType.EQUALS, ResponseAssertion::setToEqualsType );
            responsePatternTypeProcessors.put( ResponsePatternType.SUBSTRING, ResponseAssertion::setToSubstringType );
        }
        return responsePatternTypeProcessors;
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
