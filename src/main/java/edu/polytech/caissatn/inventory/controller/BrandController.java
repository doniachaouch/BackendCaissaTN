package edu.polytech.caissatn.inventory.controller;

import edu.polytech.caissatn.inventory.dto.BrandDTO;
import edu.polytech.caissatn.inventory.entity.Brand;
import edu.polytech.caissatn.inventory.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BrandDTO> save(@Valid @RequestBody BrandDTO brandDTO) {
        Brand brand = brandService.save(brandDTO);
        brandDTO.setId(brand.getId());

        return ResponseEntity.status(HttpStatus.OK).body(brandDTO);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        brandService.delete(id);
    }

    @GetMapping("/get-all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BrandDTO>> getAll() {
        return ResponseEntity.ok(this.brandService.getAll());
    }
}