# ASPAS Recuperação — estágio 1

Projeto extraído de `aspas.md` para recuperação isolada. Não execute publicação a partir deste diretório.

## Fluxo limpo de build
1. Instale JDK 17 e Android SDK API 35.
2. Gere/instale o Gradle Wrapper compatível.
3. Execute `./gradlew clean :app:assembleDebug`.
4. O APK debug usa `br.com.aspas.contingencia.recovery` e rótulo `ASPAS Recuperação TESTE`; release mantém a identidade original.

Consulte `UNIMPLEMENTED_FEATURES.md` antes de testes operacionais.
