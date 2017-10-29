package webpract.com.practical.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by wmtandroid5 on 28/10/17.
 */
@Dao
public interface DBDao {

    @Query("select * from DBModel")
    LiveData<List<DBModel>> getAllBrandItems();

    @Query("select * from DBModel where id = :id")
    DBModel getItembyId(String id);

    @Insert(onConflict = REPLACE)
    void addBrand(DBModel DBModel);

    @Query("delete from DBModel where isSync = 1")
    void clearAllSync();

    @Query("update DBModel SET isSync=1 where isSync=0")
    void updateBrand();

    @Insert
    long[] addAllBrand(DBModel[] DBModel);

    @Query("select * from DBModel where isSync = 0")
    List<DBModel> getUnSyncItems();

    @Query("select * from DBModel order by id desc limit 1")
    DBModel getLastRecord();

    @Query("select * from DBModel")
    List<DBModel> getAllBrandItemsList();
}

