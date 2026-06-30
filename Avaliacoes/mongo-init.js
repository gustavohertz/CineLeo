db = db.getSiblingDB('avaliacoes_db');

db.createUser({
    user: 'avaliacoes_user',
    pwd: 'avaliacoes_pass',
    roles: [{ role: 'readWrite', db: 'avaliacoes_db' }]
});

db.avaliacoes.createIndex(
    { usuarioId: 1, filmeId: 1, reservaId: 1 },
    { unique: true, name: "idx_avaliacao_unica" }
);

db.avaliacoes.createIndex({ filmeId: 1, status: 1 }, { name: "idx_avaliacao_filme_status" });

db.avaliacoes.createIndex({ usuarioId: 1 }, { name: "idx_avaliacao_usuario" });

print('CineLeo Avaliacoes DB inicializado com sucesso!');
