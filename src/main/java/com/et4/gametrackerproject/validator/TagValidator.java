package com.et4.gametrackerproject.validator;

import com.et4.gametrackerproject.dto.GameTagDto;
import com.et4.gametrackerproject.dto.TagDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TagValidator {
    public static List<String> validate(TagDto tagDto) {
        List<String> errors = new ArrayList<>();

        if (tagDto == null) {
            errors.add("Name is required");
            return errors;
        }

        if (!StringUtils.hasLength(tagDto.getName())) {
            errors.add("Name is required");
        } else if (tagDto.getName().length() > 100) { // Limite arbitraire
            errors.add("Name cannot exceed 100 characters");
        }

        if (tagDto.getGameTags() == null) {
            tagDto.setGameTags(Set.of());
        } else {
            for (GameTagDto gameTagDto : tagDto.getGameTags()) {
                List<String> gameTagErrors = GameTagValidator.validate(gameTagDto);
                if(!gameTagErrors.isEmpty()) {
                    errors.add("Game progress validation errors: " + String.join(", ", gameTagErrors));
                }
            }
        }

        return errors;
    }

}
