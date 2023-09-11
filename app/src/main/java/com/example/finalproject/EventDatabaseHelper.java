
package com.example.finalproject;

import static android.media.tv.TvContract.Programs.COLUMN_EVENT_ID;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EventDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "event.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_EVENTS = "events";
    private static final String COLUMN_DOCUMENT_ID = "document_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_EVENT_TYPE = "event_type";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_RISK_LEVEL = "risk_level";
    private static final String COLUMN_REGION = "region";
    private static final String COLUMN_IMAGE = "image";

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String TABLE_EVENT_STATUS = "event_status";
    private static final String COLUMN_EVENT_STATUS = "event_status";

    private static final String COLUMN_DATE = "date";

    private static EventDatabaseHelper instance;
    private static String currentUserId;

    private Context context;


    public EventDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    public static synchronized EventDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new EventDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Sets the current user ID.
     *
     * @param userId The current user ID to be set.
     */
    public static void setCurrentUserId(String userId) {
        currentUserId = userId;
    }

    /**
     * Updates an event in the database.
     *
     * @param event The event to be updated.
     * @return true if the event was updated successfully, false otherwise.
     */
    public boolean updateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the current user is the creator of the event
        if (!event.getUserId().equals(currentUserId)) {
            ContentValues values = new ContentValues();

            int rowsAffected = db.update(TABLE_EVENTS, values, COLUMN_DOCUMENT_ID + "=?", new String[]{event.getEventId()});
            return rowsAffected > 0;
        } else {
            // If the current user is the creator of the event, do not allow updating the approved status
            return false;
        }
    }

    /**
     * Generates a unique ID for an event.
     *
     * @return A unique ID for an event.
     */
    public static String generateUniqueId() {
        long timestamp = System.currentTimeMillis();
        return String.valueOf(timestamp);
    }

    /**
     * Called when the database is created for the first time.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + TABLE_EVENTS + " (" +
                COLUMN_DOCUMENT_ID + " TEXT PRIMARY KEY, " +
                COLUMN_USER_ID + " TEXT, " +
                COLUMN_EVENT_TYPE + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_RISK_LEVEL + " TEXT, " +
                COLUMN_REGION + " TEXT, " +
                COLUMN_IMAGE + " BLOB, " +
                COLUMN_DATE + " INTEGER)";
        db.execSQL(createTableStatement);

        createTableStatement = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createTableStatement);

        String CREATE_EVENT_STATUS_TABLE = "CREATE TABLE " + TABLE_EVENT_STATUS +
                "(" +
                COLUMN_DOCUMENT_ID + " TEXT," +
                COLUMN_USER_ID + " TEXT," +
                COLUMN_EVENT_STATUS + " INTEGER," +
                "PRIMARY KEY (" + COLUMN_DOCUMENT_ID + ", " + COLUMN_USER_ID + ")" +
                ")";
        db.execSQL(CREATE_EVENT_STATUS_TABLE);

        // Insert sample data into the users table
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, "unique_user_id_1");
        values.put(COLUMN_USERNAME, "user1");
        values.put(COLUMN_PASSWORD, "1234");
        db.insert(TABLE_USERS, null, values);

        values = new ContentValues();
        values.put(COLUMN_USER_ID, "unique_user_id_2");
        values.put(COLUMN_USERNAME, "user2");
        values.put(COLUMN_PASSWORD, "1234");
        db.insert(TABLE_USERS, null, values);

        values = new ContentValues();
        values.put(COLUMN_USER_ID, "unique_user_id_3");
        values.put(COLUMN_USERNAME, "user3");
        values.put(COLUMN_PASSWORD, "1234");
        db.insert(TABLE_USERS, null, values);
    }

    /**
     * Called when the database needs to be upgraded.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_STATUS);
        onCreate(db);
    }

    /**
     * Adds an event to the database.
     *
     * @param event The event to be added.
     * @return true if the event was added successfully, false otherwise.
     */
    public boolean addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_EVENTS, null, COLUMN_DOCUMENT_ID + "=?", new String[]{event.getEventId()}, null, null, null);
        boolean eventExists = cursor.getCount() > 0;
        cursor.close();

        if (eventExists) {
            // Event already exists, handle accordingly (e.g., update or skip insertion)
            return false;
        } else {
            ContentValues values = new ContentValues();
            String documentId = UUID.randomUUID().toString();
            values.put(COLUMN_DOCUMENT_ID, documentId);
            // Get current user ID from SharedPrefManager
            SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);

            String currentUserId = sharedPrefManager.getLoggedInUserId();


            values.put(COLUMN_USER_ID, currentUserId);
            values.put(COLUMN_EVENT_TYPE, event.getEventType());
            values.put(COLUMN_LOCATION, event.getLocation());
            values.put(COLUMN_DESCRIPTION, event.getDescription());
            values.put(COLUMN_RISK_LEVEL, event.getRiskLevel());
            values.put(COLUMN_REGION, event.getRegion());
            if (event.getEventImage() != null) {
                values.put(COLUMN_IMAGE, Utils.getBytes(event.getEventImage()));
            }
            values.put(COLUMN_DATE, System.currentTimeMillis());
            long insertResult = db.insert(TABLE_EVENTS, null, values);
            return insertResult != -1;
        }
    }



    /**
     * Retrieves a list of events created by the current user.
     *
     * @param currentUserId The ID of the current user.
     * @return A list of events created by the current user.
     */
    public List<Event> getEventsCreatedByUser(String currentUserId) {
        List<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EVENTS +
                " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{currentUserId});
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String eventId = cursor.getString(cursor.getColumnIndex(COLUMN_DOCUMENT_ID));
                @SuppressLint("Range") String eventType = cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_TYPE));
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                @SuppressLint("Range") String riskLevel = cursor.getString(cursor.getColumnIndex(COLUMN_RISK_LEVEL));
                @SuppressLint("Range") String region = cursor.getString(cursor.getColumnIndex(COLUMN_REGION));
                @SuppressLint("Range") byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
                @SuppressLint("Range") long dateInMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE));
                Date date = new Date(dateInMillis);

                Bitmap eventImage = null;
                if (imageBytes != null) {
                    eventImage = Utils.getImage(imageBytes);
                }

                Event event = new Event(eventId, currentUserId, eventType, location, description, riskLevel, region, eventImage, date);
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventList;
    }

    /**
     * Retrieves a list of events created by other users.
     *
     * @param userId The ID of the user.
     * @return A list of events created by other users.
     */
    public List<Event> getEventsByOtherUsers(String userId) {
        List<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EVENTS +
                " WHERE " + COLUMN_USER_ID + " != ?";
        Cursor cursor = db.rawQuery(query, new String[]{userId});
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String eventId = cursor.getString(cursor.getColumnIndex(COLUMN_DOCUMENT_ID));
                @SuppressLint("Range") String eventType = cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_TYPE));
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                @SuppressLint("Range") String riskLevel = cursor.getString(cursor.getColumnIndex(COLUMN_RISK_LEVEL));
                @SuppressLint("Range") String region = cursor.getString(cursor.getColumnIndex(COLUMN_REGION));
                @SuppressLint("Range") byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
                @SuppressLint("Range") long dateInMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE));
                Date date = new Date(dateInMillis);

                Bitmap eventImage = null;
                if (imageBytes != null) {
                    eventImage = Utils.getImage(imageBytes);
                }

                Event event = new Event(eventId, currentUserId, eventType, location, description, riskLevel, region, eventImage, date);
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventList;
    }

    /**
     * Deletes an event from the database.
     *
     * @param eventId The ID of the event to be deleted.
     * @return true if the event was deleted successfully, false otherwise.
     */
    public boolean deleteEvent(String eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_EVENTS, COLUMN_DOCUMENT_ID + "=?", new String[]{eventId});
        return result > 0;
    }

    /**
     * Authenticates a user based on their username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return true if the user is authenticated, false otherwise.
     */
    @SuppressLint("Range")
    public boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        if (cursor.moveToFirst()) {
            String storedPassword = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
            if (storedPassword != null && storedPassword.equals(password)) {
                currentUserId = cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID));
                return true;
            }
        }
        cursor.close();
        return false;
    }

    /**
     * Returns the ID of the current user.
     *
     * @return The ID of the current user.
     */
    public static String getCurrentUserId(Context context) {
        return SharedPrefManager.getInstance(context).getLoggedInUserId();
    }

    public static String generateUniqueId(String currentUserId) {
        // Implement your logic to generate a unique eventId based on the currentUserId
        // For example, you can use the currentUserId + timestamp combination
        long timestamp = System.currentTimeMillis();
        return currentUserId + "-" + timestamp;
    }

    /**
     * Retrieves the user ID based on the username.
     *
     * @param username The username.
     * @return The ID of the user.
     */
    public String getUserId(String username) {
        String userId = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        if (cursor.moveToFirst()) {
            userId = cursor.getString(0);
        }
        cursor.close();
        return userId;
    }

    /**
     * Retrieves a list of event IDs created by other users.
     *
     * @param userId The ID of the user.
     * @return A list of event IDs created by other users.
     */
    public List<String> getEventIdsByUser(String userId) {
        List<String> eventIdList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_DOCUMENT_ID + " FROM " + TABLE_EVENTS +
                " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userId});
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String eventId = cursor.getString(cursor.getColumnIndex(COLUMN_DOCUMENT_ID));
                eventIdList.add(eventId);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventIdList;
    }




    /**
     * Records the status of an event.
     *
     * @param eventId The ID of the event.
     * @param userId  The ID of the user.
     * @param status  The status of the event.
     * @return true if the event status was recorded successfully, false otherwise.
     */
    public boolean recordEventStatus(String eventId, String userId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DOCUMENT_ID, eventId);
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_EVENT_STATUS, status);

        long result = db.insertWithOnConflict(TABLE_EVENT_STATUS, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
        // if the row ID is -1, then there was an error inserting the data
        return result != -1;
    }

    /**
     * Retrieves a list of events that have not been acted upon by the user.
     *
     * @param userId The ID of the user.
     * @return A list of events that have not been acted upon by the user.
     */
    public List<Event> getEventsNotActedUponByUser(String userId) {
        List<Event> events = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + COLUMN_DOCUMENT_ID + " NOT IN (SELECT " + COLUMN_DOCUMENT_ID + " FROM " + TABLE_EVENT_STATUS + " WHERE " + COLUMN_USER_ID + " = ?)";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{userId});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String eventId = cursor.getString(cursor.getColumnIndex(COLUMN_DOCUMENT_ID));
                @SuppressLint("Range") String eventType = cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_TYPE));
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                @SuppressLint("Range") String riskLevel = cursor.getString(cursor.getColumnIndex(COLUMN_RISK_LEVEL));
                @SuppressLint("Range") String region = cursor.getString(cursor.getColumnIndex(COLUMN_REGION));
                @SuppressLint("Range") byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
                @SuppressLint("Range") long dateInMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE));
                Date date = new Date(dateInMillis);

                Bitmap eventImage = null;
                if (imageBytes != null) {
                    eventImage = Utils.getImage(imageBytes);
                }

                Event event = new Event(eventId, currentUserId, eventType, location, description, riskLevel, region, eventImage, date);
                events.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return events;
    }
}
