from rest_framework import serializers
from classic_game.models import Question, Game

class QuestionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Question
        fields = ['id', 'question_text', 'answer1', 'answer2', 'answer3', 'answer4']

class GameSerializer(serializers.ModelSerializer):
    questions = QuestionSerializer(many=True, read_only=True)

    class Meta:
        model = Game
        fields = ['id', 'player', 'questions', 'score', 'created_at']
