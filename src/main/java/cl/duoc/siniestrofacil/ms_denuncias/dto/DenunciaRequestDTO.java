package cl.duoc.siniestrofacil.ms_denuncias.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class DenunciaRequestDTO {
    @NotBlank
    private String rutAsegurado;

    @NotBlank
    private String patenteVehiculo;

    @NotBlank
    private String descripcion;

    private List<String> fotografiasUrls;
}