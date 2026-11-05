package cl.duoc.siniestrofacil.ms_denuncias.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DenunciaResponseDTO {
    private String folio;
    private String rutAsegurado;
    private String patenteVehiculo;
    private String descripcion;
    private String estado;
    private LocalDateTime fechaRegistro;
    private List<String> fotografiasUrls;
}