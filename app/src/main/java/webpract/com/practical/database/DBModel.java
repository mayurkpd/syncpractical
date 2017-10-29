package webpract.com.practical.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

/**
 * Created by wmtandroid5 on 28/10/17.
 */
@Entity
public class DBModel {


    @PrimaryKey(autoGenerate = true)
    public int id;
    private String name;
    private String description;
    private String date;
    public int serverId;
    int isSync;

    public DBModel(String name, String description, String date,@Nullable int serverId, int isSync) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.serverId = serverId;
        this.isSync = isSync;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public int getIsSync() {
        return isSync;
    }
}
