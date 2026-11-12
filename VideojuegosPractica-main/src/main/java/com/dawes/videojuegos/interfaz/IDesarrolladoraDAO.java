package com.dawes.videojuegos.interfaz;

import com.dawes.videojuegos.DTO.DesplegableDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IDesarrolladoraDAO {
	ArrayList<DesplegableDTO> listarDesplegable() throws SQLException;
}
