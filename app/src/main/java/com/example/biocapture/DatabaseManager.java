package com.example.biocapture;

import android.os.Environment;
import android.util.Log;

import com.morpho.morphosmart.sdk.CustomInteger;
import com.morpho.morphosmart.sdk.MorphoDatabase;
import com.morpho.morphosmart.sdk.MorphoField;
import com.morpho.morphosmart.sdk.MorphoSmartException;
import com.morpho.morphosmart.sdk.MorphoUser;
import com.morpho.morphosmart.sdk.TemplateType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseManager {

    private static final String TAG = "DatabaseManager";
    private static MorphoDatabase existingMorphoDatabaseInstance = null;

    // Retrofit API service instance
    private ApiService apiService;

    public DatabaseManager(ApiService apiService) {
        this.apiService = apiService;
    }

    // Method to obtain an existing MorphoDatabase instance
    public MorphoDatabase obtainExistingMorphoDatabase() throws MorphoSmartException {
        if (existingMorphoDatabaseInstance == null) {
            throw new MorphoSmartException("Existing MorphoDatabase instance is null or not available.");
        }
        return existingMorphoDatabaseInstance;
    }

    // Method to create or obtain the MorphoDatabase instance
    public MorphoDatabase createOrObtainMorphoDatabase() throws MorphoSmartException {
        if (existingMorphoDatabaseInstance == null) {
            existingMorphoDatabaseInstance = createInternalDatabase();
        }
        return existingMorphoDatabaseInstance;
    }

    // Create Internal Database
    public MorphoDatabase createInternalDatabase() throws MorphoSmartException {
        // Define the maximum number of records, fingers per record, and field size based on your requirements
        int maxRecords = 100; // Example value
        int maxFingersPerRecord = 2; // Example value
        int maxFieldSize = 1000; // Example value

        // Create MorphoDatabase object
        MorphoDatabase morphoDatabase = new MorphoDatabase();

        // Database creation
        int result = morphoDatabase.dbCreate(maxRecords, maxFingersPerRecord, TemplateType.getValue(maxFieldSize), 0, false);
        if (result != 0) {
            throw new MorphoSmartException("Error creating internal database. Error code: " + result);
        }

        // Get the path of the internal database
        String databasePath = getDatabasePath(morphoDatabase);
        Log.d(TAG, "Internal database created at: " + databasePath);

        // Define fields according to the table columns
        defineFields(morphoDatabase);

        return morphoDatabase;
    }

    private String getDatabasePath(MorphoDatabase morphoDatabase) {
        // Use the MorphoSmart SDK to get the database path
        // or use the Android file system APIs to determine the path
        // based on the application's internal storage directory
        File databaseFile = new File(Environment.getDataDirectory(), "data/biocapture/databases/morpho.db");
        return databaseFile.getAbsolutePath();
    }

    // Define fields in the internal database based on table columns
    private void defineFields(MorphoDatabase morphoDatabase) {
        try {
            // Define field for StudentId
            MorphoField studentIdField = new MorphoField();
            studentIdField.setName("StudentId");
            studentIdField.setMaxSize(50);
            morphoDatabase.putField(studentIdField, new CustomInteger());

            // Define field for StudentName
            MorphoField studentNameField = new MorphoField();
            studentNameField.setName("StudentName");
            studentNameField.setMaxSize(255);
            morphoDatabase.putField(studentNameField, new CustomInteger());

            // Define field for ClassId
            MorphoField classIdField = new MorphoField();
            classIdField.setName("ClassId");
            classIdField.setMaxSize(255);
            morphoDatabase.putField(classIdField, new CustomInteger());

            // Define field for Status
            MorphoField statusField = new MorphoField();
            statusField.setName("Status");
            statusField.setMaxSize(255);
            morphoDatabase.putField(statusField, new CustomInteger());

            // Define field for Arrears
            MorphoField arrearsField = new MorphoField();
            arrearsField.setName("Arrears");
            arrearsField.setMaxSize(20);
            morphoDatabase.putField(arrearsField, new CustomInteger());

            // Define field for Fingerprint1
            MorphoField fingerprint1Field = new MorphoField();
            fingerprint1Field.setName("Fingerprint1");
            fingerprint1Field.setMaxSize(1000);
            morphoDatabase.putField(fingerprint1Field, new CustomInteger());

            // Define field for Fingerprint2
            MorphoField fingerprint2Field = new MorphoField();
            fingerprint2Field.setName("Fingerprint2");
            fingerprint2Field.setMaxSize(1000);
            morphoDatabase.putField(fingerprint2Field, new CustomInteger());

        } catch (MorphoSmartException e) {
            e.printStackTrace();
        }
    }

    // Method to fetch and add all records from the API to the database
    public void addAllRecordsFromApiToDatabase(final MorphoDatabase morphoDatabase) {
        apiService.getAllTemplatesFromDatabase().enqueue(new Callback<List<FingerprintTemplate>>() {
            @Override
            public void onResponse(Call<List<FingerprintTemplate>> call, Response<List<FingerprintTemplate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FingerprintTemplate> fingerprintTemplates = response.body();
                    for (FingerprintTemplate template : fingerprintTemplates) {
                        addRecordToDatabase(morphoDatabase, template);
                    }
                } else {
                    // Handle unsuccessful response
                    System.err.println("Failed to fetch fingerprint templates from API");
                }
            }

            @Override
            public void onFailure(Call<List<FingerprintTemplate>> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
            }
        });
    }

    // Method to add a single record to the database
    public void addRecordToDatabase(MorphoDatabase morphoDatabase, FingerprintTemplate fingerprintTemplate) {
        try {
            // Creating MorphoField objects for each field
            MorphoField studentIdField = new MorphoField();
            studentIdField.setName("StudentId");
            studentIdField.setMaxSize(50);
            CustomInteger studentIdValue = new CustomInteger();
            studentIdValue.setValueOf(Integer.parseInt(fingerprintTemplate.getStudentId()));
            morphoDatabase.putField(studentIdField, studentIdValue);

            MorphoField studentNameField = new MorphoField();
            studentNameField.setName("StudentName");
            studentNameField.setMaxSize(255);
            CustomInteger studentNameValue = new CustomInteger();
            studentNameValue.setValueOf(Integer.parseInt(fingerprintTemplate.getStudentName()));
            morphoDatabase.putField(studentNameField, studentNameValue);

            MorphoField classIdField = new MorphoField();
            classIdField.setName("ClassId");
            classIdField.setMaxSize(255);
            CustomInteger classIdValue = new CustomInteger();
            classIdValue.setValueOf(Integer.parseInt(fingerprintTemplate.getClassId()));
            morphoDatabase.putField(classIdField, classIdValue);

            MorphoField statusField = new MorphoField();
            statusField.setName("Status");
            statusField.setMaxSize(255);
            CustomInteger statusValue = new CustomInteger();
            statusValue.setValueOf(Integer.parseInt(fingerprintTemplate.getStatus()));
            morphoDatabase.putField(statusField, statusValue);

            MorphoField arrearsField = new MorphoField();
            arrearsField.setName("Arrears");
            arrearsField.setMaxSize(20);
            CustomInteger arrearsValue = new CustomInteger();
            arrearsValue.setValueOf((int) fingerprintTemplate.getArrears()); // Assuming arrears is an integer
            morphoDatabase.putField(arrearsField, arrearsValue);

            MorphoField fingerprint1Field = new MorphoField();
            fingerprint1Field.setName("Fingerprint1");
            fingerprint1Field.setMaxSize(1000);
            CustomInteger fingerprint1Value = new CustomInteger();
            fingerprint1Value.setValueOf(Integer.parseInt(fingerprintTemplate.getFingerprint1()));
            morphoDatabase.putField(fingerprint1Field, fingerprint1Value);

            MorphoField fingerprint2Field = new MorphoField();
            fingerprint2Field.setName("Fingerprint2");
            fingerprint2Field.setMaxSize(1000);
            CustomInteger fingerprint2Value = new CustomInteger();
            fingerprint2Value.setValueOf(Integer.parseInt(fingerprintTemplate.getFingerprint2()));
            morphoDatabase.putField(fingerprint2Field, fingerprint2Value);


        } catch (MorphoSmartException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // Handle parsing errors
            e.printStackTrace();
        }
    }
    public List<CustomMorphoUser> queryDataFromInternalDB(MorphoDatabase internalDatabase) {
        List<CustomMorphoUser> users = new ArrayList<>();
        try {
            MorphoUser morphoUser = new MorphoUser(); // Create a MorphoUser object

            // Assuming you want to retrieve all users from the database
            int result = internalDatabase.dbQueryFirst(-1, "", morphoUser);

            while (result == 0) {
                // Convert MorphoUser to CustomMorphoUser
                CustomMorphoUser customUser = mapMorphoUserToCustomMorphoUser(morphoUser);
                users.add(customUser); // Add the custom user object to the list

                morphoUser = new MorphoUser(); // Create a new instance for the next iteration
                result = internalDatabase.dbQueryNext(morphoUser);
            }
        } catch (MorphoSmartException e) {
            // Handle MorphoSmartException
            e.printStackTrace();
        }
        return users;
    }

    private CustomMorphoUser mapMorphoUserToCustomMorphoUser(MorphoUser morphoUser) {
        CustomMorphoUser customMorphoUser = new CustomMorphoUser();

        // Map fields from MorphoUser to CustomMorphoUser
        customMorphoUser.setStudentId(morphoUser.getUserID()); // Assuming getUserID() returns studentId
        customMorphoUser.setStudentName(""); // You need to provide logic to retrieve student name
        customMorphoUser.setClassId(""); // You need to provide logic to retrieve class id
        customMorphoUser.setStatus(""); // You need to provide logic to retrieve status
        customMorphoUser.setArrears(0); // You need to provide logic to retrieve arrears
        customMorphoUser.setFingerprint1(new byte[0]); // You need to provide logic to retrieve fingerprint1
        customMorphoUser.setFingerprint2(new byte[0]); // You need to provide logic to retrieve fingerprint2

        return customMorphoUser;
    }

}
