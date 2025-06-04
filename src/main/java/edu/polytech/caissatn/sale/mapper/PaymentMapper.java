package edu.polytech.caissatn.sale.mapper;

import edu.polytech.caissatn.sale.dto.PaymentDTO;
import edu.polytech.caissatn.sale.entity.Payment;
import edu.polytech.caissatn.sale.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PaymentMapper {

    private final PaymentRepository paymentRepository;

    public PaymentMapper(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment toEntity(PaymentDTO paymentDTO) {
        if (paymentDTO == null) {
            return null;
        }

        Payment payment = null;
        if (paymentDTO.getId() != null) {
            Optional<Payment> optionalPayment = paymentRepository.findById(paymentDTO.getId());
            payment = optionalPayment.orElseGet(Payment::new);
        } else {
            payment = new Payment();
        }

        payment.setType(paymentDTO.getType());
        payment.setAmount(paymentDTO.getAmount());

        return payment;
    }

    public List<Payment> toEntities(List<PaymentDTO> paymentDTOs) {
        if (paymentDTOs == null || paymentDTOs.isEmpty()) {
            return Collections.emptyList();
        }
        return paymentDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public PaymentDTO toDTO(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setId(payment.getId());
        paymentDTO.setType(payment.getType());
        paymentDTO.setAmount(payment.getAmount());

        return paymentDTO;
    }

    public List<PaymentDTO> toDTOs(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) {
            return Collections.emptyList();
        }
        return payments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
