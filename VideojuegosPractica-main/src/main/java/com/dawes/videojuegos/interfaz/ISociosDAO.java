package com.dawes.videojuegos.interfaz;

import com.dawes.videojuegos.DTO.DesplegableDTO;
import com.dawes.videojuegos.DTO.SociosDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface ISociosDAO {

	ArrayList<SociosDTO> listarSocios(String nombre, String apellidos, String email, Boolean activo, Integer edadMinima) throws SQLException;

	SociosDTO buscarSocio(int idSocio) throws SQLException;

	ArrayList<DesplegableDTO> listarActivosDesplegable() throws SQLException;

	boolean existeEmail(String email) throws SQLException;

	int insertarSocio(SociosDTO socio) throws SQLException;

	void cambiarActivo(int idSocio, boolean activo) throws SQLException;
}