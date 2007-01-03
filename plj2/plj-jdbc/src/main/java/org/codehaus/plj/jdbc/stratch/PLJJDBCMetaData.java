/*
 * Created on Jul 10, 2004
 */

package org.codehaus.plj.jdbc.stratch;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author Laszlo Hornyak
 */
public class PLJJDBCMetaData implements DatabaseMetaData {

	protected PLJJDBCMetaData(PLJJDBCConnection conn) {
		this.conn = conn;
	}

	private PLJJDBCConnection conn = null;


	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDatabaseMajorVersion()
	 */
	public int getDatabaseMajorVersion() throws SQLException {
		return conn.getIntFromConf("DatabaseMajorVersion");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDatabaseMinorVersion()
	 */
	public int getDatabaseMinorVersion() throws SQLException {
		return conn.getIntFromConf("DatabaseMinorVersion");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDefaultTransactionIsolation()
	 */
	public int getDefaultTransactionIsolation() throws SQLException {
		return conn.getIntFromConf("DefaultTransactionIsolation");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDriverMajorVersion()
	 */
	public int getDriverMajorVersion() {
		try {
			return conn.getIntFromConf("DriverMajorVersion");
		} catch (SQLException e) {
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDriverMinorVersion()
	 */
	public int getDriverMinorVersion() {
		try {
			return conn.getIntFromConf("DriverMinorVersion");
		} catch (SQLException e) {
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getJDBCMajorVersion()
	 */
	public int getJDBCMajorVersion() throws SQLException {
		return conn.getIntFromConf("JDBCMajorVersion");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getJDBCMinorVersion()
	 */
	public int getJDBCMinorVersion() throws SQLException {
		return conn.getIntFromConf("JDBCMinorVersion");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxBinaryLiteralLength()
	 */
	public int getMaxBinaryLiteralLength() throws SQLException {
		return conn.getIntFromConf("MaxBinaryLiteralLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxCatalogNameLength()
	 */
	public int getMaxCatalogNameLength() throws SQLException {
		return conn.getIntFromConf("MaxCatalogNameLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxCharLiteralLength()
	 */
	public int getMaxCharLiteralLength() throws SQLException {
		return conn.getIntFromConf("MaxCharLiteralLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnNameLength()
	 */
	public int getMaxColumnNameLength() throws SQLException {
		return conn.getIntFromConf("MaxColumnNameLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInGroupBy()
	 */
	public int getMaxColumnsInGroupBy() throws SQLException {
		return conn.getIntFromConf("MaxColumnsInGroupBy");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInIndex()
	 */
	public int getMaxColumnsInIndex() throws SQLException {
		return conn.getIntFromConf("MaxColumnsInIndex");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInOrderBy()
	 */
	public int getMaxColumnsInOrderBy() throws SQLException {
		return conn.getIntFromConf("MaxColumnsInOrderBy");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInSelect()
	 */
	public int getMaxColumnsInSelect() throws SQLException {
		return conn.getIntFromConf("MaxColumnsInSelect");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInTable()
	 */
	public int getMaxColumnsInTable() throws SQLException {
		return conn.getIntFromConf("MaxColumnsInTable");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxConnections()
	 */
	public int getMaxConnections() throws SQLException {
		return conn.getIntFromConf("MaxConnections");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxCursorNameLength()
	 */
	public int getMaxCursorNameLength() throws SQLException {
		return conn.getIntFromConf("MaxCursorNameLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxIndexLength()
	 */
	public int getMaxIndexLength() throws SQLException {
		return conn.getIntFromConf("MaxIndexLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxProcedureNameLength()
	 */
	public int getMaxProcedureNameLength() throws SQLException {
		return conn.getIntFromConf("MaxProcedureNameLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxRowSize()
	 */
	public int getMaxRowSize() throws SQLException {
		return conn.getIntFromConf("MaxRowSize");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxSchemaNameLength()
	 */
	public int getMaxSchemaNameLength() throws SQLException {
		return conn.getIntFromConf("MaxSchemaNameLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxStatementLength()
	 */
	public int getMaxStatementLength() throws SQLException {
		return conn.getIntFromConf("MaxStatementLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxStatements()
	 */
	public int getMaxStatements() throws SQLException {
		return conn.getIntFromConf("MaxStatements");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxTableNameLength()
	 */
	public int getMaxTableNameLength() throws SQLException {
		return conn.getIntFromConf("MaxTableNameLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxTablesInSelect()
	 */
	public int getMaxTablesInSelect() throws SQLException {
		return conn.getIntFromConf("MaxTablesInSelect");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxUserNameLength()
	 */
	public int getMaxUserNameLength() throws SQLException {
		return conn.getIntFromConf("MaxUserNameLength");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getResultSetHoldability()
	 */
	public int getResultSetHoldability() throws SQLException {
		return conn.getIntFromConf("ResultSetHoldability");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSQLStateType()
	 */
	public int get() throws SQLException {
		return conn.getIntFromConf("SQLStateType");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#allProceduresAreCallable()
	 */
	public boolean allProceduresAreCallable() throws SQLException {
		return conn.getBooleanFromConf("ProceduresAreCallable");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#allTablesAreSelectable()
	 */
	public boolean allTablesAreSelectable() throws SQLException {
		return conn.getBooleanFromConf("allTablesAreSelectable");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()
	 */
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		return conn.getBooleanFromConf("dataDefinitionCausesTransactionCommit");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#dataDefinitionIgnoredInTransactions()
	 */
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		return conn.getBooleanFromConf("dataDefinitionIgnoredInTransactions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#doesMaxRowSizeIncludeBlobs()
	 */
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		return conn.getBooleanFromConf("doesMaxRowSizeIncludeBlobs");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#isCatalogAtStart()
	 */
	public boolean isCatalogAtStart() throws SQLException {
		return conn.getBooleanFromConf("isCatalogAtStart");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#isReadOnly()
	 */
	public boolean isReadOnly() throws SQLException {
		return conn.getBooleanFromConf("isReadOnly");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#locatorsUpdateCopy()
	 */
	public boolean locatorsUpdateCopy() throws SQLException {
		return conn.getBooleanFromConf("locatorsUpdateCopy");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#nullPlusNonNullIsNull()
	 */
	public boolean nullPlusNonNullIsNull() throws SQLException {
		return conn.getBooleanFromConf("nullPlusNonNullIsNull");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#nullsAreSortedAtEnd()
	 */
	public boolean nullsAreSortedAtEnd() throws SQLException {
		return conn.getBooleanFromConf("nullsAreSortedAtEnd");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#nullsAreSortedAtStart()
	 */
	public boolean nullsAreSortedAtStart() throws SQLException {
		return conn.getBooleanFromConf("nullsAreSortedAtStart");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#nullsAreSortedHigh()
	 */
	public boolean nullsAreSortedHigh() throws SQLException {
		return conn.getBooleanFromConf("nullsAreSortedHigh");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#nullsAreSortedLow()
	 */
	public boolean nullsAreSortedLow() throws SQLException {
		return conn.getBooleanFromConf("nullsAreSortedLow");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesLowerCaseIdentifiers()
	 */
	public boolean storesLowerCaseIdentifiers() throws SQLException {
		return conn.getBooleanFromConf("storesLowerCaseIdentifiers");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesLowerCaseQuotedIdentifiers()
	 */
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		return conn.getBooleanFromConf("storesLowerCaseQuotedIdentifiers");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesMixedCaseIdentifiers()
	 */
	public boolean storesMixedCaseIdentifiers() throws SQLException {
		return conn.getBooleanFromConf("storesMixedCaseIdentifiers");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesMixedCaseQuotedIdentifiers()
	 */
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		return conn.getBooleanFromConf("storesMixedCaseQuotedIdentifiers");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesUpperCaseIdentifiers()
	 */
	public boolean storesUpperCaseIdentifiers() throws SQLException {
		return conn.getBooleanFromConf("storesUpperCaseIdentifiers");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesUpperCaseQuotedIdentifiers()
	 */
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		return conn.getBooleanFromConf("storesUpperCaseQuotedIdentifiers");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsANSI92EntryLevelSQL()
	 */
	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
		return conn.getBooleanFromConf("supportsANSI92EntryLevelSQL");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsANSI92FullSQL()
	 */
	public boolean supportsANSI92FullSQL() throws SQLException {
		return conn.getBooleanFromConf("supportsANSI92FullSQL");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsANSI92IntermediateSQL()
	 */
	public boolean supportsANSI92IntermediateSQL() throws SQLException {
		return conn.getBooleanFromConf("supportsANSI92IntermediateSQL");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsAlterTableWithAddColumn()
	 */
	public boolean supportsAlterTableWithAddColumn() throws SQLException {
		return conn.getBooleanFromConf("supportsAlterTableWithAddColumn");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsAlterTableWithDropColumn()
	 */
	public boolean supportsAlterTableWithDropColumn() throws SQLException {
		return conn.getBooleanFromConf("supportsAlterTableWithDropColumn");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsBatchUpdates()
	 */
	public boolean supportsBatchUpdates() throws SQLException {
		return conn.getBooleanFromConf("supportsBatchUpdates");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInDataManipulation()
	 */
	public boolean supportsCatalogsInDataManipulation() throws SQLException {
		return conn.getBooleanFromConf("supportsCatalogsInDataManipulation");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInIndexDefinitions()
	 */
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		return conn.getBooleanFromConf("supportsCatalogsInIndexDefinitions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInPrivilegeDefinitions()
	 */
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		return conn.getBooleanFromConf("supportsCatalogsInPrivilegeDefinitions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInProcedureCalls()
	 */
	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
		return conn.getBooleanFromConf("supportsCatalogsInProcedureCalls");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInTableDefinitions()
	 */
	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
		return conn.getBooleanFromConf("supportsCatalogsInTableDefinitions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsColumnAliasing()
	 */
	public boolean supportsColumnAliasing() throws SQLException {
		return conn.getBooleanFromConf("supportsColumnAliasing");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsConvert()
	 */
	public boolean supportsConvert() throws SQLException {
		return conn.getBooleanFromConf("supportsConvert");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCoreSQLGrammar()
	 */
	public boolean supportsCoreSQLGrammar() throws SQLException {
		return conn.getBooleanFromConf("supportsCoreSQLGrammar");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCorrelatedSubqueries()
	 */
	public boolean supportsCorrelatedSubqueries() throws SQLException {
		return conn.getBooleanFromConf("supportsCorrelatedSubqueries");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsDataDefinitionAndDataManipulationTransactions()
	 */
	public boolean supportsDataDefinitionAndDataManipulationTransactions()
			throws SQLException {
		return conn.getBooleanFromConf("supportsDataDefinitionAndDataManipulationTransactions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsDataManipulationTransactionsOnly()
	 */
	public boolean supportsDataManipulationTransactionsOnly()
			throws SQLException {
		return conn.getBooleanFromConf("supportsDataManipulationTransactionsOnly");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsDifferentTableCorrelationNames()
	 */
	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
		return conn.getBooleanFromConf("supportsDifferentTableCorrelationNames");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsExpressionsInOrderBy()
	 */
	public boolean supportsExpressionsInOrderBy() throws SQLException {
		return conn.getBooleanFromConf("supportsExpressionsInOrderBy");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsExtendedSQLGrammar()
	 */
	public boolean supportsExtendedSQLGrammar() throws SQLException {
		return conn.getBooleanFromConf("supportsExtendedSQLGrammar");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsFullOuterJoins()
	 */
	public boolean supportsFullOuterJoins() throws SQLException {
		return conn.getBooleanFromConf("supportsFullOuterJoins");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsGetGeneratedKeys()
	 */
	public boolean supportsGetGeneratedKeys() throws SQLException {
		return conn.getBooleanFromConf("supportsGetGeneratedKeys");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsGroupBy()
	 */
	public boolean supportsGroupBy() throws SQLException {
		return conn.getBooleanFromConf("supportsGroupBy");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsGroupByBeyondSelect()
	 */
	public boolean supportsGroupByBeyondSelect() throws SQLException {
		return conn.getBooleanFromConf("supportsGroupByBeyondSelect");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsGroupByUnrelated()
	 */
	public boolean supportsGroupByUnrelated() throws SQLException {
		return conn.getBooleanFromConf("supportsGroupByUnrelated");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsIntegrityEnhancementFacility()
	 */
	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
		return conn.getBooleanFromConf("supportsIntegrityEnhancementFacility");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsLikeEscapeClause()
	 */
	public boolean supportsLikeEscapeClause() throws SQLException {
		return conn.getBooleanFromConf("supportsLikeEscapeClause");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsLimitedOuterJoins()
	 */
	public boolean supportsLimitedOuterJoins() throws SQLException {
		return conn.getBooleanFromConf("supportsLimitedOuterJoins");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMinimumSQLGrammar()
	 */
	public boolean supportsMinimumSQLGrammar() throws SQLException {
		return conn.getBooleanFromConf("supportsMinimumSQLGrammar");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMixedCaseIdentifiers()
	 */
	public boolean supportsMixedCaseIdentifiers() throws SQLException {
		return conn.getBooleanFromConf("supportsMixedCaseIdentifiers");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMixedCaseQuotedIdentifiers()
	 */
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		return conn.getBooleanFromConf("supportsMixedCaseQuotedIdentifiers");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMultipleOpenResults()
	 */
	public boolean supportsMultipleOpenResults() throws SQLException {
		return conn.getBooleanFromConf("supportsMultipleOpenResults");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMultipleResultSets()
	 */
	public boolean supportsMultipleResultSets() throws SQLException {
		return conn.getBooleanFromConf("supportsMultipleResultSets");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMultipleTransactions()
	 */
	public boolean supportsMultipleTransactions() throws SQLException {
		return conn.getBooleanFromConf("supportsMultipleTransactions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsNamedParameters()
	 */
	public boolean supportsNamedParameters() throws SQLException {
		return conn.getBooleanFromConf("supportsNamedParameters");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsNonNullableColumns()
	 */
	public boolean supportsNonNullableColumns() throws SQLException {
		return conn.getBooleanFromConf("supportsNonNullableColumns");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOpenCursorsAcrossCommit()
	 */
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		return conn.getBooleanFromConf("supportsOpenCursorsAcrossCommit");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOpenCursorsAcrossRollback()
	 */
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		return conn.getBooleanFromConf("supportsOpenCursorsAcrossRollback");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOpenStatementsAcrossCommit()
	 */
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		return conn.getBooleanFromConf("supportsOpenStatementsAcrossCommit");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOpenStatementsAcrossRollback()
	 */
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		return conn.getBooleanFromConf("supportsOpenStatementsAcrossRollback");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOrderByUnrelated()
	 */
	public boolean supportsOrderByUnrelated() throws SQLException {
		return conn.getBooleanFromConf("supportsOrderByUnrelated");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOuterJoins()
	 */
	public boolean supportsOuterJoins() throws SQLException {
		return conn.getBooleanFromConf("supportsOuterJoins");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsPositionedDelete()
	 */
	public boolean supportsPositionedDelete() throws SQLException {
		return conn.getBooleanFromConf("supportsPositionedDelete");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsPositionedUpdate()
	 */
	public boolean supportsPositionedUpdate() throws SQLException {
		return conn.getBooleanFromConf("supportsPositionedUpdate");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSavepoints()
	 */
	public boolean supportsSavepoints() throws SQLException {
		return conn.getBooleanFromConf("supportsSavepoints");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSchemasInDataManipulation()
	 */
	public boolean supportsSchemasInDataManipulation() throws SQLException {
		return conn.getBooleanFromConf("supportsSchemasInDataManipulation");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSchemasInIndexDefinitions()
	 */
	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		return conn.getBooleanFromConf("supportsSchemasInIndexDefinitions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSchemasInPrivilegeDefinitions()
	 */
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		return conn.getBooleanFromConf("supportsSchemasInPrivilegeDefinitions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSchemasInProcedureCalls()
	 */
	public boolean supportsSchemasInProcedureCalls() throws SQLException {
		return conn.getBooleanFromConf("supportsSchemasInProcedureCalls");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSchemasInTableDefinitions()
	 */
	public boolean supportsSchemasInTableDefinitions() throws SQLException {
		return conn.getBooleanFromConf("supportsSchemasInTableDefinitions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSelectForUpdate()
	 */
	public boolean supportsSelectForUpdate() throws SQLException {
		return conn.getBooleanFromConf("supportsSelectForUpdate");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsStatementPooling()
	 */
	public boolean supportsStatementPooling() throws SQLException {
		return conn.getBooleanFromConf("supportsStatementPooling");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsStoredProcedures()
	 */
	public boolean supportsStoredProcedures() throws SQLException {
		return conn.getBooleanFromConf("supportsStoredProcedures");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSubqueriesInComparisons()
	 */
	public boolean supportsSubqueriesInComparisons() throws SQLException {
		return conn.getBooleanFromConf("supportsSubqueriesInComparisons");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSubqueriesInExists()
	 */
	public boolean supportsSubqueriesInExists() throws SQLException {
		return conn.getBooleanFromConf("supportsSubqueriesInExists");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSubqueriesInIns()
	 */
	public boolean supportsSubqueriesInIns() throws SQLException {
		return conn.getBooleanFromConf("supportsSubqueriesInIns");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSubqueriesInQuantifieds()
	 */
	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
		return conn.getBooleanFromConf("supportsSubqueriesInQuantifieds");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsTableCorrelationNames()
	 */
	public boolean supportsTableCorrelationNames() throws SQLException {
		return conn.getBooleanFromConf("supportsTableCorrelationNames");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsTransactions()
	 */
	public boolean supportsTransactions() throws SQLException {
		return conn.getBooleanFromConf("supportsTransactions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsUnion()
	 */
	public boolean supportsUnion() throws SQLException {
		return conn.getBooleanFromConf("supportsUnion");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsUnionAll()
	 */
	public boolean supportsUnionAll() throws SQLException {
		return conn.getBooleanFromConf("supportsUnionAll");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#usesLocalFilePerTable()
	 */
	public boolean usesLocalFilePerTable() throws SQLException {
		return conn.getBooleanFromConf("usesLocalFilePerTable");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#usesLocalFiles()
	 */
	public boolean usesLocalFiles() throws SQLException {
		return conn.getBooleanFromConf("usesLocalFiles");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#deletesAreDetected(int)
	 */
	public boolean deletesAreDetected(int type) throws SQLException {
		return conn.getBooleanFromConf("deletesAreDetected");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#insertsAreDetected(int)
	 */
	public boolean insertsAreDetected(int type) throws SQLException {
		return conn.getBooleanFromConf("insertsAreDetected");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#othersDeletesAreVisible(int)
	 */
	public boolean othersDeletesAreVisible(int type) throws SQLException {
		return conn.getBooleanFromConf("othersDeletesAreVisible");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#othersInsertsAreVisible(int)
	 */
	public boolean othersInsertsAreVisible(int type) throws SQLException {
		return conn.getBooleanFromConf("othersInsertsAreVisible");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#othersUpdatesAreVisible(int)
	 */
	public boolean othersUpdatesAreVisible(int type) throws SQLException {
		return conn.getBooleanFromConf("othersUpdatesAreVisible");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#ownDeletesAreVisible(int)
	 */
	public boolean ownDeletesAreVisible(int type) throws SQLException {
		return conn.getBooleanFromConf("ownDeletesAreVisible");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#ownInsertsAreVisible(int)
	 */
	public boolean ownInsertsAreVisible(int type) throws SQLException {
		return conn.getBooleanFromConf("ownInsertsAreVisible");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#ownUpdatesAreVisible(int)
	 */
	public boolean ownUpdatesAreVisible(int type) throws SQLException {
		return conn.getBooleanFromConf("ownUpdatesAreVisible");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsResultSetHoldability(int)
	 */
	public boolean supportsResultSetHoldability(int holdability)
			throws SQLException {
		return conn.getBooleanFromConf("supportsResultSetHoldability");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsResultSetType(int)
	 */
	public boolean supportsResultSetType(int type) throws SQLException {
		return conn.getBooleanFromConf("supportsResultSetType");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsTransactionIsolationLevel(int)
	 */
	public boolean supportsTransactionIsolationLevel(int level)
			throws SQLException {
		return conn.getBooleanFromConf("supportsTransactionIsolationLevel");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#updatesAreDetected(int)
	 */
	public boolean updatesAreDetected(int type) throws SQLException {
		return conn.getBooleanFromConf("updatesAreDetected");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsConvert(int, int)
	 */
	public boolean supportsConvert(int fromType, int toType)
			throws SQLException {
		return conn.getBooleanFromConf("supportsConvert");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsResultSetConcurrency(int, int)
	 */
	public boolean supportsResultSetConcurrency(int type, int concurrency)
			throws SQLException {
		return conn.getBooleanFromConf("supportsResultSetConcurrency");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getCatalogSeparator()
	 */
	public String getCatalogSeparator() throws SQLException {
		return conn.getStringFromConf("getCatalogSeparator");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getCatalogTerm()
	 */
	public String getCatalogTerm() throws SQLException {
		return conn.getStringFromConf("catalogTerm");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDatabaseProductName()
	 */
	public String getDatabaseProductName() throws SQLException {
		return conn.getStringFromConf("databaseProductName");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDatabaseProductVersion()
	 */
	public String getDatabaseProductVersion() throws SQLException {
		return conn.getStringFromConf("databaseProductVersion");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDriverName()
	 */
	public String getDriverName() throws SQLException {
		return conn.getStringFromConf("driverName");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDriverVersion()
	 */
	public String getDriverVersion() throws SQLException {
		return conn.getStringFromConf("driverVersion");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getExtraNameCharacters()
	 */
	public String getExtraNameCharacters() throws SQLException {
		return conn.getStringFromConf("extraNameCharacters");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getIdentifierQuoteString()
	 */
	public String getIdentifierQuoteString() throws SQLException {
		return conn.getStringFromConf("identifierQuoteString");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getNumericFunctions()
	 */
	public String getNumericFunctions() throws SQLException {
		return conn.getStringFromConf("numericFunctions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getProcedureTerm()
	 */
	public String getProcedureTerm() throws SQLException {
		return conn.getStringFromConf("procedureTerm");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSQLKeywords()
	 */
	public String getSQLKeywords() throws SQLException {
		return conn.getStringFromConf("SQLKeywords");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSchemaTerm()
	 */
	public String getSchemaTerm() throws SQLException {
		return conn.getStringFromConf("schemaTerm");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSearchStringEscape()
	 */
	public String getSearchStringEscape() throws SQLException {
		return conn.getStringFromConf("searchStringEscape");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getStringFunctions()
	 */
	public String getStringFunctions() throws SQLException {
		return conn.getStringFromConf("stringFunctions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSystemFunctions()
	 */
	public String getSystemFunctions() throws SQLException {
		return conn.getStringFromConf("systemFunctions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTimeDateFunctions()
	 */
	public String getTimeDateFunctions() throws SQLException {
		return conn.getStringFromConf("timeDateFunctions");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getURL()
	 */
	public String getURL() throws SQLException {
		return conn.getStringFromConf("URL");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getUserName()
	 */
	public String getUserName() throws SQLException {
		//this is worng here...
		return conn.getStringFromConf("userName");
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		return conn;
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getCatalogs()
	 */
	public ResultSet getCatalogs() throws SQLException {
		return conn.getResultSetFromConf("catalogs", null);
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSchemas()
	 */
	public ResultSet getSchemas() throws SQLException {
		return conn.getResultSetFromConf("schemas", null);
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTableTypes()
	 */
	public ResultSet getTableTypes() throws SQLException {
		return conn.getResultSetFromConf("tableTypes", null);
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTypeInfo()
	 */
	public ResultSet getTypeInfo() throws SQLException {
		return conn.getResultSetFromConf("typeInfo", null);
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getExportedKeys(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getExportedKeys(String catalog, String schema, String table)
			throws SQLException {
		return conn.getResultSetFromConf("exportedKeys", new Object[]{catalog,
				schema, table});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getImportedKeys(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getImportedKeys(String catalog, String schema, String table)
			throws SQLException {
		return conn.getResultSetFromConf("importedKeys", new Object[]{catalog,
				schema, table});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getPrimaryKeys(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getPrimaryKeys(String catalog, String schema, String table)
			throws SQLException {
		return conn.getResultSetFromConf("primaryKeys", new Object[]{catalog,
				schema, table});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getProcedures(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getProcedures(String catalog, String schemaPattern,
			String procedureNamePattern) throws SQLException {
		return conn.getResultSetFromConf("procedures", new Object[]{catalog,
				schemaPattern, procedureNamePattern});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSuperTables(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getSuperTables(String catalog, String schemaPattern,
			String tableNamePattern) throws SQLException {
		return conn.getResultSetFromConf("superTables", new Object[]{catalog,
				schemaPattern, tableNamePattern});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSuperTypes(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getSuperTypes(String catalog, String schemaPattern,
			String typeNamePattern) throws SQLException {
		return conn.getResultSetFromConf("superTypes", new Object[]{catalog,
				schemaPattern, typeNamePattern});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTablePrivileges(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getTablePrivileges(String catalog, String schemaPattern,
			String tableNamePattern) throws SQLException {
		return conn.getResultSetFromConf("tablePrivileges", new Object[]{catalog,
				schemaPattern, tableNamePattern});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getVersionColumns(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getVersionColumns(String catalog, String schema,
			String table) throws SQLException {
		return conn.getResultSetFromConf("versionColumns", new Object[]{schema,
				table});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getBestRowIdentifier(java.lang.String, java.lang.String, java.lang.String, int, boolean)
	 */
	public ResultSet getBestRowIdentifier(String catalog, String schema,
			String table, int scope, boolean nullable) throws SQLException {
		return conn.getResultSetFromConf("bestRowIdentifier", new Object[]{catalog,
				schema, table, new Integer(scope), new Boolean(nullable)});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getIndexInfo(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
	 */
	public ResultSet getIndexInfo(String catalog, String schema, String table,
			boolean unique, boolean approximate) throws SQLException {
		return conn.getResultSetFromConf("indexInfo", new Object[]{catalog, schema,
				table, new Boolean(unique), new Boolean(approximate)});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getUDTs(java.lang.String, java.lang.String, java.lang.String, int[])
	 */
	public ResultSet getUDTs(String catalog, String schemaPattern,
			String typeNamePattern, int[] types) throws SQLException {
		return conn.getResultSetFromConf("UDTs", new Object[]{catalog,
				schemaPattern, typeNamePattern, types});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getAttributes(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getAttributes(String catalog, String schemaPattern,
			String typeNamePattern, String attributeNamePattern)
			throws SQLException {
		return conn.getResultSetFromConf("attributes", new Object[]{catalog,
				schemaPattern, typeNamePattern, attributeNamePattern});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getColumnPrivileges(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getColumnPrivileges(String catalog, String schema,
			String table, String columnNamePattern) throws SQLException {
		return conn.getResultSetFromConf("columnPrivileges", new Object[]{catalog,
				schema, table, columnNamePattern});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getColumns(String catalog, String schemaPattern,
			String tableNamePattern, String columnNamePattern)
			throws SQLException {
		return conn.getResultSetFromConf("columns", new Object[]{catalog,
				schemaPattern, tableNamePattern, columnNamePattern});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getProcedureColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getProcedureColumns(String catalog, String schemaPattern,
			String procedureNamePattern, String columnNamePattern)
			throws SQLException {
		return conn.getResultSetFromConf("procedureColumns", new Object[]{catalog,
				schemaPattern, procedureNamePattern, columnNamePattern});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTables(java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	public ResultSet getTables(String catalog, String schemaPattern,
			String tableNamePattern, String[] types) throws SQLException {
		return conn.getResultSetFromConf("tables", new Object[]{catalog,
				schemaPattern, tableNamePattern, types});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getCrossReference(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getCrossReference(String primaryCatalog,
			String primarySchema, String primaryTable, String foreignCatalog,
			String foreignSchema, String foreignTable) throws SQLException {
		return conn.getResultSetFromConf("crossReference", new Object[]{
				primaryCatalog, primarySchema, primaryTable, foreignCatalog,
				foreignSchema, foreignTable});
	}

	/* (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSQLStateType()
	 */
	public int getSQLStateType() throws SQLException {
		return conn.getIntFromConf("getSQLStateType");
	}

}