/*
 * Created on Jul 10, 2004
 */

package org.pgj.jdbc.scratch;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;


/**
 * @author Laszlo Hornyak
 */
public class PLJJDBCMetaData implements DatabaseMetaData {

	PLJJDBCMetaData(Configuration conf) {
		this.conf = conf;
	}

	private Configuration conf = null;

	private int getIntFromConf(String name) throws SQLException {
		try {
			return conf.getChild(name).getValueAsInteger();
		} catch (ConfigurationException e) {
			throw new SQLException("JDBC driver configuration not set for "
					.concat(name));
		}
	}

	private boolean getBooleanFromConf(String name) throws SQLException {
		try {
			return conf.getChild(name).getValueAsBoolean();
		} catch (ConfigurationException e) {
			throw new SQLException("JDBC driver configuration not set properly for "
					.concat(name));
		}
	}

	
	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDatabaseMajorVersion()
	 */
	public int getDatabaseMajorVersion() throws SQLException {
		return getIntFromConf("DatabaseMetaData");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDatabaseMinorVersion()
	 */
	public int getDatabaseMinorVersion() throws SQLException {
		return getIntFromConf("DatabaseMinorVersion");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDefaultTransactionIsolation()
	 */
	public int getDefaultTransactionIsolation() throws SQLException {
		return getIntFromConf("DefaultTransactionIsolation");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDriverMajorVersion()
	 */
	public int getDriverMajorVersion() {
		try {
			return getIntFromConf("DriverMajorVersion");
		} catch (SQLException e) {
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDriverMinorVersion()
	 */
	public int getDriverMinorVersion() {
		try {
			return getIntFromConf("DriverMinorVersion");
		} catch (SQLException e) {
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getJDBCMajorVersion()
	 */
	public int getJDBCMajorVersion() throws SQLException {
		return getIntFromConf("JDBCMajorVersion");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getJDBCMinorVersion()
	 */
	public int getJDBCMinorVersion() throws SQLException {
		return getIntFromConf("JDBCMinorVersion");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxBinaryLiteralLength()
	 */
	public int getMaxBinaryLiteralLength() throws SQLException {
		return getIntFromConf("MaxBinaryLiteralLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxCatalogNameLength()
	 */
	public int getMaxCatalogNameLength() throws SQLException {
		return getIntFromConf("MaxCatalogNameLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxCharLiteralLength()
	 */
	public int getMaxCharLiteralLength() throws SQLException {
		return getIntFromConf("MaxCharLiteralLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnNameLength()
	 */
	public int getMaxColumnNameLength() throws SQLException {
		return getIntFromConf("MaxColumnNameLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInGroupBy()
	 */
	public int getMaxColumnsInGroupBy() throws SQLException {
		return getIntFromConf("MaxColumnsInGroupBy");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInIndex()
	 */
	public int getMaxColumnsInIndex() throws SQLException {
		return getIntFromConf("MaxColumnsInIndex");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInOrderBy()
	 */
	public int getMaxColumnsInOrderBy() throws SQLException {
		return getIntFromConf("MaxColumnsInOrderBy");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInSelect()
	 */
	public int getMaxColumnsInSelect() throws SQLException {
		return getIntFromConf("MaxColumnsInSelect");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInTable()
	 */
	public int getMaxColumnsInTable() throws SQLException {
		return getIntFromConf("MaxColumnsInTable");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxConnections()
	 */
	public int getMaxConnections() throws SQLException {
		return getIntFromConf("MaxConnections");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxCursorNameLength()
	 */
	public int getMaxCursorNameLength() throws SQLException {
		return getIntFromConf("MaxCursorNameLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxIndexLength()
	 */
	public int getMaxIndexLength() throws SQLException {
		return getIntFromConf("MaxIndexLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxProcedureNameLength()
	 */
	public int getMaxProcedureNameLength() throws SQLException {
		return getIntFromConf("MaxProcedureNameLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxRowSize()
	 */
	public int getMaxRowSize() throws SQLException {
		return getIntFromConf("MaxRowSize");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxSchemaNameLength()
	 */
	public int getMaxSchemaNameLength() throws SQLException {
		return getIntFromConf("MaxSchemaNameLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxStatementLength()
	 */
	public int getMaxStatementLength() throws SQLException {
		return getIntFromConf("MaxStatementLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxStatements()
	 */
	public int getMaxStatements() throws SQLException {
		return getIntFromConf("MaxStatements");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxTableNameLength()
	 */
	public int getMaxTableNameLength() throws SQLException {
		return getIntFromConf("MaxTableNameLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxTablesInSelect()
	 */
	public int getMaxTablesInSelect() throws SQLException {
		return getIntFromConf("MaxTablesInSelect");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxUserNameLength()
	 */
	public int getMaxUserNameLength() throws SQLException {
		return getIntFromConf("MaxUserNameLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getResultSetHoldability()
	 */
	public int getResultSetHoldability() throws SQLException {
		return getIntFromConf("ResultSetHoldability");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSQLStateType()
	 */
	public int get() throws SQLException {
		return getIntFromConf("SQLStateType");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#allProceduresAreCallable()
	 */
	public boolean allProceduresAreCallable() throws SQLException {
		return getBooleanFromConf("ProceduresAreCallable");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#allTablesAreSelectable()
	 */
	public boolean allTablesAreSelectable() throws SQLException {
		return getBooleanFromConf("allTablesAreSelectable");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()
	 */
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		return getBooleanFromConf("dataDefinitionCausesTransactionCommit");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#dataDefinitionIgnoredInTransactions()
	 */
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		return getBooleanFromConf("dataDefinitionIgnoredInTransactions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#doesMaxRowSizeIncludeBlobs()
	 */
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		return getBooleanFromConf("doesMaxRowSizeIncludeBlobs");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#isCatalogAtStart()
	 */
	public boolean isCatalogAtStart() throws SQLException {
		return getBooleanFromConf("isCatalogAtStart");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#isReadOnly()
	 */
	public boolean isReadOnly() throws SQLException {
		return getBooleanFromConf("isReadOnly");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#locatorsUpdateCopy()
	 */
	public boolean locatorsUpdateCopy() throws SQLException {
		return getBooleanFromConf("locatorsUpdateCopy");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#nullPlusNonNullIsNull()
	 */
	public boolean nullPlusNonNullIsNull() throws SQLException {
		return getBooleanFromConf("nullPlusNonNullIsNull");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#nullsAreSortedAtEnd()
	 */
	public boolean nullsAreSortedAtEnd() throws SQLException {
		return getBooleanFromConf("nullsAreSortedAtEnd");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#nullsAreSortedAtStart()
	 */
	public boolean nullsAreSortedAtStart() throws SQLException {
		return getBooleanFromConf("nullsAreSortedAtStart");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#nullsAreSortedHigh()
	 */
	public boolean nullsAreSortedHigh() throws SQLException {
		return getBooleanFromConf("nullsAreSortedHigh");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#nullsAreSortedLow()
	 */
	public boolean nullsAreSortedLow() throws SQLException {
		return getBooleanFromConf("nullsAreSortedLow");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesLowerCaseIdentifiers()
	 */
	public boolean storesLowerCaseIdentifiers() throws SQLException {
		return getBooleanFromConf("storesLowerCaseIdentifiers");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesLowerCaseQuotedIdentifiers()
	 */
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		return getBooleanFromConf("storesLowerCaseQuotedIdentifiers");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesMixedCaseIdentifiers()
	 */
	public boolean storesMixedCaseIdentifiers() throws SQLException {
		return getBooleanFromConf("storesMixedCaseIdentifiers");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesMixedCaseQuotedIdentifiers()
	 */
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		return getBooleanFromConf("storesMixedCaseQuotedIdentifiers");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesUpperCaseIdentifiers()
	 */
	public boolean storesUpperCaseIdentifiers() throws SQLException {
		return getBooleanFromConf("storesUpperCaseIdentifiers");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesUpperCaseQuotedIdentifiers()
	 */
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		return getBooleanFromConf("storesUpperCaseQuotedIdentifiers");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsANSI92EntryLevelSQL()
	 */
	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
		return getBooleanFromConf("supportsANSI92EntryLevelSQL");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsANSI92FullSQL()
	 */
	public boolean supportsANSI92FullSQL() throws SQLException {
		return getBooleanFromConf("supportsANSI92FullSQL");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsANSI92IntermediateSQL()
	 */
	public boolean supportsANSI92IntermediateSQL() throws SQLException {
		return getBooleanFromConf("supportsANSI92IntermediateSQL");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsAlterTableWithAddColumn()
	 */
	public boolean supportsAlterTableWithAddColumn() throws SQLException {
		return getBooleanFromConf("supportsAlterTableWithAddColumn");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsAlterTableWithDropColumn()
	 */
	public boolean supportsAlterTableWithDropColumn() throws SQLException {
		return getBooleanFromConf("supportsAlterTableWithDropColumn");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsBatchUpdates()
	 */
	public boolean supportsBatchUpdates() throws SQLException {
		return getBooleanFromConf("supportsBatchUpdates");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInDataManipulation()
	 */
	public boolean supportsCatalogsInDataManipulation() throws SQLException {
		return getBooleanFromConf("supportsCatalogsInDataManipulation");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInIndexDefinitions()
	 */
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		return getBooleanFromConf("supportsCatalogsInIndexDefinitions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInPrivilegeDefinitions()
	 */
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		return getBooleanFromConf("supportsCatalogsInPrivilegeDefinitions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInProcedureCalls()
	 */
	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
		return getBooleanFromConf("supportsCatalogsInProcedureCalls");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInTableDefinitions()
	 */
	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
		return getBooleanFromConf("supportsCatalogsInTableDefinitions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsColumnAliasing()
	 */
	public boolean supportsColumnAliasing() throws SQLException {
		return getBooleanFromConf("supportsColumnAliasing");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsConvert()
	 */
	public boolean supportsConvert() throws SQLException {
		return getBooleanFromConf("supportsConvert");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCoreSQLGrammar()
	 */
	public boolean supportsCoreSQLGrammar() throws SQLException {
		return getBooleanFromConf("supportsCoreSQLGrammar");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCorrelatedSubqueries()
	 */
	public boolean supportsCorrelatedSubqueries() throws SQLException {
		return getBooleanFromConf("supportsCorrelatedSubqueries");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsDataDefinitionAndDataManipulationTransactions()
	 */
	public boolean supportsDataDefinitionAndDataManipulationTransactions()
			throws SQLException {
		return getBooleanFromConf("supportsDataDefinitionAndDataManipulationTransactions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsDataManipulationTransactionsOnly()
	 */
	public boolean supportsDataManipulationTransactionsOnly()
			throws SQLException {
		return getBooleanFromConf("supportsDataManipulationTransactionsOnly");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsDifferentTableCorrelationNames()
	 */
	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsExpressionsInOrderBy()
	 */
	public boolean supportsExpressionsInOrderBy() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsExtendedSQLGrammar()
	 */
	public boolean supportsExtendedSQLGrammar() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsFullOuterJoins()
	 */
	public boolean supportsFullOuterJoins() throws SQLException {
		return getBooleanFromConf("supportsFullOuterJoins");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsGetGeneratedKeys()
	 */
	public boolean supportsGetGeneratedKeys() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsGroupBy()
	 */
	public boolean supportsGroupBy() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsGroupByBeyondSelect()
	 */
	public boolean supportsGroupByBeyondSelect() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsGroupByUnrelated()
	 */
	public boolean supportsGroupByUnrelated() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsIntegrityEnhancementFacility()
	 */
	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsLikeEscapeClause()
	 */
	public boolean supportsLikeEscapeClause() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsLimitedOuterJoins()
	 */
	public boolean supportsLimitedOuterJoins() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMinimumSQLGrammar()
	 */
	public boolean supportsMinimumSQLGrammar() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMixedCaseIdentifiers()
	 */
	public boolean supportsMixedCaseIdentifiers() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMixedCaseQuotedIdentifiers()
	 */
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMultipleOpenResults()
	 */
	public boolean supportsMultipleOpenResults() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMultipleResultSets()
	 */
	public boolean supportsMultipleResultSets() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMultipleTransactions()
	 */
	public boolean supportsMultipleTransactions() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsNamedParameters()
	 */
	public boolean supportsNamedParameters() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsNonNullableColumns()
	 */
	public boolean supportsNonNullableColumns() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOpenCursorsAcrossCommit()
	 */
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOpenCursorsAcrossRollback()
	 */
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOpenStatementsAcrossCommit()
	 */
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOpenStatementsAcrossRollback()
	 */
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOrderByUnrelated()
	 */
	public boolean supportsOrderByUnrelated() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOuterJoins()
	 */
	public boolean supportsOuterJoins() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsPositionedDelete()
	 */
	public boolean supportsPositionedDelete() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsPositionedUpdate()
	 */
	public boolean supportsPositionedUpdate() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSavepoints()
	 */
	public boolean supportsSavepoints() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSchemasInDataManipulation()
	 */
	public boolean supportsSchemasInDataManipulation() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSchemasInIndexDefinitions()
	 */
	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSchemasInPrivilegeDefinitions()
	 */
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSchemasInProcedureCalls()
	 */
	public boolean supportsSchemasInProcedureCalls() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSchemasInTableDefinitions()
	 */
	public boolean supportsSchemasInTableDefinitions() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSelectForUpdate()
	 */
	public boolean supportsSelectForUpdate() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsStatementPooling()
	 */
	public boolean supportsStatementPooling() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsStoredProcedures()
	 */
	public boolean supportsStoredProcedures() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSubqueriesInComparisons()
	 */
	public boolean supportsSubqueriesInComparisons() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSubqueriesInExists()
	 */
	public boolean supportsSubqueriesInExists() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSubqueriesInIns()
	 */
	public boolean supportsSubqueriesInIns() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSubqueriesInQuantifieds()
	 */
	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsTableCorrelationNames()
	 */
	public boolean supportsTableCorrelationNames() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsTransactions()
	 */
	public boolean supportsTransactions() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsUnion()
	 */
	public boolean supportsUnion() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsUnionAll()
	 */
	public boolean supportsUnionAll() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#usesLocalFilePerTable()
	 */
	public boolean usesLocalFilePerTable() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#usesLocalFiles()
	 */
	public boolean usesLocalFiles() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#deletesAreDetected(int)
	 */
	public boolean deletesAreDetected(int type) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#insertsAreDetected(int)
	 */
	public boolean insertsAreDetected(int type) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#othersDeletesAreVisible(int)
	 */
	public boolean othersDeletesAreVisible(int type) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#othersInsertsAreVisible(int)
	 */
	public boolean othersInsertsAreVisible(int type) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#othersUpdatesAreVisible(int)
	 */
	public boolean othersUpdatesAreVisible(int type) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#ownDeletesAreVisible(int)
	 */
	public boolean ownDeletesAreVisible(int type) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#ownInsertsAreVisible(int)
	 */
	public boolean ownInsertsAreVisible(int type) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#ownUpdatesAreVisible(int)
	 */
	public boolean ownUpdatesAreVisible(int type) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsResultSetHoldability(int)
	 */
	public boolean supportsResultSetHoldability(int holdability)
			throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsResultSetType(int)
	 */
	public boolean supportsResultSetType(int type) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsTransactionIsolationLevel(int)
	 */
	public boolean supportsTransactionIsolationLevel(int level)
			throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#updatesAreDetected(int)
	 */
	public boolean updatesAreDetected(int type) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsConvert(int, int)
	 */
	public boolean supportsConvert(int fromType, int toType)
			throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsResultSetConcurrency(int, int)
	 */
	public boolean supportsResultSetConcurrency(int type, int concurrency)
			throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getCatalogSeparator()
	 */
	public String getCatalogSeparator() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getCatalogTerm()
	 */
	public String getCatalogTerm() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDatabaseProductName()
	 */
	public String getDatabaseProductName() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDatabaseProductVersion()
	 */
	public String getDatabaseProductVersion() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDriverName()
	 */
	public String getDriverName() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDriverVersion()
	 */
	public String getDriverVersion() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getExtraNameCharacters()
	 */
	public String getExtraNameCharacters() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getIdentifierQuoteString()
	 */
	public String getIdentifierQuoteString() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getNumericFunctions()
	 */
	public String getNumericFunctions() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getProcedureTerm()
	 */
	public String getProcedureTerm() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSQLKeywords()
	 */
	public String getSQLKeywords() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSchemaTerm()
	 */
	public String getSchemaTerm() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSearchStringEscape()
	 */
	public String getSearchStringEscape() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getStringFunctions()
	 */
	public String getStringFunctions() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSystemFunctions()
	 */
	public String getSystemFunctions() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTimeDateFunctions()
	 */
	public String getTimeDateFunctions() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getURL()
	 */
	public String getURL() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getUserName()
	 */
	public String getUserName() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getCatalogs()
	 */
	public ResultSet getCatalogs() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSchemas()
	 */
	public ResultSet getSchemas() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTableTypes()
	 */
	public ResultSet getTableTypes() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTypeInfo()
	 */
	public ResultSet getTypeInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getExportedKeys(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getExportedKeys(String catalog, String schema, String table)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getImportedKeys(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getImportedKeys(String catalog, String schema, String table)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getPrimaryKeys(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getPrimaryKeys(String catalog, String schema, String table)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getProcedures(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getProcedures(String catalog, String schemaPattern,
			String procedureNamePattern) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSuperTables(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getSuperTables(String catalog, String schemaPattern,
			String tableNamePattern) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSuperTypes(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getSuperTypes(String catalog, String schemaPattern,
			String typeNamePattern) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTablePrivileges(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getTablePrivileges(String catalog, String schemaPattern,
			String tableNamePattern) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getVersionColumns(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getVersionColumns(String catalog, String schema,
			String table) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getBestRowIdentifier(java.lang.String, java.lang.String, java.lang.String, int, boolean)
	 */
	public ResultSet getBestRowIdentifier(String catalog, String schema,
			String table, int scope, boolean nullable) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getIndexInfo(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
	 */
	public ResultSet getIndexInfo(String catalog, String schema, String table,
			boolean unique, boolean approximate) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getUDTs(java.lang.String, java.lang.String, java.lang.String, int[])
	 */
	public ResultSet getUDTs(String catalog, String schemaPattern,
			String typeNamePattern, int[] types) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getAttributes(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getAttributes(String catalog, String schemaPattern,
			String typeNamePattern, String attributeNamePattern)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getColumnPrivileges(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getColumnPrivileges(String catalog, String schema,
			String table, String columnNamePattern) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getColumns(String catalog, String schemaPattern,
			String tableNamePattern, String columnNamePattern)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getProcedureColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getProcedureColumns(String catalog, String schemaPattern,
			String procedureNamePattern, String columnNamePattern)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTables(java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	public ResultSet getTables(String catalog, String schemaPattern,
			String tableNamePattern, String[] types) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getCrossReference(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getCrossReference(String primaryCatalog,
			String primarySchema, String primaryTable, String foreignCatalog,
			String foreignSchema, String foreignTable) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSQLStateType()
	 */
	public int getSQLStateType() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

}