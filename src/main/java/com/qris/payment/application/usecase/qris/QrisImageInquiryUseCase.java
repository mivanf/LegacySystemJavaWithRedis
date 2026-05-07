package com.qris.payment.application.usecase.qris;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.qris.payment.exception.InvalidQrisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class QrisImageInquiryUseCase {

    private static final Logger log = LoggerFactory.getLogger(QrisImageInquiryUseCase.class);

    private final QrisInquiryUseCase qrisInquiryUseCase;

    public QrisImageInquiryUseCase(QrisInquiryUseCase qrisInquiryUseCase) {
        this.qrisInquiryUseCase = qrisInquiryUseCase;
    }

    public QrisInquiryUseCase.InquiryResult execute(MultipartFile imageFile) {
        String qrisPayload = decodeQrImage(imageFile);
        return qrisInquiryUseCase.execute(qrisPayload);
    }

    /**
     * Decode a QR code image to extract the QRIS payload text.
     */
    public String decodeQrImage(MultipartFile imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile.getInputStream());
            if (image == null) {
                throw new InvalidQrisException("Cannot read image file");
            }

            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decode(bitmap);
            String text = result.getText();

            if (text == null || text.isBlank()) {
                throw new InvalidQrisException("No QR code data found in image");
            }

            log.info("Successfully decoded QR image, payload length: {}", text.length());
            return text;

        } catch (NotFoundException e) {
            throw new InvalidQrisException("No QR code found in the uploaded image");
        } catch (IOException e) {
            throw new InvalidQrisException("Failed to process uploaded image: " + e.getMessage());
        }
    }
}
