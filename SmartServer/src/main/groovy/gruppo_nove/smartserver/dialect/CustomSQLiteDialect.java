package gruppo_nove.smartserver.dialect;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.DialectDelegateWrapper;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.spi.JdbcTypeRegistry;

public class CustomSQLiteDialect extends Dialect {

    public CustomSQLiteDialect() {
        super();
    }

    @Override
    public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.contributeTypes(typeContributions, serviceRegistry);
        JdbcTypeRegistry jdbcTypeRegistry = typeContributions.getTypeConfiguration().getJdbcTypeRegistry();

        jdbcTypeRegistry.addDescriptor(SqlTypes.INTEGER, jdbcTypeRegistry.getDescriptor(SqlTypes.INTEGER));
        jdbcTypeRegistry.addDescriptor(SqlTypes.VARCHAR, jdbcTypeRegistry.getDescriptor(SqlTypes.VARCHAR));
        jdbcTypeRegistry.addDescriptor(SqlTypes.BOOLEAN, jdbcTypeRegistry.getDescriptor(SqlTypes.BOOLEAN));
        jdbcTypeRegistry.addDescriptor(SqlTypes.BLOB, jdbcTypeRegistry.getDescriptor(SqlTypes.BLOB));
        jdbcTypeRegistry.addDescriptor(SqlTypes.REAL, jdbcTypeRegistry.getDescriptor(SqlTypes.REAL));
    }

    @Override
    public boolean supportsIfExistsBeforeTableName() {
        return true; // Supporto per "IF EXISTS"
    }

    @Override
    public boolean dropConstraints() {
        return false; // SQLite non supporta il drop delle constraints
    }
}

