from rest_framework import viewsets
from django.conf import settings
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from rest_framework import generics, permissions
from rest_framework.parsers import MultiPartParser, FormParser, JSONParser
import requests
from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from django.utils.decorators import method_decorator
from django.views import View
from django.http import HttpResponse
from rest_framework.permissions import AllowAny
from dj_rest_auth.registration.views import ConfirmEmailView
import re

@method_decorator(csrf_exempt, name='dispatch')
class CustomConfirmEmailView(ConfirmEmailView):
    authentication_classes = []  # Deshabilita todas las clases de autenticación
    permission_classes = [AllowAny]  # Permite el acceso a cualquier usuario, autenticado o no

    @method_decorator(csrf_exempt)
    def post(self, request, *args, **kwargs):
        response = super().post(request, *args, **kwargs)
        # Verifica si la confirmación fue exitosa
        if response.status_code == 302:  # 302 es un código de redirección
            return HttpResponse("Confirmación de correo electrónico exitosa.", status=200)
        else:
            return response

@method_decorator(csrf_exempt, name='dispatch')
class ActivateAccountView(View):
    authentication_classes = []  # Deshabilita todas las clases de autenticación
    permission_classes = [AllowAny]  # Permite el acceso a cualquier usuario, autenticado o no

    @method_decorator(csrf_exempt)
    def get(self, request, key):
        # Define la URL de la API para la activación
        api_url = f'{settings.BASE_URL}/api/users/registration/account-confirm-email/{key}/'.format(key=key)

        # Realiza la llamada POST al servidor para activar la cuenta
        response = requests.post(api_url, data={'key': key})

        # Comprueba la respuesta y muestra un mensaje adecuado al usuario
        if response.status_code == 200:
            # La cuenta ha sido activada
            return render(request, 'activation_success.html')
        else:
            # Algo salió mal, maneja el error
            return render(request, 'activation_fail.html', {'error': response.text})

class EmailRecoveryView(View):
    def get(self, request, *args, **kwargs):
        reset_url = request.GET.get('resetUrl', '')
        
        # Extraer uid y token usando una expresión regular
        match = re.search(r'reset/confirm/([^/]+)/([^/]+)$', reset_url)
        if match:
            uid = match.group(1)
            token = match.group(2)
        else:
            uid = ''
            token = ''
        
        print("reset_url: ", reset_url)
        print("UID: ", uid)
        print("Token: ", token)

        context = {
            'uid': uid,
            'token': token,
        }
        return render(request, 'email_recovery.html', context)
    
class RecoverySuccess(View):
    def get(self, request, *args, **kwargs):
        return render(request, 'recovery_success.html')