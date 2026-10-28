// Edge Function de SQLite Cloud: "pokedex"
// Se crea en el dashboard (Edge Functions -> JavaScript), con la base de datos "pokedex" seleccionada.
// La app solo manda { action, ... }; el SQL vive aquí y no se puede ejecutar SQL arbitrario desde afuera.

const body = request.data || {}
const action = body.action

const int = (v) => Math.trunc(Number(v))
const text = (v) => String(v ?? '')

await connection.sql`USE DATABASE pokedex;`
await connection.sql`CREATE TABLE IF NOT EXISTS favoritos (
  id INTEGER PRIMARY KEY,
  nombre TEXT NOT NULL,
  imagenUrl TEXT NOT NULL,
  agregadoEn INTEGER NOT NULL,
  nota TEXT
);`
await connection.sql`CREATE TABLE IF NOT EXISTS pokemon_detalle (
  id INTEGER PRIMARY KEY,
  json TEXT NOT NULL
);`

switch (action) {
  case 'favoritos_listar': {
    const rows = await connection.sql`SELECT * FROM favoritos ORDER BY agregadoEn DESC;`
    return { data: rows }
  }
  case 'favoritos_guardar': {
    const id = int(body.id)
    if (!Number.isFinite(id)) throw new Error('id invalido')
    await connection.sql`INSERT OR REPLACE INTO favoritos (id, nombre, imagenUrl, agregadoEn, nota)
      VALUES (${id}, ${text(body.nombre)}, ${text(body.imagenUrl)}, ${int(body.agregadoEn)}, ${body.nota ?? null});`
    return { data: 'OK' }
  }
  case 'favoritos_eliminar': {
    const id = int(body.id)
    if (!Number.isFinite(id)) throw new Error('id invalido')
    await connection.sql`DELETE FROM favoritos WHERE id = ${id};`
    return { data: 'OK' }
  }
  case 'detalle_obtener': {
    const id = int(body.id)
    const rows = await connection.sql`SELECT json FROM pokemon_detalle WHERE id = ${id};`
    return { data: rows }
  }
  case 'detalle_guardar': {
    const id = int(body.id)
    await connection.sql`INSERT OR REPLACE INTO pokemon_detalle (id, json) VALUES (${id}, ${text(body.json)});`
    return { data: 'OK' }
  }
  default:
    throw new Error('accion desconocida: ' + action)
}
