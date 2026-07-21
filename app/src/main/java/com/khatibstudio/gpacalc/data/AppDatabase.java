package com.khatibstudio.gpacalc.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.khatibstudio.gpacalc.data.dao.GradingScaleDao;
import com.khatibstudio.gpacalc.data.dao.InstitutionPresetDao;
import com.khatibstudio.gpacalc.data.dao.ProfileDao;
import com.khatibstudio.gpacalc.data.dao.SchoolDao;
import com.khatibstudio.gpacalc.data.dao.UniversityDao;
import com.khatibstudio.gpacalc.data.entity.AdmissionCutoff;
import com.khatibstudio.gpacalc.data.entity.Course;
import com.khatibstudio.gpacalc.data.entity.ExamRecord;
import com.khatibstudio.gpacalc.data.entity.GradePoint;
import com.khatibstudio.gpacalc.data.entity.GradingScale;
import com.khatibstudio.gpacalc.data.entity.InstitutionPreset;
import com.khatibstudio.gpacalc.data.entity.Profile;
import com.khatibstudio.gpacalc.data.entity.Semester;
import com.khatibstudio.gpacalc.data.entity.Subject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                GradingScale.class,
                GradePoint.class,
                Profile.class,
                ExamRecord.class,
                Subject.class,
                Semester.class,
                Course.class,
                AdmissionCutoff.class,
                InstitutionPreset.class   // added in v2
        },
        version = 4,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // Fixed-size pool: DB writes should never block the UI thread.
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public abstract GradingScaleDao gradingScaleDao();
    public abstract SchoolDao schoolDao();
    public abstract UniversityDao universityDao();
    public abstract ProfileDao profileDao();
    public abstract InstitutionPresetDao institutionPresetDao();

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "gpacalc_db")
                            // During development, wipe and reseed on schema change.
                            // Before production release: replace with proper Migration scripts.
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .addCallback(seedCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Seeds default grading scales and institution presets on first creation so
    // the app is immediately usable without any manual setup.
    private static final RoomDatabase.Callback seedCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull androidx.sqlite.db.SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                AppDatabase instance = INSTANCE;
                if (instance != null) {
                    DefaultScaleSeeder.seed(instance);
                }
            });
        }
    };
}
