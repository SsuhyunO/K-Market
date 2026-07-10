package org.example.k_market.dto.category;

import lombok.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTreeDTO {
    private int cateId;
    private String name;
    private List<CategoryDTO> children;

    private static final Pattern EMOJI_PATTERN = Pattern.compile(
            "[\\x{1F300}-\\x{1FAFF}\\x{2600}-\\x{27BF}\\x{2190}-\\x{21FF}\\x{2B00}-\\x{2BFF}]+"
    );

    public String getIcon() {
        Matcher m = EMOJI_PATTERN.matcher(name);
        return m.find() ? m.group() : "";
    }

    public String getDisplayName() {
        return EMOJI_PATTERN.matcher(name).replaceAll("").trim();
    }
}
