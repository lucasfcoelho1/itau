# Etapa 1: build da aplicação
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /build

# Copia tudo pro container (exceto o que estiver no .dockerignore)
COPY . .

# Compila o projeto (pula os testes pra agilizar build local)
RUN ./mvnw clean package -DskipTests -Dmaven.test.skip=true

# Etapa 2: imagem final, apenas com o JAR
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copia o .jar gerado na etapa anterior
COPY --from=builder /build/target/itau-0.0.1-SNAPSHOT.jar app.jar

# Executa o JAR
ENTRYPOINT ["java", "-jar", "app.jar"]