package webpract.com.practical.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by wmtandroid5 on 28/10/17.
 */

@Database(entities = {DBModel.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
public static final String brand_db="brand_db";

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, brand_db
                    )
                            .build();
        }
        return INSTANCE;
    }

    public abstract DBDao brandModel();

}