# CapZip - Encurtador de Links

CapZip é uma aplicação para encurtar URLs com funcionalidades avançadas de gerenciamento e rastreamento de acessos.

## Funcionalidades

- Autenticação de usuário com JWT para login e refresh token.
- Redefinição de senha por meio de código de confirmação enviado por e-mail.
- Encurtamento de URLs, tanto para usuários autenticados quanto não autenticados.
- Redirecionamento de URLs encurtadas para seus destinos originais.
- Rastreamento e gerenciamento de links encurtados para usuários autenticados:
  - Registro de acessos (IP, localização, data, user agent).
  - Contagem de cliques por período do dia.
  - Relatório de cliques por cidade.

## Endpoints

### Authentication

- `POST /auth/signin`: Autentica um usuário e retorna um token JWT.
- `PUT /auth/refresh/{username}`: Atualiza o token JWT.
- `POST /auth/logout`: Encerra a sessão do usuário autenticado.

### Link Shortening

- `POST /api/v1/link/shorten-link`: Encurta um link para usuários autenticados.
- `POST /api/v1/link/shorten-link-no-auth`: Encurta um link para usuários não autenticados.
- `GET /api/v1/link/my-link-short`: Lista todos os links encurtados pelo usuário autenticado.
- `DELETE /{shortLink}`: Deleta um link encurtado específico do usuário autenticado.
- `GET /api/v1/total/short-link`: Retorna o total de links encurtados pelo usuário.

### Click Tracking

- `GET /{shortLink}/`: Redireciona para o link original e registra o acesso.
- `GET /api/v1/clicks/all`: Lista todos os acessos para um link encurtado específico.
- `GET /api/v1/clicks/by-city`: Lista os acessos por cidade para um link encurtado específico.
- `GET /api/v1/clicks/by-period`: Retorna estatísticas de acessos por período do dia para um link encurtado específico.
- `GET /api/v1/total/clicks`: Retorna o total de cliques registrados.

### User Management

- `POST /api/v1/user/register`: Registra um novo usuário na aplicação.
- `POST /api/v1/user/reset-password`: Envia um código de redefinição de senha para o e-mail do usuário.
- `POST /api/v1/user/reset-password/validate`: Valida o código de redefinição de senha e atualiza a senha do usuário.

## Tecnologias Utilizadas

- Java Spring Boot
- JWT para autenticação
- PostgreSQL para armazenamento de dados
- Swagger para documentação de API
- Lombok para redução de código boilerplate

## Configuração

Para executar a aplicação localmente, certifique-se de ter o JDK 11+ e o PostgreSQL configurados corretamente. Configure também as variáveis de ambiente necessárias, como `DOMAIN_URL` para o domínio base da aplicação.

## Contribuição

Sinta-se à vontade para contribuir com melhorias através de pull requests ou abrindo novas issues para sugestões e problemas encontrados.

## Licença

Este projeto está licenciado sob a MIT License - veja o arquivo [LICENSE](LICENSE) para mais detalhes.
## Tecnologias Utilizadas

- Java Spring Boot
- JWT para autenticação
- PostgreSQL para armazenamento de dados
- Swagger para documentação de API
- Lombok para redução de código boilerplate
- Entre outros.

## Configuração

Para executar a aplicação localmente:

### Pré-requisitos

- JDK ou superior instalado
- PostgreSQL configurado
- Variáveis de ambiente configuradas (ex.: `DOMAIN_URL` para o domínio base da aplicação)

### Baixando e Executando o Projeto

1. Clone o repositório:

   ```bash
   git clone https://github.com/seu-usuario/capzip.gitps://github.com/seu-usuario/capzip.git
