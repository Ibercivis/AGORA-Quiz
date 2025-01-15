# classic_game/api/urls.py
from django.urls import path
from .views import GameViewSet

urlpatterns = [
    # Path to list all games
    path('games/', GameViewSet.as_view({
        'get': 'list'
    }), name='game-list'),

    # Path for actions on a specific game (retrieve, update, partial_update, destroy)
    path('games/<int:pk>/', GameViewSet.as_view({
        'get': 'retrieve',
        'put': 'update',
        'patch': 'partial_update',
        'delete': 'destroy'
    }), name='game-detail'),

    # Path to create a new game
    path('games/start/', GameViewSet.as_view({
        'post': 'start_game'
    }), name='game-start'),

    # Path to start a category game
    path('games/start_category/', GameViewSet.as_view({
        'post': 'start_category_game'
    }), name='game-start-category'),

    # Path to start a time trial game
    path('games/start_time_trial/', GameViewSet.as_view({
        'post': 'start_time_trial'
    }), name='game-start-time-trial'),

    # Path to answer a question on a specific game
    path('games/<int:pk>/answer/', GameViewSet.as_view({
        'post': 'answer'
    }), name='game-answer'),

    path('games/<int:pk>/answer_time_trial/', GameViewSet.as_view({
        'post': 'answer_time_trial'
    }), name='game-answer-time-trial'),

    # Path to resume a game
    path('games/resume/', GameViewSet.as_view({
        'get': 'resume'
    }), name='game-resume'),

    # Path to abandon a game
    path('games/<int:pk>/abandon/', GameViewSet.as_view({
        'post': 'abandon_game'
    }), name='game-abandon'),

    # Path to finish a game
    path('games/<int:pk>/finish_game/', GameViewSet.as_view({
        'post': 'finish_game'
    }), name='game-finish'),
]

