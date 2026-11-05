package cl.duoc.siniestrofacil.ms_denuncias.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Fotografia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String urlImagen;

    @ManyToOne
    @JoinColumn(name = "denuncia_id")
    @JsonIgnore
    private Denuncia denuncia;
}