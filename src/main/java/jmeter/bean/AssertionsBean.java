package jmeter.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "assertions")
@XmlAccessorType( XmlAccessType.FIELD)
public class AssertionsBean {

    @XmlElement(name = "assertion")
    private List<AssertionBean> assertions;

    public List<AssertionBean> getAssertions() {
        return assertions;
    }

    public void setAssertions( List<AssertionBean> assertions ) {
        this.assertions = assertions;
    }
}
