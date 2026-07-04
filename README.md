# Kegel

Aplicativo Android em Kotlin para lembretes de exercícios de Kegel e meditação.

## Funcionalidades

- Configuração de horário ativo diário
- Ajuste do número de exercícios de Kegel (0–10)
- Ajuste do número de meditações (0–3)
- Duração do exercício: 2 ou 3 minutos
- Duração da meditação: 5 a 10 minutos
- Notificações agendadas aleatoriamente dentro do período ativo
- Reinicia agendamento após reboot

## Como usar

1. Crie um repositório no GitHub e envie estes arquivos.
2. Mantenha a branch `main`.
3. O workflow GitHub Actions irá compilar o APK em cada push para `main`.

## APK

O APK de debug será gerado em `app/build/outputs/apk/debug/app-debug.apk` e ficará disponível como artefato do workflow.
