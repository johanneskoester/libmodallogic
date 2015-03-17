<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:xalan="http://xml.apache.org/xalan"
    exclude-result-prefixes="xalan">
  <xsl:output indent="no"/>
  <xsl:output method="text" />
  <xsl:template match="conjunction">(<xsl:for-each select="*"><xsl:apply-templates select="."/><xsl:if test="position()!=last()">∧</xsl:if></xsl:for-each>)</xsl:template>
  <xsl:template match="disjunction">(<xsl:for-each select="*"><xsl:apply-templates select="."/><xsl:if test="position()!=last()">∨</xsl:if></xsl:for-each>)</xsl:template>
  <xsl:template match="negation">(¬<xsl:apply-templates/>)</xsl:template>
  <xsl:template match="necessity">(□<xsl:apply-templates/>)</xsl:template>
  <xsl:template match="possibility">(◊<xsl:apply-templates/>)</xsl:template>
  <xsl:template match="proposition">{<xsl:for-each select="*"><xsl:apply-templates/><xsl:if test="position()!=last()">,</xsl:if></xsl:for-each>}</xsl:template>
</xsl:stylesheet>