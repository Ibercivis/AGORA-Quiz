# classic_game/api/views.py
from django.shortcuts import get_object_or_404
from django.core.exceptions import ValidationError
from django.http import Http404
from rest_framework import viewsets
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework import status
from rest_framework.permissions import IsAuthenticated
from classic_game.models import Question, Game
from .serializers import QuestionSerializer, GameSerializer  
from users.models import UserProfile
import random


class QuestionViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Question.objects.all()
    serializer_class = QuestionSerializer

class GameViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Game.objects.all()
    serializer_class = GameSerializer

    def calculate_current_question_index(self, game):
        # Calculate the index of the current question
        answered_questions_count = len(game.responses)
        return answered_questions_count + 1  # +1 because the index is 1-based
    def calculate_correct_answers_count(self, game):
        # Calculate the number of correct answers
        return sum(1 for response in game.responses if response.get('correct', False))

    @action(detail=False, methods=['post'])
    def start_game(self, request, *args, **kwargs):
        # Check if there is already a game in progress for the current user
        if Game.objects.filter(player=request.user, status='in_progress').exists():
            return Response({'detail': 'Ya hay una partida en progreso.'}, status=status.HTTP_400_BAD_REQUEST)

        # Select random questions for the game
        questions = random.sample(list(Question.objects.all()), 9)
        game = Game.objects.create(player=request.user)
        game.questions.set(questions)
        game.status = 'in_progress'
        game.save()
        # Find the first question not answered yet
        next_question = None
        for question in game.questions.all():
            if not any(r['question_id'] == question.id for r in game.responses):
                next_question = question
                break
        
        # Update the user profile
        profile = UserProfile.objects.get(user=game.player)
        profile.update_on_game_end(abandoned=False)

        response_data = {
        'game': GameSerializer(game).data,
        'score': game.score,
        'status': game.status,
        'next_question': QuestionSerializer(next_question).data if next_question else None,
        'current_question_index': 1,
        'correct_answers_count': 0,
        }
        return Response(response_data, status=status.HTTP_200_OK)
    
    @action(detail=False, methods=['post'])
    def start_category_game(self, request, *args, **kwargs):
        category = request.data.get('category')
        if category not in [choice[0] for choice in Question.CATEGORY_CHOICES]:
            return Response({'detail': 'Categoría no válida.'}, status=status.HTTP_400_BAD_REQUEST)

        # Check if there is already a game in progress for the current user
        questions = random.sample(list(Question.objects.filter(category=category)), 9)
        game = Game.objects.create(player=request.user, game_type='category')
        game.questions.set(questions)
        game.status = 'in_progress'
        game.save()

        # Find the first question not answered yet
        next_question = questions[0] if questions else None
        response_data = {
            'game': GameSerializer(game).data,
            'score': game.score,
            'status': game.status,
            'next_question': QuestionSerializer(next_question).data if next_question else None,
            'current_question_index': 1,
            'correct_answers_count': 0,
        }
        return Response(response_data, status=status.HTTP_200_OK)

    @action(detail=False, methods=['post'])
    def start_time_trial(self, request, *args, **kwargs):
        # Check if there is already a time trial game in progress for the current user
        if Game.objects.filter(player=request.user, status='in_progress', game_type='time_trial').exists():
            return Response({'detail': 'Ya hay una partida contrarreloj en progreso.'}, status=status.HTTP_400_BAD_REQUEST)

        # Start a new time trial game
        questions = random.sample(list(Question.objects.all()), 79)
        game = Game.objects.create(player=request.user, game_type='time_trial')
        game.questions.set(questions)
        game.status = 'in_progress'
        game.time_left = 60  # 60 seconds
        game.save()

        # Find the first question not answered yet
        next_question = None
        for question in game.questions.all():
            if not any(r['question_id'] == question.id for r in game.responses):
                next_question = question
                break

        response_data = {
            'game': GameSerializer(game).data,
            'score': game.score,
            'status': game.status,
            'next_question': QuestionSerializer(next_question).data if next_question else None,
            'current_question_index': 1,
            'correct_answers_count': 0,
            'time_left': game.time_left,
        }
        return Response(response_data, status=status.HTTP_200_OK)

    @action(detail=True, methods=['post'])
    def answer(self, request, pk=None):
        game = self.get_object()

        # Check if the game has already been completed
        if game.status == 'completed':
            return Response({'detail': 'Esta partida ya ha sido completada.', 'status': game.status}, status=status.HTTP_400_BAD_REQUEST)

        question_id = request.data.get('question_id')
        answer = request.data.get('answer')

        # Check if the question has already been answered
        if any(r['question_id'] == question_id for r in game.responses):
            raise ValidationError('Esta pregunta ya ha sido respondida.')

        # Get the question and check if it belongs to the current game
        question = get_object_or_404(Question, id=question_id)
        if question not in game.questions.all():
            raise ValidationError('Esta pregunta no pertenece a la partida actual.')

        # Check if the answer is correct
        correct = (question.correct_answer == int(answer))
        if correct:
            game.score += 5  # points for correct answer
        else:
            game.score -= 3  # points for incorrect answer
        # Update the user profile
        profile = UserProfile.objects.get(user=game.player)
        profile.update_on_answer(correct=correct)

        # Register the response
        game.responses.append({'question_id': question_id, 'answer': answer, 'correct': correct})

        # Check if the game has finished
        game.status = 'completed' if len(game.responses) >= game.questions.count() else 'in_progress'
        game.save()

        # Calculate remaining questions
        remaining_questions = game.questions.exclude(id__in=[r['question_id'] for r in game.responses])
        remaining_questions_data = QuestionSerializer(remaining_questions, many=True).data

        # Find the next question not answered yet
        next_question = None
        for question in game.questions.all():
            if not any(r['question_id'] == question.id for r in game.responses):
                next_question = question
                break

        # Calculate the index of the current question
        current_question_index = self.calculate_current_question_index(game)

        # Calculate the number of correct answers
        correct_answers_count = self.calculate_correct_answers_count(game)

        # Return the response
        response_data = {
        'correct': correct,
        'score': game.score,
        'status': game.status,
        'next_question': QuestionSerializer(next_question).data if next_question else None,
        'current_question_index': current_question_index,
        'correct_answers_count': correct_answers_count,
        }
        return Response(response_data, status=status.HTTP_200_OK)

    @action(detail=True, methods=['post'])
    def answer_time_trial(self, request, pk=None):
        game = self.get_object()

        # Check if the game has already been completed
        if game.status == 'completed':
            return Response({'detail': 'Esta partida ya ha sido completada.', 'status': game.status}, status=status.HTTP_400_BAD_REQUEST)

        question_id = request.data.get('question_id')
        answer = request.data.get('answer')
        time_left = request.data.get('time_left')

        # Check if the question has already been answered
        if any(r['question_id'] == question_id for r in game.responses):
            raise ValidationError('Esta pregunta ya ha sido respondida.')

        # Get the question and check if it belongs to the current game
        question = get_object_or_404(Question, id=question_id)
        if question not in game.questions.all():
            raise ValidationError('Esta pregunta no pertenece a la partida actual.')

        # Check if the answer is correct
        correct = (question.correct_answer == int(answer))
        if correct:
            game.update_on_correct_answer()
        else:
            game.update_on_incorrect_answer()

        # Update the time left
        game.time_left = max(0, int(time_left))  # Ensure time_left is not negative

        # Actualizar el perfil del usuario
        profile = UserProfile.objects.get(user=game.player)
        profile.update_on_answer(correct=correct)

        # Register the response
        game.responses.append({'question_id': question_id, 'answer': answer, 'correct': correct})

        # Check if the game has finished
        if game.is_time_over() or len(game.responses) >= game.questions.count():
            game.status = 'completed'
        else:
            game.status = 'in_progress'
        game.save()

        # Calculate remaining questions
        remaining_questions = game.questions.exclude(id__in=[r['question_id'] for r in game.responses])
        remaining_questions_data = QuestionSerializer(remaining_questions, many=True).data

        # Find the next question not answered yet
        next_question = None
        for question in game.questions.all():
            if not any(r['question_id'] == question.id for r in game.responses):
                next_question = question
                break

        # Calculate the index of the current question
        current_question_index = self.calculate_current_question_index(game)

        # Calculate the number of correct answers
        correct_answers_count = self.calculate_correct_answers_count(game)

        # Return the response
        response_data = {
            'correct': correct,
            'score': game.score,
            'status': game.status,
            'next_question': QuestionSerializer(next_question).data if next_question else None,
            'current_question_index': current_question_index,
            'correct_answers_count': correct_answers_count,
            'time_left': game.time_left,
        }
        return Response(response_data, status=status.HTTP_200_OK)


    @action(detail=False, methods=['get'])
    def resume(self, request):
        # Search for the game in progress for the current user
        game = Game.objects.filter(player=request.user, status='in_progress').first()
        if not game:
            return Response({'message': 'No hay juego en progreso.'}, status=status.HTTP_200_OK)

        # Find the first question not answered yet
        next_question = None
        for question in game.questions.all():
            if not any(r['question_id'] == question.id for r in game.responses):
                next_question = question
                break

        if next_question is None:
            raise Http404("No hay más preguntas o el juego está completo.")
        
        current_question_index = self.calculate_current_question_index(game)
        # Calculate the number of correct answers
        correct_answers_count = self.calculate_correct_answers_count(game)

        response_data = {
            'game': GameSerializer(game).data,
            'next_question': QuestionSerializer(next_question).data,
            'current_question_index': current_question_index,
            'correct_answers_count': correct_answers_count,
            'time_left': game.time_left,
        }
        print(response_data)
        return Response(response_data)

    @action(detail=True, methods=['post'])
    def finish_game(self, request, pk=None):
        game = self.get_object()

        if game.status != 'in_progress':
            return Response({'detail': 'Game is not in progress.'}, status=status.HTTP_400_BAD_REQUEST)

        if game.game_type == 'time_trial':
            max_time = request.data.get('max_time_trial_time')
            if max_time:
                profile = UserProfile.objects.get(user=game.player)
                profile.update_max_time_trial_points(int(game.score))
                profile.update_max_time_trial_time(int(max_time))
                game.update_max_time_trial_time(int(max_time))

        game.status = 'completed'
        game.save()

        # Update the user profile
        profile = UserProfile.objects.get(user=game.player)
        profile.update_on_game_end(abandoned=False)

        return Response({'status': 'Game marked as completed.'}, status=status.HTTP_200_OK)

    @action(detail=True, methods=['post'])
    def abandon_game(self, request, pk=None):
        game = self.get_object()
        game.status = 'incomplete'
        game.save()

        # Update the user profile
        profile = UserProfile.objects.get(user=game.player)
        profile.update_on_game_end(abandoned=True)

        return Response({'status': 'Game marked as incomplete.'}, status=status.HTTP_200_OK)
