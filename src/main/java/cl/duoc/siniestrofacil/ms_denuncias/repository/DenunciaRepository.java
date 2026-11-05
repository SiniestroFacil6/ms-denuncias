package cl.duoc.siniestrofacil.ms_denuncias.repository;

import cl.duoc.siniestrofacil.ms_denuncias.model.Denuncia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {
}