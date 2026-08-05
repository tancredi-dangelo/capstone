package tancredidangelo.capstone.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tancredidangelo.capstone.exceptions.BadRequestException;

import java.io.IOException;
import java.util.Map;
@Service
public class CloudinaryService {

    /// dependency injection

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }


    // methods



    /// UPLOAD MEDIA

    public String uploadMedia(MultipartFile file, String folderName) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty or missing.");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderName,
                            "resource_type", "auto"
                    )
            );

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new BadRequestException("Error during media upload: " + e.getMessage());
        }
    }



    ///  DELETE MEDIA

    public void deleteMedia(String publicId, String resourceType) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", resourceType));
        } catch (IOException e) {
            throw new BadRequestException("Could not delete media from Cloudinary: " + e.getMessage());
        }
    }
}
