from django.db import models

# Create your models here.
class Question(models.Model):
    question_text = models.CharField(max_length=255)
    answer1 = models.CharField(max_length=255)
    answer2 = models.CharField(max_length=255)
    answer3 = models.CharField(max_length=255)
    answer4 = models.CharField(max_length=255)
    correct_answer = models.IntegerField()

class Game(models.Model):
    STATUS_CHOICES = [
        ('in_progress', 'En Progreso'),
        ('completed', 'Completada'),
        ('incomplete', 'Incompleta'),
    ]

    player = models.ForeignKey('auth.User', related_name='games', on_delete=models.CASCADE)
    questions = models.ManyToManyField(Question)
    score = models.IntegerField(default=0)
    created_at = models.DateTimeField(auto_now_add=True)
    status = models.CharField(max_length=11, choices=STATUS_CHOICES, default='in_progress')
    responses = models.JSONField(default=list)  # Almacena las respuestas como [{'question_id': x, 'answer': y}]