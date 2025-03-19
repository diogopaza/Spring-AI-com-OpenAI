package com.diogo.ia.controller;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("idade")
public class IdadeController {

    private final ChatClient chatClient;

    public IdadeController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping
    public String categorizadorIdade(String idade) {
        String categorizador = """
                 Você é um categorizador de idade, deve responder a pergunta dizendo se uma pessoa" +
                 é uma criança, adolescente entre outros.
                 
                 ### exemplo de pergunta
                 30
                 
                 ### resposta
                 Adulto
                 
                """;

        var totTokens = this.contarTokens(categorizador, idade);
        System.out.println("Total de tokens na requisicao: " + totTokens);
        return this.chatClient.prompt()
                .user(idade)
                .advisors(new SimpleLoggerAdvisor())
                .system(categorizador)
                .call()
                .content();
    }

    private int contarTokens(String system, String user) {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding enc = registry.getEncodingForModel(ModelType.GPT_4O_MINI);
        return enc.countTokens(system+user);
    }
}
