<xsl:stylesheet version = '1.0'
	xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>

	<xsl:template match="/">
		<xsl:apply-templates select="container"/>
	</xsl:template>

	<xsl:template match="container">
		<config>
		<xsl:apply-templates select="component"/>
		</config>
	</xsl:template>

	<xsl:template match="component">
		<xsl:element name="{@name}">
			<xsl:copy-of select="configuration/*"/>
		</xsl:element>
	</xsl:template>	

</xsl:stylesheet>