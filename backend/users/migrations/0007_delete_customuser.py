# Generated by Django 4.2.9 on 2024-05-28 18:35

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('users', '0006_customuser'),
    ]

    operations = [
        migrations.DeleteModel(
            name='CustomUser',
        ),
    ]
