from django.conf import settings
from django.db import models

class UserProfile(models.Model):
    user = models.OneToOneField(settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name='profile')
    total_points = models.IntegerField(default=0)
    total_games_played = models.IntegerField(default=0)
    total_games_abandoned = models.IntegerField(default=0)
    total_correct_answers = models.IntegerField(default=0)
    total_incorrect_answers = models.IntegerField(default=0)

    def __str__(self):
        return f'{self.user.username} Profile'

    def update_on_answer(self, correct):
        if correct:
            self.correct_answers += 1
            self.total_points += 5  # Cantidad de puntos por respuesta correcta
        else:
            self.incorrect_answers += 1
            self.total_points -= 3  # Cantidad de puntos por respuesta incorrecta
        self.save()

    def update_on_game_end(self, abandoned=False):
        self.games_played += 1
        if abandoned:
            self.games_abandoned += 1
        self.save()
