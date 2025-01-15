# users/models.py
from django.conf import settings
from django.db import models
from django.contrib.auth.models import AbstractUser
import os 

def user_profile_image_path(instance, filename):
    # File will be uploaded to MEDIA_ROOT/profile_images/profile_<user_id>.<file_extension>
    extension = filename.split('.')[-1]
    filename = f'profile_{instance.user.id}.{extension}'
    return os.path.join('profile_images', filename)

class UserProfile(models.Model):
    user = models.OneToOneField(settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name='profile')
    profile_image = models.ImageField(upload_to=user_profile_image_path, blank=True, null=True)
    total_points = models.IntegerField(default=0)
    total_games_played = models.IntegerField(default=0)
    total_games_abandoned = models.IntegerField(default=0)
    total_correct_answers = models.IntegerField(default=0)
    total_incorrect_answers = models.IntegerField(default=0)
    max_time_trial_points = models.IntegerField(default=0)
    max_time_trial_time = models.IntegerField(default=0)

    def __str__(self):
        return f'{self.user.username} Profile'

    def update_on_answer(self, correct):
        if correct:
            self.total_correct_answers += 1
            self.total_points += 5  
        else:
            self.total_incorrect_answers += 1
            self.total_points -= 3  
        self.save()

    def update_max_time_trial_points(self, new_points):
        if new_points > self.max_time_trial_points:
            self.max_time_trial_points = new_points
            self.save()

    def update_max_time_trial_time(self, new_time):
        if new_time > self.max_time_trial_time:
            self.max_time_trial_time = new_time
            self.save()

    def update_on_game_end(self, abandoned=False):
        self.total_games_played += 1
        if abandoned:
            self.total_games_abandoned += 1
        self.save()
