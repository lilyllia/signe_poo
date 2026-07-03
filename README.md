# Guia de Execução Local — SIGNE 

**Objetivo deste guia:** te deixar com o projeto rodando na sua máquina — backend e frontend, ao mesmo tempo — em poucos minutos. Siga na ordem, sem pular etapas; cada comando tem uma explicação do que ele faz e por quê.

 

---

## Parte 1 — Pré-requisitos (instalar antes de começar)

Instale estas ferramentas na sua máquina **antes** de clonar o repositório:

| Ferramenta | Versão recomendada | Para quê serve |
|---|---|---|
| **Git** | qualquer versão recente | Clonar o repositório e controlar versões do código |
| **JDK (Java Development Kit)** | 21 (LTS) — recomendo a distribuição **Eclipse Temurin** | Compilar e rodar o backend Java |
| **Node.js** | 20.x LTS (inclui o `npm` junto) | Rodar e instalar dependências do frontend React |
| **IDE para o backend** | IntelliJ IDEA (Community é gratuita) ou VS Code | Editar e depurar o código Java |
| **IDE para o frontend** | VS Code | Editar o código React |

> Você **não precisa instalar o Maven separadamente** — o projeto já vem com o Maven Wrapper (`mvnw`), que baixa e usa a versão certa automaticamente. Isso evita que cada dev tenha uma versão diferente de Maven instalada.

### Como conferir se já está tudo instalado

```bash
git --version
java -version
node -v
npm -v
```

Se `java -version` mostrar algo diferente de `21.x.x`, baixe o JDK 21 em [adoptium.net](https://adoptium.net) antes de continuar — versões incompatíveis do Java são a causa mais comum de erro de build em times iniciantes.

### Extensões de IDE recomendadas

**Se for usar VS Code para tudo:**
- `Extension Pack for Java` (Microsoft) — suporte completo a Java
- `Spring Boot Extension Pack` (VMware) — autocomplete e execução de projetos Spring Boot
- `ESLint` — aponta erros de estilo/qualidade no código React em tempo real
- `Prettier - Code formatter` — formata o código automaticamente ao salvar, evitando diffs de formatação nos Pull Requests
- `ES7+ React/Redux/React-Native snippets` — atalhos para criar componentes React mais rápido

**Se preferir IntelliJ IDEA para o backend:** o suporte a Spring Boot já vem embutido, sem precisar instalar plugin.

---

## Parte 2 — Clonar o repositório

```bash
git clone <URL_DO_REPOSITORIO>
cd signe
```

Confira se a estrutura já veio com as pastas certas:

```bash
ls
```

Você deve ver algo como `backend/  frontend/  docs/  README.md  LICENSE  .gitignore`. Se não aparecer, cheque o repositório remoto e mande mensagem avisando aos demais do problema.

---

## Parte 3 — Baixar as dependências do backend

```bash
cd backend
./mvnw clean install
```

**O que está acontecendo aqui:**
- `./mvnw` (em vez de `mvn`) roda o Maven Wrapper que já veio no projeto — ele garante que você está usando a **mesma versão de Maven** que o resto do time, sem precisar instalar nada manualmente.
- `clean` apaga qualquer resíduo de build anterior (pasta `target/`).
- `install` faz três coisas em sequência: baixa todas as dependências declaradas no `pom.xml` (para uma pasta local chamada `.m2`, fora do repositório), compila o código e roda os testes automatizados.

Se tudo estiver certo, você verá `BUILD SUCCESS` no final do terminal. Se aparecer `BUILD FAILURE`, leia a mensagem de erro com calma — normalmente indica um teste quebrado ou uma versão de Java incompatível.

---

## Parte 4 — Baixar as dependências do frontend

Em um novo terminal (ou navegando de volta):

```bash
cd frontend
npm install
```

**O que está acontecendo:** o `npm install` lê o arquivo `package.json` (a "lista de compras" de bibliotecas do projeto) e baixa exatamente essas versões para dentro da pasta `node_modules/`. É por isso que o `.gitignore` exclui essa pasta do Git — ela é 100% reconstruível rodando esse único comando, então não faz sentido versionar.

> **Dica:** sempre que você der um `git pull` e notar que o arquivo `package.json` ou `pom.xml` mudou, rode `npm install` ou `./mvnw clean install` de novo antes de continuar trabalhando — é sinal de que alguém do time adicionou uma dependência nova.

---

## Parte 5 — Rodar backend e frontend ao mesmo tempo

Os dois servidores rodam **em processos separados**, então você precisa de **dois terminais abertos simultaneamente** (duas abas, duas janelas, ou dois painéis divididos — no VS Code isso é feito com o atalho de "split terminal", geralmente `Ctrl+Shift+5` no Windows/Linux ou `Cmd+\` no Mac).

### Terminal A — Backend (Spring Boot)

```bash
cd backend
./mvnw spring-boot:run
```

Isso sobe um servidor web local na porta `8080`. Você vai ver logs do Spring Boot no terminal, terminando com algo como `Tomcat started on port(s): 8080`. Deixe este terminal aberto — fechar ele derruba o backend.

### Terminal B — Frontend (React + Vite)

```bash
cd frontend
npm run dev
```

O Vite sobe um servidor de desenvolvimento, geralmente na porta `5173`, e imprime no terminal um link tipo `http://localhost:5173`. Abra esse endereço no navegador — qualquer alteração que você salvar no código React aparece na tela quase instantaneamente (hot reload), sem precisar recarregar a página manualmente.

---

## ⚠️ Dica avançada — quando o frontend for chamar o backend

Quando vocês começarem a fazer o React consumir a API do Spring Boot via `axios`, é normal esbarrar em um erro de **CORS** no console do navegador (o backend, por padrão, bloqueia requisições vindas de uma origem diferente, como `localhost:5173` → `localhost:8080`). Isso é esperado e tem solução simples (configurar CORS no Spring Boot ou um proxy no `vite.config.js`) — não se preocupe com isso agora, é assunto para quando o time começar a integrar as duas pontas de verdade.

---

## ✅ Checklist final — "Estou pronto para codar?"

- [ ] `java -version` mostra Java 21
- [ ] `node -v` e `npm -v` respondem sem erro
- [ ] Repositório clonado e pastas `backend/`, `frontend/`, `docs/` visíveis
- [ ] `./mvnw clean install` terminou com `BUILD SUCCESS`
- [ ] `npm install` terminou sem erros dentro de `frontend/`
- [ ] Terminal A rodando `./mvnw spring-boot:run` sem erros
- [ ] Terminal B rodando `npm run dev`, com `http://localhost:5173` abrindo no navegador
- [ ] Extensões de IDE recomendadas instaladas

Se todos os itens estiverem marcados, você está pronto para pegar sua primeira tarefa no board do GitHub Projects. Qualquer erro nos passos acima, poste no canal do Discord do seu módulo antes de tentar "resolver sozinho por horas" — provavelmente alguém do time já passou pelo mesmo problema.