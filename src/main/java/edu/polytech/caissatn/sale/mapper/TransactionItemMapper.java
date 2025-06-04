package edu.polytech.caissatn.sale.mapper;

import edu.polytech.caissatn.inventory.repository.ProductRepository;
import edu.polytech.caissatn.sale.dto.TransactionItemDTO;
import edu.polytech.caissatn.sale.entity.TransactionItem;
import edu.polytech.caissatn.sale.repository.TransactionItemRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TransactionItemMapper {
    private final TransactionItemRepository transactionItemRepository;
    private final ProductRepository productRepository;

    public TransactionItemMapper(TransactionItemRepository transactionItemRepository, ProductRepository productRepository) {
        this.transactionItemRepository = transactionItemRepository;
        this.productRepository = productRepository;
    }

    public TransactionItem toEntity(TransactionItemDTO dto) {
        if (dto == null) return null;

        TransactionItem entity = dto.getId() != null
                ? transactionItemRepository.findById(dto.getId()).orElse(new TransactionItem())
                : new TransactionItem();

        productRepository.findById(dto.getProduct()).ifPresent(entity::setProduct);
        entity.setQty(dto.getQty());
        entity.setPrice(dto.getPrice());

        return entity;
    }

    public List<TransactionItem> toEntities(List<TransactionItemDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return Collections.emptyList();
        return dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public TransactionItemDTO toDTO(TransactionItem entity) {
        if (entity == null) return null;
        TransactionItemDTO dto = new TransactionItemDTO();
        dto.setId(entity.getId());
        dto.setProduct(entity.getProduct() != null ? entity.getProduct().getId() : null);
        dto.setQty(entity.getQty());
        dto.setPrice(entity.getPrice());
        return dto;
    }

    public List<TransactionItemDTO> toDTOs(List<TransactionItem> entities) {
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
