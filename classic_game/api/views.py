from django.shortcuts import get_object_or_404
from django.core.exceptions import ValidationError

from rest_framework import viewsets
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework import status
from rest_framework.permissions import IsAuthenticated

from .models import Question, Game
from .serializers import QuestionSerializer, GameSerializer  

import random


class QuestionViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Question.objects.all()
    serializer_class = QuestionSerializer

class GameViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Game.objects.all()
    serializer_class = GameSerializer

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
        return Response(self.get_serializer(game).data)

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

        # Devolver la respuesta incluyendo el estado de la partida
        return Response({
            'correct': correct,
            'score': game.score,
            'remaining_questions': remaining_questions_data,
            'status': game.status  # Incluye el estado de la partida
        }, status=status.HTTP_200_OK)

        @action(detail=True, methods=['post'])
        def abandon_game(self, request, pk=None):
            game = self.get_object()
            game.status = 'incomplete'
            game.save()
            return Response({'status': 'Game marked as incomplete.'}, status=status.HTTP_200_OK)
