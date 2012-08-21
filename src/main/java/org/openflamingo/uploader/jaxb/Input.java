//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.21 at 06:30:54 오후 KST 
//


package org.openflamingo.uploader.jaxb;

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
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openflamingo.org/schema/uploader}selector" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="excludeOnNotExist" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "selector"
})
@XmlRootElement(name = "input")
public class Input {

    @XmlElement(required = true)
    protected List<Selector> selector;
    @XmlAttribute(name = "excludeOnNotExist")
    protected Boolean excludeOnNotExist;

    /**
     * Gets the value of the selector property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the selector property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSelector().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Selector }
     * 
     * 
     */
    public List<Selector> getSelector() {
        if (selector == null) {
            selector = new ArrayList<Selector>();
        }
        return this.selector;
    }

    /**
     * Gets the value of the excludeOnNotExist property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isExcludeOnNotExist() {
        if (excludeOnNotExist == null) {
            return true;
        } else {
            return excludeOnNotExist;
        }
    }

    /**
     * Sets the value of the excludeOnNotExist property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExcludeOnNotExist(Boolean value) {
        this.excludeOnNotExist = value;
    }

}
