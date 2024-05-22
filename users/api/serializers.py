from rest_framework import serializers
from users.models import UserProfile
from django.core.files.storage import default_storage

class UserProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserProfile
        fields = ['profile_image', 'total_points', 'total_games_played', 'total_games_abandoned', 'total_correct_answers', 'total_incorrect_answers']

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