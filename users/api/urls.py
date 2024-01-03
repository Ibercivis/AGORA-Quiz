from django.urls import path
from django.conf.urls import include
from dj_rest_auth.views import PasswordResetConfirmView, PasswordResetView
from dj_rest_auth.registration.views import VerifyEmailView, ConfirmEmailView
from django.conf import settings
from django.conf.urls.static import static
from users.api.views import EmailRecoveryView, ActivateAccountView, CustomConfirmEmailView, RecoverySuccess


urlpatterns = [
    path('users/activate-account/<str:key>/', ActivateAccountView.as_view(), name='activate-account'),
    path('users/registration/account-confirm-email/<str:key>/', CustomConfirmEmailView.as_view(), name='account_confirm_email'),
    path('users/registration/', include('dj_rest_auth.registration.urls')),
    path('users/account-confirm-email/', ConfirmEmailView.as_view(), name='account_email_verification_sent'),
    path('users/authentication/', include('dj_rest_auth.urls')),
    path('users/authentication/password/reset/', include('django.contrib.auth.urls')),
    path('users/authentication/password/reset/confirm/<str:uidb64>/<str:token>', PasswordResetConfirmView.as_view(), name='password_reset_confirm'),
    path('users/email_recovery/', EmailRecoveryView.as_view(), name='email_recovery'),
    path('users/recovery_success/', RecoverySuccess.as_view(), name='recovery_success'),
] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)