package com.dawes.videojuegos.DTO;

import java.time.LocalDate;

public class SociosDTO {

    private Integer idSocio;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;
    private LocalDate fechaAlta;
    private boolean activo;
    private Integer edad;
    private Integer prestamosAbiertos;

    public SociosDTO() {
    }


    public SociosDTO(String nombre, String apellidos, String email, String telefono, LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
    }

    public SociosDTO(Integer idSocio, String nombre, String apellidos, String email, String telefono, LocalDate fechaNacimiento,
                     LocalDate fechaAlta, boolean activo, Integer edad, Integer prestamosAbiertos) {
        this.idSocio = idSocio;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaAlta = fechaAlta;
        this.activo = activo;
        this.edad = edad;
        this.prestamosAbiertos = prestamosAbiertos;
    }

    public Integer getIdSocio() { return idSocio; }
    public void setIdSocio(Integer idSocio) { this.idSocio = idSocio; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public LocalDate getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta = fechaAlta; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public Integer getPrestamosAbiertos() { return prestamosAbiertos; }
    public void setPrestamosAbiertos(Integer prestamosAbiertos) { this.prestamosAbiertos = prestamosAbiertos; }
}