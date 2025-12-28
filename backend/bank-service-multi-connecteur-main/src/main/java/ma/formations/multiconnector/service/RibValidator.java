package ma.formations.multiconnector.service;

import org.springframework.stereotype.Component;

/**
 * Validateur de RIB marocain (UC-3 - RG_9)
 * Un RIB marocain valide contient 24 chiffres
 */
@Component
public class RibValidator {

    /**
     * Valide le format d'un RIB marocain
     * @param rib le RIB à valider
     * @return true si le RIB est valide, false sinon
     */
    public boolean isValidRib(String rib) {
        if (rib == null || rib.isEmpty()) {
            return false;
        }

        // Un RIB marocain valide contient exactement 24 chiffres
        // Format : XXXX XXXX XXXX XXXX XXXX XXXX (24 chiffres)
        String ribWithoutSpaces = rib.replaceAll("\\s+", "");

        return ribWithoutSpaces.matches("\\d{24}");
    }

    /**
     * Retourne un message d'erreur détaillé si le RIB est invalide
     * @param rib le RIB à valider
     * @return le message d'erreur ou null si valide
     */
    public String getValidationError(String rib) {
        if (rib == null || rib.isEmpty()) {
            return "Le RIB ne peut pas être vide";
        }

        String ribWithoutSpaces = rib.replaceAll("\\s+", "");

        if (!ribWithoutSpaces.matches("\\d+")) {
            return "Le RIB ne doit contenir que des chiffres";
        }

        if (ribWithoutSpaces.length() != 24) {
            return String.format("Le RIB doit contenir exactement 24 chiffres (actuellement: %d)",
                    ribWithoutSpaces.length());
        }

        return null; // RIB valide
    }
}