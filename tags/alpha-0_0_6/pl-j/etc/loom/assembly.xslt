<xsl:stylesheet version = '1.0'
	xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>

	<xsl:template match="/">
		<xsl:apply-templates select="container"/>
	</xsl:template>

	<xsl:template match="container">
		<assembly>
		<xsl:apply-templates select="component"/>
		</assembly>
	</xsl:template>

	<xsl:template match="component">
		<block>
		<xsl:attribute name="name">
			<xsl:value-of select="@name"/>
		</xsl:attribute>
		<xsl:attribute name="class">
			<xsl:value-of select="@class"/>
		</xsl:attribute>
		
		<xsl:apply-templates select="dependencies"/>
		
		</block>
	</xsl:template>
	
	<xsl:template match="dependencies">
		<xsl:apply-templates select="dependency"/>
	</xsl:template>

	<xsl:template match="dependency">
		<provide>
			<xsl:attribute name="name">
				<xsl:value-of select="@source"/>
			</xsl:attribute>
			<xsl:attribute name="role">
				<xsl:value-of select="@key"/>
			</xsl:attribute>
			<xsl:attribute name="alias">
				<xsl:value-of select="@key"/>
			</xsl:attribute>
		</provide>
	</xsl:template>

</xsl:stylesheet>