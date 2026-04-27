# Sistema de Gerenciamento de Projetos

Este projeto é uma aplicação mobile Android desenvolvida para gerenciar membros,projetos e tarefa, utilizando tecnologias modernas e uma arquitetura organizada.

## 🚀 Tecnologias Utilizadas

*   **Linguagens:** Java (Lógica de negócio e Banco de Dados) e Kotlin (Interface de Usuário).
*   **Interface:** Jetpack Compose (Material Design 3).
*   **Banco de Dados:** Room (SQLite) para persistência local.
*   **Arquitetura:** Baseada em camadas (Data, Domain, UI, DI).

## 📁 Estrutura do Projeto

*   `app/src/main/java/com/example/sistemagerenciamentoprojetos/`
    *   `data/`: Gerenciamento de dados e repositórios.
    *   `domain/`: Regras de negócio e modelos.
        *   `membros/`: Módulo de gerenciamento de membros.
            *   `cadastrarMembro/`: Casos de uso e serviços para cadastro.
    *   `ui/`: Camada de apresentação.
        *   `screens/`: Telas da aplicação.
        *   `components/`: Componentes reutilizáveis.
        *   `theme/`: Configurações de cores e estilos.
    *   `di/`: Configurações para Injeção de Dependência.

## ⚙️ Funcionalidades Implementadas

### Gestão de Membros
*   **Cadastro de Membros:** Permite o registro de membros com os campos:
    *   ID (Automático)
    *   Nome
    *   Cargo
    *   E-mail
*   **Regra de Negócio:**
    *   Validação de campos obrigatórios.
    *   **Unicidade:** O sistema impede o cadastro de dois membros com o mesmo endereço de e-mail.
*   **Persistência:** Todos os dados são salvos localmente utilizando o Room Database.

## 🛠️ Como rodar o projeto

1.  Clone o repositório.
2.  Abra o projeto no **Android Studio**.
3.  Aguarde a sincronização do Gradle.
4.  Execute em um emulador ou dispositivo físico com Android 7.0 (API 24) ou superior.

---
*Desenvolvido como parte do 3º Semestre de BSI - IFBA.*
