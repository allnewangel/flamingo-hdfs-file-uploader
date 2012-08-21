//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.21 at 11:53:13 오후 KST 
//


package org.openflamingo.uploader.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
 *         &lt;element name="cronExpression" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="timezone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="triggerPriority" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="misfireInstructions" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="type" default="MISFIRE_INSTRUCTION_SMART_POLICY">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *                       &lt;enumeration value="MISFIRE_INSTRUCTION_SMART_POLICY"/>
 *                       &lt;enumeration value="MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY"/>
 *                       &lt;enumeration value="MISFIRE_INSTRUCTION_DO_NOTHING"/>
 *                       &lt;enumeration value="MISFIRE_INSTRUCTION_FIRE_NOW"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://www.openflamingo.org/schema/uploader}start" minOccurs="0"/>
 *         &lt;element ref="{http://www.openflamingo.org/schema/uploader}end" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cronExpression",
    "timezone",
    "triggerPriority",
    "misfireInstructions",
    "start",
    "end"
})
@XmlRootElement(name = "schedule")
public class Schedule {

    @XmlElement(required = true)
    protected String cronExpression;
    protected String timezone;
    @XmlElement(defaultValue = "5")
    protected Integer triggerPriority;
    protected Schedule.MisfireInstructions misfireInstructions;
    protected Start start;
    protected End end;

    /**
     * Gets the value of the cronExpression property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCronExpression() {
        return cronExpression;
    }

    /**
     * Sets the value of the cronExpression property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCronExpression(String value) {
        this.cronExpression = value;
    }

    /**
     * Gets the value of the timezone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * Sets the value of the timezone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimezone(String value) {
        this.timezone = value;
    }

    /**
     * Gets the value of the triggerPriority property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTriggerPriority() {
        return triggerPriority;
    }

    /**
     * Sets the value of the triggerPriority property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTriggerPriority(Integer value) {
        this.triggerPriority = value;
    }

    /**
     * Gets the value of the misfireInstructions property.
     * 
     * @return
     *     possible object is
     *     {@link Schedule.MisfireInstructions }
     *     
     */
    public Schedule.MisfireInstructions getMisfireInstructions() {
        return misfireInstructions;
    }

    /**
     * Sets the value of the misfireInstructions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Schedule.MisfireInstructions }
     *     
     */
    public void setMisfireInstructions(Schedule.MisfireInstructions value) {
        this.misfireInstructions = value;
    }

    /**
     * Gets the value of the start property.
     * 
     * @return
     *     possible object is
     *     {@link Start }
     *     
     */
    public Start getStart() {
        return start;
    }

    /**
     * Sets the value of the start property.
     * 
     * @param value
     *     allowed object is
     *     {@link Start }
     *     
     */
    public void setStart(Start value) {
        this.start = value;
    }

    /**
     * Gets the value of the end property.
     * 
     * @return
     *     possible object is
     *     {@link End }
     *     
     */
    public End getEnd() {
        return end;
    }

    /**
     * Sets the value of the end property.
     * 
     * @param value
     *     allowed object is
     *     {@link End }
     *     
     */
    public void setEnd(End value) {
        this.end = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="type" default="MISFIRE_INSTRUCTION_SMART_POLICY">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
     *             &lt;enumeration value="MISFIRE_INSTRUCTION_SMART_POLICY"/>
     *             &lt;enumeration value="MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY"/>
     *             &lt;enumeration value="MISFIRE_INSTRUCTION_DO_NOTHING"/>
     *             &lt;enumeration value="MISFIRE_INSTRUCTION_FIRE_NOW"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class MisfireInstructions {

        @XmlAttribute(name = "type")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String type;

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getType() {
            if (type == null) {
                return "MISFIRE_INSTRUCTION_SMART_POLICY";
            } else {
                return type;
            }
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(String value) {
            this.type = value;
        }

    }

}
