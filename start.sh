#!/bin/bash
echo "🚀 Iniciando ecossistema CineLeo..."
docker compose up -d
echo ""
echo "📋 Serviços e portas:"
echo "  Eureka:         http://localhost:8761"
echo "  Eventos:        http://localhost:8082"
echo "  Usuários:       http://localhost:8083"
echo "  Pagamento:      http://localhost:5000"
echo "  Notification:   http://localhost:8000"
echo "  Kafka Consumer: http://localhost:8081"
echo "  Observabilidade:http://localhost:8090"
echo ""
docker compose ps