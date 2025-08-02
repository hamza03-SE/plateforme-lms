# Utilise une image Java légère pour exécuter l’app
FROM eclipse-temurin:17-jdk-alpine

# Crée un répertoire pour l’application
WORKDIR /app

# Copie le fichier .jar généré par Maven
COPY target/*.jar app.jar

# Port exposé (même que celui dans application.properties ou docker-compose)
EXPOSE 8089

# Commande d’exécution
ENTRYPOINT ["java", "-jar", "app.jar"]
