# classic_game/management/commands/import_questions.py
# Usage: python manage.py import_questions <csv_file>
import csv
from django.core.management.base import BaseCommand
from classic_game.models import Question

class Command(BaseCommand):
    help = 'Load a questions bank from a CSV file into the database'

    def add_arguments(self, parser):
        parser.add_argument('csv_file', type=str, help='The CSV file to import.')

    def handle(self, *args, **kwargs):
        csv_file = kwargs['csv_file']
        
        with open(csv_file, 'r', encoding='utf-8') as file:
            reader = csv.DictReader(file)
            for row in reader:
                question_text = row['question'][:255]
                answer1 = row['answer1'][:255]
                answer2 = row['answer2'][:255]
                answer3 = row['answer3'][:255]
                answer4 = row['answer4'][:255]
                reference = row.get('reference', '')[:255]  # Campo de referencia opcional
                category = row.get('category')

                # Validar el campo category
                if category not in [choice[0] for choice in Question.CATEGORY_CHOICES]:
                    self.stdout.write(self.style.ERROR(f'Invalid category: {category}'))
                    continue

                correct_answer = int(row['correctAnswer'])
                if not 1 <= correct_answer <= 4:
                    self.stdout.write(self.style.ERROR(f'Invalid correctAnswer value: {correct_answer}'))
                    continue

                Question.objects.create(
                    question_text=question_text,
                    answer1=answer1,
                    answer2=answer2,
                    answer3=answer3,
                    answer4=answer4,
                    correct_answer=correct_answer,
                    reference=reference,
                    category=category
                )
        self.stdout.write(self.style.SUCCESS('Successfully loaded questions into the database'))
