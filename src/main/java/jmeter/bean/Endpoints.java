package jmeter.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "endpoints")
@XmlAccessorType( XmlAccessType.FIELD)
public class Endpoints {

    @XmlElement(name = "endpoint")
    private List<Endpoint> endpoints = null;

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints( List<Endpoint> endpoints ) {
        this.endpoints = endpoints;
    }
}
