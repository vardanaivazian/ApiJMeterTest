package jmeter.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "endpoint")
@XmlAccessorType( XmlAccessType.FIELD)
public class Endpoint {

    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "domain")
    private String domain;

    @XmlElement(name = "port")
    private int port;

    @XmlElement(name = "path")
    private String path;

    @XmlElement(name = "params")
    private Parameters params;
    
    @XmlElement(name = "method")
    private String method;

    @XmlAttribute(name = "responseCode")
    private int responseCode;

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain( String domain ) {
        this.domain = domain;
    }

    public int getPort() {
        return port;
    }

    public void setPort( int port ) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }
    
    public void setPath( String path ) {
        this.path = path;
    }

    public Parameters getParams() {
        return params;
    }

    public void setParams( Parameters params ) {
        this.params = params;
    }

    public String getMethod() {
        return method;
    }
    
    public void setMethod( String method ) {
        this.method = method;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode( int responseCode ) {
        this.responseCode = responseCode;
    }
}
