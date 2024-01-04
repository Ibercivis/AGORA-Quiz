from django.urls import path
from .views import GameViewSet

urlpatterns = [
    # Ruta para obtener la lista de juegos
    path('games/', GameViewSet.as_view({
        'get': 'list'
    }), name='game-list'),

    # Ruta para acciones relacionadas con un juego específico (detalles, actualización, eliminación)
    path('games/<int:pk>/', GameViewSet.as_view({
        'get': 'retrieve',
        'put': 'update',
        'patch': 'partial_update',
        'delete': 'destroy'
    }), name='game-detail'),

    # Ruta para iniciar un juego
    path('games/start/', GameViewSet.as_view({
        'post': 'start_game'
    }), name='game-start'),

    # Ruta para responder una pregunta en un juego específico
    path('games/<int:pk>/answer/', GameViewSet.as_view({
        'post': 'answer'
    }), name='game-answer'),

    # Ruta para reanudar un juego
    path('games/resume/', GameViewSet.as_view({
        'get': 'resume'
    }), name='game-resume'),

    # Ruta para abandonar un juego
    path('games/<int:pk>/abandon/', GameViewSet.as_view({
        'post': 'abandon_game'
    }), name='game-abandon'),
]

