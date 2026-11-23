package pe.edu.agroretorno.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import pe.edu.agroretorno.entity.CategoriaMaterial;
import pe.edu.agroretorno.entity.EstadoMaterial;

/** Registro y actualización de materiales (RF04). cantidadInicial solo se usa al registrar. */
public class MaterialRequest {

    @NotBlank(message = "Indique el código")
    @Pattern(regexp = "^[A-Za-z0-9-]{3,20}$", message = "Código: 3 a 20 letras, números o guiones")
    private String codigo;

    @NotBlank(message = "Indique el nombre")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String nombre;

    @NotNull(message = "Seleccione la categoría")
    private CategoriaMaterial categoria;

    @Size(max = 500, message = "Máximo 500 caracteres")
    private String descripcion;

    @NotBlank(message = "Indique la unidad de medida")
    @Size(max = 30, message = "Máximo 30 caracteres")
    private String unidad;

    @PositiveOrZero(message = "La cantidad inicial no puede ser negativa")
    @Max(value = 100000, message = "Cantidad fuera de rango")
    private Integer cantidadInicial = 0;

    private EstadoMaterial estado = EstadoMaterial.ACTIVO;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public CategoriaMaterial getCategoria() { return categoria; }
    public void setCategoria(CategoriaMaterial categoria) { this.categoria = categoria; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public Integer getCantidadInicial() { return cantidadInicial; }
    public void setCantidadInicial(Integer cantidadInicial) { this.cantidadInicial = cantidadInicial; }
    public EstadoMaterial getEstado() { return estado; }
    public void setEstado(EstadoMaterial estado) { this.estado = estado; }
}
