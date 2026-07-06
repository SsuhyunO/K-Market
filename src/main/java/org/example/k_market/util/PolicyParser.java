package org.example.k_market.util;

import org.example.k_market.dto.PolicyArticleDTO;
import org.example.k_market.dto.PolicyDTO;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PolicyParser {

    public static List<PolicyArticleDTO> splitArticles(String content) {
        List<PolicyArticleDTO> articles = new ArrayList<>();

        if (content == null || content.isBlank()) {
            return articles;
        }

        // "제1조 (...)" / "제2조 (...)" 같은 조 제목 찾기
        Pattern pattern = Pattern.compile("(제\\d+조\\s*\\([^\\)]+\\))");
        Matcher matcher = pattern.matcher(content);

        List<Integer> startIndexes = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        while (matcher.find()) {
            startIndexes.add(matcher.start());
            titles.add(matcher.group());
        }

        for (int i = 0; i < startIndexes.size(); i++) {
            int start = startIndexes.get(i);
            int end = (i + 1 < startIndexes.size()) ? startIndexes.get(i + 1) : content.length();

            String block = content.substring(start, end).trim();

            String title = titles.get(i);
            String body = block.substring(title.length()).trim();

            articles.add(new PolicyArticleDTO(title, body));
        }

        return articles;
    }
}
