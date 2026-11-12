package com.dawes.videojuegos.service;

import com.dawes.videojuegos.DTO.SociosDTO;
import com.dawes.videojuegos.interfaz.ISociosDAO;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.regex.Pattern;

@Service
public class SociosService {
	private static final int LONGITUD_MAXIMA_NOMBRE = 40;
	private static final int LONGITUD_MAXIMA_APELLIDOS = 80;
	private static final int LONGITUD_MAXIMA_EMAIL = 100;
	private static final Pattern PATRON_TELEFONO = Pattern.compile("^\\d{9}$");
	private static final Pattern PATRON_EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

	private final ISociosDAO sociosDAO;

	public SociosService(ISociosDAO sociosDAO) {
		this.sociosDAO = sociosDAO;
	}

	public ArrayList<SociosDTO> listarSocios(String nombre, String apellidos, String email, Boolean activo, Integer edadMinima) throws SQLException {
		return sociosDAO.listarSocios(limpiar(nombre), limpiar(apellidos), limpiar(email), activo, edadMinima);
	}

	public int insertarSocio(SociosDTO socio) throws SQLException, ValidacionException {
		ArrayList<String> errores = new ArrayList<>();

		String nombre = socio.getNombre() == null ? "" : socio.getNombre().trim();
		if (nombre.isEmpty()) {
			errores.add("El nombre es obligatorio.");
		} else if (nombre.length() > LONGITUD_MAXIMA_NOMBRE) {
			errores.add("El nombre no puede tener más de " + LONGITUD_MAXIMA_NOMBRE + " caracteres.");
		}

		String apellidos = socio.getApellidos() == null ? "" : socio.getApellidos().trim();
		if (apellidos.isEmpty()) {
			errores.add("Los apellidos son obligatorios.");
		} else if (apellidos.length() > LONGITUD_MAXIMA_APELLIDOS) {
			errores.add("Los apellidos no pueden tener más de " + LONGITUD_MAXIMA_APELLIDOS + " caracteres.");
		}

		String email = socio.getEmail() == null ? "" : socio.getEmail().trim();
		if (email.isEmpty()) {
			errores.add("El email es obligatorio.");
		} else if (email.length() > LONGITUD_MAXIMA_EMAIL || !PATRON_EMAIL.matcher(email).matches()) {
			errores.add("El email no tiene un formato válido.");
		} else if (sociosDAO.existeEmail(email)) {
			errores.add("Ya existe un socio con el email " + email);
		}

		String telefono = socio.getTelefono() == null ? "" : socio.getTelefono().trim();
		if (telefono.isEmpty()) {
			errores.add("El teléfono es obligatorio.");
		} else if (!PATRON_TELEFONO.matcher(telefono).matches()) {
			errores.add("El teléfono debe tener exactamente 9 dígitos.");
		}
		
		LocalDate fechaNacimiento = socio.getFechaNacimiento();
		if (fechaNacimiento == null) {
			errores.add("La fecha de nacimiento es obligatoria.");
		} else if (fechaNacimiento.isAfter(LocalDate.now())) {
			errores.add("La fecha de nacimiento no puede ser futura.");
		}

		if (!errores.isEmpty()) {
			throw new ValidacionException(String.join(" ", errores));
		}

		socio.setNombre(nombre);
		socio.setApellidos(apellidos);
		socio.setEmail(email);
		socio.setTelefono(telefono);
		socio.setFechaAlta(LocalDate.now());
		socio.setActivo(true);
		return sociosDAO.insertarSocio(socio);
	}

	/**
	 * Da de baja (activo = false) o reactiva (activo = true) a un socio.
	 * No se puede dar de baja a un socio que todavía tiene préstamos abiertos.
	 */
	public SociosDTO cambiarActivo(Integer idSocio, boolean activo) throws SQLException, ValidacionException {
		if (idSocio == null) {
			throw new ValidacionException("Debes indicar el socio.");
		}
		SociosDTO socio = sociosDAO.buscarSocio(idSocio);
		if (socio == null) {
			throw new ValidacionException("El socio seleccionado no existe.");
		}
		if (socio.isActivo() == activo) {
			throw new ValidacionException("El socio " + socio.getNombre() + " " + socio.getApellidos()
					+ " ya está " + (activo ? "activo" : "dado de baja") + ".");
		}
		if (!activo && socio.getPrestamosAbiertos() > 0) {
			throw new ValidacionException("No se puede dar de baja a " + socio.getNombre() + " " + socio.getApellidos()
					+ ": tiene " + socio.getPrestamosAbiertos() + " préstamo(s) abierto(s).");
		}
		sociosDAO.cambiarActivo(idSocio, activo);
		socio.setActivo(activo);
		return socio;
	}

	private String limpiar(String texto) {
		return (texto == null || texto.isBlank()) ? null : texto.trim();
	}
}