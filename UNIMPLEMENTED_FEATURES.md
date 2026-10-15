# Recursos ainda não implementados

- Sincronização real mDNS/Ktor: `SyncEngineImpl` retorna estado local e não descobre nem replica pares.
- Bootstrap de dados: não há chamada para importar `catalog.json` nem semeadura de mesas; sem isso a UI inicia sem catálogo/mesas.
- Interface de bundles Parrilla, pesos, meios-preços e modificadores não está integrada ao fluxo de pedido.
- Gestão de caixa, usuários, auditoria, reversões de pagamento, taxa removível e administração não têm telas/fluxos completos.
- Encaminhamento de impressão a pares é apenas um evento, sem projeção/transporte.
- Teste/reimpressão/cancelamento não expõe todos os comandos necessários pela UI; motivo de cancelamento não é persistido no lote de impressão.
- Recuperação/replay de projeções e snapshots não estão acionados; projeções só acompanham eventos observados após inicialização.
- Segurança operacional: PIN inicial `0000` é provisório e deve ser alterado antes de uso.
- Não foram realizados build, instalação, testes instrumentados, testes de impressora ou publicação neste estágio.
