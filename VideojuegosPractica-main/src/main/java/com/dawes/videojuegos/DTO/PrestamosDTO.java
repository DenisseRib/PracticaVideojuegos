package com.dawes.videojuegos.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PrestamosDTO {
	private Integer id;
	private Integer idCopia;
	private String socio;
	private String videojuego;
	private String plataforma;
	private String codigoCopia;
	private LocalDate fechaPrestamo;
	private LocalDate fechaLimite;
	private LocalDate fechaDevolucion;
	private BigDecimal multa;
	private String estado;
	private int diasRetraso;
	private int renovaciones;

	public PrestamosDTO() {
	}

	public PrestamosDTO(Integer id, Integer idCopia, String socio, String videojuego, String plataforma,
	                    String codigoCopia, LocalDate fechaPrestamo, LocalDate fechaLimite,
	                    LocalDate fechaDevolucion, BigDecimal multa, int renovaciones) {
		this.id = id;
		this.idCopia = idCopia;
		this.socio = socio;
		this.videojuego = videojuego;
		this.plataforma = plataforma;
		this.codigoCopia = codigoCopia;
		this.fechaPrestamo = fechaPrestamo;
		this.fechaLimite = fechaLimite;
		this.fechaDevolucion = fechaDevolucion;
		this.multa = multa;
		this.renovaciones = renovaciones;
	}

	public Integer getId() { return id; }
	public Integer getIdCopia() { return idCopia; }
	public String getSocio() { return socio; }
	public String getVideojuego() { return videojuego; }
	public String getPlataforma() { return plataforma; }
	public String getCodigoCopia() { return codigoCopia; }
	public LocalDate getFechaPrestamo() { return fechaPrestamo; }
	public LocalDate getFechaLimite() { return fechaLimite; }
	public LocalDate getFechaDevolucion() { return fechaDevolucion; }
	public BigDecimal getMulta() { return multa; }
	public int getRenovaciones() { return renovaciones; }
	public String getEstado() { return estado; }
	public void setEstado(String estado) { this.estado = estado; }
	public int getDiasRetraso() { return diasRetraso; }
	public void setDiasRetraso(int diasRetraso) { this.diasRetraso = diasRetraso; }
}
