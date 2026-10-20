package com.example.tareaapis

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tareaapis.data.local.FavoritoEntity
import com.example.tareaapis.data.local.PokemonCacheEntity
import com.example.tareaapis.data.local.PokemonDatabase
import com.example.tareaapis.data.local.PokemonDetalleEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Pruebas de la base de datos Room (favoritos, caché de la lista y caché del detalle). */
@RunWith(AndroidJUnit4::class)
class PokemonDatabaseTest {

    private lateinit var context: Context
    private lateinit var db: PokemonDatabase

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, PokemonDatabase::class.java).build()
    }

    @After
    fun tearDown() = db.close()

    // ---- Etapa 1: favoritos ----

    @Test
    fun favorito_se_guarda_y_se_quita() = runBlocking {
        val dao = db.favoritoDao()
        dao.insertar(FavoritoEntity(25, "Pikachu", "url25"))
        assertEquals(listOf(25), dao.observarIdsFavoritos().first())

        dao.eliminarPorId(25)
        assertTrue(dao.observarIdsFavoritos().first().isEmpty())
    }

    @Test
    fun ejercicio1_contador_es_el_numero_de_ids() = runBlocking {
        val dao = db.favoritoDao()
        dao.insertar(FavoritoEntity(1, "Bulbasaur", "u1"))
        dao.insertar(FavoritoEntity(4, "Charmander", "u4"))
        dao.insertar(FavoritoEntity(7, "Squirtle", "u7"))
        assertEquals(3, dao.observarIdsFavoritos().first().size)
    }

    @Test
    fun ejercicio2_orden_por_fecha_y_por_nombre() = runBlocking {
        val dao = db.favoritoDao()
        dao.insertar(FavoritoEntity(7, "Squirtle", "u", agregadoEn = 100))
        dao.insertar(FavoritoEntity(1, "bulbasaur", "u", agregadoEn = 300))
        dao.insertar(FavoritoEntity(4, "Charmander", "u", agregadoEn = 200))

        // Por fecha: el más reciente primero
        assertEquals(listOf(1, 4, 7), dao.observarFavoritos().first().map { it.id })
        // Por nombre: alfabético sin distinguir mayúsculas
        assertEquals(listOf(1, 4, 7), dao.observarFavoritosPorNombre().first().map { it.id })

        dao.insertar(FavoritoEntity(25, "Abra", "u", agregadoEn = 50))
        assertEquals(25, dao.observarFavoritosPorNombre().first().first().id)
        assertEquals(25, dao.observarFavoritos().first().last().id)
    }

    @Test
    fun ejercicio4_nota_se_guarda_y_se_borra() = runBlocking {
        val dao = db.favoritoDao()
        dao.insertar(FavoritoEntity(25, "Pikachu", "u"))
        assertNull(dao.observarFavoritos().first().single().nota)

        dao.actualizarNota(25, "mi favorito")
        assertEquals("mi favorito", dao.observarFavoritos().first().single().nota)

        dao.actualizarNota(25, null)
        assertNull(dao.observarFavoritos().first().single().nota)
    }

    // ---- Etapa 2: caché ----

    @Test
    fun cache_se_guarda_ordenada_por_id() = runBlocking {
        val dao = db.pokemonCacheDao()
        assertEquals(0, dao.contar())
        dao.insertarTodos(listOf(PokemonCacheEntity(3, "c", "u"), PokemonCacheEntity(1, "a", "u")))
        assertEquals(2, dao.contar())
        assertEquals(listOf(1, 3), dao.observarTodos().first().map { it.id })
    }

    @Test
    fun ejercicio3_reemplazar_cache_no_toca_los_favoritos() = runBlocking {
        db.favoritoDao().insertar(FavoritoEntity(1, "Bulbasaur", "u"))
        val cache = db.pokemonCacheDao()
        cache.insertarTodos(listOf(PokemonCacheEntity(1, "viejo", "u"), PokemonCacheEntity(2, "viejo2", "u")))

        cache.reemplazarTodos(listOf(PokemonCacheEntity(5, "nuevo", "u")))

        assertEquals(listOf(5), cache.observarTodos().first().map { it.id })
        assertEquals(listOf(1), db.favoritoDao().observarIdsFavoritos().first())
    }

    @Test
    fun ejercicio5_detalle_se_guarda_y_se_lee() = runBlocking {
        val dao = db.pokemonCacheDao()
        assertNull(dao.obtenerDetalle(25))
        dao.guardarDetalle(PokemonDetalleEntity(25, """{"id":25}"""))
        assertEquals("""{"id":25}""", dao.obtenerDetalle(25))
    }

    // ---- Migraciones ----

    @Test
    fun migracion_de_la_version_1_conserva_los_favoritos() = runBlocking {
        val nombre = "migracion-test.db"
        context.deleteDatabase(nombre)

        // Base de datos tal como la dejaba la versión 1 de la app (solo "favoritos", sin "nota")
        val v1 = FrameworkSQLiteOpenHelperFactory().create(
            androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(nombre)
                .callback(object : androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `favoritos` (`id` INTEGER NOT NULL, " +
                                "`nombre` TEXT NOT NULL, `imagenUrl` TEXT NOT NULL, " +
                                "`agregadoEn` INTEGER NOT NULL, PRIMARY KEY(`id`))"
                        )
                        db.execSQL("INSERT INTO favoritos VALUES (25, 'Pikachu', 'url25', 123)")
                    }

                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                })
                .build()
        )
        v1.writableDatabase.close()
        v1.close()

        // Se abre con la versión actual: Room corre MIGRACION_1_2, 2_3 y 3_4 y valida el esquema
        val migrada = PokemonDatabase.crear(context, nombre)
        try {
            val favorito = migrada.favoritoDao().observarFavoritos().first().single()
            assertEquals(25, favorito.id)
            assertEquals("Pikachu", favorito.nombre)
            assertNull(favorito.nota)
            assertEquals(0, migrada.pokemonCacheDao().contar())
            assertNull(migrada.pokemonCacheDao().obtenerDetalle(25))
        } finally {
            migrada.close()
            context.deleteDatabase(nombre)
        }
    }
}
