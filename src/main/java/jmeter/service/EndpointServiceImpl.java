package jmeter.service;

import jmeter.bean.Endpoint;
import jmeter.bean.Endpoints;
import jmeter.exception.AppException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class EndpointServiceImpl implements EndpointService {

    private static final Logger LOGGER = Logger.getLogger(EndpointServiceImpl.class.getName());
    
    //cache endpoints
    private List<Endpoint> cacheEndpoints;
    
    public List<Endpoint> getEndpoints() {
        if( !cacheEndpoints.isEmpty() ) return cacheEndpoints; 
        try {
            File file = new File(PropertyService.INSTANCE.get( "endpoints.path" ));
            JAXBContext jaxbContext = JAXBContext.newInstance( Endpoints.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Endpoints endpoints = (Endpoints) jaxbUnmarshaller.unmarshal(file);
            cacheEndpoints = endpoints.getEndpoints();
            return cacheEndpoints;
        } catch ( JAXBException e) {
            LOGGER.severe( e.getCause().toString() );
            throw new AppException(e.getMessage());
        }
    }
}
