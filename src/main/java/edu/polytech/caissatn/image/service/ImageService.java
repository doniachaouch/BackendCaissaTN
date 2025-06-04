//package edu.polytech.caissatn.image.service;
//
//
//
//import edu.polytech.caissatn.image.entity.Image;
//import edu.polytech.caissatn.image.repository.ImageRepository;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.StringUtils;
//import org.springframework.web.multipart.MultipartFile;
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import java.util.Objects;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//@Transactional
//
//public class ImageService {
//    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);
//    private final ImageRepository imageRepository;
//
//    private final ImageUtils imageUtils;
//
//
//    public ImageService(ImageRepository imageRepository, FileStorageProperties fileStorageProperties, ImageUtils imageUtils) {
//        this.imageRepository = imageRepository;
//        Path baseUploadPath = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
//        this.imageUtils = imageUtils;
//        logger.info("Base upload path: {}", baseUploadPath);
//    }
//
//    public Image save(MultipartFile file, String imageType) throws IOException {
//        logger.debug("Saving image with type: {} and file name: {}", imageType, file.getOriginalFilename());
//        Path tenantPath = imageUtils.getTenantDirectory();
//        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
//        String fileCode = RandomStringUtils.randomAlphanumeric(8);
//        Path imagePath = imageUtils.resolveImagePath(tenantPath, imageType, fileCode, fileName);
//
//
//        // Read the original image from the MultipartFile
//        BufferedImage originalImage;
//        try {
//            originalImage = ImageIO.read(file.getInputStream());
//            if (originalImage == null) {
//                throw new IOException("The image could not be read.");
//            }
//            logger.info("Original image size: {} pixels", originalImage.getWidth() * originalImage.getHeight());
//        } catch (IOException e) {
//            logger.error("Error reading the image from the file: {}", fileName, e);
//            throw new IOException("Error reading the image from the file: " + fileName, e);
//        }
//
//        // Resize the image using ImageUtils
//        BufferedImage resizedImage;
//        try {
//            resizedImage = imageUtils.resizeImage(originalImage, imageType);
//            logger.info("Resized image dimensions: {}x{}", resizedImage.getWidth(), resizedImage.getHeight());
//        } catch (Exception e) {
//            logger.error("Error resizing the image: {}", fileName, e);
//            throw new IOException("Error resizing the image: " + fileName, e);
//        }
//
//
//
//        // Compress the resized image to reduce the file size
//        try {
//            // Log the size of the resized image before compression
//            ByteArrayOutputStream baosBefore = new ByteArrayOutputStream();
//            ImageIO.write(resizedImage, "jpg", baosBefore);
//            long originalSize = baosBefore.size(); // Get size before compression
//            logger.info("Resized image size before compression: {} bytes", originalSize);
//
//            // Adjust the compression quality as needed (0.5f is 50% quality)
//            imageUtils.compressImageToJPEG(resizedImage, imagePath, 0.2f); // 0.5f is the compression quality
//
//            // Log the size of the compressed image
//            ByteArrayOutputStream baosAfter = new ByteArrayOutputStream();
//            ImageIO.write(resizedImage, "jpg", baosAfter);
//            long compressedSize = baosAfter.size(); // Get size after compression
//
//            // Calculate and log the reduction in size
//            long sizeReduction = originalSize - compressedSize;
//            double reductionPercentage = ((double) sizeReduction / originalSize) * 100;
//            logger.info("Compressed image size after compression: {} bytes", compressedSize);
//            logger.info("Image size reduced by: {} bytes ({}%)", sizeReduction, String.format("%.2f", reductionPercentage));
//        } catch (IOException e) {
//            logger.error("Error compressing the image: {}", fileName, e);
//            throw new IOException("Error compressing the image: " + fileName, e);
//        }
//
//        // Create and save the Image object with the file code
//        Image image = new Image();
//        image.setFileCode(fileCode);
//        try {
//            Image savedImage = imageRepository.save(image);
//            logger.info("Image saved with file code: {}", savedImage.getFileCode());
//            return savedImage;
//        } catch (Exception e) {
//            logger.error("Error saving the image object in the database", e);
//            throw new IOException("Error saving the image object in the database", e);
//        }
//
//    }
//
//
//    public byte[] get(UUID id, String imageType) throws IOException {
//        logger.debug("Retrieving image with ID: {} and imageType: {}", id, imageType);
//
//        // Fetch the image from the repository
//        Image image = imageRepository.findById(id)
//                .orElseThrow(() -> {
//                    logger.warn("Image with ID {} not found", id);
//                    return new RuntimeException("Image not found");
//                });
//
//        Path tenantPath = imageUtils.getTenantDirectory();
//        // Find the image file path
//        Optional<Path> imagePathOptional = imageUtils.findImagePath(tenantPath, image.getFileCode(), imageType);
//        if (imagePathOptional.isPresent()) {
//            // Read the image file as a BufferedImage
//            Path imagePath = imagePathOptional.get();
//            logger.info("Image found at path: {}", imagePath);
//            BufferedImage originalImage = ImageIO.read(imagePath.toFile());
//            // Resize the image using the resizeImage method
//            BufferedImage resizedImage = imageUtils.resizeImage(originalImage, imageType);
//
//            // Convert the resized image to byte array
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(resizedImage, "png", baos);  // Write the image in PNG format
//            return baos.toByteArray();
//        } else {
//            logger.error("Image with ID {} not found", id);
//            return imageUtils.getFallbackImageBytes();
//        }
//    }
//
//
//
//    public void delete(UUID id, String imageType) throws IOException {
//        logger.debug("Deleting image with ID: {} and imageType: {}", id, imageType);
//
//        Image image = imageRepository.findById(id)
//                .orElseThrow(() -> {
//                    logger.error("Image with ID {} not found for deletion", id);
//                    return new RuntimeException("Image not found for deletion");
//                });
//
//        Path tenantPath = imageUtils.getTenantDirectory();
//        Optional<Path> imagePathOptional = imageUtils.findImagePath(tenantPath, image.getFileCode(), imageType);
//
//        if (imagePathOptional.isPresent()) {
//            Files.delete(imagePathOptional.get());
//            logger.info("Image with ID {} deleted successfully", id);
//        } else {
//            logger.warn("Image file not found at expected path for deletion");
//            throw new RuntimeException("Image file not found");
//        }
//
//        imageRepository.delete(image);
//    }
//}
