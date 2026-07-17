package br.com.signe.client.repository;

import br.com.signe.client.domain.AnamnesisRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnamnesisRecordRepository extends JpaRepository<AnamnesisRecord, UUID> {

    // como a ficha não é diretamente associada ao cliente (uma escolha que visa escalabilidade de um futuro sistema de histórico de fichas de anamnese, ao invés do campo de "observações de progresso" que seria mais simples), é necessário buscar a ficha de anamnese pelo id do cliente
    Optional<AnamnesisRecord> findByClientId(UUID clientId);

}
