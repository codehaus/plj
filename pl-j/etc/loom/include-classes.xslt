<xsl:stylesheet version = '1.0'
	xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>

	<xsl:template match="/">
		<maven>
			<loom>
				<lib>
					<include>
						<xsl:apply-templates select="container"/>
					</include>
				</lib>
			</loom>
		</maven>
	</xsl:template>

	<xsl:template match="container">
		<xsl:apply-templates select="classloader"/>
	</xsl:template>

	<xsl:template match="classloader">
		<xsl:apply-templates select="classpath"/>
	</xsl:template>

	<xsl:template match="repository">
		<xsl:apply-templates select="resource"/>
	</xsl:template>
	
	<xsl:template match="resource">
		${maven.repo}/<xsl:value-of select="translate(@id,':','/')"/>-<xsl:value-of select="@version"/>.jar:
	</xsl:template>

</xsl:stylesheet>