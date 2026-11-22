# FinanControl

Sistema desktop de controle financeiro pessoal desenvolvido em **Java 17+**, **JavaFX (FXML + CSS)**, **JDBC puro** e **H2 Database** para a disciplina de Programação Orientada a Objetos (POO).

---

## Stack Tecnológica

- **Java 17+** (compilação configurada para `release 17`)
- **JavaFX 21.0.2** (`javafx-controls`, `javafx-fxml`, `javafx-graphics`)
- **H2 Database 2.2.224** (banco relacional embarcado via JDBC puro, 3FN)
- **jBCrypt 0.4** (hash de senhas)
- **Maven** (com Maven Wrapper incluso em `.mvn/wrapper`)

---

## Funcionalidades

- **Login e Cadastro:** autenticação com verificação de senha via BCrypt e criação automática de categorias padrão (`Salário`, `Freelance`, `Alimentação`, `Moradia`, `Lazer`).
- **CRUD completo de Transações:** criação, listagem, edição e exclusão de receitas e despesas vinculadas ao usuário logado.
- **CRUD completo de Categorias:** criação, listagem, edição e exclusão de categorias (`RECEITA` ou `DESPESA`), com bloqueio para categorias que já possuem transações (`ON DELETE RESTRICT`).
- **CRUD completo de Usuários:** cadastro, listagem, edição de perfil/senha e exclusão (removendo transações e categorias vinculadas).
- **Dashboard financeiro:** cálculo de saldo líquido atual, total de receitas, total de despesas, saldo devedor (`DÍVIDA / PRÓX. MÊS` quando despesas superam receitas) e gráfico de pizza (`PieChart`) interativo por categoria.

---

## Estrutura do Projeto

```text
financontrol/
├── pom.xml
├── RODAR_SISTEMA.bat
└── src/main/
    ├── java/com/financontrol/
    │   ├── Launcher.java                  # Ponto de entrada para executar direto pela IDE
    │   ├── MainApp.java                   # Inicialização do JavaFX e navegação entre telas
    │   ├── controller/
    │   │   ├── LoginController.java       # Tela de login e cadastro
    │   │   └── PrincipalController.java   # Dashboard, gráfico e abas CRUD
    │   ├── dao/
    │   │   ├── Repositorio.java           # Interface genérica CRUD (Repositorio<T>)
    │   │   ├── UsuarioDAO.java
    │   │   ├── CategoriaDAO.java
    │   │   └── TransacaoDAO.java
    │   ├── exception/
    │   │   └── ValidacaoException.java    # Exceção customizada de validação
    │   ├── model/
    │   │   ├── Usuario.java
    │   │   ├── Categoria.java
    │   │   ├── Transacao.java             # Classe abstrata + Factory Method criar()
    │   │   ├── Receita.java
    │   │   └── Despesa.java
    │   └── util/
    │       └── ConexaoDB.java             # Singleton de conexão H2 + sessão do usuário
    └── resources/
        ├── script.sql                     # DDL das tabelas (usuarios, categorias, transacoes)
        ├── css/style.css
        └── fxml/
            ├── login.fxml
            └── principal.fxml
```

---

## Conceitos de POO e Padrões Aplicados

| Conceito / Padrão | Onde está no código |
| :--- | :--- |
| **Encapsulamento** | Atributos `private` com `getters`/`setters` em `Usuario`, `Categoria` e `Transacao`. |
| **Herança** | `Receita` e `Despesa` estendem a classe abstrata `Transacao`. |
| **Polimorfismo** | `calcularImpactoNoSaldo()` retorna `+valor` em `Receita` e `-valor` em `Despesa`, somado em `PrincipalController.atualizarTudo()`. |
| **Interface + Generics** | `Repositorio<T>` define o contrato CRUD implementado por `UsuarioDAO`, `CategoriaDAO` e `TransacaoDAO`. |
| **DAO (Data Access Object)** | Pacote `com.financontrol.dao` isola todo o SQL/JDBC das telas. |
| **Singleton** | `ConexaoDB.getInstancia()` garante instância única de inicialização do H2 e controle da sessão. |
| **Factory Method** | `Transacao.criar(...)` instancia `Receita` ou `Despesa` conforme o tipo selecionado. |
| **Coleções (`List` e `Map`)** | `List<T>` nas listagens e `Map<String, Double>` (`HashMap`) para agrupar despesas por categoria no gráfico. |
| **Exceções Customizadas** | `ValidacaoException` lançada em erros de entrada/autenticação e tratada nos controllers com `Alert`. |

---

## Como Executar

**Pré-requisito:** JDK 17 ou superior instalado.

### 1. Pela IDE (IntelliJ, VS Code, Antigravity IDE ou Eclipse)
1. Abra a pasta `financontrol` como projeto **Maven** (`pom.xml`).
2. Execute o método `main` da classe `com.financontrol.Launcher` *(ou rode a task Maven `javafx:run`)*.

### 2. Pelo Terminal (dentro da pasta `financontrol`)
Com Maven instalado:
```bash
mvn javafx:run
```
Sem Maven instalado (usando o Maven Wrapper no PowerShell):
```powershell
java "-Dmaven.multiModuleProjectDirectory=$PWD" -cp ".mvn\wrapper\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain javafx:run
```

### 3. Pelo Windows (2 cliques)
Execute o arquivo `RODAR_SISTEMA.bat` na raiz do projeto (após extrair o `.zip`).

---

## Conta de Demonstração e Banco de Dados

- Ao iniciar pela primeira vez, `ConexaoDB` cria o banco embarcado em `./data/financontrol_db`, executa `src/main/resources/script.sql` e cadastra o usuário padrão:
  - **E-mail:** `demo@financontrol.com`
  - **Senha:** `123456`
- Na tela de login existe o botão **"Preencher Conta Demo"** para acesso rápido.
