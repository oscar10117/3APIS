package com.example.tareaapis.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [FavoritoEntity::class, PokemonCacheEntity::class, PokemonDetalleEntity::class],
    version = 4,
    exportSchema = false
)
abstract class PokemonDatabase : RoomDatabase() {

    abstract fun favoritoDao(): FavoritoDao
    abstract fun pokemonCacheDao(): PokemonCacheDao

    companion object {
        @Volatile
        private var instancia: PokemonDatabase? = null

        // Version 1 solo tenia "favoritos": se agrega la cache sin perder los favoritos.
        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `pokemon_cache` (" +
                        "`id` INTEGER NOT NULL, " +
                        "`nombre` TEXT NOT NULL, " +
                        "`imagenUrl` TEXT NOT NULL, " +
                        "PRIMARY KEY(`id`))"
                )
            }
        }

        // Version 3: los favoritos ganan una nota opcional.
        private val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE favoritos ADD COLUMN nota TEXT")
            }
        }

        // Version 4: tabla para guardar el detalle de cada pokemon.
        private val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `pokemon_detalle` (" +
                        "`id` INTEGER NOT NULL, " +
                        "`json` TEXT NOT NULL, " +
                        "PRIMARY KEY(`id`))"
                )
            }
        }

        // Singleton: una sola instancia de la base de datos en toda la app.
        fun obtener(context: Context): PokemonDatabase =
            instancia ?: synchronized(this) {
                instancia ?: crear(context, "pokedex.db").also { instancia = it }
            }

        // Sin singleton: lo usan las pruebas para abrir un archivo de base de datos aparte.
        fun crear(context: Context, nombre: String): PokemonDatabase =
            Room.databaseBuilder(context.applicationContext, PokemonDatabase::class.java, nombre)
                .addMigrations(MIGRACION_1_2, MIGRACION_2_3, MIGRACION_3_4)
                .build()
    }
}
