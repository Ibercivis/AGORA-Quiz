# users/api/serializers.py
from dj_rest_auth.registration.serializers import RegisterSerializer
from rest_framework import serializers
from users.models import UserProfile
from django.core.files.storage import default_storage
from django.contrib.auth.models import User
from django.contrib.auth import get_user_model

class UserUpdateSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['username', 'email']

class UserProfileSerializer(serializers.ModelSerializer):
    username = serializers.SerializerMethodField()
    email = serializers.SerializerMethodField()

    class Meta:
        model = UserProfile
        fields = ['username', 'email', 'profile_image', 'total_points', 'total_games_played', 'total_games_abandoned', 'total_correct_answers', 'total_incorrect_answers', 'max_time_trial_time', 'max_time_trial_points']

    def get_username(self, obj):
        return obj.user.username

    def get_email(self, obj):
        return obj.user.email

    def update(self, instance, validated_data):
        profile_image = validated_data.get('profile_image')
        if profile_image is None:  # Si se recibe un valor None para la imagen de perfil, eliminar la imagen existente.
            if instance.profile_image:
                # Eliminar el archivo de imagen del servidor
                if default_storage.exists(instance.profile_image.name):
                    default_storage.delete(instance.profile_image.name)
                # Eliminar la referencia de la imagen del objeto
                instance.profile_image = None
        return super().update(instance, validated_data)

class UserRankingSerializer(serializers.ModelSerializer):
    username = serializers.CharField(source='user.username')
    profile_image_url = serializers.SerializerMethodField()

    class Meta:
        model = UserProfile
        fields = ['username', 'total_points', 'max_time_trial_time', 'profile_image_url']

    def get_profile_image_url(self, obj):
        if obj.profile_image:
            return obj.profile_image.url
        return None