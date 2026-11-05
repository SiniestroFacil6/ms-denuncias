package cl.duoc.siniestrofacil.ms_denuncias.service;

import cl.duoc.siniestrofacil.ms_denuncias.dto.DenunciaRequestDTO;
import cl.duoc.siniestrofacil.ms_denuncias.dto.DenunciaResponseDTO;
import cl.duoc.siniestrofacil.ms_denuncias.model.Denuncia;
import cl.duoc.siniestrofacil.ms_denuncias.model.Fotografia;
import cl.duoc.siniestrofacil.ms_denuncias.repository.DenunciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DenunciaServiceTest {

    @Mock
    private DenunciaRepository denunciaRepository;

    @InjectMocks
    private DenunciaService denunciaService;

    private Denuncia denunciaMock;
    private DenunciaRequestDTO requestDTOMock;

    @BeforeEach
    void setUp() {
        denunciaMock = new Denuncia();
        denunciaMock.setId(1L);
        denunciaMock.setFolio("A1B2C3D4");
        denunciaMock.setRutAsegurado("19123456-7");
        denunciaMock.setPatenteVehiculo("AB12CD");
        denunciaMock.setDescripcion("Choque leve");
        denunciaMock.setEstado("PENDIENTE_ASIGNACION");
        denunciaMock.setFechaRegistro(LocalDateTime.now());

        Fotografia foto = new Fotografia();
        foto.setUrlImagen("http://foto.com/1.jpg");
        denunciaMock.setFotografias(List.of(foto));

        requestDTOMock = new DenunciaRequestDTO();
        requestDTOMock.setRutAsegurado("19123456-7");
        requestDTOMock.setPatenteVehiculo("AB12CD");
        requestDTOMock.setDescripcion("Choque leve");
        requestDTOMock.setFotografiasUrls(List.of("http://foto.com/1.jpg"));
    }

    @Test
    void testRegistrarDenuncia() {
        when(denunciaRepository.save(any(Denuncia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DenunciaResponseDTO response = denunciaService.registrarDenuncia(requestDTOMock);

        assertNotNull(response.getFolio());
        assertNotNull(response.getFechaRegistro());
        assertEquals("PENDIENTE_ASIGNACION", response.getEstado());
        assertEquals("19123456-7", response.getRutAsegurado());
        assertEquals(1, response.getFotografiasUrls().size());
        verify(denunciaRepository, times(1)).save(any(Denuncia.class));
    }

    @Test
    void testObtenerTodas() {
        when(denunciaRepository.findAll()).thenReturn(List.of(denunciaMock));

        List<DenunciaResponseDTO> lista = denunciaService.obtenerTodas();

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        assertEquals("A1B2C3D4", lista.get(0).getFolio());
        verify(denunciaRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPorId() {
        when(denunciaRepository.findById(1L)).thenReturn(Optional.of(denunciaMock));

        Optional<DenunciaResponseDTO> response = denunciaService.obtenerPorId(1L);

        assertTrue(response.isPresent());
        assertEquals("A1B2C3D4", response.get().getFolio());
        verify(denunciaRepository, times(1)).findById(1L);
    }

    @Test
    void testActualizarEstado_Exito() {
        when(denunciaRepository.findById(1L)).thenReturn(Optional.of(denunciaMock));
        when(denunciaRepository.save(any(Denuncia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DenunciaResponseDTO response = denunciaService.actualizarEstado(1L, "EN_REVISION");

        assertEquals("EN_REVISION", response.getEstado());
        verify(denunciaRepository, times(1)).save(any(Denuncia.class));
    }

    @Test
    void testActualizarEstado_Falla_NoEncontrado() {
        when(denunciaRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            denunciaService.actualizarEstado(1L, "EN_REVISION");
        });

        assertEquals("Denuncia no encontrada con ID: 1", exception.getMessage());
        verify(denunciaRepository, never()).save(any(Denuncia.class));
    }

    @Test
    void testEliminarDenuncia() {
        doNothing().when(denunciaRepository).deleteById(1L);

        denunciaService.eliminarDenuncia(1L);

        verify(denunciaRepository, times(1)).deleteById(1L);
    }
}