# 🚀 OrçaFácil - Sistema de Gestão de Orçamentos

Um sistema web para agilizar a criação, gestão e emissão de orçamentos em PDF para prestadores de serviços e pequenas empresas.

Um projeto desenvolvido como MVP (Produto Mínimo Viável) com foco em arquitetura limpa, testes automatizados e boas práticas de Engenharia de Software.

## 📋 Funcionalidades

- **Gestão de Cadastros:**
  - Clientes (CRUD completo).
  - Produtos e Serviços (com distinção de lógica de precificação).
  - Configurações da Empresa (Single-tenant).
- **Motor de Orçamentos:**
  - Seleção dinâmica de produtos.
  - **Lógica de Preço Híbrida:** Suporta itens com preço fixo (Produto) e preço aberto (Serviço/Mão de Obra).
  - Cálculo automático de subtotais e totais com descontos.
- **Geração de Documentos:**
  - Exportação de orçamento profissional em **PDF** (usando OpenPDF).
  - Inclusão automática de logo e dados da empresa.
- **Segurança:**
  - Autenticação via Spring Security.

## 🛠️ Tecnologias Utilizadas

- **Backend** Java 17, Spring Boot 3.5.7
- **Banco de Dados:** SQL Server (com JPA/Hibernate)
- **Frontend:** Thymeleaf (Server-side rendering), HTML5, JavaScript Vanilla
- **Relatórios:** OpenPDF
- **Testes:** JUnit 5, Mockito (Cobertura de Unit e Integration)
- **Ferramentas:** Maven, Lombok

## 🏗️ Arquitetura e Padrões

O projeto segue uma arquitetura MVC clássica, com forte separação de responsabilidades:

- **DTOs (Data Transfer Objects):** Para desaclopar a camada de persistência da camada de visualização e permitir validações (`@valid`).
- **Service Layer:** Onde reside toda a regra de negócio (ex: cálculo de validade, travas de segurança de preço).
- **Repository Pattern:** Abstração do acesso a dados com Spring Data JPA.
- **Testes Automatizados:**
  - *Unitários:* Isolamento de regras de negócio com Mockito.
  - *Integração/Web:* Validação de Controllers e fluxo HTTP com `@WebMvcTest`.

## 🚀 Como Rodar o Projeto

### Pré-requitos
- Java 17 ou superior.
- Maven.
- SQL Server rodando localmente (porta 1433).

### Configuração do Banco
1. Crie um banco de dados vazio no SQL Server chamado `orcamento_db`.
2. Configure suas credenciais no arquivo `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=SEU_USUARIO
   spring.datasource.password=SUA_SENHA

### Executando
1. Abra o terminal e clone o repositório:
   ```bash
   git clone [https://github.com/MaykCruz/orcamento-mvp.git](https://github.com/SEU-USUARIO/orcamento-mvp.git)

2. Entre na pasta do projeto:
   ```bash
   cd orcamento-mvp

3. Execute a aplicação usando o Wrapper do Maven (isso baixa as dependências e inicia o servidor):
    ```bash
   ./mvnw spring-boot:run

(No Windows, use `mvnw.cmd spring-bot:run` se o comando acima não funcionar).

4. Assim que o terminal mostrar a mensagem `Started OrcamentoMvpApplication`, acesse em seu navegador: http://localhost:8080

### Login Padrão (Ambiente de Dev)

Para o primeiro acesso, utilize o usuário administrador que é criado automaticamente na primeira execução:
- Usuário: `admin@gmail.com`
- Senha: `123456`