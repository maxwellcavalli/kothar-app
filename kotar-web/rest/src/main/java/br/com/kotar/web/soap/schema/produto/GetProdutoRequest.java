//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.04.24 at 07:46:49 AM BRT 
//


package br.com.kotar.web.soap.schema.produto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="produtoFilter" type="{http://kotar.com.br/web/soap/schema/produto}produtoFilter"/&gt;
 *         &lt;element name="token" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "produtoFilter",
    "token"
})
@XmlRootElement(name = "getProdutoRequest")
public class GetProdutoRequest {

    @XmlElement(required = true)
    protected ProdutoFilter produtoFilter;
    @XmlElement(required = true)
    protected String token;

    /**
     * Gets the value of the produtoFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ProdutoFilter }
     *     
     */
    public ProdutoFilter getProdutoFilter() {
        return produtoFilter;
    }

    /**
     * Sets the value of the produtoFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProdutoFilter }
     *     
     */
    public void setProdutoFilter(ProdutoFilter value) {
        this.produtoFilter = value;
    }

    /**
     * Gets the value of the token property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the value of the token property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToken(String value) {
        this.token = value;
    }

}
