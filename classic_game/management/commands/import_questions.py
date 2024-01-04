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
                # Recorta los campos a 255 caracteres si son más largos
                question_text = row['question'][:255]
                answer1 = row['answer1'][:255]
                answer2 = row['answer2'][:255]
                answer3 = row['answer3'][:255]
                answer4 = row['answer4'][:255]

                # Asegúrate de que el campo correct_answer es un entero y está dentro de un rango válido
                try:
                    correct_answer = int(row['correctAnswer'])
                    if not 1 <= correct_answer <= 4:
                        self.stdout.write(self.style.ERROR(f'Invalid correctAnswer value: {correct_answer}'))
                        continue
                except ValueError:
                    self.stdout.write(self.style.ERROR(f'Invalid correctAnswer format: {row["correctAnswer"]}'))
                    continue

                Question.objects.create(
                    question_text=question_text,
                    answer1=answer1,
                    answer2=answer2,
                    answer3=answer3,
                    answer4=answer4,
                    correct_answer=correct_answer
                )
        self.stdout.write(self.style.SUCCESS('Successfully loaded questions into the database'))
