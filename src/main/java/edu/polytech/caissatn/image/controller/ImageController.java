//package edu.polytech.caissatn.image.controller;
//
//
//import edu.polytech.caissatn.image.dto.ImageDTO;
//import edu.polytech.caissatn.image.entity.Image;
//import edu.polytech.caissatn.image.mapper.ImageMapper;
//import edu.polytech.caissatn.image.service.ImageService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import org.springframework.http.HttpStatus;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/image")
//public class ImageController {
//    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
//    private final ImageService imageService;
//    private final ImageMapper imageMapper;
//
//
//    public ImageController(ImageService imageService, ImageMapper imageMapper) {
//        this.imageService = imageService;
//        this.imageMapper = imageMapper;
//    }
//    @PostMapping("/save")
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<ImageDTO> save(@RequestParam("image") MultipartFile file,
//                                         @RequestParam("imageType") String imageType) throws IOException {
//        logger.debug("Received request to save image with type: {}", imageType);
//
//        // Validate image type
//        if (!isValidImageType(imageType)) {
//            logger.warn("Invalid image type: {}", imageType);
//            return ResponseEntity.badRequest().body(null);
//        }
//
//
//        try {
//            Image image = imageService.save(file, imageType);
//            ImageDTO imageDTO = imageMapper.toDTO(image);
//            logger.info("Image saved successfully with file code: {}", image.getFileCode());
//            return ResponseEntity.status(HttpStatus.CREATED).body(imageDTO);
//
//        } catch (IOException e) {
//            logger.error("Error saving image: {}", file.getOriginalFilename(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//    // Helper method to validate image types
//    private boolean isValidImageType(String imageType) {
//        return "products".equalsIgnoreCase(imageType) ||
//                "brands".equalsIgnoreCase(imageType) ||
//                "categories".equalsIgnoreCase(imageType);
//    }
//
//    @GetMapping("/get/{id}")
//    public ResponseEntity<byte[]> get(@PathVariable String id, @RequestParam String imageType) throws IOException {
//        logger.debug("Received request to get image with ID: {} and imageType: {}", id, imageType);
//        try {
//            byte[] imageData = imageService.get(UUID.fromString(id), imageType);
//            return new ResponseEntity<>(imageData, HttpStatus.OK);
//        } catch (IOException e) {
//            logger.error("Error retrieving image with ID: {}", id, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//
//    @DeleteMapping("/{id}/{imageType}")
//    public ResponseEntity<Void> deleteImage(@PathVariable UUID id, @PathVariable String imageType) {
//        logger.debug("Received request to delete image with ID: {} and imageType: {}", id, imageType);
//        try {
//            // Call the delete method in ImageService
//            imageService.delete(id, imageType);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } catch (IOException e) {
//            logger.error("Error deleting image with ID: {}", id, e);
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        } catch (RuntimeException e) {
//            logger.warn("Image with ID {} not found for deletion", id);
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//}