package ma.formations.multiconnector.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service d'envoi d'emails (RG_7)
 * Pour UC-2 : Envoyer les credentials au nouveau client
 */
@Service
@AllArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    /**
     * Envoie un email avec les credentials au nouveau client (RG_7)
     * Note: Pour l'instant, on simule l'envoi (log)
     * En production, il faudrait configurer Spring Mail
     */
    public void sendCredentialsEmail(String email, String username, String password) {
        // Simuler l'envoi d'email
        String message = String.format(
                "Bienvenue sur eBank!\n\n" +
                        "Vos identifiants de connexion:\n" +
                        "Login: %s\n" +
                        "Mot de passe: %s\n\n" +
                        "Veuillez changer votre mot de passe lors de votre première connexion.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe eBank",
                username, password
        );

        logger.info("=== EMAIL ENVOYÉ ===");
        logger.info("À: {}", email);
        logger.info("Sujet: Vos identifiants eBank");
        logger.info("Message:\n{}", message);
        logger.info("==================");

        // TODO: En production, remplacer par un vrai service d'envoi d'email
        // Exemple avec Spring Mail:
        // SimpleMailMessage mailMessage = new SimpleMailMessage();
        // mailMessage.setTo(email);
        // mailMessage.setSubject("Vos identifiants eBank");
        // mailMessage.setText(message);
        // mailSender.send(mailMessage);
    }
}