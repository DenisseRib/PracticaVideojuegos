package com.dawes.videojuegos.DTO;

public class CopiaDTO {
	private Integer id;
	private String codigoInventario;
	private String titulo;
	private Integer anioLanzamiento;
	private Integer pegi;
	private String plataforma;
	private String estado;
	private boolean activa;
	private int prestamosAbiertos;
	private boolean disponible;

	public CopiaDTO(Integer id, String codigoInventario, String titulo, Integer anioLanzamiento, Integer pegi,
	                String plataforma, String estado, boolean activa, int prestamosAbiertos) {
		this.id = id;
		this.codigoInventario = codigoInventario;
		this.titulo = titulo;
		this.anioLanzamiento = anioLanzamiento;
		this.pegi = pegi;
		this.plataforma = plataforma;
		this.estado = estado;
		this.activa = activa;
		this.prestamosAbiertos = prestamosAbiertos;
	}

	public Integer getId() { return id; }
	public String getCodigoInventario() { return codigoInventario; }
	public String getTitulo() { return titulo; }
	public Integer getAnioLanzamiento() { return anioLanzamiento; }
	public Integer getPegi() { return pegi; }
	public String getPlataforma() { return plataforma; }
	public String getEstado() { return estado; }
	public boolean isActiva() { return activa; }
	public int getPrestamosAbiertos() { return prestamosAbiertos; }
	public boolean isDisponible() { return disponible; }
	public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
