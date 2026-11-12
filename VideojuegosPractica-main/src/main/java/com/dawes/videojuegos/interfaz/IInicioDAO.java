package com.dawes.videojuegos.interfaz;

import com.dawes.videojuegos.DTO.ContadoresInicioDTO;

import java.sql.SQLException;
import java.time.LocalDate;

public interface IInicioDAO {
	ContadoresInicioDTO obtenerContadores(LocalDate hoy) throws SQLException;
}
