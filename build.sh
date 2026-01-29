#!/usr/bin/env bash
# Build script para Render (sem Docker)

set -e

echo "🔧 Instalando dependências e compilando..."
./mvnw clean package -DskipTests

echo "✅ Build concluído!"