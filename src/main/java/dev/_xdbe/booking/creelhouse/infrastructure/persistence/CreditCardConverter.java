package dev._xdbe.booking.creelhouse.infrastructure.persistence;


import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;

import dev._xdbe.booking.creelhouse.infrastructure.persistence.CryptographyHelper;


@Converter
public class CreditCardConverter implements AttributeConverter<String, String> {

    @Autowired
    private CryptographyHelper cryptographyHelper;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        // Step 7a: Encrypt the PAN before storing it in the database
        if (attribute == null) {
            return null;
        }

        try {
            return CryptographyHelper.encryptData(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt credit card number", e);
        }
        // Step 7a: End of PAN encryption
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        // Step 7b: Decrypt the PAN when reading it from the database
        if (dbData == null) {
            return null;
        }
        try {
            String pan = CryptographyHelper.decryptData(dbData);
            return panMasking(pan);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt credit card number", e);
        }
        // Step 7b: End of PAN decryption
    }

    private String panMasking(String pan) {
        // Step 6:
        if (pan == null) {
            return null;
        }

        if (pan.length() <= 8) {
            return pan;
        }

        String firstFour = pan.substring(0, 4);
        String lastFour = pan.substring(pan.length() - 4);

        StringBuilder maskedMiddle = new StringBuilder();
        for (int i = 0; i < pan.length() - 8; i++) {
            maskedMiddle.append('*');
        }

        return firstFour + maskedMiddle + lastFour;
        // Step 6: End
    }

    
}