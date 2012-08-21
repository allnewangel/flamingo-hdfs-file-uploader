//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.21 at 11:53:13 오후 KST 
//


package org.openflamingo.uploader.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="ingress" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.openflamingo.org/schema/uploader}local"/>
 *                   &lt;element ref="{http://www.openflamingo.org/schema/uploader}http"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="outgress" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.openflamingo.org/schema/uploader}hdfs"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "ingress",
    "outgress"
})
@XmlRootElement(name = "policy")
public class Policy {

    protected Policy.Ingress ingress;
    protected Policy.Outgress outgress;

    /**
     * Gets the value of the ingress property.
     * 
     * @return
     *     possible object is
     *     {@link Policy.Ingress }
     *     
     */
    public Policy.Ingress getIngress() {
        return ingress;
    }

    /**
     * Sets the value of the ingress property.
     * 
     * @param value
     *     allowed object is
     *     {@link Policy.Ingress }
     *     
     */
    public void setIngress(Policy.Ingress value) {
        this.ingress = value;
    }

    /**
     * Gets the value of the outgress property.
     * 
     * @return
     *     possible object is
     *     {@link Policy.Outgress }
     *     
     */
    public Policy.Outgress getOutgress() {
        return outgress;
    }

    /**
     * Sets the value of the outgress property.
     * 
     * @param value
     *     allowed object is
     *     {@link Policy.Outgress }
     *     
     */
    public void setOutgress(Policy.Outgress value) {
        this.outgress = value;
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
     *       &lt;choice>
     *         &lt;element ref="{http://www.openflamingo.org/schema/uploader}local"/>
     *         &lt;element ref="{http://www.openflamingo.org/schema/uploader}http"/>
     *       &lt;/choice>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "local",
        "http"
    })
    public static class Ingress {

        protected Local local;
        protected Http http;

        /**
         * Gets the value of the local property.
         * 
         * @return
         *     possible object is
         *     {@link Local }
         *     
         */
        public Local getLocal() {
            return local;
        }

        /**
         * Sets the value of the local property.
         * 
         * @param value
         *     allowed object is
         *     {@link Local }
         *     
         */
        public void setLocal(Local value) {
            this.local = value;
        }

        /**
         * Gets the value of the http property.
         * 
         * @return
         *     possible object is
         *     {@link Http }
         *     
         */
        public Http getHttp() {
            return http;
        }

        /**
         * Sets the value of the http property.
         * 
         * @param value
         *     allowed object is
         *     {@link Http }
         *     
         */
        public void setHttp(Http value) {
            this.http = value;
        }

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
     *       &lt;choice>
     *         &lt;element ref="{http://www.openflamingo.org/schema/uploader}hdfs"/>
     *       &lt;/choice>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "hdfs"
    })
    public static class Outgress {

        protected Hdfs hdfs;

        /**
         * Gets the value of the hdfs property.
         * 
         * @return
         *     possible object is
         *     {@link Hdfs }
         *     
         */
        public Hdfs getHdfs() {
            return hdfs;
        }

        /**
         * Sets the value of the hdfs property.
         * 
         * @param value
         *     allowed object is
         *     {@link Hdfs }
         *     
         */
        public void setHdfs(Hdfs value) {
            this.hdfs = value;
        }

    }

}
