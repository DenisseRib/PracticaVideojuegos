package com.dawes.videojuegos.Repository;

import com.dawes.videojuegos.DTO.DesplegableDTO;
import com.dawes.videojuegos.DTO.SociosDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

@Repository
public class SociosRepository {
    private static final Logger logger = LoggerFactory.getLogger(SociosRepository.class);
    private static final String SQL_SELECT_SOCIOS =
            "SELECT s.id, s.nombre, s.apellidos, s.email, s.telefono, s.fecha_nacimiento, "
                    + "s.fecha_alta AS fechaAlta, s.activo, "
                    + "TIMESTAMPDIFF(YEAR, s.fecha_nacimiento, CURDATE()) AS edad, "
                    + "(SELECT COUNT(*) FROM prestamos p "
                    + "WHERE p.id_socio = s.id AND p.fecha_devolucion IS NULL) AS prestamosAbiertos "
                    + "FROM socios s";
    private final DataSource dataSource;

    public SociosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<SociosDTO> listarSocios(String nombre, String apellidos, String email, Boolean activo, Integer edadMinima) throws SQLException {
        StringBuilder sql = new StringBuilder(SQL_SELECT_SOCIOS + " WHERE 1 = 1");
        ArrayList<Object> parametros = new ArrayList<>();

        if (nombre != null && !nombre.trim().isEmpty()) {
            sql.append(" AND s.nombre LIKE ?");
            parametros.add("%" + nombre.trim() + "%");
        }
        if (apellidos != null && !apellidos.trim().isEmpty()) {
            sql.append(" AND s.apellidos LIKE ?");
            parametros.add("%" + apellidos.trim() + "%");
        }
        if (email != null && !email.trim().isEmpty()) {
            sql.append(" AND s.email LIKE ?");
            parametros.add("%" + email.trim() + "%");
        }
        if (activo != null) {
            sql.append(" AND s.activo = ?");
            parametros.add(activo);
        }
        if (edadMinima != null) {
            sql.append(" AND TIMESTAMPDIFF(YEAR, s.fecha_nacimiento, CURDATE()) >= ?");
            parametros.add(edadMinima);
        }
        sql.append(" ORDER BY s.apellidos, s.nombre");

        ArrayList<SociosDTO> listaSocios = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement sentencia = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                sentencia.setObject(i + 1, parametros.get(i));
            }
            logger.debug("Query a ejecutar: {}", sentencia);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    listaSocios.add(mapearSocio(resultado));
                }
            }
        }
        return listaSocios;
    }

    /** Devuelve el socio (con edad y préstamos abiertos) o null si no existe. */
    public SociosDTO buscarSocio(int idSocio) throws SQLException {
        String sql = SQL_SELECT_SOCIOS + " WHERE s.id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement sentencia = connection.prepareStatement(sql)) {
            sentencia.setInt(1, idSocio);
            logger.debug("Query a ejecutar: {}", sentencia);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? mapearSocio(resultado) : null;
            }
        }
    }

    /** Socios activos (id + nombre completo) para el desplegable de registrar préstamo. */
    public ArrayList<DesplegableDTO> listarActivosDesplegable() throws SQLException {
        String sql = "SELECT id, CONCAT(nombre, ' ', apellidos) AS nombreCompleto "
                + "FROM socios WHERE activo = 1 ORDER BY apellidos, nombre";
        ArrayList<DesplegableDTO> listaSocios = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement sentencia = connection.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                listaSocios.add(new DesplegableDTO(resultado.getInt("id"), resultado.getString("nombreCompleto")));
            }
        }
        return listaSocios;
    }

    public boolean existeEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM socios WHERE email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement sentencia = connection.prepareStatement(sql)) {
            sentencia.setString(1, email);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() && resultado.getInt(1) > 0;
            }
        }
    }

    public void cambiarActivo(int idSocio, boolean activo) throws SQLException {
        String sql = "UPDATE socios SET activo = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement sentencia = connection.prepareStatement(sql)) {
            sentencia.setBoolean(1, activo);
            sentencia.setInt(2, idSocio);
            logger.debug("Query a ejecutar: {}", sentencia);
            sentencia.executeUpdate();
        }
    }

    public int insertarSocio(SociosDTO socio) throws SQLException {
        String sql = "INSERT INTO socios (nombre, apellidos, email, telefono, fecha_nacimiento, fecha_alta, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement sentencia = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, socio.getNombre());
            sentencia.setString(2, socio.getApellidos());
            sentencia.setString(3, socio.getEmail());
            sentencia.setString(4, socio.getTelefono());
            sentencia.setDate(5, Date.valueOf(socio.getFechaNacimiento()));
            sentencia.setDate(6, Date.valueOf(socio.getFechaAlta()));
            sentencia.setBoolean(7, socio.isActivo());

            logger.debug("Query a ejecutar: {}", sentencia);
            sentencia.executeUpdate();

            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                }
            }
        }
        throw new SQLException("La base de datos no ha devuelto el id del socio insertado");
    }

    private SociosDTO mapearSocio(ResultSet resultado) throws SQLException {
        return new SociosDTO(
                resultado.getInt("id"),
                resultado.getString("nombre"),
                resultado.getString("apellidos"),
                resultado.getString("email"),
                resultado.getString("telefono"),
                resultado.getDate("fecha_nacimiento").toLocalDate(),
                resultado.getDate("fechaAlta").toLocalDate(),
                resultado.getBoolean("activo"),
                resultado.getInt("edad"),
                resultado.getInt("prestamosAbiertos")
        );
    }
}