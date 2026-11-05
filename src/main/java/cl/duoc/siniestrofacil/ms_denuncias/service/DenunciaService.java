package cl.duoc.siniestrofacil.ms_denuncias.service;

import cl.duoc.siniestrofacil.ms_denuncias.dto.DenunciaRequestDTO;
import cl.duoc.siniestrofacil.ms_denuncias.dto.DenunciaResponseDTO;
import cl.duoc.siniestrofacil.ms_denuncias.model.Denuncia;
import cl.duoc.siniestrofacil.ms_denuncias.model.Fotografia;
import cl.duoc.siniestrofacil.ms_denuncias.repository.DenunciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DenunciaService {

    @Autowired
    private DenunciaRepository denunciaRepository;

    public DenunciaResponseDTO registrarDenuncia(DenunciaRequestDTO requestDTO) {
        Denuncia denuncia = new Denuncia();
        denuncia.setRutAsegurado(requestDTO.getRutAsegurado());
        denuncia.setPatenteVehiculo(requestDTO.getPatenteVehiculo());
        denuncia.setDescripcion(requestDTO.getDescripcion());
        denuncia.setFolio(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        denuncia.setFechaRegistro(LocalDateTime.now());
        denuncia.setEstado("PENDIENTE_ASIGNACION");

        if (requestDTO.getFotografiasUrls() != null) {
            List<Fotografia> fotos = new ArrayList<>();
            for (String url : requestDTO.getFotografiasUrls()) {
                Fotografia foto = new Fotografia();
                foto.setUrlImagen(url);
                foto.setDenuncia(denuncia);
                fotos.add(foto);
            }
            denuncia.setFotografias(fotos);
        }

        Denuncia denunciaGuardada = denunciaRepository.save(denuncia);
        return mapToDTO(denunciaGuardada);
    }

    public List<DenunciaResponseDTO> obtenerTodas() {
        return denunciaRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<DenunciaResponseDTO> obtenerPorId(Long id) {
        return denunciaRepository.findById(id).map(this::mapToDTO);
    }

    public DenunciaResponseDTO actualizarEstado(Long id, String nuevoEstado) {
        return denunciaRepository.findById(id).map(denunciaExistente -> {
            denunciaExistente.setEstado(nuevoEstado);
            return mapToDTO(denunciaRepository.save(denunciaExistente));
        }).orElseThrow(() -> new RuntimeException("Denuncia no encontrada con ID: " + id));
    }

    public void eliminarDenuncia(Long id) {
        denunciaRepository.deleteById(id);
    }

    private DenunciaResponseDTO mapToDTO(Denuncia denuncia) {
        DenunciaResponseDTO dto = new DenunciaResponseDTO();
        dto.setFolio(denuncia.getFolio());
        dto.setRutAsegurado(denuncia.getRutAsegurado());
        dto.setPatenteVehiculo(denuncia.getPatenteVehiculo());
        dto.setDescripcion(denuncia.getDescripcion());
        dto.setEstado(denuncia.getEstado());
        dto.setFechaRegistro(denuncia.getFechaRegistro());

        if (denuncia.getFotografias() != null) {
            dto.setFotografiasUrls(denuncia.getFotografias()
                    .stream()
                    .map(Fotografia::getUrlImagen)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}