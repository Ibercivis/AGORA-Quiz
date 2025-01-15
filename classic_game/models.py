# classic_game/models.py
from django.db import models
from django.conf import settings

class Question(models.Model):
    CATEGORY_CHOICES = [
        ('Climate change fundamentals', 'Climate change fundamentals'),
        ('Climate change disinformation', 'Climate change disinformation'),
        ('Climate change communication and narratives', 'Climate change communication and narratives'),
        ('Media literacy', 'Media literacy'),
    ]

    question_text = models.CharField(max_length=255)
    answer1 = models.CharField(max_length=255)
    answer2 = models.CharField(max_length=255)
    answer3 = models.CharField(max_length=255)
    answer4 = models.CharField(max_length=255)
    correct_answer = models.IntegerField()
    reference = models.TextField(blank=True, null=True)
    category = models.CharField(max_length=50, choices=CATEGORY_CHOICES)

class Game(models.Model):
    STATUS_CHOICES = [
        ('in_progress', 'In Progress'),
        ('completed', 'Completed'),
        ('incomplete', 'Incompleted'),
    ]

    GAME_TYPE_CHOICES = [
        ('classic', 'Classic'),
        ('time_trial', 'Time Trial'),
        ('category', 'Category'),
    ]

    player = models.ForeignKey(settings.AUTH_USER_MODEL, related_name='games', on_delete=models.CASCADE)
    questions = models.ManyToManyField(Question)
    score = models.IntegerField(default=0)
    created_at = models.DateTimeField(auto_now_add=True)
    status = models.CharField(max_length=11, choices=STATUS_CHOICES, default='in_progress')
    responses = models.JSONField(default=list)  # Store the responses of the player as a list of dictionaries
    game_type = models.CharField(max_length=11, choices=GAME_TYPE_CHOICES, default='classic')
    time_left = models.IntegerField(default=60)  # Time left in seconds for Time Trial game
    max_time_trial_time = models.IntegerField(default=0)

    def update_on_correct_answer(self):
        self.score += 5
        self.time_left += 5

    def update_on_incorrect_answer(self):
        self.score -= 3
        self.time_left -= 3

    def is_time_over(self):
        return self.time_left <= 0

    def update_max_time_trial_time(self, new_time):
        if new_time > self.max_time_trial_time:
            self.max_time_trial_time = new_time
            self.save()