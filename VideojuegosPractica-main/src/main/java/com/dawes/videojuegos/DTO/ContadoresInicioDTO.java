package com.dawes.videojuegos.DTO;

public class ContadoresInicioDTO {
	private int videojuegos;
	private int sociosActivos;
	private int prestamosAbiertos;
	private int prestamosVencidos;
	private int copiasDanadas;

	public ContadoresInicioDTO() {
	}

	public ContadoresInicioDTO(int videojuegos, int sociosActivos, int prestamosAbiertos, int prestamosVencidos,
	                           int copiasDanadas) {
		this.videojuegos = videojuegos;
		this.sociosActivos = sociosActivos;
		this.prestamosAbiertos = prestamosAbiertos;
		this.prestamosVencidos = prestamosVencidos;
		this.copiasDanadas = copiasDanadas;
	}

	public int getVideojuegos() {
		return videojuegos;
	}

	public void setVideojuegos(int videojuegos) {
		this.videojuegos = videojuegos;
	}

	public int getSociosActivos() {
		return sociosActivos;
	}

	public void setSociosActivos(int sociosActivos) {
		this.sociosActivos = sociosActivos;
	}

	public int getPrestamosAbiertos() {
		return prestamosAbiertos;
	}

	public void setPrestamosAbiertos(int prestamosAbiertos) {
		this.prestamosAbiertos = prestamosAbiertos;
	}

	public int getPrestamosVencidos() {
		return prestamosVencidos;
	}

	public void setPrestamosVencidos(int prestamosVencidos) {
		this.prestamosVencidos = prestamosVencidos;
	}

	public int getCopiasDanadas() {
		return copiasDanadas;
	}

	public void setCopiasDanadas(int copiasDanadas) {
		this.copiasDanadas = copiasDanadas;
	}
}
