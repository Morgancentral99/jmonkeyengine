/**
 * texture3DType.java
 *
 * This file was generated by XMLSpy 2007sp2 Enterprise Edition.
 *
 * YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
 * OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.
 *
 * Refer to the XMLSpy Documentation for further details.
 * http://www.altova.com/xmlspy
 */


package com.jmex.model.collada.schema;

import com.jmex.xml.types.SchemaNCName;

public class texture3DType extends com.jmex.xml.xml.Node {

	public texture3DType(texture3DType node) {
		super(node);
	}

	public texture3DType(org.w3c.dom.Node node) {
		super(node);
	}

	public texture3DType(org.w3c.dom.Document doc) {
		super(doc);
	}

	public texture3DType(com.jmex.xml.xml.Document doc, String namespaceURI, String prefix, String name) {
		super(doc, namespaceURI, prefix, name);
	}
	
	public void adjustPrefix() {
		for (	org.w3c.dom.Node tmpNode = getDomFirstChild( Attribute, null, "index" );
				tmpNode != null;
				tmpNode = getDomNextChild( Attribute, null, "index", tmpNode )
			) {
			internalAdjustPrefix(tmpNode, false);
		}
		for (	org.w3c.dom.Node tmpNode = getDomFirstChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "value" );
				tmpNode != null;
				tmpNode = getDomNextChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "value", tmpNode )
			) {
			internalAdjustPrefix(tmpNode, true);
			new gl_sampler3D(tmpNode).adjustPrefix();
		}
		for (	org.w3c.dom.Node tmpNode = getDomFirstChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "param" );
				tmpNode != null;
				tmpNode = getDomNextChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "param", tmpNode )
			) {
			internalAdjustPrefix(tmpNode, true);
		}
	}
	public void setXsiType() {
 		org.w3c.dom.Element el = (org.w3c.dom.Element) domNode;
		el.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "texture3D");
	}

	public static int getindexMinCount() {
		return 1;
	}

	public static int getindexMaxCount() {
		return 1;
	}

	public int getindexCount() {
		return getDomChildCount(Attribute, null, "index");
	}

	public boolean hasindex() {
		return hasDomChild(Attribute, null, "index");
	}

	public GL_MAX_TEXTURE_IMAGE_UNITS_index newindex() {
		return new GL_MAX_TEXTURE_IMAGE_UNITS_index();
	}

	public GL_MAX_TEXTURE_IMAGE_UNITS_index getindexAt(int index) throws Exception {
		return new GL_MAX_TEXTURE_IMAGE_UNITS_index(getDomNodeValue(getDomChildAt(Attribute, null, "index", index)));
	}

	public org.w3c.dom.Node getStartingindexCursor() throws Exception {
		return getDomFirstChild(Attribute, null, "index" );
	}

	public org.w3c.dom.Node getAdvancedindexCursor( org.w3c.dom.Node curNode ) throws Exception {
		return getDomNextChild( Attribute, null, "index", curNode );
	}

	public GL_MAX_TEXTURE_IMAGE_UNITS_index getindexValueAtCursor( org.w3c.dom.Node curNode ) throws Exception {
		if( curNode == null )
			throw new com.jmex.xml.xml.XmlException("Out of range");
		else
			return new GL_MAX_TEXTURE_IMAGE_UNITS_index(getDomNodeValue(curNode));
	}

	public GL_MAX_TEXTURE_IMAGE_UNITS_index getindex() throws Exception 
 {
		return getindexAt(0);
	}

	public void removeindexAt(int index) {
		removeDomChildAt(Attribute, null, "index", index);
	}

	public void removeindex() {
		removeindexAt(0);
	}

	public org.w3c.dom.Node addindex(GL_MAX_TEXTURE_IMAGE_UNITS_index value) {
		if( value.isNull() )
			return null;

		return  appendDomChild(Attribute, null, "index", value.toString());
	}

	public org.w3c.dom.Node addindex(String value) throws Exception {
		return addindex(new GL_MAX_TEXTURE_IMAGE_UNITS_index(value));
	}

	public void insertindexAt(GL_MAX_TEXTURE_IMAGE_UNITS_index value, int index) {
		insertDomChildAt(Attribute, null, "index", index, value.toString());
	}

	public void insertindexAt(String value, int index) throws Exception {
		insertindexAt(new GL_MAX_TEXTURE_IMAGE_UNITS_index(value), index);
	}

	public void replaceindexAt(GL_MAX_TEXTURE_IMAGE_UNITS_index value, int index) {
		replaceDomChildAt(Attribute, null, "index", index, value.toString());
	}

	public void replaceindexAt(String value, int index) throws Exception {
		replaceindexAt(new GL_MAX_TEXTURE_IMAGE_UNITS_index(value), index);
	}

	public static int getvalue2MinCount() {
		return 1;
	}

	public static int getvalue2MaxCount() {
		return 1;
	}

	public int getvalue2Count() {
		return getDomChildCount(Element, "http://www.collada.org/2005/11/COLLADASchema", "value");
	}

	public boolean hasvalue2() {
		return hasDomChild(Element, "http://www.collada.org/2005/11/COLLADASchema", "value");
	}

	public gl_sampler3D newvalue2() {
		return new gl_sampler3D(domNode.getOwnerDocument().createElementNS("http://www.collada.org/2005/11/COLLADASchema", "value"));
	}

	public gl_sampler3D getvalue2At(int index) throws Exception {
		return new gl_sampler3D(getDomChildAt(Element, "http://www.collada.org/2005/11/COLLADASchema", "value", index));
	}

	public org.w3c.dom.Node getStartingvalue2Cursor() throws Exception {
		return getDomFirstChild(Element, "http://www.collada.org/2005/11/COLLADASchema", "value" );
	}

	public org.w3c.dom.Node getAdvancedvalue2Cursor( org.w3c.dom.Node curNode ) throws Exception {
		return getDomNextChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "value", curNode );
	}

	public gl_sampler3D getvalue2ValueAtCursor( org.w3c.dom.Node curNode ) throws Exception {
		if( curNode == null )
			throw new com.jmex.xml.xml.XmlException("Out of range");
		else
			return new gl_sampler3D(curNode);
	}

	public gl_sampler3D getvalue2() throws Exception 
 {
		return getvalue2At(0);
	}

	public void removevalue2At(int index) {
		removeDomChildAt(Element, "http://www.collada.org/2005/11/COLLADASchema", "value", index);
	}

	public void removevalue2() {
		removevalue2At(0);
	}

	public org.w3c.dom.Node addvalue2(gl_sampler3D value) {
		return appendDomElement("http://www.collada.org/2005/11/COLLADASchema", "value", value);
	}

	public void insertvalue2At(gl_sampler3D value, int index) {
		insertDomElementAt("http://www.collada.org/2005/11/COLLADASchema", "value", index, value);
	}

	public void replacevalue2At(gl_sampler3D value, int index) {
		replaceDomElementAt("http://www.collada.org/2005/11/COLLADASchema", "value", index, value);
	}

	public static int getparamMinCount() {
		return 1;
	}

	public static int getparamMaxCount() {
		return 1;
	}

	public int getparamCount() {
		return getDomChildCount(Element, "http://www.collada.org/2005/11/COLLADASchema", "param");
	}

	public boolean hasparam() {
		return hasDomChild(Element, "http://www.collada.org/2005/11/COLLADASchema", "param");
	}

	public SchemaNCName newparam() {
		return new SchemaNCName();
	}

	public SchemaNCName getparamAt(int index) throws Exception {
		return new SchemaNCName(getDomNodeValue(getDomChildAt(Element, "http://www.collada.org/2005/11/COLLADASchema", "param", index)));
	}

	public org.w3c.dom.Node getStartingparamCursor() throws Exception {
		return getDomFirstChild(Element, "http://www.collada.org/2005/11/COLLADASchema", "param" );
	}

	public org.w3c.dom.Node getAdvancedparamCursor( org.w3c.dom.Node curNode ) throws Exception {
		return getDomNextChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "param", curNode );
	}

	public SchemaNCName getparamValueAtCursor( org.w3c.dom.Node curNode ) throws Exception {
		if( curNode == null )
			throw new com.jmex.xml.xml.XmlException("Out of range");
		else
			return new SchemaNCName(getDomNodeValue(curNode));
	}

	public SchemaNCName getparam() throws Exception 
 {
		return getparamAt(0);
	}

	public void removeparamAt(int index) {
		removeDomChildAt(Element, "http://www.collada.org/2005/11/COLLADASchema", "param", index);
	}

	public void removeparam() {
		removeparamAt(0);
	}

	public org.w3c.dom.Node addparam(SchemaNCName value) {
		if( value.isNull() )
			return null;

		return  appendDomChild(Element, "http://www.collada.org/2005/11/COLLADASchema", "param", value.toString());
	}

	public org.w3c.dom.Node addparam(String value) throws Exception {
		return addparam(new SchemaNCName(value));
	}

	public void insertparamAt(SchemaNCName value, int index) {
		insertDomChildAt(Element, "http://www.collada.org/2005/11/COLLADASchema", "param", index, value.toString());
	}

	public void insertparamAt(String value, int index) throws Exception {
		insertparamAt(new SchemaNCName(value), index);
	}

	public void replaceparamAt(SchemaNCName value, int index) {
		replaceDomChildAt(Element, "http://www.collada.org/2005/11/COLLADASchema", "param", index, value.toString());
	}

	public void replaceparamAt(String value, int index) throws Exception {
		replaceparamAt(new SchemaNCName(value), index);
	}

}
