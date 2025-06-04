package edu.polytech.caissatn.sale.mapper;



import edu.polytech.caissatn.sale.dto.RegisterDTO;
import edu.polytech.caissatn.sale.entity.Register;
import edu.polytech.caissatn.sale.repository.RegisterRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RegisterMapper {
    private final RegisterRepository registerRepository;

    public RegisterMapper(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    public Register toEntity(RegisterDTO registerDTO) {
        if (registerDTO == null) {
            return null;
        }

        Register register;
        if (registerDTO.getId() != null) {
            register = this.registerRepository.findById(registerDTO.getId())
                    .orElse(new Register());  // Évite NoSuchElementException
        } else {
            register = new Register();
        }

        register.setStartDate(registerDTO.getStartDate());
        register.setOpeningCash(registerDTO.getOpeningCash());
        register.setEndDate(registerDTO.getEndDate());
        register.setClosingCash(registerDTO.getClosingCash());
        register.setNote(registerDTO.getNote());
        register.setExpectedCash(registerDTO.getExpectedCash());

        return register;
    }

    public List<Register> toEntities(List<RegisterDTO> registerDTOs) {
        if (registerDTOs == null || registerDTOs.isEmpty()) {
            return List.of();  // Retourne une liste vide, pas null
        }
        return registerDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public RegisterDTO toDTO(Register register) {
        if (register == null) {
            return null;
        }

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setId(register.getId());
        registerDTO.setStartDate(register.getStartDate());
        registerDTO.setOpeningCash(register.getOpeningCash());
        registerDTO.setEndDate(register.getEndDate());
        registerDTO.setClosingCash(register.getClosingCash());
        registerDTO.setNote(register.getNote());
        registerDTO.setExpectedCash(register.getExpectedCash());

        return registerDTO;
    }

    public List<RegisterDTO> toDTOs(List<Register> registers) {
        if (registers == null || registers.isEmpty()) {
            return List.of();  // Retourne une liste vide, pas null
        }
        return registers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}


