from django.contrib import admin
from .models import Question, Game

@admin.register(Question)
class QuestionAdmin(admin.ModelAdmin):
    list_display = ('id', 'question_text', 'correct_answer')  # Los campos que quieres ver en la lista
    search_fields = ('question_text',)  # Campos por los cuales quieres permitir la búsqueda
    list_filter = ('correct_answer',)  # Filtros que puedes usar a la derecha

@admin.register(Game)
class GameAdmin(admin.ModelAdmin):
    list_display = ('id', 'player', 'score', 'status', 'created_at')  # Los campos que quieres ver en la lista
    list_filter = ('status', 'created_at')  # Filtros que puedes usar a la derecha
    search_fields = ('player__username',)  # Permitir búsqueda por nombre de usuario del jugador

