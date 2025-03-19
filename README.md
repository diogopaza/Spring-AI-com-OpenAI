# Spring-AI-com-OpenAI

<h3>Dependencias</h3>
<ul>
  <li>Spring Web</li>
  <li>Spring Boot Dev Tools</li>
  <li>OpenAI</li>  
</ul>

<h3>Interface AiClient</h3>
<p>A interface AiClient atua como uma interface uniforme para interagir com varios modelos de IA, incluindo OpennAI e AzureOpenAI. A interface gerencia a complexidade da preparacao de solicitacoes e da analise de respostas, oferecendo uma interacao direta e simplificada com a API.</p>

<h3>Codigo inicial</h3>

```java
   return this.chatClient.prompt()
                .system(system)
                .user(pergunta)
                .options(
                        ChatOptions.builder()
                                .temperature(0.85)
                                .build())
                .call()
                .content();

```

<p>O código acima usa o atributo this.chatClient que e injetado na classe. Pontos importantes a notar abaixo:
<ul>
  <li>.system(system) == estamos definindo que a IA deve ser algo especifico, como um chatbot de auxlio a cliente, em nosso caso a IA e um categorizador de produtos.</li>
  <li>.user(pergunta) == recebemos uma String via query da URL com a pergunta vinda do usario/sistema.</li>
  <li>.temperature(0.85) == o parametro tempareture controla a aleatoriedade da geracao de texto. Valores mais baixos (ex: 0.1) fazem com que o modelo gere respostas mais previsiveis e deterministicas.</li>
</ul>
</p>

<h3>Tokens</h3>
<p>
  Tokens sao unidades basicas de texto que um modelo de linguagem utiliza para processar e gerar informacoes. Eles sao como blocos de construcao que compoem palavras, frases e sentenças. Dependendo da implementacao, um token pode ser:
  <ul>
    <li>plavras inteiras</li>
    <li>sub-palavras</li>
    <li>caracteres individuais</li>
  </ul>

</p>
<h4>Descobrindo total de tokens de uma requisicao</h4>
<p>
  Nos testes realizados conseguimos duas formas de obter o total de tokens, a primeira e através dos logs que o SpringAi fornece, conforme codigo abaixo:
  <br>
  No application.properties e necessario definir essa configuracao para habilitar os logs do SpringAi = <strong>logging.level.org.springframework.ai.chat.client.advisor=DEBUG</strong>

  ```java
  return this.chatClient.prompt()
                .user(idade)
                .advisors(new SimpleLoggerAdvisor())
                .system(categorizador)
                .call()
                .content();
    }

```
E possivel notar a adicao do metodo <strong>.advisors(new SimpleLoggerAdvisor())</strong>, dessa forma os log serao exibidos no console. Abaixo vemos o retorno de um log realizando uma requisicao no endpoint do categorizador:
```
AdvisedRequest[chatModel=OpenAiChatModel [defaultOptions=OpenAiChatOptions: {"streamUsage":false,"model":"gpt-4o-mini","temperature":0.7}], userText=25, systemText= Você é um categorizador de idade, deve responder a pergunta dizendo se uma pessoa" +
 é uma criança, adolescente entre outros.
```
No log abaixo temos uma parte do Json retornado da requisicao, onde sao exibidas informacoes relativas a quantidade de tokens:

```
   "usage" : {
      "promptTokens" : 53,
      "completionTokens" : 3,
      "totalTokens" : 56,
      "generationTokens" : 3,
      "nativeUsage" : {
        "completion_tokens" : 3,
        "prompt_tokens" : 53,
        "total_tokens" : 56,
        "prompt_tokens_details" : {
          "audio_tokens" : 0,
          "cached_tokens" : 0
        },
        "completion_tokens_details" : {
          "reasoning_tokens" : 0,
          "accepted_prediction_tokens" : 0,
          "audio_tokens" : 0,
          "rejected_prediction_tokens" : 0
        }
      }
    }
```
</p>
<p>No proximo exemplo iremos obter o total de token usando uma biblioteca externa, chamada de JTokkit - Java Tokenizer Kit que pode ser obtida em 
  <a href="https://github.com/knuddelsgmbh/jtokkit"> neste link.</a></p> 
  <p>O codigo abaixo cria um novo metodo chamado contarTokens e chamamos esse metodo onde queremos realizar a contagem de tokens. Mais abaixo temo o retorno total de 42 tokens, um retorno um pouco diferente do valor obtido diretamente pela api da OpenAI que retornou 56 tokens.

```java
  private int contarTokens(String system, String user) {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding enc = registry.getEncodingForModel(ModelType.GPT_4O_MINI);
        return enc.countTokens(system+user);
    }

```

```
Total de tokens na requisicao: 42

```


  </p>

<h3>OpenAI Image Generation</h3>
  <p>O proximo passo e a geracao de imagens atraves de uma entrada no prompt.</p>
  <p>O codigo abaixo injeta ImageModel para ser usada junto a api da OpenAI. O metodo gerarImagem define o tamamho da imagem e retorna uma url da imagem gerada pela OpenAI. 

  ```java
  private final ImageModel imageModel;

    public ImagemController(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @GetMapping
    public String gerarImagem(String prompt) {
        var options = ImageOptionsBuilder.builder()
                .height(1024)
                .width(1024)
                .build();
        var response = imageModel.call(new ImagePrompt(prompt, options));
        return response.getResult().getOutput().getUrl();
    }

  ```


  </p>



