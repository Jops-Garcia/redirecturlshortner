# Microserviço: Redirecionamento de URL Encurtada

Este microserviço é responsável por redirecionar os usuários para a URL original com base em um código encurtado. Ele verifica se o código existe no AWS S3 e se ainda está válido, retornando a URL original ou informando sobre a expiração.

---

## Funcionalidade

### Fluxo de Operação:
1. **Recebe uma solicitação** via AWS API Gateway contendo o código da URL encurtada.
2. **Busca os metadados** no S3 usando o código fornecido.
3. **Verifica** se a URL encurtada está expirada.
4. **Redireciona** para a URL original se estiver válida ou retorna um status de expiração.

---

## Estrutura do Código

### Principais Classes:
- **`Main.java`**:
  - Handler principal da função Lambda.
  - Lê o código da URL, busca os dados no S3 e redireciona com base nas regras de expiração.
- **`UrlData.java`**:
  - Classe modelo contendo a URL original e o tempo de expiração.

### Estrutura de Arquivos:
```plaintext
src/
└── main/
    └── java/
        └── com/
            └── garciajops/
                └── redirecturlshortner/
                    ├── Main.java
                    └── UrlData.java
pom.xml
```

---

## Configuração e Deploy

### Pré-requisitos:
1. **AWS CLI** configurado.
2. **Bucket S3** criado (ex: `garciajops-url-shortener-storage`).
3. **IAM Role** com permissões de leitura no S3 e execução de funções Lambda.

### Etapas de Deploy:
1. **Construção do Projeto**:
   ```bash
   mvn clean package
   ```
2. **Upload da Função Lambda**:
   - Faça o upload do arquivo `.jar` gerado para a AWS Lambda.
   - Defina o **handler** como:
     ```
     com.garciajops.redirecturlshortner.Main::handleRequest
     ```

3. **Configuração do API Gateway**:
   - Crie um endpoint **GET** associado à função Lambda.
   - Passe o código encurtado como parte da URL (ex: `/redirect/{code}`).

---

## Exemplo de Requisição

### Redirecionar para a URL Original:
- **Método**: GET
- **Endpoint**: `/redirect/{shortUrlCode}`
  - Exemplo: `/redirect/abc12345`

### Respostas Possíveis:
1. **Redirecionamento bem-sucedido**:
   - **Status**: 302
   - **Header**: `Location: https://www.exemplo.com`

2. **URL expirada**:
   ```json
   {
     "statusCode": 410,
     "body": "This URL has expired"
   }
   ```

3. **Erro de código inválido**:
   ```json
   {
     "statusCode": 400,
     "body": "Invalid input: 'shortUrlCode' is required"
   }
   ```

---

## Dependências Maven

O projeto utiliza as seguintes dependências:

```xml
<dependencies>
    <dependency>
        <groupId>com.amazonaws</groupId>
        <artifactId>aws-lambda-java-core</artifactId>
        <version>1.2.1</version>
    </dependency>
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>s3</artifactId>
        <version>2.17.106</version>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.34</version>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.12.3</version>
    </dependency>
</dependencies>
```

---
