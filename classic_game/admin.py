from django.contrib import admin
from .models import Question, Game

@admin.register(Question)
class QuestionAdmin(admin.ModelAdmin):
    list_display = ('id', 'question_text', 'correct_answer')  
    search_fields = ('question_text',)  
    list_filter = ('correct_answer',)  

@admin.register(Game)
class GameAdmin(admin.ModelAdmin):
    list_display = ('id', 'player', 'score', 'status', 'created_at')  
    list_filter = ('status', 'created_at')  
    search_fields = ('player__username',)  

