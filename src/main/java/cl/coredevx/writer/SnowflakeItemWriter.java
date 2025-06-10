package cl.coredevx.writer;

import cl.coredevx.config.SnowflakeProperties;
import cl.coredevx.model.Persona;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class SnowflakeItemWriter implements ItemWriter<Persona> {

    private final SnowflakeProperties snowflakeProperties;

    public SnowflakeItemWriter(SnowflakeProperties snowflakeProperties) {
        this.snowflakeProperties = snowflakeProperties;
    }

    @Override
    public void write(Chunk<? extends Persona> chunk) throws Exception {
        Properties properties = new Properties();
        properties.put("user", snowflakeProperties.getUser());
        properties.put("password", snowflakeProperties.getPassword());
        properties.put("database", snowflakeProperties.getDatabase());
        properties.put("schema", snowflakeProperties.getSchema());
        properties.put("warehouse", snowflakeProperties.getWarehouse());

        try (Connection conn = DriverManager.getConnection(snowflakeProperties.getUrl(), properties)) {
            String sql = "INSERT INTO PERSONAS (ID, NOMBRE, CORREO) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for ( Persona persona : chunk ) {
                    stmt.setLong(1, persona.getId());
                    stmt.setString(2, persona.getNombre());
                    stmt.setString(3, persona.getCorreo());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            } catch (SQLException e) {
                throw new RuntimeException("Error writing to Snowflake", e);
            }
        };
    }

}
