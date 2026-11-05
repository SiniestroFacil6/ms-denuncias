package cl.duoc.siniestrofacil.ms_denuncias.controller;

import cl.duoc.siniestrofacil.ms_denuncias.dto.DenunciaRequestDTO;
import cl.duoc.siniestrofacil.ms_denuncias.dto.DenunciaResponseDTO;
import cl.duoc.siniestrofacil.ms_denuncias.service.DenunciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/denuncias")
public class DenunciaController {

    @Autowired
    private DenunciaService denunciaService;

    @PostMapping
    public ResponseEntity<DenunciaResponseDTO> registrarDenuncia(
            @Valid @RequestBody DenunciaRequestDTO requestDTO,
            JwtAuthenticationToken jwt) {

        DenunciaResponseDTO responseDTO = denunciaService.registrarDenuncia(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DenunciaResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(denunciaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DenunciaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return denunciaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<DenunciaResponseDTO> actualizarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(denunciaService.actualizarEstado(id, nuevoEstado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> eliminarDenuncia(@PathVariable Long id) {
        denunciaService.eliminarDenuncia(id);
        return ResponseEntity.noContent().build();
    }
}