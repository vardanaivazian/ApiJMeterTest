package jmeter.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "params")
@XmlAccessorType( XmlAccessType.FIELD)
public class Parameters {

    @XmlElement(name = "param")
    private List<Parameter> params = null;
    
    public List<Parameter> getParams() {
        return params;
    }

    public void setParams( List<Parameter> params ) {
        this.params = params;
    }
}
