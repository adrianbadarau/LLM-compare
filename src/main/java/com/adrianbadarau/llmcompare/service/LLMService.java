package com.adrianbadarau.llmcompare.service;

import com.adrianbadarau.llmcompare.model.DataItem;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;

@Service
public class LLMService {
    private final OllamaChatModel chatModel;

    @Autowired
    public LLMService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String compareItems(DataItem item1, DataItem item2) {
        var prompt = getComparePrompt(item1, item2);
        var res =  chatModel.call(
                new Prompt(prompt)
        );

        return res.getResult().getOutput().getText();
    }

    private String getComparePrompt(DataItem item1, DataItem item2) {
        var prompt = new StringBuilder();

        prompt.append("Please tell me if the items bellow are referring to the same issue: \n");
        prompt.append("--- START Item 1 ---").append("\n").append(item1.getData()).append("\n").append("--- END Item 1 ---").append("\n");
        prompt.append("--- START Item 2 ---").append("\n").append(item2.getData()).append("\n").append("--- END Item 2 ---").append("\n");
        prompt.append("All I want to know is if the items are referring to the same issue, please try to give me a --YES-- or --NO-- answer.");

        return prompt.toString();
    }
}
