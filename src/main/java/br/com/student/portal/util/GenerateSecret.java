package br.com.student.portal.util;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilitário para gerar chaves JWT seguras.
 * Execute como: mvn exec:java -Dexec.mainClass="br.com.student.portal.util.GenerateSecret"
 */
public class GenerateSecret {

    public static void main(String[] args) {
        String secret = generateSecret();
        System.out.println("=== SUA CHAVE JWT ===");
        System.out.println(secret);
        System.out.println("=====================");
        System.out.println("Copie esta chave para o arquivo .env ou variáveis de ambiente");
    }

    public static String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 256 bits
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}