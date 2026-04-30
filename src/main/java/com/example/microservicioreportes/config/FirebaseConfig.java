package com.example.microservicioreportes.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.path:}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount;
            
            // Si se proporciona una ruta, usar ese archivo
            if (firebaseConfigPath != null && !firebaseConfigPath.isEmpty()) {
                serviceAccount = new FileInputStream(firebaseConfigPath);
            } else {
                // Intentar cargar desde classpath (para desarrollo)
                try {
                    serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream();
                } catch (Exception e) {
                    // Si no existe el archivo, inicializar sin autenticación (modo desarrollo)
                    System.out.println("No se encontró configuración de Firebase. Ejecutando en modo desarrollo sin autenticación.");
                    return;
                }
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase inicializado correctamente");
            }
        } catch (Exception e) {
            System.out.println("Error al inicializar Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}