//package edu.polytech.caissatn.image.service;
//
//
//
//import org.springframework.stereotype.Service;
//
//import javax.imageio.IIOImage;
//import javax.imageio.ImageIO;
//import javax.imageio.ImageWriteParam;
//import javax.imageio.ImageWriter;
//import javax.imageio.stream.ImageOutputStream;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Iterator;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//import java.util.UUID;
//@Service
//
//public class ImageUtils {
//    private final Path baseUploadPath;
//    private final PointOfSaleRepository posRepository;
//    private final CurrentTenantResolver currentTenantResolver;
//
//    public ImageUtils(FileStorageProperties fileStorageProperties, PointOfSaleRepository posRepository, CurrentTenantResolver currentTenantResolver) {
//        this.baseUploadPath = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
//        this.posRepository = posRepository;
//        this.currentTenantResolver = currentTenantResolver;
//    }
//    public Path resolveImagePath(Path tenantPath, String imageType, String fileCode, String fileName) {
//        return switch (imageType.toLowerCase()) {
//            case "brands" -> tenantPath.resolve("brands-images").resolve(fileCode + "-" + fileName);
//            case "categories" -> tenantPath.resolve("categories-images").resolve(fileCode + "-" + fileName);
//            case "products" -> tenantPath.resolve("products-images").resolve(fileCode + "-" + fileName);
//            default -> tenantPath.resolve("products-images").resolve(fileCode + "-" + fileName); // Default case
//        };
//    }
//
//    public Optional<Path> findImagePath(Path basePath, String fileCode, String imageType) throws IOException {
//        // Attempt to find the image in the tenant directory
//        Optional<Path> imagePathOptional;
//
//        try (var paths = Files.walk(basePath.resolve(imageType + "-images"))) {
//            imagePathOptional = paths.filter(path -> path.getFileName().toString().startsWith(fileCode)).findFirst();
//        }
//
//        if (imagePathOptional.isEmpty()) {
//            // Determine tenant's PosType
//            String tenantId = currentTenantResolver.getCurrentIdentifier();
//            System.out.println(tenantId);
//            Optional<PointOfSale> posOptional = posRepository.findById(UUID.fromString(tenantId));
//
//            PosType posType = posOptional.map(PointOfSale::getPosType)
//                    .orElseThrow(() -> new NoSuchElementException("PointOfSale not found for tenantId: " + tenantId));
//
//            // Search in the default directory for the determined PosType
//            Path defaultsPath = baseUploadPath.resolve((posType.getDefaultSchemaName())).resolve(imageType + "-images");
//            try (var paths = Files.walk(defaultsPath)) {
//                imagePathOptional = paths.filter(path -> path.getFileName().toString().startsWith(fileCode)).findFirst();
//            }
//        }
//        return imagePathOptional;
//    }
//
//    public Path getTenantDirectory() throws IOException {
//        // Get the current tenant schema name
//        String tenantId = currentTenantResolver.resolveCurrentTenantIdentifier();
//        Path tenantPath = baseUploadPath.resolve(tenantId);
//
//        // Create tenant directories if they don't exist
//        Files.createDirectories(tenantPath.resolve("brands-images"));
//        Files.createDirectories(tenantPath.resolve("categories-images"));
//        Files.createDirectories(tenantPath.resolve("products-images"));
//        return tenantPath;
//    }
//
//    public byte[] getFallbackImageBytes() throws IOException {
//        Path fallbackPath = Paths.get("assets/images/img.png");
//        return Files.readAllBytes(fallbackPath); // Load the fallback image as a byte array
//    }
//    // Method to resize the image to 700x700 for products and 300x300 for brands or categories.
//    public  BufferedImage resizeImage(BufferedImage originalImage, String imageType)  throws IOException {
//        // Determine target dimensions based on image type
//        int targetWidth = imageType.equals("products") ? 700 : 300;
//        int targetHeight = imageType.equals("products") ? 700 : 300;
//
//        // Create a new buffered image with the target dimensions
//        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
//
//        // Resize the original image
//        Graphics2D graphics2D = resizedImage.createGraphics();
//
//        try {
//            // Fill the background with white
//            graphics2D.setColor(Color.WHITE);
//            graphics2D.fillRect(0, 0, targetWidth, targetHeight);
//
//            // Draw the original image onto the resized image
//            graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
//
//            // Optional: Improve image quality using rendering hints
//            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//        } catch (Exception e) {
//            throw new IOException("Error during image resizing", e);
//
//        } finally {
//            graphics2D.dispose();  // Dispose graphics context
//        }
//
//        return resizedImage;
//    }
//
//    // Method to compress Image to JPEG
//
//    public void compressImageToJPEG(BufferedImage inputImage, Path outputPath, float compressionQuality) throws IOException {
//        System.out.println("Step 1: Compressing image to JPEG format");
//
//        // Ensure output directory exists
//        File outputFile = outputPath.toFile();
//        File parentDir = outputFile.getParentFile();
//        if (!parentDir.exists() && !parentDir.mkdirs()) {
//            throw new IOException("Failed to create directories for: " + outputPath);
//        }
//
//        // Find JPEG writers
//        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
//        if (!writers.hasNext()) {
//            throw new IOException("No ImageWriter available for JPEG format");
//        }
//
//        ImageWriter writer = writers.next(); // Use the first available writer
//        ImageWriteParam writeParam = writer.getDefaultWriteParam();
//
//        // Enable compression and set compression quality
//        if (writeParam.canWriteCompressed()) {
//            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//            writeParam.setCompressionQuality(compressionQuality); // Value between 0 (max compression) and 1 (max quality)
//        } else {
//            throw new UnsupportedOperationException("Compression is not supported by this ImageWriter");
//        }
//
//        // Create output stream and write compressed image
//        try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(outputFile)) {
//            writer.setOutput(outputStream);
//            writer.write(null, new IIOImage(inputImage, null, null), writeParam);
//        } finally {
//            writer.dispose(); // Release resources
//        }
//
//        System.out.println("Image successfully compressed and saved to: " + outputPath);
//    }
//
//}
//
