package com.ibercivis.agora.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ibercivis.agora.classes.Question;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuestionDeserializer implements JsonDeserializer<Question> {
    @Override
    public Question deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        int id = jsonObject.get("id").getAsInt();
        String questionText = jsonObject.get("question_text").getAsString();
        List<String> answers = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            JsonElement answerElement = jsonObject.get("answer" + i);
            if (answerElement != null && !answerElement.isJsonNull()) {
                answers.add(answerElement.getAsString());
            } else {
                answers.add(null); // Could be null if the answer is not present
            }
        }
        JsonElement correctAnswerElement = jsonObject.get("correct_answer");
        int correctAnswer = correctAnswerElement != null ? correctAnswerElement.getAsInt() : -1; // Assure that correctAnswer is always a valid index

        String reference = jsonObject.has("reference") && !jsonObject.get("reference").isJsonNull()
                ? jsonObject.get("reference").getAsString() : "N/A";

        return new Question(id, questionText, answers, correctAnswer, reference);
    }
}


