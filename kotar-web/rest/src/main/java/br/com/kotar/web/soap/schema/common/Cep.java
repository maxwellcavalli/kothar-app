//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.04.24 at 07:46:49 AM BRT 
//


package br.com.kotar.web.soap.schema.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cep complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cep"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cep_nome" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="codigoPostal" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cep_bairro" type="{http://kotar.com.br/web/soap/schema/common}bairro"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cep", propOrder = {
    "cepNome",
    "codigoPostal",
    "cepBairro"
})
public class Cep {

    @XmlElement(name = "cep_nome", required = true)
    protected String cepNome;
    @XmlElement(required = true)
    protected String codigoPostal;
    @XmlElement(name = "cep_bairro", required = true)
    protected Bairro cepBairro;

    /**
     * Gets the value of the cepNome property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCepNome() {
        return cepNome;
    }

    /**
     * Sets the value of the cepNome property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCepNome(String value) {
        this.cepNome = value;
    }

    /**
     * Gets the value of the codigoPostal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoPostal() {
        return codigoPostal;
    }

    /**
     * Sets the value of the codigoPostal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoPostal(String value) {
        this.codigoPostal = value;
    }

    /**
     * Gets the value of the cepBairro property.
     * 
     * @return
     *     possible object is
     *     {@link Bairro }
     *     
     */
    public Bairro getCepBairro() {
        return cepBairro;
    }

    /**
     * Sets the value of the cepBairro property.
     * 
     * @param value
     *     allowed object is
     *     {@link Bairro }
     *     
     */
    public void setCepBairro(Bairro value) {
        this.cepBairro = value;
    }

}