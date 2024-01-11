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

import random


class QuestionViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Question.objects.all()
    serializer_class = QuestionSerializer

class GameViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Game.objects.all()
    serializer_class = GameSerializer

    def calculate_current_question_index(self, game):
        # Calcula el índice de la pregunta actual
        answered_questions_count = len(game.responses)
        return answered_questions_count + 1  # +1 ya que el índice debería comenzar desde 1

    def calculate_correct_answers_count(self, game):
        # Cuenta el número de respuestas correctas
        return sum(1 for response in game.responses if response.get('correct', False))

    @action(detail=False, methods=['post'])
    def start_game(self, request, *args, **kwargs):
        # Verificar si ya existe una partida en progreso
        if Game.objects.filter(player=request.user, status='in_progress').exists():
            return Response({'detail': 'Ya hay una partida en progreso.'}, status=status.HTTP_400_BAD_REQUEST)

        # Iniciar un nuevo juego con un conjunto aleatorio de preguntas
        questions = random.sample(list(Question.objects.all()), 9)
        game = Game.objects.create(player=request.user)
        game.questions.set(questions)
        game.status = 'in_progress'
        game.save()
        # Encuentra la próxima pregunta que no ha sido respondida
        next_question = None
        for question in game.questions.all():
            if not any(r['question_id'] == question.id for r in game.responses):
                next_question = question
                break
        #return Response(self.get_serializer(game).data)
        response_data = {
        'game': GameSerializer(game).data,
        'score': game.score,
        'status': game.status,
        'next_question': QuestionSerializer(next_question).data if next_question else None,
        'current_question_index': 1,
        'correct_answers_count': 0,
        }
        return Response(response_data, status=status.HTTP_200_OK)

    @action(detail=True, methods=['post'])
    def answer(self, request, pk=None):
        game = self.get_object()

        # Verifica si el juego ya ha sido completado
        if game.status == 'completed':
            return Response({'detail': 'Esta partida ya ha sido completada.', 'status': game.status}, status=status.HTTP_400_BAD_REQUEST)

        question_id = request.data.get('question_id')
        answer = request.data.get('answer')

        # Verificar si la pregunta ya ha sido respondida
        if any(r['question_id'] == question_id for r in game.responses):
            raise ValidationError('Esta pregunta ya ha sido respondida.')

        # Obtener la pregunta y verificar que pertenezca a la partida actual
        question = get_object_or_404(Question, id=question_id)
        if question not in game.questions.all():
            raise ValidationError('Esta pregunta no pertenece a la partida actual.')

        # Comprobar si la respuesta es correcta
        correct = (question.correct_answer == int(answer))
        if correct:
            game.score += 5  # puntos por respuesta correcta
        else:
            game.score -= 3  # puntos por respuesta incorrecta

        # Registrar la respuesta
        game.responses.append({'question_id': question_id, 'answer': answer, 'correct': correct})

        # Comprobar si la partida ha finalizado
        game.status = 'completed' if len(game.responses) >= game.questions.count() else 'in_progress'
        game.save()

        # Calcular preguntas restantes
        remaining_questions = game.questions.exclude(id__in=[r['question_id'] for r in game.responses])
        remaining_questions_data = QuestionSerializer(remaining_questions, many=True).data

        # Encuentra la próxima pregunta que no ha sido respondida
        next_question = None
        for question in game.questions.all():
            if not any(r['question_id'] == question.id for r in game.responses):
                next_question = question
                break

        # Calcular el índice de la pregunta actual
        current_question_index = self.calculate_current_question_index(game)

        # Calcular el número de respuestas acertadas
        correct_answers_count = self.calculate_correct_answers_count(game)

        # Devolver la respuesta
        response_data = {
        'correct': correct,
        'score': game.score,
        'status': game.status,
        'next_question': QuestionSerializer(next_question).data if next_question else None,
        'current_question_index': current_question_index,
        'correct_answers_count': correct_answers_count,
        }
        return Response(response_data, status=status.HTTP_200_OK)

    @action(detail=False, methods=['get'])
    def resume(self, request):
        # Busca un juego en progreso para el usuario actual
        game = Game.objects.filter(player=request.user, status='in_progress').first()
        if not game:
            return Response({'message': 'No hay juego en progreso.'}, status=status.HTTP_200_OK)

        # Encuentra la próxima pregunta que no ha sido respondida
        next_question = None
        for question in game.questions.all():
            if not any(r['question_id'] == question.id for r in game.responses):
                next_question = question
                break

        if next_question is None:
            raise Http404("No hay más preguntas o el juego está completo.")
        
        current_question_index = self.calculate_current_question_index(game)
        # Calcular el número de respuestas acertadas
        correct_answers_count = self.calculate_correct_answers_count(game)

        response_data = {
            'game': GameSerializer(game).data,
            'next_question': QuestionSerializer(next_question).data,
            'current_question_index': current_question_index,
            'correct_answers_count': correct_answers_count,
        }
        return Response(response_data)

    @action(detail=True, methods=['post'])
    def abandon_game(self, request, pk=None):
        game = self.get_object()
        game.status = 'incomplete'
        game.save()
        return Response({'status': 'Game marked as incomplete.'}, status=status.HTTP_200_OK)
