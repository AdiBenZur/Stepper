//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.06.06 at 11:53:32 AM IDT 
//


package xml.jaxb.schema.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element ref="{}ST-Continuation-Mapping" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="target-flow" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "stContinuationMapping"
})
@XmlRootElement(name = "ST-Continuation")
public class STContinuation {

    @XmlElement(name = "ST-Continuation-Mapping")
    protected List<STContinuationMapping> stContinuationMapping;
    @XmlAttribute(name = "target-flow", required = true)
    protected String targetFlow;

    /**
     * Gets the value of the stContinuationMapping property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stContinuationMapping property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSTContinuationMapping().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link STContinuationMapping }
     * 
     * 
     */
    public List<STContinuationMapping> getSTContinuationMapping() {
        if (stContinuationMapping == null) {
            stContinuationMapping = new ArrayList<STContinuationMapping>();
        }
        return this.stContinuationMapping;
    }

    /**
     * Gets the value of the targetFlow property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetFlow() {
        return targetFlow;
    }

    /**
     * Sets the value of the targetFlow property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetFlow(String value) {
        this.targetFlow = value;
    }

}
