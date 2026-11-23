package pe.edu.agroretorno.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Registro y actualización de productores (RF03).
 * username, dni y password se exigen al registrar; al actualizar, password es opcional.
 */
public class ProductorRequest {

    @Pattern(regexp = "^[a-z0-9._]{4,30}$",
            message = "Usuario: 4 a 30 caracteres en minúscula, números, punto o guion bajo")
    private String username;

    @Size(max = 72, message = "La contraseña no puede superar 72 caracteres")
    private String password;

    @NotBlank(message = "Indique el DNI")
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe tener exactamente 8 dígitos")
    private String dni;

    @NotBlank(message = "Indique los nombres")
    @Size(max = 80, message = "Máximo 80 caracteres")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Los nombres solo pueden contener letras")
    private String nombres;

    @NotBlank(message = "Indique los apellidos")
    @Size(max = 80, message = "Máximo 80 caracteres")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Los apellidos solo pueden contener letras")
    private String apellidos;

    @Pattern(regexp = "^(9[0-9]{8})?$", message = "El teléfono debe tener 9 dígitos y empezar con 9")
    private String telefono;

    @NotBlank(message = "Indique el sector o comunidad")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String sector;

    private boolean activo = true;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
