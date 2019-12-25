package jmeter.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "assertion")
@XmlAccessorType( XmlAccessType.FIELD)
public class AssertionBean {

    @XmlElement(name = "testString")
    private String testString;

    @XmlElement(name = "responseField")
    private ResponseField responseField;

    @XmlElement(name = "responsePatternType")
    private ResponsePatternType responsePatternType;

    @XmlElement(name = "not")
    private boolean not;

    public String getTestString() {
        return testString;
    }

    public void setTestString( String testString ) {
        this.testString = testString;
    }

    public ResponseField getResponseField() {
        return responseField;
    }

    public void setResponseField( ResponseField responseField ) {
        this.responseField = responseField;
    }

    public ResponsePatternType getResponsePatternType() {
        return responsePatternType;
    }

    public void setResponsePatternType( ResponsePatternType responsePatternType ) {
        this.responsePatternType = responsePatternType;
    }

    public boolean isNot() {
        return not;
    }

    public void setNot( boolean not ) {
        this.not = not;
    }
}
