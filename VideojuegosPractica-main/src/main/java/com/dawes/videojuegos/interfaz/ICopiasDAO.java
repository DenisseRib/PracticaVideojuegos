package com.dawes.videojuegos.interfaz;

import com.dawes.videojuegos.DTO.CopiaDTO;
import com.dawes.videojuegos.DTO.DesplegableDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface ICopiasDAO {

	ArrayList<DesplegableDTO> listarDisponiblesDesplegable() throws SQLException;

	CopiaDTO buscarCopia(int idCopia) throws SQLException;

	ArrayList<CopiaDTO> listarCopias(String titulo, Integer idPlataforma, String estado) throws SQLException;
}
