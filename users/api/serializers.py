from rest_framework import serializers
from users.models import UserProfile

class UserProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserProfile
        fields = ['profile_image', 'total_points', 'total_games_played', 'total_games_abandoned', 'total_correct_answers', 'total_incorrect_answers']